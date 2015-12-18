angular.module('SiemGui')
	.factory('ChartsService', ['$log', 'WebSocketService', function($log, wsService) {
		// We return this object to anything injecting our service
		var Service = {};


		/**
		 * Public service function the request data for a chart.
		 * This function requests the chart of active incidents per threat level.
		 *
		 * @param {Integer} chartIndex Index of the chart element in the view
		 * @returns {undefined}
		 */
		Service.requestIncidentsByThreatLevelChart = function(chartIndex) {
			$log.debug('Sending message to request chart information about active incidents by threat level');

			var payload = {
				chartIndex: chartIndex
			};

			wsService.send('/app/charts/incidentsbythreatlevel', payload);
		};


		return Service;
	}]);