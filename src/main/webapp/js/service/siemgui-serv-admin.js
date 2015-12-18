/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.factory('AdminService', ['$log', 'WebSocketService', function($log, wsService) {
		// We return this object to anything injecting our service
		var Service = {};


		Service.requestUserList = function() {
			$log.debug('Sending message to request user list');

			wsService.send('/app/admin/users/list', {});
		};


		Service.storeUserDetails = function(userId, siemAuthorized, siemAdminAuthorized) {
			$log.debug('Sending message to request user list');

			var payload = {
				userId: userId,
				siemAuthorized: siemAuthorized,
				siemAdminAuthorized: siemAdminAuthorized
			};

			wsService.send('/app/admin/users/store', payload);
		};


		return Service;
	}]);