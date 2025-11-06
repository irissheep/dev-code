package org.springblade.modules.beixiang.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import okhttp3.Response;
import org.springblade.common.constant.DictConstant;
import org.springblade.common.constant.ServiceConstant;
import org.springblade.common.utils.OkHttpUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.beixiang.controller.IotController;
import org.springblade.modules.beixiang.dto.ProductAddOrUpdateDTO;
import org.springblade.modules.beixiang.dto.ProductDTO;
import org.springblade.modules.beixiang.entity.Device;
import org.springblade.modules.beixiang.entity.Product;
import org.springblade.modules.beixiang.enums.CellEnum;
import org.springblade.modules.beixiang.service.ProductService;
import org.springblade.modules.beixiang.mapper.ProductMapper;
import org.springblade.modules.beixiang.vo.InventoryVO;
import org.springblade.modules.beixiang.vo.ProductVO;
import org.springblade.modules.beixiang.vo.SaleRankVO;
import org.springblade.modules.system.service.IDictBizService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * @author haiyu
 * @description 针对表【bx_product】的数据库操作Service实现
 * @createDate 2024-06-17 17:07:51
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
	implements ProductService {

	@Resource
	private IotController iotController;
	@Resource
	private BladeRedis bladeRedis;


	@Resource
	private IDictBizService dictBizService;
	/**
	 * 提交商品信息
	 **/
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Long submit(ProductAddOrUpdateDTO dto) {

		Product product;
		if (dto.getId()!=null) {
			product = getById(dto.getId());
			if(product==null) {
				throw new ServiceException("商品不存在");
			}
			product = BeanUtil.copyProperties(dto, Product.class);
			//修改商品
			updateById(product);

		} else {
			product = BeanUtil.copyProperties(dto, Product.class);
			product.setDeviceId(product.getDeviceId());
			//新增商品
			save(product);
		}
		Device device = Db.getById(product.getDeviceId(), Device.class);
		bladeRedis.hSet("product",device.getCell(),dto.getWeight());
		return product.getId();

	}
	/**
	 * 获取商品详情
	 **/
	@Override
	public ProductVO detail(String id) {
		Product product = getById(id);
		if(product==null) {
			throw new ServiceException("商品不存在");
		}
		Device device = Db.getById(product.getDeviceId(), Device.class);
		ProductVO vo = BeanUtil.copyProperties(product, ProductVO.class);
		vo.setCell(CellEnum.parse(device.getCell()).getName());
		vo.setShelf(device.getName());
		return vo;
	}
	/**
     * 获取商品分页数据
     **/
	@Override
	public IPage<ProductVO> getProductList(Query query, ProductDTO dto) {
		IPage<ProductVO> page = baseMapper.getProductByPage(Condition.getPage(query), dto);
		page.getRecords().forEach(product -> {
			//阈值
			Integer value = product.getThresholdValue();
			//库存
			Integer number = product.getNumber();
			//商品状态
			String status = bladeRedis.get("status");
			product.setCell(CellEnum.parse(product.getCell()).getName());
				if(StringUtils.hasLength(status)){
					if (status.equals("1")) {
						product.setStatus("补货中");
					}else if(status.equals("2")){
						//完成补货
						product.setStatus("");
						bladeRedis.del("status");

					}
				}else{
					if(value>=number){
						product.setStatus("库存不足");
					}else {
						//库存充足
						product.setStatus("");
					}

				}
		});
		return page;
	}
	//商品库存统计
	@Override
	public List<InventoryVO> inventory() {
		List<InventoryVO> inventory = baseMapper.getInventory();
		return inventory;
	}
	//status 1 开始补货, 2 完成补货
	@Override
	public boolean replenishment(String status) {
		bladeRedis.set("status",status);

		Map<String, String> header = new HashMap<>();
		header.put("client_type", "web");
		header.put("Content-Type", "application/json");
		header.put("token", iotController.getToken());
		//String body = JSON.toJSONString(param);

		String deviceId = dictBizService.getValue(DictConstant.DEVICE_ID, "shelf1");
		String url = ServiceConstant.BASE_URL + "/api/mqtt/async/"+deviceId+"/commands";
		try {
			Response response = null;
			if(status.equals(ServiceConstant.STATUS_ON)){
				response = OkHttpUtil.postJson(url, ServiceConstant.COMMAND_ON, header);
			}else{
				response = OkHttpUtil.postJson(url, ServiceConstant.COMMAND_OFF, header);
			}

			String s = response.body().string();
			JSONObject object = JSON.parseObject(s);
			int code = object.getInteger("code");
			if (code ==50014) {
				iotController.login("test035", "12345035");
			}
			if(code!= 2000&&code !=50014){
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}
	//销量排行 condition 降序 desc, 升序 asc
	@Override
	public List<SaleRankVO> SaleRank(String condition) {

		return baseMapper.SaleRank(condition);
	}
	//补货预警
	@Override
	public List<ProductVO> replenishWarn() {
		//查询阈值大于库存的数据
		List<Product> list = list(new QueryWrapper<Product>().apply("threshold_value > number").eq("is_deleted",0));
		List<Device> devices = Db.list(Device.class);
		List<ProductVO> vos = BeanUtil.copyProperties(list, ProductVO.class);
		Map<Long, String> map = devices.stream().collect(Collectors.toMap(Device::getId, o -> o.getName() + "-" + CellEnum.parse(o.getCell()).getName()));
		vos.forEach(product ->{
			product.setShelf(map.get(product.getDeviceId()));
		});
		return vos;
	}

	@Override
	public List<String> listName() {
		List<Product> list = list(new QueryWrapper<Product>().eq("is_deleted",0));
		if(CollectionUtils.isEmpty(list)){
            return null;
        }
		return list.stream().map(Product::getName).collect(Collectors.toList());
	}
}




