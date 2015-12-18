/* 
 * Copyright (C) 2015 DECOIT GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.decoit.siemgui.config;

import de.decoit.siemgui.security.RtAuthenticationProvider;
import de.decoit.siemgui.security.RtSessionLogoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;


/**
 * Configuration class to configure Spring Security.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf()
				.disable() // Automatic CSRF protection is not available with custom login form
				.headers()
				.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
				.and()
				.formLogin()
				.defaultSuccessUrl("/index.html", true)
				.loginPage("/login.html")
				.failureUrl("/login.html?error=true")
				.permitAll()
				.and()
				.logout()
				.addLogoutHandler(getRtSessionLogoutHandler())
				.logoutSuccessUrl("/login.html?logout=true")
				.logoutUrl("/logout.html")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
				.permitAll()
				.and()
				.authorizeRequests()
				.antMatchers("/css/**",
						"/fonts/**",
						"/img/simu.svg",
						"/js/lib/angular.min.js",
						"/js/lib/angular.min.js.map",
						"/js/siemgui-login-app.js",
						"/js/lib/ui-bootstrap-tpls-0.11.0.min.js").permitAll()
				.anyRequest().authenticated();
	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.authenticationProvider(getRtAuthenticationProvider());
	}


	/**
	 * Bean factory method for RtAuthenticationProvider
	 *
	 * @return RtAuthenticationProvider bean
	 */
	@Bean
	protected AuthenticationProvider getRtAuthenticationProvider() {
		return new RtAuthenticationProvider();
	}


	/**
	 * Bean factory method for RtSessionLogoutHandler
	 *
	 * @return RtSessionLogoutHandler bean
	 */
	@Bean
	protected LogoutHandler getRtSessionLogoutHandler() {
		return new RtSessionLogoutHandler();
	}
}
