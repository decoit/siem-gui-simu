/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.controller('ErrorMessagesController', ['$scope', '$modal', '$log', 'StompSubscriptionService', function($scope, $modal, $log, subService) {
		var stompChannels = [];
		var logoutForced = false;

		$scope.alerts = [];

		stompChannels.push({
			name:  '/user/queue/errors',
			callback: errorNotificationCallback
		});

		subService.subscribeChannels('ErrorMessagesController', stompChannels);

		// Unsubscribe from STOMP message channels when this scope is destroyed
		$scope.$on('$destroy', function() {
			subService.unsubscribeChannels('ErrorMessagesController');
		});


		/**
		 * Close an error message.
		 *
		 * @param {Integer} index Index of the notification
		 * @returns {undefined}
		 */
		$scope.closeAlert = function(index) {
			$scope.alerts.splice(index, 1);
		};


		/**
		 * Callback function to be registered to incoming error notifications.
		 * It processes the data of the notification and displays an error message. If
		 * a forced logout is required it will open a modal dialog preventing further
		 * interaction with the user interface.
		 *
		 * @param {Object} data Message data
		 * @returns {undefined}
		 */
		function errorNotificationCallback(data) {
			$log.debug('errorNotificationCallback, data:');
			$log.debug(data);

			var errorMsg = JSON.parse(data.body);

			$log.debug('errorNotificationCallback, errorMsg:');
			$log.debug(errorMsg);

			if(!logoutForced) {
				if(errorMsg.forceLogout) {
					logoutForced = true;

					$modal.open({
						templateUrl: 'templates/forcelogout.html',
						controller: 'ForceLogoutController',
						scope: $scope,
						backdrop: 'static',
						resolve: {
							msg: function() {
								return errorMsg;
							}
						}
					});
				}
				else {
					$scope.alerts.push(errorMsg);
				}
			}
			else {
				$log.debug('Error forcing logout is being processed, discarding error message');
			}
		};
	}]);