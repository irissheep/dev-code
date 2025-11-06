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
package org.springblade.modules.resource.builder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.AllArgsConstructor;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.enums.OssEnum;
import org.springblade.core.oss.enums.OssStatusEnum;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springblade.modules.resource.entity.Oss;
import org.springblade.modules.resource.rule.context.OssContext;
import org.springblade.modules.resource.service.IOssService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springblade.core.cache.constant.CacheConstant.RESOURCE_CACHE;

/**
 * Oss云存储统一构建类
 *
 * @author Chill
 */
@AllArgsConstructor
public class OssBuilder {

	public static final String OSS_CODE = "oss:code:";
	public static final String OSS_PARAM_KEY = "code";

	private final OssProperties ossProperties;
	private final IOssService ossService;
	private final FlowExecutor flowExecutor;

	/**
	 * OssTemplate配置缓存池
	 */
	private final Map<String, OssTemplate> templatePool = new ConcurrentHashMap<>();

	/**
	 * oss配置缓存池
	 */
	private final Map<String, Oss> ossPool = new ConcurrentHashMap<>();

	/**
	 * 获取template
	 *
	 * @return OssTemplate
	 */
	public OssTemplate template() {
		return template(StringPool.EMPTY);
	}

	/**
	 * 获取template
	 *
	 * @param code 资源编号
	 * @return OssTemplate
	 */
	public OssTemplate template(String code) {
		String tenantId = AuthUtil.getTenantId();
		Oss oss = getOss(tenantId, code);

		OssContext ossContext = new OssContext();
		ossContext.setOss(oss);
		ossContext.setOssProperties(ossProperties);
		ossContext.setOssPool(ossPool);
		ossContext.setTemplatePool(templatePool);

		LiteflowResponse resp = flowExecutor.execute2Resp("ossChain", tenantId, ossContext);
		if (resp.isSuccess()) {
			OssContext contextBean = resp.getFirstContextBean();
			return contextBean.getOssTemplate();
		} else {
			throw new ServiceException("未获取到对应的对象存储配置");
		}
	}

	/**
	 * 获取对象存储实体
	 *
	 * @param tenantId 租户ID
	 * @return Oss
	 */
	public Oss getOss(String tenantId, String code) {
		String key = tenantId;
		LambdaQueryWrapper<Oss> lqw = Wrappers.<Oss>query().lambda().eq(Oss::getTenantId, tenantId);
		// 获取传参的资源编号并查询，若有则返回，若没有则调启用的配置
		String ossCode = StringUtil.isBlank(code) ? WebUtil.getParameter(OSS_PARAM_KEY) : code;
		if (StringUtil.isNotBlank(ossCode)) {
			key = key.concat(StringPool.DASH).concat(ossCode);
			lqw.eq(Oss::getOssCode, ossCode);
		} else {
			lqw.eq(Oss::getStatus, OssStatusEnum.ENABLE.getNum());
		}
		Oss oss = CacheUtil.get(RESOURCE_CACHE, OSS_CODE, key, () -> {
			Oss o = ossService.getOne(lqw);
			// 若为空则调用默认配置
			if (o == null || o.getId() == null) {
				Oss defaultOss = new Oss();
				defaultOss.setId(0L);
				defaultOss.setCategory(OssEnum.of(ossProperties.getName()).getCategory());
				defaultOss.setEndpoint(ossProperties.getEndpoint());
				defaultOss.setTransformEndpoint(ossProperties.getTransformEndpoint());
				defaultOss.setBucketName(ossProperties.getBucketName());
				defaultOss.setAccessKey(ossProperties.getAccessKey());
				defaultOss.setSecretKey(ossProperties.getSecretKey());
				return defaultOss;
			} else {
				return o;
			}
		});
		if (oss == null || oss.getId() == null) {
			throw new ServiceException("未获取到对应的对象存储配置");
		} else {
			return oss;
		}
	}

}
