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

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springblade.common.cache.ParamCache;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.auth.enums.UserEnum;
import org.springblade.modules.system.entity.Tenant;
import org.springblade.modules.system.entity.User;

import java.util.Date;

import static org.springblade.common.constant.TenantConstant.DEFAULT_PASSWORD;
import static org.springblade.common.constant.TenantConstant.PASSWORD_KEY;

/**
 * 租户用户构建
 *
 * @author Chill
 */
@LiteflowComponent(id = "tenantUserRule", name = "租户用户构建")
public class TenantUserRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		// 获取上下文
		TenantContext contextBean = this.getFirstContextBean();
		Tenant tenant = contextBean.getTenant();

		// 新建租户对应的默认管理用户
		User user = new User();
		user.setTenantId(tenant.getTenantId());
		user.setName("admin");
		user.setRealName("admin");
		user.setAccount("admin");
		// 获取参数配置的密码
		String password = Func.toStr(ParamCache.getValue(PASSWORD_KEY), DEFAULT_PASSWORD);
		user.setPassword(password);
		user.setBirthday(new Date());
		user.setSex(1);
		user.setUserType(UserEnum.WEB.getCategory());
		user.setIsDeleted(BladeConstant.DB_NOT_DELETED);

		// 设置上下文
		contextBean.setUser(user);
	}
}
