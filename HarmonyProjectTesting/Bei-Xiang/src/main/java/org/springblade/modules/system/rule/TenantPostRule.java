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
import org.springblade.modules.system.entity.Post;
import org.springblade.modules.system.entity.Tenant;

/**
 * 租户岗位构建
 *
 * @author Chill
 */
@LiteflowComponent(id = "tenantPostRule", name = "租户岗位构建")
public class TenantPostRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		// 获取上下文
		TenantContext contextBean = this.getFirstContextBean();
		Tenant tenant = contextBean.getTenant();

		// 新建租户对应的默认岗位
		Post post = new Post();
		post.setTenantId(tenant.getTenantId());
		post.setCategory(1);
		post.setPostCode("ceo");
		post.setPostName("首席执行官");
		post.setSort(1);

		// 设置上下文
		contextBean.setPost(post);

	}
}
