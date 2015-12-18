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


/**
 * An interface to define a service that sends heartbeat messages to the client and
 * can validate the response.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface HeartbeatService {
	/**
	 * Validate if the provided heartbeat ID is the currently active heartbeat ID.
	 * This method should return true if and only if hbId.equals(currentHbId) == true.
	 *
	 * @param hbId Heartbeat ID to check for validity
	 * @return true if the heartbeat ID is valid, false otherwise
	 */
	public boolean validateHeartbeatId(String hbId);


	/**
	 * Validate if a client is alive.
	 * This method should be provided with the last heartbeat ID the client has answered.
	 * The ID is then checked if it is one of the X last heartbeat IDs. If not, the client is to be
	 * considered dead and its session should be invalidated. The number of valid heartbeats (X) should be configurable.
	 *
	 * @param hbId Heartbeat ID to check for validity
	 * @return true if the client is considered alive, false otherwise
	 */
	public boolean validateClientAlive(String hbId);


	/**
	 * Get the currently active heartbeat ID.
	 *
	 * @return The current heartbeat ID
	 */
	public String getCurrentHeartbeat();


	/**
	 * Send a heartbeat message to all clients.
	 * This method should be executed in a fixed interval.
	 */
	public void sendHeartbeat();
}
