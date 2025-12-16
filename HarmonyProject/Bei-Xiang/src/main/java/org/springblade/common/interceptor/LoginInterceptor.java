package org.springblade.common.interceptor;

import org.springblade.core.log.exception.ServiceException;
import org.springblade.modules.system.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * 请求拦截器
 * @author MI
 * @date 2023/10/03
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

	/***
	 * 在请求处理之前进行调用(Controller方法调用之前)
	 @param request
	 @param response
	 @param handler
	 @return boolean
	 @throws Exception
	 */
	@Resource
	TokenUtil tokenUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		/**
		 * 从请求头中取出token，并判断其是否存在和合法
		 */
		String token = request.getHeader("token");
		if (token != null && tokenUtil.verifyToken(token)) {
			return true;
		}else {
			throw new ServiceException("请登录");
		}
	}

	/***
	 * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
	 @param request
	 @param response
	 @param handler
	 @param modelAndView
	 @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	/***
	 * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
	 @param request
	 @param response
	 @param handler
	 @param ex
	 @throws Exception
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}


