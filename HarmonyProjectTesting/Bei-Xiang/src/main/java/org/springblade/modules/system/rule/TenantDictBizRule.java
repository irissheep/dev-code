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

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.modules.system.entity.DictBiz;
import org.springblade.modules.system.entity.Tenant;
import org.springblade.modules.system.service.IDictBizService;

import java.util.LinkedList;
import java.util.List;

/**
 * 租户业务字典构建
 *
 * @author Chill
 */
@LiteflowComponent(id = "tenantDictBizRule", name = "租户业务字典构建")
public class TenantDictBizRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		// 获取上下文
		TenantContext contextBean = this.getFirstContextBean();
		Tenant tenant = contextBean.getTenant();
		IDictBizService dictBizService = contextBean.getDictBizService();

		// 新建租户对应的默认业务字典
		LinkedList<DictBiz> dictBizs = new LinkedList<>();
		List<DictBiz> dictBizList = getDictBizs(dictBizService, tenant.getTenantId(), dictBizs);

		// 设置上下文
		contextBean.setDictBizList(dictBizList);

	}


	private List<DictBiz> getDictBizs(IDictBizService dictBizService, String tenantId, LinkedList<DictBiz> dictBizs) {
		List<DictBiz> dictBizList = dictBizService.list(Wrappers.<DictBiz>query().lambda().eq(DictBiz::getParentId, BladeConstant.TOP_PARENT_ID).eq(DictBiz::getTenantId, BladeConstant.ADMIN_TENANT_ID).eq(DictBiz::getIsDeleted, BladeConstant.DB_NOT_DELETED));
		dictBizList.forEach(dictBiz -> {
			Long oldParentId = dictBiz.getId();
			Long newParentId = IdWorker.getId();
			dictBiz.setId(newParentId);
			dictBiz.setTenantId(tenantId);
			dictBizs.add(dictBiz);
			recursionDictBiz(dictBizService, tenantId, oldParentId, newParentId, dictBizs);
		});
		return dictBizs;
	}

	private void recursionDictBiz(IDictBizService dictBizService, String tenantId, Long oldParentId, Long newParentId, LinkedList<DictBiz> dictBizs) {
		List<DictBiz> dictBizList = dictBizService.list(Wrappers.<DictBiz>query().lambda().eq(DictBiz::getParentId, oldParentId).eq(DictBiz::getIsDeleted, BladeConstant.DB_NOT_DELETED));
		dictBizList.forEach(dictBiz -> {
			Long oldSubParentId = dictBiz.getId();
			Long newSubParentId = IdWorker.getId();
			dictBiz.setId(newSubParentId);
			dictBiz.setTenantId(tenantId);
			dictBiz.setParentId(newParentId);
			dictBizs.add(dictBiz);
			recursionDictBiz(dictBizService, tenantId, oldSubParentId, newSubParentId, dictBizs);
		});
	}

}
