/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.controller("ForceLogoutController", ['$scope', '$interval', '$window', '$log', 'msg', function($scope, $interval, $window, $log, msg) {
		var promise;

		$scope.$on('$destroy', function() {
			if(angular.isDefined(promise)) {
				$interval.cancel(promise);
			}
		});

		$scope.time = msg.time;
		$scope.text = msg.text;
		$scope.secondsToClose = 10;

		$log.debug($scope.time + " " + $scope.text);

		promise = $interval(logoutCountdown, 1000);


		/**
		 * Function to be called by an interval to implement the logout countdown.
		 *
		 * @returns {undefined}
		 */
		function logoutCountdown() {
			if($scope.secondsToClose === 0) {
				$interval.cancel(promise);

				$window.location.href = 'logout.html';
			}
			else {
				$scope.secondsToClose -= 1;
			}
		}
	}]);