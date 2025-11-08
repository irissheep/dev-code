package org.springblade.modules.beixiang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.beixiang.dto.BillDTO;
import org.springblade.modules.beixiang.dto.NumberCountDTO;
import org.springblade.modules.beixiang.entity.*;
import org.springblade.modules.beixiang.enums.ActionStatusEnum;
import org.springblade.modules.beixiang.enums.BillStatusEnum;
import org.springblade.modules.beixiang.service.AccountService;
import org.springblade.modules.beixiang.service.BillService;
import org.springblade.modules.beixiang.mapper.BillMapper;
import org.springblade.modules.beixiang.service.RelUserService;
import org.springblade.modules.beixiang.util.SerialNumberGenUtil;
import org.springblade.modules.beixiang.vo.BillVO;
import org.springblade.modules.beixiang.vo.ProductResVO;
import org.springblade.modules.beixiang.vo.PurchaseBehaviorVO;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IUserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author haiyu
 * @description 针对表【bx_bill】的数据库操作Service实现
 * @createDate 2024-06-17 17:07:51
 */
@Service
@Slf4j
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill>
	implements BillService {
	@Resource
	private AccountService accountService;

	@Resource
	private BladeRedis bladeRedis;

	@Resource
	private RelUserService relUserService;

	@Resource
	private IUserService userService;


	//private Long userId = 1806202243577384962l;

	@Override
	public BillVO detail(Long id) {
		Bill bill = getById(id);
		if (bill == null) {
			return null;
		}
		BillVO vo = BeanUtil.copyProperties(bill, BillVO.class);
		Long userId = bill.getUserId();
		User user = Db.getById(userId, User.class);
		if (user != null) {
			String account = user.getAccount();
			vo.setAccount(account);
		}
		List<ProductRelBill> list = Db.lambdaQuery(ProductRelBill.class).eq(ProductRelBill::getBillId, id).list();
		Map<Long, Integer> map = new HashMap<>();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(productRelBill -> {
				Long productId = productRelBill.getProductId();
				Integer productNumber = productRelBill.getProductNumber();
				map.put(productId, productNumber);
			});
			List<Long> ids = list.stream().map(ProductRelBill::getProductId).collect(Collectors.toList());
			List<Product> productList = Db.lambdaQuery(Product.class).in(Product::getId, ids).eq(Product::getIsDeleted, 0).list();
			List<ProductResVO> productResVOList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(productList)) {
				productList.forEach(product -> {
					ProductResVO productResVO = BeanUtil.copyProperties(product, ProductResVO.class);
					Long productId = product.getId();
					Integer productNumber = map.get(productId);
					if (productNumber != null) {
						productResVO.setNumber(productNumber);
					}
					productResVOList.add(productResVO);
				});
			}
			vo.setProductList(productResVOList);
			vo.setPictureUrl("");
		}
		return vo;
	}

	@Override
	public IPage<BillVO> getPage(Query query, Long userId, String keyword) {
		IPage<BillVO> page = baseMapper.getPage(Condition.getPage(query), userId, keyword);
		List<BillVO> records = page.getRecords();

		if (CollectionUtils.isEmpty(records)) {
			return page;
		}
		Set<Long> productIds = records.stream().map(BillVO::getProductId).collect(Collectors.toSet());
		List<Product> productList = Db.lambdaQuery(Product.class).in(Product::getId, productIds).list();
		List<ProductResVO> productResVOs = BeanUtil.copyToList(productList, ProductResVO.class);
		Map<Long, ProductResVO> productMap = productResVOs.stream().collect(Collectors.toMap(ProductResVO::getId, Function.identity()));
		Account account = Db.lambdaQuery(Account.class).eq(Account::getUserId, userId).one();
		Set<Long> userIds = records.stream().filter(x -> x.getUserId() != null).map(BillVO::getUserId).collect(Collectors.toSet());
		List<User> users = userService.list(new LambdaQueryWrapper<User>().in(User::getId, userIds).eq(User::getIsDeleted, 0));
		Map<Long, String> userMap = users.stream().collect(Collectors.toMap(User::getId, User::getAccount));
		//Map<Long, Long> billMap = records.stream().collect(Collectors.toMap(BillVO::getId, BillVO::getUserId));
		records.stream().forEach(billVO -> {
			List<ProductResVO> list = new ArrayList<>();
			list.add(productMap.get(billVO.getProductId()));
			billVO.setProductList(list);
			if (account != null) {
				billVO.setBalance(account.getBalance());
			} else {
				billVO.setBalance(BigDecimal.valueOf(0));
			}
			if(billVO.getUserId()!=null){
				billVO.setAccount(userMap.get(billVO.getUserId()));
			}


		});
		return page;
	}

	@Override
	public boolean editStatus(Message msg) {
		Message message = Db.lambdaQuery(Message.class)
			.eq(Message::getId, msg.getId())
			.eq(Message::getUserId, msg.getUserId())
			.one();
		message.setStatus(msg.getStatus());
		return Db.updateById(message);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean handleBill(BillDTO dto) {
		Bill bill = new Bill();
		BeanUtil.copyProperties(dto, bill);
		// 0为忽略 1 为处理 订单
		if ("1".equals(dto.getHandle())) {
			// 验证必要参数
			if (dto.getUserId() == null) {
				throw new RuntimeException("用户ID不能为空");
			}
			if (dto.getAmount() == null) {
				throw new RuntimeException("金额不能为空");
			}
			if (dto.getStatus() == null) {
				throw new RuntimeException("订单状态不能为空");
			}
			
			// 查询账户 - 确保userId类型正确
			Long userId = dto.getUserId();
			if (userId == null) {
				throw new RuntimeException("用户ID无效");
			}
			
			Account account = Db.lambdaQuery(Account.class)
				.eq(Account::getUserId, userId)
				.eq(Account::getIsDeleted, 0) // 确保账户未被删除
				.one();
			
			if (account == null) {
				throw new RuntimeException("未找到用户账户，用户ID: " + userId + "。请确认该用户是否已创建账户。");
			}
			
			// 验证账户余额字段（使用try-catch捕获可能的NullPointerException）
			BigDecimal balance = null;
			try {
				balance = account.getBalance();
			} catch (NullPointerException e) {
				throw new RuntimeException("账户对象异常，用户ID: " + userId + "，错误: " + e.getMessage());
			}
			
			if (balance == null) {
				throw new RuntimeException("账户余额信息不完整，用户ID: " + userId);
			}
			
			BigDecimal amount = dto.getAmount();
			if (amount == null) {
				throw new RuntimeException("扣款金额不能为空");
			}
			
			if (BillStatusEnum.ABNORMAL_FETCH.getCode().equals(dto.getStatus())) {
				// 异常取走 - 扣款
				if (balance.compareTo(amount) < 0) {
					throw new RuntimeException("账户余额不足，当前余额: " + balance + ", 需要扣款: " + amount);
				}
				BigDecimal subBalance = balance.subtract(amount);
				account.setBalance(subBalance);
				boolean updateResult = Db.updateById(account);
				if (!updateResult) {
					throw new RuntimeException("更新账户余额失败");
				}
				accountService.sendConsumptionMsg(bill, false, subBalance, amount, null);
				bill.setStatus(BillStatusEnum.FINISH.getCode());
			} else if (BillStatusEnum.ABNORMAL_FALLBACK.getCode().equals(dto.getStatus())) {
				// 异常退回 - 退款
				BigDecimal addBalance = balance.add(amount);
				account.setBalance(addBalance);
				boolean updateResult = Db.updateById(account);
				if (!updateResult) {
					throw new RuntimeException("更新账户余额失败");
				}
				accountService.sendConsumptionMsg(bill, true, addBalance, amount, null);
				bill.setStatus(BillStatusEnum.FINISH.getCode());
			}
		} else if ("0".equals(dto.getHandle())) {
			// 忽略操作 - 只更新订单状态，不处理账户
			// 可以在这里添加忽略后的状态更新逻辑，如果需要的话
		}
		return updateById(bill);
	}

	@Override
	public List<PurchaseBehaviorVO> purchaseBehavior(NumberCountDTO dto) {

		return baseMapper.purchaseBehavior(dto);
	}

	@Override
	public Map<String, String> saleCount() {
		List<Bill> billList = list(new LambdaQueryWrapper<Bill>()
			.eq(Bill::getStatus, 1)
			.eq(Bill::getIsDeleted, 0));
		BigDecimal totalAmount = new BigDecimal(0);
		int count = 0;
		for (Bill bill : billList) {
			BigDecimal amount = bill.getTotalAmount();
			if (bill.getAction().equals(ActionStatusEnum.FETCH.getCode())) {
				totalAmount = totalAmount.add(amount);
				count++;
			} else {
				totalAmount = totalAmount.subtract(amount);
				count--;
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("totalAmount", totalAmount + "");
		map.put("count", count + "");
		return map;
	}

	@Async
	@Override
	public void handleData(String deviceId, Map<String, Double> weightMap, Date recentTime, String ntpTime,Long userId) {
		//查询货架
		List<Device> deviceList = Db.lambdaQuery(Device.class).eq(Device::getDeviceNo, deviceId).eq(Device::getIsDeleted, 0).list();
		//RelUser relUser = getRelUser(ntpTime);
		/*if(relUser!=null) {
			userId = Long.valueOf(relUser.getUserId());
		}*/
		//货架不存在 添加货架
		if (CollectionUtils.isEmpty(deviceList)) {
			Long deviceNo = bladeRedis.incr("device");
			for (Map.Entry<String, Double> entry : weightMap.entrySet()) {
				Device device = new Device();
				device.setTotalWeight(entry.getValue());
				device.setDeviceNo(deviceId);
				device.setRecentReportTime(recentTime);
				device.setCell(entry.getKey());

				device.setCategory("货架");

				//判断设备编号是否存在,不存在,设置编号,存在加一
				if (bladeRedis.exists("device")) {
					device.setName("货架" + deviceNo);
					bladeRedis.set(deviceId, device.getName());
				} else {
					device.setName("货架" + 1);
					bladeRedis.set("device", 1);
					bladeRedis.set(deviceId, device.getName());
				}
				//保存货架
				Db.save(device);
				bladeRedis.hSet("shelf:" + deviceId, device.getId(), entry.getValue());
			}
		} else {
			//货架存在
			List<Long> deviceIds = deviceList.stream().map(Device::getId).collect(Collectors.toList());
			//获取货架格子
			Map<Long, String> map = deviceList.stream().collect(Collectors.toMap(Device::getId, Device::getCell));
			//查询货架格子对应的商品
			List<Product> products = Db.lambdaQuery(Product.class).in(Product::getDeviceId, deviceIds).eq(Product::getIsDeleted, 0).list();
			//String status = redisTemplate.opsForValue().get("status");
			//获取商品状态
			String status = bladeRedis.get("status");
			List<User> list = Db.lambdaQuery(User.class).eq(User::getCategory, "2").list();
			List<Long> merchantIds = list.stream().map(User::getId).collect(Collectors.toList());
			String ids = Func.join(merchantIds, ",");
			//遍历商品
			for (Product product : products) {
				Double totalWeight = 0d;
				//获取原先商品总重量
				if (bladeRedis.hExists("shelf:" + deviceId, product.getId())) {
					totalWeight = bladeRedis.hGet("shelf:" + deviceId, product.getId());
				} else {
					totalWeight = Db.getById(product.getDeviceId(), Device.class).getTotalWeight();
				}
				//String deviceName = bladeRedis.get(deviceId);
				//得到商品对应格子
				String cell = map.get(product.getDeviceId());
				//获取数据上报的重量
				Double weight = weightMap.get(cell);
				//得到商品重量的变化  原先商品的总重量  -  现在商品的总重量
				double subWeight = totalWeight - weight;
				Integer number = 0;
				Integer change = 0;
				boolean isReturn = false;
				//小于等于0 商品被退回
				if (subWeight < 0) {
					//商品变化数量
					change = BigDecimal.valueOf(Math.abs(subWeight)).divide(product.getWeight(), 0, RoundingMode.HALF_UP).intValue();
					number = BigDecimal.valueOf(weight).divide(product.getWeight(), 0, RoundingMode.HALF_UP).intValue();
					product.setNumber(number);

					isReturn = true;
				} else if (subWeight > 0) {
					//大于0 商品被取走
					change = BigDecimal.valueOf(subWeight).divide(product.getWeight(), 0, RoundingMode.HALF_UP).intValue();
					//product.setNumber(product.getNumber() - number);
					number = BigDecimal.valueOf(weight).divide(product.getWeight(), 0, RoundingMode.HALF_UP).intValue();
					product.setNumber(number);
				} else {
					//重量无变化 跳过当前循环,进行下一次循环 不进行后续操作
					continue;
				}
				//更新商品数据
				Db.updateById(product);

				//redisTemplate.opsForHash().put("shelf:" + deviceId, product.getId(), weight);
				bladeRedis.hSet("shelf:" + deviceId, product.getId(), weight);
				//更新货架数据
				Device device = new Device();
				device.setId(product.getDeviceId());
				device.setTotalWeight(weight);
				device.setRecentReportTime(recentTime);
				Db.updateById(device);
				//商品状态 处于补货中 不进行后续操作
				if (StringUtils.hasLength(status) && status.equals("1")) {
					continue;
				}
				//商品变化数量等于0 不进行后续操作
				if (change != 0) {
					//商品总额
					BigDecimal totalAmount = BigDecimal.valueOf(change).multiply(product.getPrice());

					Bill bill = new Bill();
					Date date = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					String curDate = dateFormat.format(date);
					//设置订单编号
					String no = SerialNumberGenUtil.createAutoID(bladeRedis, "NO", 4);
					no = "No" + curDate + no;
					bill.setBillNo(no);



					bill.setTotalNumber(change);
					//relUser = getRelUser(ntpTime);
					bill.setUserId(userId);
//					if(relUser != null){
//						userId = Long.valueOf(relUser.getUserId());
//					}
					//归还
					if (isReturn) {
						bill.setTotalAmount(totalAmount);
						bill.setAction(ActionStatusEnum.FALLBACK.getCode());
							if(userId==null) {

								bill.setStatus(BillStatusEnum.ABNORMAL_FALLBACK.getCode());
								Db.save(bill);
								accountService.sendBillMsg(true,bill, product,ntpTime);
							}else {
								//Bill one = baseMapper.getBillIdByproductId(product.getId(), userId);
								bill.setCreateUser(userId);
								bill.setStatus(BillStatusEnum.FINISH.getCode());
								bill.setUserId(userId);
								bill.setUpdateUser(userId);
								Db.save(bill);
								accountService.sendBillMsg(false,bill, product,ntpTime);
							}

					} else {
						//拿走
						bill.setAction(ActionStatusEnum.FETCH.getCode());
						bill.setMerchantId(ids);
						bill.setTotalAmount(totalAmount);

						if(userId==null) {
							bill.setStatus(BillStatusEnum.ABNORMAL_FETCH.getCode());
							Db.save(bill);
							accountService.sendBillMsg(true,bill, product,ntpTime);
						}else {
							bill.setStatus(BillStatusEnum.FINISH.getCode());
							Db.save(bill);
							accountService.sendBillMsg(false,bill, product,ntpTime);
						}
					}

					ProductRelBill relBill = new ProductRelBill();
					relBill.setProductId(product.getId());
					relBill.setProductNumber(change);
					relBill.setBillId(bill.getId());
					relBill.setCreateUser(userId);
					relBill.setUpdateUser(userId);

					Db.save(relBill);
					bladeRedis.hSet("billNo", bill.getId(), change);
					accountService.payment(change, product, bill, isReturn,ntpTime);
				}
			}
		}
	}


	private RelUser getRelUser(String ntpTime) {
		//RelUser relUser = Db.getOne(new LambdaQueryWrapper<RelUser>().eq(RelUser::getNtpTime, ntpTime));
		RelUser relUser = relUserService.getOne(new LambdaQueryWrapper<RelUser>().eq(RelUser::getNtpTime, ntpTime));
		return relUser;
	}


}




