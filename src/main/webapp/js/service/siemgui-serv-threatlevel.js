/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.factory('ThreatLevelService', ['$log', 'WebSocketService', function($log, wsService) {
		// We return this object to anything injecting our service
		var Service = {};


		/**
		 * Public service function to send a STOMP message to request the current threat level.
		 *
		 * @returns {undefined}
		 */
		Service.requestThreatLevel = function() {
			$log.debug('Sending message to request the current threat level');

			wsService.send('/app/threatlevel', {});
		};


		return Service;
	}]);