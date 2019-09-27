package com.noovitec.mpb.app;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Component
@Order(2)
class AuthenticationFilter extends UrlBasedCorsConfigurationSource implements Filter {

	private final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	@Autowired
	private MpbAuthenticationContext mpbAuthenticationContext;

	@Override
	public void doFilter(ServletRequest servletRrequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRrequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		// Skip OPTIONS for CORS preflight handshake.
		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		} else {
			if (!request.getRequestURI().contains("api/user/login")) {
				if (request.getCookies() == null) {
					log.info("Cookie not found");
					response.sendError(HttpStatus.UNAUTHORIZED.value(), "Cookie not found");
					return;
				}
				String sid = null;
				for (Cookie cookie : request.getCookies()) {
					if (cookie.getName().equals("SID")) {
						sid = cookie.getValue();
					}
				}
				if (sid == null) {
					log.info("SID cookie not found");
					response.sendError(HttpStatus.UNAUTHORIZED.value(), "SID cookie not found");
					return;
				}
				if (!mpbAuthenticationContext.hasSid(sid)) {
					log.info("SID not matched");
					response.sendError(HttpStatus.UNAUTHORIZED.value(), "SID not matched");
					return;
				}
			}
			chain.doFilter(servletRrequest, servletResponse);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}