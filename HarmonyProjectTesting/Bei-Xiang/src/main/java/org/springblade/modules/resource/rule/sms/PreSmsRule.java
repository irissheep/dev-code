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
package org.springblade.modules.resource.rule.sms;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springblade.core.sms.SmsTemplate;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.resource.entity.Sms;
import org.springblade.modules.resource.rule.context.SmsContext;

import java.util.Map;

/**
 * Sms前置处理
 *
 * @author Chill
 */
@LiteflowComponent(id = "preSmsRule", name = "SMS构建前置处理")
public class PreSmsRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		String tenantId = this.getRequestData();
		SmsContext contextBean = this.getContextBean(SmsContext.class);
		Map<String, Sms> smsPool = contextBean.getSmsPool();
		Map<String, SmsTemplate> templatePool = contextBean.getTemplatePool();
		Sms sms = contextBean.getSms();
		Sms smsCached = smsPool.get(tenantId);
		SmsTemplate template = templatePool.get(tenantId);
		// 若为空或者不一致，则重新加载
		if (Func.hasEmpty(template, smsCached) || !sms.getTemplateId().equals(smsCached.getTemplateId()) || !sms.getAccessKey().equals(smsCached.getAccessKey())) {
			contextBean.setIsCached(Boolean.FALSE);
		} else {
			contextBean.setIsCached(Boolean.TRUE);
		}
	}

}
