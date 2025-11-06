package org.springblade.modules.beixiang.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springblade.common.utils.OkHttpUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.beixiang.controller.IotController;
import org.springblade.modules.beixiang.dto.DeviceAddOrUpdateDTO;
import org.springblade.modules.beixiang.dto.DeviceDTO;
import org.springblade.modules.beixiang.entity.Device;
import org.springblade.modules.beixiang.entity.Product;
import org.springblade.modules.beixiang.enums.CellEnum;
import org.springblade.modules.beixiang.enums.DeviceEnum;
import org.springblade.modules.beixiang.service.DeviceService;
import org.springblade.modules.beixiang.mapper.DeviceMapper;
import org.springblade.modules.beixiang.vo.DeviceTreeVO;
import org.springblade.modules.beixiang.vo.DeviceVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author haiyu
 * @description 针对表【bx_device】的数据库操作Service实现
 * @createDate 2024-06-17 17:07:51
 */
@Service
@Slf4j
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
	implements DeviceService {

	@Resource
	private IotController iotController;

	@Resource
	private BladeRedis bladeRedis;

	private Integer count = 0;

	@Transactional
	@Override
	public Long submit(DeviceAddOrUpdateDTO dto) {
		Device device;
		if (StringUtils.hasLength(dto.getId())) {
			device = getById(dto.getId());
			if (device == null) {
				throw new RuntimeException("设备不存在");
			}
			//修改设备
			updateById(device);
		} else {
			device = BeanUtil.copyProperties(dto, Device.class);
			//新增设备
			save(device);
		}
		return device.getId();
	}

	//获取设备分页列表 关键字查询
	@Override
	public IPage<DeviceVO> getDeviceList(Query query, DeviceDTO dto) {


		QueryWrapper<Device> wrapper = new QueryWrapper<>();


		wrapper.select("max(name) name, max(category) category, max(recent_report_time) recentReportTime, max(status) status, max(device_no) deviceNo")
			.and(StringUtils.hasLength(dto.getKeyword()), x -> x.like("category", dto.getKeyword())
				.or().like("name", dto.getKeyword())
				.or().like("device_no", dto.getKeyword())
			).groupBy("device_no");

		IPage<Device> page = page(Condition.getPage(query), wrapper);
		List<Device> records = page.getRecords();
		List<DeviceVO> vos = BeanUtil.copyProperties(records, DeviceVO.class);
		IPage<DeviceVO> pageVo = new Page<>();
		if (CollectionUtils.isEmpty(vos)) {
			return pageVo;
		}

		vos.forEach(device -> {
			if (DeviceEnum.parse(device.getStatus()) != null) {
				device.setStatus(getStatus());
			}
			//device.setCell(CellEnum.parse(device.getCell()).getName());
		});
		pageVo.setRecords(vos);
		pageVo.setTotal(page.getTotal());
		pageVo.setSize(page.getSize());
		pageVo.setCurrent(page.getCurrent());
		return pageVo;
	}

	//设备预警
	@Override
	public List<DeviceVO> deviceWarn() {
		QueryWrapper<Device> wrapper = new QueryWrapper<>();
		wrapper.select("max(name) name, max(category) category, max(recent_report_time) recentReportTime, max(status) status, max(device_no) deviceNo, max(cell) cell")
			.eq("status", 0)
			.groupBy("device_no");
		List<Device> deviceList = list(wrapper);
		List<DeviceVO> deviceVOS = BeanUtil.copyProperties(deviceList, DeviceVO.class);
		deviceVOS.forEach(device -> {
			device.setCell(CellEnum.parse(device.getCell()).getName());
		});
		return deviceVOS;
	}
	//设备树形结构
	@Override
	public List<DeviceTreeVO> tree() {
		List<Device> deviceList = this.list(new LambdaQueryWrapper<Device>().select(Device::getName).eq(Device::getStatus, 0).groupBy(Device::getName));
		List<DeviceTreeVO> list = new ArrayList<>();
		deviceList.forEach(device -> {
			String name = device.getName();
			List<Device> devices = this.list(new LambdaQueryWrapper<Device>()
				.select(Device::getId, Device::getName, Device::getCell)
				.eq(Device::getStatus, 0).eq(Device::getName, name)
				.groupBy(Device::getId, Device::getName, Device::getCell));
			DeviceTreeVO deviceTreeVO = BeanUtil.copyProperties(device, DeviceTreeVO.class);
			deviceTreeVO.setId(null);
			deviceTreeVO.setCell(null);
			List<DeviceTreeVO> deviceTreeVOS = new ArrayList<>();
			devices.forEach(x -> {
				DeviceTreeVO treeVO = new DeviceTreeVO();
				treeVO.setId(x.getId());
				treeVO.setName(x.getName());
				treeVO.setCell(CellEnum.parse(x.getCell()).getName());
				deviceTreeVOS.add(treeVO);
			});
			deviceTreeVO.setChildrenTreeVO(deviceTreeVOS);

			list.add(deviceTreeVO);
		});
		return list;
	}

	//通过物联网平台获取设备状态
	@SneakyThrows
	public String getStatus() {
		String baseUrl = "http://117.78.16.25:9999";
		String deviceId = "kixzybvctfbj-1800739805722574925_smart_warehouse";
		String appId = "r3mcn013stoy-1612994706281594953";
		String id = "990";
		String api = "/api/product/platformDevice/state/" + appId + "/" + deviceId + "/" + id;
		String url = baseUrl + api;
		Map<String, String> header = new HashMap<>();
		header.put("client_type", "web");
		header.put("Content-Type", "application/json");
		String token = bladeRedis.getStringRedisTemplate().opsForValue().get("iot_token").trim();
		log.info("token: {}", token);
		header.put("token", token);
		Response response = OkHttpUtil.postJson(url, "{}", header);

		String s = response.body().string();
		JSONObject object = JSON.parseObject(s);
		String errorCode = object.getString("code");
		if (errorCode.equals("50014")) {
			iotController.login("test035", "12345035");
			return DeviceEnum.Offline.getCode();
		}
		JSONObject data = object.getJSONObject("data");
		String state = data.getString("deviceState");

		String code = "";
		if (state.equals("offLine")) {
			code = DeviceEnum.Offline.getCode();
		} else {
			code = DeviceEnum.Online.getCode();
		}
		this.update().set("status", code).eq("device_no", deviceId).update();
		return code;
	}

}




