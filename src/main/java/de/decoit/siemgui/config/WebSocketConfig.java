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

import de.decoit.siemgui.stomp.interceptors.ClientAliveInterceptor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;


/**
 * Configuration class to configure and setup the Spring WebSocket and Messaging modules.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Configuration
@EnableWebSocketMessageBroker
@ComponentScan(basePackages = {"de.decoit.siemgui.websocket"})
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	@Autowired
	private ApplicationContext ctx;


	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/queue/", "/topic/");
		config.setApplicationDestinationPrefixes("/app");
	}


	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
				.addEndpoint("/gui-stomp")
				.withSockJS().setClientLibraryUrl("https://cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js")
				.setInterceptors(new HttpSessionHandshakeInterceptor())
				.setHeartbeatTime(10000);
	}


	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		ClientAliveInterceptor cai = ctx.getBean(ClientAliveInterceptor.class);
		registration.setInterceptors(cai);

		ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
		exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		exec.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
		exec.setMaxPoolSize(Integer.MAX_VALUE);
		exec.setQueueCapacity(Integer.MAX_VALUE);
		exec.setKeepAliveSeconds(300);
		exec.setAllowCoreThreadTimeOut(true);
		registration.taskExecutor(exec);
	}


	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
		exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		exec.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
		exec.setMaxPoolSize(999);
		exec.setQueueCapacity(999);
		exec.setKeepAliveSeconds(300);
		exec.setAllowCoreThreadTimeOut(true);
		registration.taskExecutor(exec);
	}


	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		int fiveMinutesInMillis = 5 * 60 * 1000;

		registration.setSendTimeLimit(fiveMinutesInMillis);
	}


	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		long tenMinutesInMillis = 10 * 60 * 1000;

		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setAsyncSendTimeout(tenMinutesInMillis);
		container.setMaxSessionIdleTimeout(tenMinutesInMillis);

		return container;
	}
}
