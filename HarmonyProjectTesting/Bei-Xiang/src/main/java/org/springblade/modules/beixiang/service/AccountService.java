package org.springblade.modules.beixiang.service;

import org.springblade.modules.beixiang.dto.AccountAddDTO;
import org.springblade.modules.beixiang.entity.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.beixiang.entity.Bill;
import org.springblade.modules.beixiang.entity.Product;

import java.math.BigDecimal;

/**
* @author haiyu
* @description 针对表【bx_account】的数据库操作Service
* @createDate 2024-06-17 17:07:51
*/
public interface AccountService extends IService<Account> {

    Long add(AccountAddDTO dto);

	void payment(Integer number, Product product, Bill bill, boolean isReturn,String ntpTime);

	void sendConsumptionMsg(Bill bill, boolean isReturn, BigDecimal balance, BigDecimal value,String ntpTime);
	void sendBillMsg(boolean isAbnormal, Bill bill,Product product,String ntpTime);
}
