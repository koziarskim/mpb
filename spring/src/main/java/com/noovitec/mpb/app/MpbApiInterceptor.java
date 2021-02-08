package com.noovitec.mpb.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.noovitec.mpb.jms.message.JmsMessage;

@Component
public class MpbApiInterceptor implements HandlerInterceptor {

	private final Logger log = LoggerFactory.getLogger(MpbApiInterceptor.class);
	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		String yearContext = request.getParameter("yearContext");
		if(yearContext == null) {
			yearContext = "y2020";
//			throw new Exception("Tenant schema not specified!");
		}
		MpbTenantContext.setCurrentTenant(yearContext);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model) throws Exception {
		for(JmsMessage message: MpbTenantContext.getMessages()) {
			jmsTemplate.convertAndSend(message.getType(), message);
		}
		MpbTenantContext.clear();
		MpbTenantContext.clearMessages();
	}

}
