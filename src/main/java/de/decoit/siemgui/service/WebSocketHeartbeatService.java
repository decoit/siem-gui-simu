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
package de.decoit.siemgui.service;

import com.google.common.collect.EvictingQueue;
import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgHeartbeat;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


/**
 * Implementation of the HeartbeatService that uses a WebSocket to send its heartbeat messages.
 * Heartbeat messages are sent in an interval of 30 seconds using the @Scheduled annotation of Spring.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class WebSocketHeartbeatService implements HeartbeatService {
	private final Logger LOG = LoggerFactory.getLogger(WebSocketHeartbeatService.class.getName());
	private EvictingQueue<String> heartbeartsConsideredAlive;
	private String lastHeartbeat;

	@Autowired
	private SystemConfig sysConf;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;


	@Override
	public boolean validateClientAlive(String hbId) {
		if (hbId != null) {
			return heartbeartsConsideredAlive.contains(hbId);
		}
		else {
			return false;
		}
	}


	@Override
	public boolean validateHeartbeatId(String hbId) {
		if (hbId != null) {
			return lastHeartbeat.equals(hbId);
		}
		else {
			return false;
		}
	}


	@Override
	public String getCurrentHeartbeat() {
		return lastHeartbeat;
	}


	@Override
	@Scheduled(fixedRate = 7000)
	public void sendHeartbeat() {
		String hbId = generateHeartbeatId();

		MsgHeartbeat msg = new MsgHeartbeat();
		msg.setHeartbeatId(hbId);

		simpMessagingTemplate.convertAndSend("/topic/heartbeat", msg);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Sent heartbeat: " + hbId);
		}
	}


	/**
	 * This method is called after construction of the object.
	 * It initializes the EvictingQueue and generated an initial heartbeat ID.
	 */
	@PostConstruct
	private void init() {
		heartbeartsConsideredAlive = EvictingQueue.create(sysConf.getHeartbeatThreshold());
		generateHeartbeatId();
	}


	/**
	 * Generate a new heartbeat ID.
	 * This is done by using the UUID class and converting the UUID to a string.
	 *
	 * @return Generated heartbeat ID
	 */
	private String generateHeartbeatId() {
		UUID newUuid = UUID.randomUUID();
		lastHeartbeat = newUuid.toString();
		heartbeartsConsideredAlive.add(lastHeartbeat);

		return lastHeartbeat;
	}
}
