package org.springblade.modules.beixiang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.beixiang.dto.DeviceAddOrUpdateDTO;
import org.springblade.modules.beixiang.dto.DeviceDTO;
import org.springblade.modules.beixiang.dto.ProductAddOrUpdateDTO;
import org.springblade.modules.beixiang.dto.ProductDTO;
import org.springblade.modules.beixiang.entity.Device;
import org.springblade.modules.beixiang.service.DeviceService;
import org.springblade.modules.beixiang.vo.DeviceTreeVO;
import org.springblade.modules.beixiang.vo.DeviceVO;
import org.springblade.modules.beixiang.vo.ProductVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("device")
@Api(tags = "设备接口")
public class DeviceController {
	@Resource
	private DeviceService deviceService;
	@ApiOperation(value = "提交", notes = "新增或修改")
	@PostMapping("submit")
	public R<Long> submit(@RequestBody DeviceAddOrUpdateDTO dto) {
		return R.data(deviceService.submit(dto));
	}

	@GetMapping("page")
	@ApiOperation(value = "设备分页", notes = "")
	public R<IPage<DeviceVO>> page(Query query, DeviceDTO dto) {
		return R.data(deviceService.getDeviceList(query,dto));
	}

	@GetMapping("list")
	@ApiOperation(value = "设备列表", notes = "")
	public R<List<DeviceTreeVO>> tree() {
		return R.data(deviceService.tree());
	}



	@GetMapping("deviceWarn")
	@ApiOperation(value = "设备预警", notes = "")
	public R<List<DeviceVO>> deviceWarn() {
		return R.data(deviceService.deviceWarn());
	}
}
