/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.modules.system.rule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springblade.common.cache.ParamCache;
import org.springblade.core.tenant.TenantId;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.system.entity.Tenant;
import org.springblade.modules.system.service.ITenantService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springblade.common.constant.TenantConstant.ACCOUNT_NUMBER_KEY;
import static org.springblade.common.constant.TenantConstant.DEFAULT_ACCOUNT_NUMBER;

/**
 * 租户构建
 *
 * @author Chill
 */
@LiteflowComponent(id = "tenantRule", name = "租户构建")
public class TenantRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		// 获取上下文
		TenantContext contextBean = this.getFirstContextBean();
		Tenant tenant = contextBean.getTenant();
		TenantId tenantIdGenerator = contextBean.getTenantIdGenerator();
		ITenantService tenantService = contextBean.getTenantService();

		// 获取租户ID
		List<Tenant> tenants = tenantService.list(Wrappers.<Tenant>query().lambda().eq(Tenant::getIsDeleted, BladeConstant.DB_NOT_DELETED));
		List<String> codes = tenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());
		String tenantId = getTenantId(tenantIdGenerator, codes);
		tenant.setTenantId(tenantId);
		// 获取参数配置的账号额度
		int accountNumber = Func.toInt(ParamCache.getValue(ACCOUNT_NUMBER_KEY), DEFAULT_ACCOUNT_NUMBER);
		tenant.setAccountNumber(accountNumber);

		// 设置上下文
		contextBean.setTenant(tenant);

	}

	private String getTenantId(TenantId tenantIdGenerator, List<String> codes) {
		String code = tenantIdGenerator.generate();
		if (codes.contains(code)) {
			return getTenantId(tenantIdGenerator, codes);
		}
		return code;
	}
}
