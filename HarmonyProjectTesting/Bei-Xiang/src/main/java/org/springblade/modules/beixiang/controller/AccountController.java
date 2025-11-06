package org.springblade.modules.beixiang.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.modules.beixiang.dto.AccountAddDTO;
import org.springblade.modules.beixiang.dto.ProductAddOrUpdateDTO;
import org.springblade.modules.beixiang.entity.Account;
import org.springblade.modules.beixiang.entity.AccountLog;
import org.springblade.modules.beixiang.service.AccountLogService;
import org.springblade.modules.beixiang.service.AccountService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("account")
@Api(tags = "账户接口")
public class AccountController {

	@Resource
	private AccountService accountService;

	@Resource
	private AccountLogService accountLogService;
	@ApiOperation(value = "充值", notes = "")
	@PostMapping("add")
	public R<Long> add(@RequestBody AccountAddDTO dto) {
		return R.data(accountService.add(dto));
	}

	@GetMapping("log")
	@ApiOperation(value = "充值记录", notes = "传入accountId")
	public R<IPage<AccountLog>> log(Query query, Long accountId) {
		return R.data(accountLogService.page(Condition.getPage(query),new LambdaQueryWrapper<AccountLog>().eq(AccountLog::getAccountId, accountId)));
	}

	@GetMapping("getBalance")
	@ApiOperation(value = "账户余额", notes = "传入用户id")
	public R<Account> getBalance(Long userId) {
		Account account = accountService.getOne(new LambdaQueryWrapper<Account>().eq(Account::getUserId, userId)
			.eq(Account::getIsDeleted, 0));
		return R.data(account);
	}
}
