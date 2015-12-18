angular.module('SiemGui')
	.controller('PushNotificationController', ['$scope', '$log', '$http', 'StompSubscriptionService', function($scope, $log, $http, subService) {
		var stompChannels = [];

		stompChannels.push({
			name:'/topic/push',
			callback:pushNotificationCallback
		});
		stompChannels.push({
			name:'/topic/heartbeat',
			callback:heartbeatCallback
		});

		/**
		 * Remove all notifications from the list in the view
		 * @returns {undefined}
		 */
		$scope.clearNotifications = function() {
			$log.debug('Push notifications cleared');

			$scope.notifications = [];
		};

		$scope.notifications = [];

		subService.subscribeChannels('PushNotificationController', stompChannels);


		/**
		 * Callback to be bound to the push notification STOMP message channel. Will add a received
		 * notification to the notification list in the view. New notifications will be shown first.
		 *
		 * @param {Object} data Received STOMP message
		 * @returns {undefined}
		 */
		function pushNotificationCallback(data) {
			$log.debug('Push notification received:');
			$log.debug(data);

			var content = JSON.parse(data.body);

			$log.debug('pushNotificationCallback, content:')
			$log.debug(content);

			$scope.notifications.unshift(content);
		}


		/**
		 * Callback to be registered to incoming heartbeat messages.
		 * It will process the incoming data and send an alive notification to the backend.
		 *
		 * @param {Object} data Received STOMP message
		 * @returns {undefined}
		 */
		function heartbeatCallback(data) {
			$log.debug('Heartbeat received:');
			$log.debug(data);

			var heartbeat = JSON.parse(data.body);
			var payload = {
				heartbeatId: heartbeat.heartbeatId
			};


			$http.post('heartbeat/alive', payload);
			$log.debug('Alive notification sent');
		}
	}]);