angular.module('SiemGui')
	.factory('OverviewService', ['$log', 'WebSocketService', function($log, wsService) {
		// We return this object to anything injecting our service
		var Service = {};


		/**
		 * Public service function to send a STOMP message to request the overview tickets summary information.
		 *
		 * @returns {undefined}
		 */
		Service.requestOverviewTicktsInfo = function() {
			$log.debug('Sending message to request overview tickets summary information');

			wsService.send('/app/overview/reqinfo/tickets', {});
		};


		/**
		 * Public service function to send a STOMP message to request the overview incidents summary information.
		 *
		 * @returns {undefined}
		 */
		Service.requestOverviewIncidentsInfo = function() {
			$log.debug('Sending message to request overview incidents summary information');

			wsService.send('/app/overview/reqinfo/incidents', {});
		};


		return Service;
	}]);