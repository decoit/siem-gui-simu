angular.module('SiemGui')
	.controller('OverviewController', ['$scope', '$log', 'OverviewService', 'StompSubscriptionService', function($scope, $log, viewService, subService) {
		var stompChannels = [];

		$scope.info = {
			tickets: {
				ticketsNew: 'laden...',
				ticketsOpen: 'laden...',
				ticketsResolved: 'laden...',
				myTicketsNew: 'laden...',
				myTicketsOpen: 'laden...',
				myTicketsResolved: 'laden...'
			},
			incidents: {
				incidentsNew: 'laden...',
				incidentsInProgress: 'laden...',
				incidentsUnknown: 'laden...',
				incidentsLowRisk: 'laden...',
				incidentsMediumRisk: 'laden...',
				incidentsHighRisk: 'laden...'
			},
			events: {
				eventsToday: 'laden...',
				eventsYesterday: 'laden...',
				eventsThisWeek: 'laden...',
				eventsLastWeek: 'laden...'
			}
		};

		// Message channel to receive overview information summary
		stompChannels.push({
			name:  '/user/queue/overview/reqinfo/tickets',
			callback: overviewInfoTicketsCallback
		});
		stompChannels.push({
			name:  '/user/queue/overview/reqinfo/incidents',
			callback: overviewInfoIncidentsCallback
		});

		subService.subscribeChannels('OverviewController', stompChannels);

		// Unsubscribe from STOMP message channels when this scope is destroyed
		$scope.$on('$destroy', function() {
			subService.unsubscribeChannels('OverviewController');
		});

		$scope.$parent.navpath = 'overview';

		// TODO: Just for testing purposes
		$scope.$parent.dynamic = Math.round(Math.random() * 100);

		viewService.requestOverviewTicktsInfo();
		viewService.requestOverviewIncidentsInfo();


		/**
		 * This callback is called when ticket summary for the overview is received via
		 * the WebSocket.
		 *
		 * @param {Object} data Received STOMP message object
		 * @returns {undefined}
		 */
		function overviewInfoTicketsCallback(data) {
			$log.debug('overviewInfoTicketsCallback, data:');
			$log.debug(data);

			var ticketsSummary = JSON.parse(data.body);

			$log.debug('overviewInfoTicketsCallback, ticketsSummary:');
			$log.debug(ticketsSummary);

			$scope.info.tickets = ticketsSummary;
		}


		/**
		 * This callback is called when incident summary for the overview is received via
		 * the WebSocket.
		 *
		 * @param {Object} data Received STOMP message object
		 * @returns {undefined}
		 */
		function overviewInfoIncidentsCallback(data) {
			$log.debug('overviewInfoIncidentsCallback, data:');
			$log.debug(data);

			var incidentsSummary = JSON.parse(data.body);

			$log.debug('overviewInfoIncidentsCallback, incidentsSummary:');
			$log.debug(incidentsSummary);

			$scope.info.incidents = incidentsSummary;
		}
	}]);