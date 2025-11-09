package com.backdevfc.discoveryserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrfConfig -> csrfConfig.ignoringRequestMatchers("/eureka/**"))
				.authorizeHttpRequests(auth -> {
					auth.requestMatchers("/login/**").anonymous();
					auth.anyRequest().authenticated();
				})
				.formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
				.httpBasic(Customizer.withDefaults())
				.build();
	}
}