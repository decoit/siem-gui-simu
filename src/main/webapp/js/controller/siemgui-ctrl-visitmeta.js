angular.module('SiemGui')
	.controller('VisITMetaController', ['$scope', '$q', '$log', '$stateParams', function($scope, $q, $log, $stateParams) {
		// TODO: To be filled with logic, load VisITMeta JS App here
		var iFrameContentWindow;

		$scope.$parent.navpath = 'visitmeta';

		console.log('###################################');
		console.log($stateParams);

		if($stateParams.graph !== null && $stateParams.graph.json !== null) {
			iFrameContentWindow = document.getElementById('visitmeta-frame').contentWindow;

			iFrameContentWindow.loadDefer = $q.defer();
			iFrameContentWindow.loadDefer.promise.then(function(msg) {
				$log.debug('loadDefer resolved, injecting data: ' + msg);
				iFrameContentWindow.updateData($stateParams.graph.json);

				iFrameContentWindow.dataTransferDefer.resolve('data injected');
			});
		}
	}]);