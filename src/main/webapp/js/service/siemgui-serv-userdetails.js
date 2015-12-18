angular.module('SiemGui')
	.factory('UserDetailsService', ['$http', '$log', function($http, $log) {
		// We return this object to anything injecting our service
		var Service = {};
		
		Service.activeUser = {};

		Service.promise = $http.get('get-active-user', {responseType:'json'});
		Service.promise.then(httpCallback);


		/**
		 * Private service function used as callback on the HTTP request.
		 * It stores the received data into the public member variable activeUser.
		 *
		 * @param {Object} response HTTP response
		 * @returns {undefined}
		 */
		function httpCallback(response) {
			$log.debug(response);

			Service.activeUser = response.data;
		}


		return Service;
	}]);