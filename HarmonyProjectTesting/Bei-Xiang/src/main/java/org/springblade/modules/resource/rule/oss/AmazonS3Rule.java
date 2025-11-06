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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springblade.core.oss.OssTemplate;
import org.springblade.core.oss.S3Template;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.resource.entity.Oss;
import org.springblade.modules.resource.rule.context.OssContext;

/**
 * Amazon S3 云存储构建类
 *
 * @author Chill
 */
@LiteflowComponent(id = "amazonS3Rule", name = "amazonS3OSS构建")
public class AmazonS3Rule extends NodeComponent {

	@Override
	public void process() throws Exception {
		// 获取上下文
		OssContext contextBean = this.getContextBean(OssContext.class);
		Oss oss = contextBean.getOss();
		OssRule ossRule = contextBean.getOssRule();

		// 创建配置类
		OssProperties ossProperties = new OssProperties();
		ossProperties.setEndpoint(oss.getEndpoint());
		ossProperties.setTransformEndpoint(oss.getTransformEndpoint());
		ossProperties.setAccessKey(oss.getAccessKey());
		ossProperties.setSecretKey(oss.getSecretKey());
		ossProperties.setBucketName(oss.getBucketName());
		ossProperties.setRegion(oss.getRegion());
		// 创建客户端
		AWSCredentials credentials = new BasicAWSCredentials(ossProperties.getAccessKey(), ossProperties.getSecretKey());
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSignerOverride("AWSS3V4SignerType");
		AmazonS3 amazonS3 = AmazonS3ClientBuilder
			.standard()
			.withEndpointConfiguration(new AwsClientBuilder.
				EndpointConfiguration(ossProperties.getEndpoint(),
				StringUtil.isBlank(ossProperties.getRegion()) ? Regions.DEFAULT_REGION.name() : Regions.fromName(ossProperties.getRegion()).getName()))
			.withPathStyleAccessEnabled(true)
			.withClientConfiguration(clientConfiguration)
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.build();
		OssTemplate ossTemplate = new S3Template(amazonS3, ossRule, ossProperties);

		// 设置上下文
		contextBean.setOssTemplate(ossTemplate);

	}
}
