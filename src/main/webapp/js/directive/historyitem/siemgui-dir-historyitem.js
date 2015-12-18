/*
 * No license defined yet.
 */
angular.module('SiemGui')
	.controller('SiemGuiHistoryItemController', ['$scope', '$filter', '$log', function($scope, $filter, $log) {
		switch($scope.item.type) {
			case 'CREATED':
				$scope.title = 'Erstellt';
				$scope.contentText = formatTextContent($scope.item.text);
				break;
			case 'TAKEN':
				$scope.title = 'Übernommen';
				$scope.contentText = 'Die Bearbeitung wurde von <strong>' + $filter('stripHtml')($scope.item.newOwner.realName) + '</strong> übernommen.';
				break;
			case 'FIELD_CHANGED':
				$scope.title = 'Feld geändert';
				$scope.contentText = 'Der Wert des Feldes <strong>' + $filter('stripHtml')($scope.item.field) + '</strong> wurde von <strong>' + $filter('stripHtml')($scope.item.oldValue) + '</strong> auf <strong>' + $filter('stripHtml')($scope.item.newValue) + '</strong> geändert.';
				break;
			case 'STATUS_CHANGED':
				$scope.title = 'Status geändert';
				$scope.contentText = 'Der Status wurde von <strong>' + $filter('stripHtml')($scope.item.oldStatus) + '</strong> auf <strong>' + $filter('stripHtml')($scope.item.newStatus) + '</strong> geändert.';
				break;
			case 'COMMENTED':
				$scope.title = 'Kommentar';
				$scope.contentText = formatTextContent($scope.item.text);
				break;
			case 'ANSWERED':
				$scope.title = 'Antwort';
				$scope.contentText = formatTextContent($scope.item.text);
				break;
		}

		$scope.date = formatLocalDate($scope.item.date);
		$scope.time = formatLocalTime($scope.item.date);
		$scope.realName = $scope.item.creator.realName;

		$log.debug('History item, $scope.item:');
		$log.debug($scope.item);
		$log.debug('History item, $scope.quoteCallback:');
		$log.debug($scope.quoteCallback);
		$log.debug('History item, $scope.quoteCallbackArg:');
		$log.debug($scope.quoteCallbackArg);


		$scope.callQuoteCallback = function() {
			$scope.quoteCallback({ 'arg': $scope.quoteCallbackArg});
		};


		function formatLocalDate(dateObj) {
			if(dateObj) {
				var date = dateObj.year + "-";

				if(dateObj.monthValue < 10) {
					date += "0";
				}
				date += dateObj.monthValue + "-";

				if(dateObj.dayOfMonth < 10) {
					date += "0";
				}
				date += dateObj.dayOfMonth;

				return date;
			}
			else {
				$log.debug('Invalid date object:');
				$log.debug(dateObj);

				return '0000-00-00';
			}
		};


		function formatLocalTime(timeObj) {
			if(timeObj) {
				var time = '';

				if(timeObj.hour < 10) {
					time += "0";
				}
				time += timeObj.hour + ":";

				if(timeObj.minute < 10) {
					time += "0";
				}
				time += timeObj.minute + ":";

				if(timeObj.second < 10) {
					time += "0";
				}
				time += timeObj.second;

				return time;
			}
			else {
				$log.debug('Invalid time object:');
				$log.debug(timeObj);

				return '00:00:00';
			}
		}


		function formatTextContent(text) {
			var output = $filter('stripHtml')(text);
			output = $filter('stripLineBreaks')(output);

			output = output.replace(/\[br\]/g, '<br>');
			output = output.replace(/\[bq\]/g, '<blockquote>');
			output = output.replace(/\[\/bq\]/g, '</blockquote>');
			output = output.replace(/\[p\]/g, '<p>');
			output = output.replace(/\[\/p\]/g, '</p>');
			output = output.replace(/\[b\]/g, '<strong>');
			output = output.replace(/\[\/b\]/g, '</strong>');
			output = output.replace(/\[i\]/g, '<i>');
			output = output.replace(/\[\/i\]/g, '</i>');

			return output;
		}
	}]);


angular.module('SiemGui')
	.directive('siemguiHistoryItem', function() {
		return {
			restrict: 'E',
			scope: {
				item: '=',
				quote: '@',
				quoteCallback: '&',
				quoteCallbackArg: '@'
			},
			templateUrl: 'js/directive/historyitem/historyitem.html',
			controller: 'SiemGuiHistoryItemController'
		};
	});
