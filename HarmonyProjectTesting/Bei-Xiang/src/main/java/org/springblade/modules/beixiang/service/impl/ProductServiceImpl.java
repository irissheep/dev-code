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
import org.springblade.modules.beixiang.dto.ProductReplenishDTO;
import org.springblade.modules.beixiang.dto.ProductReplenishItemDTO;
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
	@Transactional(rollbackFor = Exception.class)
	public boolean replenishment(ProductReplenishDTO dto) {
		if (dto == null || !StringUtils.hasLength(dto.getStatus())) {
			throw new ServiceException("补货状态不能为空");
		}
		String status = dto.getStatus();
		System.out.println("========== 补货请求开始 ==========");
		System.out.println("补货状态: " + status);
		System.out.println("接收到的DTO: " + JSON.toJSONString(dto));
		System.out.println("items数量: " + (dto.getItems() != null ? dto.getItems().size() : 0));
		System.out.println("productList数量: " + (dto.getProductList() != null ? dto.getProductList().size() : 0));
		
		bladeRedis.set("status", status);

		Map<String, String> header = new HashMap<>();
		header.put("client_type", "web");
		header.put("Content-Type", "application/json");
		header.put("token", iotController.getToken());

		String deviceId = dictBizService.getValue(DictConstant.DEVICE_ID, "shelf1");
		String url = ServiceConstant.BASE_URL + "/api/mqtt/async/" + deviceId + "/commands";
		boolean iotSuccess = false;
		try {
			Response response;
			if (ServiceConstant.STATUS_ON.equals(status)) {
				response = OkHttpUtil.postJson(url, ServiceConstant.COMMAND_ON, header);
			} else {
				response = OkHttpUtil.postJson(url, ServiceConstant.COMMAND_OFF, header);
			}

			String s = response.body().string();
			JSONObject object = JSON.parseObject(s);
			int code = object.getInteger("code");
			if (code == 50014) {
				iotController.login("test035", "12345035");
			}
			if (code == 2000 || code == 50014) {
				iotSuccess = true;
			} else {
				System.out.println("IoT指令发送失败，code: " + code + "，但继续处理库存更新");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IoT指令发送异常: " + e.getMessage() + "，但继续处理库存更新");
			// 对于完成补货，即使IoT指令失败也要更新库存
			if (ServiceConstant.STATUS_ON.equals(status)) {
				throw new ServiceException("补货指令发送失败");
			}
		}

		if (ServiceConstant.STATUS_OFF.equals(status)) {
			// 兼容两种前端提交格式：items 或 productList
			List<ProductReplenishItemDTO> items = dto.getItems();
			System.out.println("处理完成补货，原始items: " + (items != null ? items.size() : 0));
			
			if (CollectionUtils.isEmpty(items) && dto.getProductList() != null && !dto.getProductList().isEmpty()) {
				System.out.println("items为空，使用productList转换");
				items = dto.getProductList().stream().map(sp -> {
					ProductReplenishItemDTO it = new ProductReplenishItemDTO();
					Long productId = sp.getId();
					if (productId == null) {
						System.out.println("警告: productList中的id为null，跳过该项");
						return null;
					}
					it.setProductId(productId);
					it.setQuantity(sp.getNumber());
					System.out.println("转换商品: productId=" + productId + " (类型: " + productId.getClass().getName() + "), quantity=" + sp.getNumber());
					return it;
				}).filter(it -> it != null).collect(Collectors.toList());
			}

			if (!CollectionUtils.isEmpty(items)) {
				System.out.println("开始更新库存，共" + items.size() + "个商品");
				for (ProductReplenishItemDTO item : items) {
					if (item == null || item.getProductId() == null) {
						System.out.println("跳过无效商品项: " + item);
						continue;
					}
					Long productId = item.getProductId();
					System.out.println("处理商品ID: " + productId + " (类型: " + productId.getClass().getName() + ")");
					Integer qty = item.getQuantity();
					if (qty == null || qty <= 0) {
						System.out.println("跳过数量无效的商品: productId=" + productId + ", quantity=" + qty);
						continue;
					}
					Product product = getById(productId);
					if (product == null) {
						System.out.println("商品不存在，ID：" + productId + "，尝试查询数据库...");
						// 尝试直接查询数据库，看看是否存在
						Product checkProduct = baseMapper.selectById(productId);
						if (checkProduct == null) {
							throw new ServiceException("商品不存在，ID：" + productId);
						}
						product = checkProduct;
					}
					int origin = product.getNumber() == null ? 0 : product.getNumber();
					int newNumber = origin + qty;
					System.out.println("更新商品库存: productId=" + item.getProductId() + 
						", 原库存=" + origin + ", 补货数量=" + qty + ", 新库存=" + newNumber);
					product.setNumber(newNumber);
					boolean success = updateById(product);
					if (!success) {
						throw new ServiceException("更新库存失败，商品ID：" + item.getProductId());
					}
					System.out.println("商品库存更新成功: productId=" + item.getProductId());
				}
				System.out.println("所有商品库存更新完成");
			} else {
				System.out.println("警告: 没有需要更新的商品数据");
			}
		}
		System.out.println("========== 补货请求结束 ==========");
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




