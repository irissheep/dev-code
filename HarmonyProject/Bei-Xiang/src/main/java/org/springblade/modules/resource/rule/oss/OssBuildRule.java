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
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.oss.enums.OssEnum;
import org.springblade.modules.resource.rule.context.OssContext;
import org.springblade.modules.resource.entity.Oss;

/**
 * Oss构建判断
 *
 * @author Chill
 */
@LiteflowComponent(id = "ossBuildRule", name = "OSS构建条件判断")
public class OssBuildRule extends NodeSwitchComponent {

	@Override
	public String processSwitch() {
		OssContext contextBean = this.getContextBean(OssContext.class);
		Oss oss = contextBean.getOss();
		if (oss.getCategory() == OssEnum.MINIO.getCategory()) {
			return "minioRule";
		} else if (oss.getCategory() == OssEnum.QINIU.getCategory()) {
			return "qiniuOssRule";
		} else if (oss.getCategory() == OssEnum.ALI.getCategory()) {
			return "aliOssRule";
		} else if (oss.getCategory() == OssEnum.TENCENT.getCategory()) {
			return "tencentCosRule";
		} else if (oss.getCategory() == OssEnum.HUAWEI.getCategory()) {
			return "huaweiObsRule";
		} else if (oss.getCategory() == OssEnum.AMAZONS3.getCategory()) {
			return "amazonS3Rule";
		}
		throw new ServiceException("未找到OSS配置");
	}
}
