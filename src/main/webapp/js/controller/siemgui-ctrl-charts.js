angular.module('SiemGui')
	.controller("ChartsController", ['$scope', '$log', 'ChartsService', 'StompSubscriptionService', function($scope, $log, viewService, subService) {
		var stompChannels = [];
		var chartPlaceholder = {
			options: {},
			series: [],
			loading: true,
			title: {
				text: "Lade Daten..."
			},
			size: {
				height: 300
			}
		};

		// Message channel to receive the event list
		stompChannels.push({
			name:  '/user/queue/charts',
			callback: chartsCallback
		});

		subService.subscribeChannels('ChartsController', stompChannels);

		// Unsubscribe from STOMP message channels when this scope is destroyed
		$scope.$on('$destroy', function() {
			subService.unsubscribeChannels('ChartsController');
		});

		$scope.$parent.navpath = 'charts';
		$scope.charts = [];

		$scope.charts[0] = chartPlaceholder;
//		$scope.charts[1] = chartPlaceholder;
//		$scope.charts[2] = chartPlaceholder;

//		viewService.requestEventsTodayChart(0);
//		viewService.requestEventsTodayPerSensorChart(1);
		viewService.requestIncidentsByThreatLevelChart(0);


		/**
		 * Callback for the STOMP message containing the event list.
		 * Stores the message contents in the $scope.
		 *
		 * @param {Object} data STOMP message data
		 * @returns {undefined}
		 */
		function chartsCallback(data) {
			$log.debug('chartsCallback, data:');
			$log.debug(data);

			var fetchedChart = JSON.parse(data.body);

			$log.debug('chartsCallback, fetchedChart:');
			$log.debug(fetchedChart);

			var chart = {
				options: {
					chart: {
						type: fetchedChart.type
					},
					xAxis: fetchedChart.xAxis,
					yAxis: fetchedChart.yAxis,
					legend: {
						enabled: false
					}
				},
				series: fetchedChart.series,
				title: {
					text: fetchedChart.title
				},
				size: {
					height: 300
				}
			};

			$scope.charts[fetchedChart.chartIndex] = chart;
		}
	}]);