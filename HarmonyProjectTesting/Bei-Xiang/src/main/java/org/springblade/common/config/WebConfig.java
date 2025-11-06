package org.springblade.common.config;

import org.springblade.common.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Resource
	LoginInterceptor loginInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		/**
		 * 登录拦截器
		 * */
		registry.addInterceptor(loginInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/blade-system/user/login","/blade-system/user/register","/images/**","/iot_report").
			order(1);

	}
}
