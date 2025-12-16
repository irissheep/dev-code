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

import lombok.Data;
import org.springblade.core.tenant.TenantId;
import org.springblade.modules.system.entity.*;
import org.springblade.modules.system.service.IDictBizService;
import org.springblade.modules.system.service.IMenuService;
import org.springblade.modules.system.service.ITenantService;

import java.util.List;

/**
 * 租户上下文
 *
 * @author Chill
 */
@Data
public class TenantContext {

	/**
	 * 菜单业务
	 */
	private IMenuService menuService;

	/**
	 * 字典业务
	 */
	private IDictBizService dictBizService;

	/**
	 * 租户业务
	 */
	private ITenantService tenantService;

	/**
	 * 租户ID生成器
	 */
	private TenantId tenantIdGenerator;

	/**
	 * 租户
	 */
	private Tenant tenant;

	/**
	 * 角色
	 */
	private Role role;

	/**
	 * 角色菜单合集
	 */
	private List<RoleMenu> roleMenuList;

	/**
	 * 机构
	 */
	private Dept dept;

	/**
	 * 岗位
	 */
	private Post post;

	/**
	 * 业务字典合集
	 */
	private List<DictBiz> dictBizList;

	/**
	 * 用户
	 */
	private User user;

}
