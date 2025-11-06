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
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.modules.system.entity.Dept;
import org.springblade.modules.system.entity.Tenant;

/**
 * 租户机构构建
 *
 * @author Chill
 */
@LiteflowComponent(id = "tenantDeptRule", name = "租户机构构建")
public class TenantDeptRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		// 获取上下文
		TenantContext contextBean = this.getFirstContextBean();
		Tenant tenant = contextBean.getTenant();

		// 新建租户对应的默认部门
		Dept dept = new Dept();
		dept.setTenantId(tenant.getTenantId());
		dept.setParentId(BladeConstant.TOP_PARENT_ID);
		dept.setAncestors(String.valueOf(BladeConstant.TOP_PARENT_ID));
		dept.setDeptName(tenant.getTenantName());
		dept.setFullName(tenant.getTenantName());
		dept.setDeptCategory(1);
		dept.setSort(2);
		dept.setIsDeleted(BladeConstant.DB_NOT_DELETED);

		// 设置上下文
		contextBean.setDept(dept);

	}
}
