/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.controller("IncidentCommentFormController", ['$scope', '$log', 'IncidentsService', 'StompSubscriptionService', 'details', 'history', 'formatHistoryItem', function($scope, $log, viewService, subService, details, history, formatHistoryItem) {
		$scope.details = details;
		$scope.history = history;
		$scope.formatHistoryItem = formatHistoryItem;
		$scope.commentText = '';

		$log.debug('Comment form, incident details:');
		$log.debug(details);
		$log.debug('Comment form, incident history:');
		$log.debug(history);

		var stompChannels = [];

		stompChannels.push({
			name:  '/user/queue/incidents/commentconfirmed',
			callback: commentConfirmedCallback
		});

		subService.subscribeChannels('IncidentCommentFormController', stompChannels);


		// Unsubscribe from STOMP message channels when this scope is destroyed
		$scope.$on('$destroy', function() {
			subService.unsubscribeChannels('IncidentCommentFormController');
		});


		$scope.sendComment = function() {
			viewService.postComment($scope.details.incDetails.id, $scope.commentText);
		};


		$scope.validateComment = function() {
			return $scope.commentText.length < 10;
		};


		$scope.quoteComment = function(index) {
			$log.debug('Quote comment: ' + index);

			$scope.commentText.trim();
			if($scope.commentText.length > 0) {
				if($scope.commentText.charAt($scope.commentText.length-1) !== '\n') {
					$scope.commentText += '\n';
				}
			}

			$scope.commentText += '[bq]';
			$scope.commentText += $scope.history[index].text;
			$scope.commentText += '[/bq]\n';
		};


		function commentConfirmedCallback() {
			$log.debug('Comment posted successfully');

			$scope.$close('Comment posted');
			$scope.$parent.openIncidentDetails($scope.details.incIndex, $scope.details.active);
		}
	}]);