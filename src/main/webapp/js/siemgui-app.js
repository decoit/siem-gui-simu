/*
 * Main AngularJS module for the SIEM GUI system. This module is loaded after successful authentication.
 */
var app = angular.module('SiemGui', ['ngSanitize', 'ui.bootstrap', 'ui.router', 'highcharts-ng']);

app.config(['$stateProvider', '$urlRouterProvider', '$logProvider', function($stateProvider, $urlRouterProvider, $logProvider) {
	// Enable/disable debug logging
	$logProvider.debugEnabled(true);

	// Setup routing
	$urlRouterProvider.otherwise('/overview');

	$stateProvider
		.state('overview', {
			url: '/overview',
			templateUrl: 'templates/overview.html',
			controller: 'OverviewController',
			resolve: {
				userdetails: ['UserDetailsService', function(UserDetailsService) {
					return UserDetailsService.promise;
				}]
			}
		})
		.state('charts', {
			url: '/charts',
			templateUrl: 'templates/charts.html',
			controller: 'ChartsController',
			resolve: {
				userdetails: ['UserDetailsService', function(UserDetailsService) {
					return UserDetailsService.promise;
				}]
			}
		})
		.state('incidents', {
			url: '/incidents',
			templateUrl: 'templates/incidents.html',
			controller: 'IncidentsController',
			resolve: {
				userdetails: ['UserDetailsService', function(UserDetailsService) {
					return UserDetailsService.promise;
				}]
			}
		})
		.state('admin', {
			url: '/admin',
			templateUrl: 'templates/admin.html',
			controller: 'AdminController',
			resolve: {
				userdetails: ['UserDetailsService', function(UserDetailsService) {
					return UserDetailsService.promise;
				}]
			}
		})
		.state('admin.users', {
			url: '/users/{userId}',
			templateUrl: 'templates/admin.users.html',
			controller: 'AdminUsersController'
		})
		.state('visitmeta', {
			url: '/visitmeta',
			templateUrl: 'templates/visitmeta.html',
			controller: 'VisITMetaController',
			params: {
				graph: {
					value: null,
					squash: true
				}
			},
			resolve: {
				userdetails: ['UserDetailsService', function(UserDetailsService) {
					return UserDetailsService.promise;
				}]
			}
		});
}]);

app.run(['$rootScope', '$log', '$location', 'WebSocketService', function($rootScope, $log, $location, wsService) {
	// Define global functions

	/**
	 * Take the JSON representation of a Java LocalDateTime object and format it into a date string.
	 *
	 * @param {Object} dateObj JSON object of a Java LocalDateTime
	 * @returns {String} Formatted date string
	 */
	$rootScope.formatLocalDateTime = function(dateObj) {
		if(dateObj) {
			var date = dateObj.year + "-";

			if(dateObj.monthValue < 10) {
				date += "0";
			}
			date += dateObj.monthValue + "-";

			if(dateObj.dayOfMonth < 10) {
				date += "0";
			}
			date += dateObj.dayOfMonth + " ";

			if(dateObj.hour < 10) {
				date += "0";
			}
			date += dateObj.hour + ":";

			if(dateObj.minute < 10) {
				date += "0";
			}
			date += dateObj.minute + ":";

			if(dateObj.second < 10) {
				date += "0";
			}
			date += dateObj.second;

			return date;
		}
		else {
			return "Invalid date Object";
		}
	};


	// Disconnect the WebSocket if $rootScope is destroyed
	$rootScope.$on('$destroy', function() {
		wsService.disconnect();
	});

	$log.debug('Host: ' + $location.host());
	$log.debug('Port: ' + $location.port());

	$log.debug('Starting WebSocket connection attempt');

	// Start connection attempt to the WebSocket
	wsService.connect('/siem-gui/gui-stomp', function(){});
}]);