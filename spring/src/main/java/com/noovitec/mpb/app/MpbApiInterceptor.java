package com.noovitec.mpb.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.noovitec.mpb.entity.User;
import com.noovitec.mpb.repo.UserRepo;

@Component
public class MpbApiInterceptor implements HandlerInterceptor {

	private final Logger log = LoggerFactory.getLogger(MpbApiInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {
//		log.info("afterCompletion...");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
//		log.info("preHandle...");
		String year = request.getParameter("yearContext");
		MpbTenantContext.setCurrentTenant("y"+year);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model) throws Exception {
//		log.info("postHandle...");
		MpbTenantContext.clear();
	}

}
