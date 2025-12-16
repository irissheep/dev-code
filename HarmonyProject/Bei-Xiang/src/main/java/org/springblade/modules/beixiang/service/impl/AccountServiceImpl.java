package org.springblade.modules.beixiang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springblade.common.constant.ServiceConstant;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.beixiang.dto.AccountAddDTO;
import org.springblade.modules.beixiang.entity.*;
import org.springblade.modules.beixiang.enums.BillStatusEnum;
import org.springblade.modules.beixiang.service.AccountService;
import org.springblade.modules.beixiang.mapper.AccountMapper;
import org.springblade.modules.beixiang.service.WebSocket;
import org.springblade.modules.system.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author haiyu
 * @description 针对表【bx_account】的数据库操作Service实现
 * @createDate 2024-06-17 17:07:51
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
	implements AccountService {

	@Resource
	private WebSocket webSocket;

	@Resource
	private BladeRedis bladeRedis;


	@Override
	public Long add(AccountAddDTO dto) {
		User user = Db.getById(dto.getUserId(), User.class);
		if (user == null) {
			throw new IllegalStateException("当前用户不存在");
		}
		Account account = getOne(new QueryWrapper<Account>().eq("user_id", user.getId())
			.eq("is_deleted", 0));
		if (account == null) {
			account = new Account();
			account.setBalance(dto.getRechargeAmount());
			account.setUserId(user.getId());
			save(account);
		} else {
			BigDecimal balance = account.getBalance();
			BigDecimal total = balance.add(dto.getRechargeAmount());
			account.setBalance(total);
			updateById(account);

		}
		AccountLog accountLog = new AccountLog();
		accountLog.setAccountId(account.getId());
		accountLog.setRechargeAmount(dto.getRechargeAmount());
		accountLog.setCreateUser(user.getId());
		accountLog.setCreateTime(new Date());
		Db.save(accountLog);
		return account.getId();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public synchronized void payment(Integer number, Product product, Bill bill, boolean isReturn,String ntpTime) {
		BigDecimal price = product.getPrice();
		BigDecimal value = price.multiply(new BigDecimal(number));
		Long userId = bill.getUserId();
		if(userId!=null) {
			Account account = this.getOne(new LambdaQueryWrapper<Account>().eq(Account::getUserId, userId));
			BigDecimal balance = account.getBalance();

			if (isReturn) {
				BigDecimal addBalance = balance.add(value);
				account.setBalance(addBalance);
				sendConsumptionMsg(bill, isReturn, addBalance, value,ntpTime);
			} else {
				BigDecimal subBalance = balance.subtract(value);
				account.setBalance(subBalance);
				sendConsumptionMsg(bill, isReturn, subBalance, value,ntpTime);
				sendBillMsg(false,bill,product,ntpTime);
			}
			this.updateById(account);
		}
	}

	public void sendConsumptionMsg(Bill bill, boolean isReturn, BigDecimal balance, BigDecimal value,String ntpTime) {
		String billNo = bill.getBillNo();
		Long userId = bill.getUserId();
		Message message = new Message();
		message.setNtpTime(ntpTime);
		message.setType(BillStatusEnum.FINISH.getCode());
		message.setBillId(bill.getId());
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sf.format(date);
		message.setCreateTime(format);
		message.setUserId(userId+"");
		if (isReturn) {

			message.setTitle("商品退回");

			message.setMsg("您的退回商品订单" + billNo + "金额为" + value + "元，已返回您的账户余额！");
			Db.save(message);
		} else {
			if (balance.doubleValue() < 0) {
				Message rechargeMessage = new Message();
				rechargeMessage.setUserId(userId+"");
				rechargeMessage.setCreateTime(format);
				rechargeMessage.setTitle("充值提醒");
				rechargeMessage.setMsg("您的账户余额已不足,请及时充值");
				rechargeMessage.setType(BillStatusEnum.FINISH.getCode());
				Db.save(rechargeMessage);
				webSocket.sendOneMessage(userId + "", rechargeMessage);
			}
			message.setTitle("购买扣款");
			message.setMsg("您购买的订单" + billNo + "消费金额为" + value + "元，已从您的账户余额扣款！");
			Db.save(message);
		}
		webSocket.sendOneMessage(userId + "", message);
	}

	public void sendBillMsg(boolean isAbnormal,Bill bill,Product product,String ntpTime) {

		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sf.format(date);
		if(isAbnormal){
			Message message = new Message();
			message.setUserId(bill.getMerchantId());
			message.setCreateTime(format);
			message.setBillId(bill.getId());
			message.setNtpTime(ntpTime);
			if (bill.getStatus().equals(BillStatusEnum.ABNORMAL_FETCH.getCode())) {
				message.setTitle("异常取走");
				message.setType("1");
				message.setMsg("检测到您的商品被异常取走，请及时处理！");

			} else if (bill.getStatus().equals(BillStatusEnum.ABNORMAL_FALLBACK.getCode())) {
				message.setTitle("异常退回");
				message.setType("2");
				message.setMsg("检测到您有商品异常退回，请及时处理！");
			}
			Db.save(message);
			webSocket.sendMoreMessage( Func.split(bill.getMerchantId(),","), message);
		}

		if (product.getNumber()<product.getThresholdValue()){
			Message buhuoMsg = new Message();
			buhuoMsg.setCreateTime(format);
			buhuoMsg.setUserId(bill.getMerchantId());
			buhuoMsg.setTitle("补货预警");
			buhuoMsg.setType("0");
			buhuoMsg.setMsg("您的商品："+product.getName()+" 库存量已不足，请及时补货！");
			Db.save(buhuoMsg);
			webSocket.sendMoreMessage(Func.split(bill.getMerchantId(),","), buhuoMsg);
		}

	}
}




