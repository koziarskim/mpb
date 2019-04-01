package com.noovitec.mpb.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan({ "com.noovitec.mpb.rest", "com.noovitec.mpb.service", "com.noovitec.mpb.app" })
@EntityScan("com.noovitec.mpb.entity")
@EnableJpaRepositories("com.noovitec.mpb.repo")
@Configuration
public class MpbConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		ApiInterceptor apiInterceptor = new ApiInterceptor();
		registry.addInterceptor(apiInterceptor);
		WebMvcConfigurer.super.addInterceptors(registry);
	}

//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**").allowedOrigins("*").allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH").allowCredentials(true)
//		.allowedHeaders(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
//	}
}