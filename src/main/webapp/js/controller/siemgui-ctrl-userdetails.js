angular.module('SiemGui')
	.controller('UserDetailsController', ['$scope', '$rootScope', '$log', 'UserDetailsService', function($scope, $rootScope, $log, viewService) {
		viewService.promise.then(ownUserDetailsCallback);

		/**
		 * Callback to be bound to the push notification STOMP message channel. Will add a received
		 * notification to the notification list in the view. New notifications will be shown first.
		 *
		 * @returns {undefined}
		 */
		function ownUserDetailsCallback() {
			//$log.debug(response);
			$log.debug(viewService.activeUser);

			$scope.userdetails = viewService.activeUser;
			$rootScope.username = viewService.activeUser.username;
			$rootScope.userIsAdmin = (viewService.activeUser.roles.indexOf('ROLE_SIEM_ADMIN') !== -1);
		}
	}]);