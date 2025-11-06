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
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.tool.utils.Func;
import org.springblade.modules.resource.rule.context.OssContext;
import org.springblade.modules.resource.entity.Oss;

import java.util.Map;

/**
 * OSS缓存读取校验
 *
 * @author Chill
 */
@LiteflowComponent(id = "ossReadRule", name = "OSS缓存读取校验")
public class OssReadRule extends NodeComponent {
	@Override
	public void process() throws Exception {
		String tenantId = this.getRequestData();
		OssContext contextBean = this.getContextBean(OssContext.class);
		Map<String, Oss> ossPool = contextBean.getOssPool();
		Map<String, OssTemplate> templatePool = contextBean.getTemplatePool();
		Oss ossCached = ossPool.get(tenantId);
		OssTemplate template = templatePool.get(tenantId);
		if (Func.hasEmpty(template, ossCached)) {
			throw new ServiceException("OSS缓存读取失败!");
		}
	}
}
