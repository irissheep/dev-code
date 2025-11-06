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
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.sms.SmsTemplate;
import org.springblade.core.sms.props.SmsProperties;
import org.springblade.modules.resource.entity.Sms;

import java.util.Map;

/**
 * Sms上下文
 *
 * @author Chill
 */
@Data
public class SmsContext {
	/**
	 * 是否有缓存
	 */
	private Boolean isCached;
	/**
	 * sms数据
	 */
	private Sms sms;
	/**
	 * sms接口
	 */
	private SmsTemplate smsTemplate;
	/**
	 * sms配置
	 */
	private SmsProperties smsProperties;
	/**
	 * redis工具
	 */
	private BladeRedis bladeRedis;
	/**
	 * SmsTemplate配置缓存池
	 */
	private Map<String, SmsTemplate> templatePool;
	/**
	 * sms配置缓存池
	 */
	private Map<String, Sms> smsPool;


}
