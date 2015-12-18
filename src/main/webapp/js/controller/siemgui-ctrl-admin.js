/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.controller("AdminController", ['$scope', '$log', function($scope, $log) {
		var stompChannels = [];

		$scope.$parent.navpath = 'admin';
		$scope.subnavpath = null;


		$scope.setSubnavpathUsers = function() {
			$scope.subnavpath = 'users';
			$log.debug('Subnavpath: ' + $scope.subnavpath);
		};


		$scope.setSubnavpathRecommendations = function() {
			$scope.subnavpath = 'recommendations';
			$log.debug('Subnavpath: ' + $scope.subnavpath);
		};


		$scope.setSubnavpathRules = function() {
			$scope.subnavpath = 'rules';
			$log.debug('Subnavpath: ' + $scope.subnavpath);
		};
	}]);