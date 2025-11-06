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
package org.springblade.modules.resource.rule.context;

import lombok.Data;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.modules.resource.entity.Oss;

import java.util.Map;

/**
 * Oss上下文
 *
 * @author Chill
 */
@Data
public class OssContext {
	/**
	 * 是否有缓存
	 */
	private Boolean isCached;
	/**
	 * oss数据
	 */
	private Oss oss;
	/**
	 * oss规则
	 */
	private OssRule ossRule;
	/**
	 * oss接口
	 */
	private OssTemplate ossTemplate;
	/**
	 * oss配置
	 */
	private OssProperties ossProperties;
	/**
	 * OssTemplate配置缓存池
	 */
	private Map<String, OssTemplate> templatePool;
	/**
	 * oss配置缓存池
	 */
	private Map<String, Oss> ossPool;


}
