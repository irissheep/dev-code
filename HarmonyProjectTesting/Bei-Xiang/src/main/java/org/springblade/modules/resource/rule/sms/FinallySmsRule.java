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
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.sms.SmsTemplate;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.resource.entity.Sms;
import org.springblade.modules.resource.rule.context.SmsContext;

import java.util.Map;

/**
 * Sms后置处理
 *
 * @author Chill
 */
@LiteflowComponent(id = "finallySmsRule", name = "SMS构建后置处理")
public class FinallySmsRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		String tenantId = this.getRequestData();
		SmsContext contextBean = this.getContextBean(SmsContext.class);
		Map<String, Sms> smsPool = contextBean.getSmsPool();
		Map<String, SmsTemplate> templatePool = contextBean.getTemplatePool();

		if (contextBean.getIsCached()) {
			SmsTemplate template = templatePool.get(tenantId);
			contextBean.setSmsTemplate(template);
		} else {
			Sms sms = contextBean.getSms();
			SmsTemplate template = contextBean.getSmsTemplate();
			if (Func.hasEmpty(template, sms)) {
				throw new ServiceException("SMS接口读取失败!");
			} else {
				templatePool.put(tenantId, template);
				smsPool.put(tenantId, sms);
			}
		}

	}
}
