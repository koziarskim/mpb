package com.noovitec.mpb.app;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Component
@Order(2)
class AuthenticationFilter extends UrlBasedCorsConfigurationSource implements Filter {

	@Override
	public void doFilter(ServletRequest servletRrequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRrequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		// Skip OPTIONS for CORS preflight handshake.
		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		} else {
			//Get cookie from HTTP request.
			//If not exist return 401
			//Get cookie from server storage (server session?).
			//If not exist return 401
			//Compare both cookies
			//IF not the same, return 401
//			response.sendError(HttpStatus.UNAUTHORIZED.value(), "Testing....");
			chain.doFilter(servletRrequest, servletResponse);
		}

	}

}