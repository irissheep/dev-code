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
package org.springblade.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.AllArgsConstructor;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tenant.TenantId;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.DesUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.system.entity.*;
import org.springblade.modules.system.mapper.TenantMapper;
import org.springblade.modules.system.rule.TenantContext;
import org.springblade.modules.system.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springblade.common.constant.TenantConstant.DES_KEY;
import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@AllArgsConstructor
public class TenantServiceImpl extends BaseServiceImpl<TenantMapper, Tenant> implements ITenantService {

	private final TenantId tenantIdGenerator;
	private final IRoleService roleService;
	private final IMenuService menuService;
	private final IDeptService deptService;
	private final IPostService postService;
	private final IRoleMenuService roleMenuService;
	private final IDictBizService dictBizService;
	private final IUserService userService;
	private final FlowExecutor flowExecutor;

	@Override
	public IPage<Tenant> selectTenantPage(IPage<Tenant> page, Tenant tenant) {
		return page.setRecords(baseMapper.selectTenantPage(page, tenant));
	}

	@Override
	public Tenant getByTenantId(String tenantId) {
		return getOne(Wrappers.<Tenant>query().lambda().eq(Tenant::getTenantId, tenantId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean submitTenant(Tenant tenant) {
		if (Func.isEmpty(tenant.getId())) {
			TenantContext tenantContext = new TenantContext();
			tenantContext.setTenantIdGenerator(tenantIdGenerator);
			tenantContext.setTenant(tenant);
			tenantContext.setMenuService(menuService);
			tenantContext.setDictBizService(dictBizService);
			tenantContext.setTenantService(this);

			LiteflowResponse resp = flowExecutor.execute2Resp("tenantChain", null, tenantContext);
			if (resp.isSuccess()) {
				Role role = tenantContext.getRole();
				roleService.save(role);

				Long roleId = role.getId();
				List<RoleMenu> roleMenuList = tenantContext.getRoleMenuList();
				roleMenuList.forEach(roleMenu -> roleMenu.setRoleId(roleId));
				roleMenuService.saveBatch(roleMenuList);

				Dept dept = tenantContext.getDept();
				deptService.save(dept);

				Post post = tenantContext.getPost();
				postService.save(post);

				List<DictBiz> dictBizList = tenantContext.getDictBizList();
				dictBizService.saveBatch(dictBizList);

				User user = tenantContext.getUser();
				user.setRoleId(String.valueOf(role.getId()));
				user.setDeptId(String.valueOf(dept.getId()));
				user.setPostId(String.valueOf(post.getId()));
				userService.submit(user);
			} else {
				throw new ServiceException("租户业务数据构建异常");
			}
		}
		CacheUtil.clear(SYS_CACHE, tenant.getTenantId());
		return super.saveOrUpdate(tenant);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeTenant(List<Long> ids) {
		List<String> tenantIds = this.list(Wrappers.<Tenant>query().lambda().in(Tenant::getId, ids))
			.stream().map(tenant -> Func.toStr(tenant.getTenantId())).distinct().collect(Collectors.toList());
		CacheUtil.clear(SYS_CACHE, tenantIds);
		if (tenantIds.contains(BladeConstant.ADMIN_TENANT_ID)) {
			throw new ServiceException("不可删除管理租户!");
		}
		boolean tenantTemp = this.deleteLogic(ids);
		boolean userTemp = userService.remove(Wrappers.<User>query().lambda().in(User::getTenantId, tenantIds));
		return tenantTemp && userTemp;
	}

	@Override
	public boolean setting(Integer accountNumber, Date expireTime, String ids) {
		List<String> tenantIds = this.list(Wrappers.<Tenant>query().lambda().in(Tenant::getId, ids))
			.stream().map(tenant -> Func.toStr(tenant.getTenantId())).distinct().collect(Collectors.toList());
		CacheUtil.clear(SYS_CACHE, tenantIds);
		Func.toLongList(ids).forEach(id -> {
			Kv kv = Kv.create().set("accountNumber", accountNumber).set("expireTime", expireTime).set("id", id);
			String licenseKey = DesUtil.encryptToHex(JsonUtil.toJson(kv), DES_KEY);
			update(
				Wrappers.<Tenant>update().lambda()
					.set(Tenant::getAccountNumber, accountNumber)
					.set(Tenant::getExpireTime, expireTime)
					.set(Tenant::getLicenseKey, licenseKey)
					.eq(Tenant::getId, id)
			);
		});
		return true;
	}

}
