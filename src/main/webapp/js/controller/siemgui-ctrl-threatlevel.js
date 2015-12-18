/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.controller('ThreatLevelController', ['$scope', '$log', 'StompSubscriptionService', 'ThreatLevelService', function($scope, $log, subService, tlService) {
		var stompChannels = [];

		$scope.threatlevel = [];

		stompChannels.push({
			name:'/topic/push',
			callback:pushNotificationCallback
		});
		stompChannels.push({
			name:'/topic/threatlevel',
			callback: threatLevelCallback
		});

		subService.subscribeChannels('ThreatLevelController', stompChannels);

		tlService.requestThreatLevel();


		/**
		 * Callback to be bound to the push notification STOMP message channel.
		 *
		 * @returns {undefined}
		 */
		function pushNotificationCallback() {
			$log.debug('Push notification received, updating threat level');

			tlService.requestThreatLevel();
		}


		/**
		 * Callback to be bound to the threat level STOMP message channel.
		 * Will parse the threat level data and update the view accordingly.
		 *
		 * @param {Object} data Received STOMP message
		 * @returns {undefined}
		 */
		function threatLevelCallback(data) {
			$log.debug('threatLevelCallback, data');
			$log.debug(data);

			var content = JSON.parse(data.body);

			$log.debug('threatLevelCallback, content');
			$log.debug(content);

			$scope.threatlevel = [];

			if(content.lowRiskDisplay > 0) {
				$scope.threatlevel.push({
					value: content.lowRiskDisplay,
					type: 'success'
				});
			}

			if(content.mediumRiskDisplay > 0) {
				$scope.threatlevel.push({
					value: content.mediumRiskDisplay,
					type: 'warning'
				});
			}

			if(content.highRiskDisplay > 0) {
				$scope.threatlevel.push({
					value: content.highRiskDisplay,
					type: 'danger'
				});
			}
		}
	}]);