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
import com.yomahub.liteflow.core.NodeSwitchComponent;
import org.springblade.modules.resource.rule.context.OssContext;

/**
 * Oss缓存判断
 *
 * @author Chill
 */
@LiteflowComponent(id = "ossCacheRule", name = "OSS缓存判断")
public class OssCacheRule extends NodeSwitchComponent {

	@Override
	public String processSwitch() {
		OssContext contextBean = this.getContextBean(OssContext.class);
		// 若判断配置已缓存则直接读取，否则进入下一步构建新数据
		if (contextBean.getIsCached()) {
			return "ossReadRule";
		} else {
			return "ossNewRule";
		}
	}

}
