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
package org.springblade.modules.resource.rule.oss;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.BladeOssRule;
import org.springblade.modules.resource.rule.context.OssContext;
import org.springblade.modules.resource.entity.Oss;

/**
 * OSS数据创建
 *
 * @author Chill
 */
@LiteflowComponent(id = "ossDataRule", name = "OSS数据创建")
public class OssDataRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		OssContext contextBean = this.getContextBean(OssContext.class);
		Oss oss = contextBean.getOss();
		OssProperties ossProperties = contextBean.getOssProperties();
		// 若采用默认设置则开启多租户模式, 若是用户自定义oss则不开启
		if (oss.getEndpoint().equals(ossProperties.getEndpoint()) && oss.getAccessKey().equals(ossProperties.getAccessKey()) && ossProperties.getTenantMode()) {
			contextBean.setOssRule(new BladeOssRule(Boolean.TRUE));
		} else {
			contextBean.setOssRule(new BladeOssRule(Boolean.FALSE));
		}
	}
}
