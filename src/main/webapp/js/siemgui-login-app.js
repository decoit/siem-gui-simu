/*
 * AngularJS module for the login page of the SIEM GUI system.
 */
var app = angular.module('SiemGuiLogin', ['ui.bootstrap']);

app.controller('LoginController', ['$scope', '$location', function($scope, $location) {
	// Determine if the login page was called due to login error or logout
	var logout = ($location.absUrl().indexOf('logout=true') > -1);
	var error = ($location.absUrl().indexOf('error=true') > -1);

	$scope.alert = {};

	// Only show alert if page was called by login error or logout
	$scope.alert.show = (logout || error);

	if(logout) {
		$scope.alert.type = 'success';
		$scope.alert.text = 'Sie wurden erfolgreich abgemeldet.';
	}
	else if(error) {
		$scope.alert.type = 'danger';
		$scope.alert.text = 'Benutzername oder Passwort falsch! Bitte korrigieren Sie Ihre Eingaben und versuchen Sie es erneut!';
	}
	else {
		$scope.alert.type = '';
		$scope.alert.text = '';
	}

	/**
	 * Hide the alert. This function has no use if no alert is shown.
	 *
	 * @returns {undefined}
	 */
	$scope.hideAlerts = function() {
		$scope.alert.show = false;
	};
}]);