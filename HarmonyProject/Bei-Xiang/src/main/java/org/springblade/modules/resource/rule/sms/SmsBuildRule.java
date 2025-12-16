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
import com.yomahub.liteflow.core.NodeSwitchComponent;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.sms.enums.SmsEnum;
import org.springblade.modules.resource.entity.Sms;
import org.springblade.modules.resource.rule.context.SmsContext;

/**
 * Sms构建判断
 *
 * @author Chill
 */
@LiteflowComponent(id = "smsBuildRule", name = "SMS构建条件判断")
public class SmsBuildRule extends NodeSwitchComponent {

	@Override
	public String processSwitch() {
		SmsContext contextBean = this.getContextBean(SmsContext.class);
		Sms sms = contextBean.getSms();

		if (contextBean.getIsCached()) {
			return "cacheSmsRule";
		} else if (sms.getCategory() == SmsEnum.YUNPIAN.getCategory()) {
			return "yunpianSmsRule";
		} else if (sms.getCategory() == SmsEnum.QINIU.getCategory()) {
			return "qiniuSmsRule";
		} else if (sms.getCategory() == SmsEnum.ALI.getCategory()) {
			return "aliSmsRule";
		} else if (sms.getCategory() == SmsEnum.TENCENT.getCategory()) {
			return "tencentSmsRule";
		}

		throw new ServiceException("未找到SMS配置");
	}
}
