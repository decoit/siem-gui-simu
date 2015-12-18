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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


/**
 * Configuration class to configure the Spring Web MVC module and define beans required by it.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"de.decoit.siemgui.web"})
public class WebConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("/css/**").setCachePeriod(31556926);
		registry.addResourceHandler("/img/**").addResourceLocations("/img/**").setCachePeriod(31556926);
		registry.addResourceHandler("/js/lib/**").addResourceLocations("/js/lib/**").setCachePeriod(31556926);
		registry.addResourceHandler("/js/controller/**").addResourceLocations("/js/controller/**").setCachePeriod(0);
		registry.addResourceHandler("/js/directive/**").addResourceLocations("/js/directive/**").setCachePeriod(0);
		registry.addResourceHandler("/js/filter/**").addResourceLocations("/js/filter/**").setCachePeriod(0);
		registry.addResourceHandler("/js/service/**").addResourceLocations("/js/service/**").setCachePeriod(0);
		registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/**").setCachePeriod(31556926);
		registry.addResourceHandler("/templates/**").addResourceLocations("/templates/**").setCachePeriod(0);	// Disable caching of templates
	}


	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}


	/**
	 * Bean factory method for InternalResourceViewResolver
	 *
	 * @return InternalResourceViewResolver bean
	 */
	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/");
		resolver.setSuffix(".html");

		return resolver;
	}


	/**
	 * Bean factory method for RequestContextListener
	 *
	 * @return RequestContextListener bean
	 */
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}
}
