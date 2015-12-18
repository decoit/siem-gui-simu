angular.module('SiemGui')
	.controller("IncidentsController", ['$scope', '$log', '$modal', 'IncidentsService', 'StompSubscriptionService', function($scope, $log, $modal, viewService, subService) {
		var stompChannels = [];

		$scope.activeIncidents = [];
		$scope.resolvedIncidents = [];
		$scope.sortedBy = {
			active: {
				date: false,
				dateRev: false,
				title: false,
				titleRev: false,
				risk: false,
				riskRev: false,
				dueOn: false,
				dueOnRev: false,
				status: false,
				statusRev: false,
				owner: false,
				ownerRef: false
			},
			resolved: {
				date: false,
				dateRev: false,
				resDate: false,
				resDateRev: false,
				title: false,
				titleRev: false,
				risk: false,
				riskRev: false
			}
		};

		// Message channels to receive the list of active and resolved incidents
		stompChannels.push({
			name: '/user/queue/incidents/activelist',
			callback: activeIncidentsListCallback
		});
		stompChannels.push({
			name: '/user/queue/incidents/resolvedlist',
			callback: resolvedIncidentsListCallback
		});
		stompChannels.push({
			name: '/user/queue/incidents/takeresult',
			callback: listResultCallback
		});
		stompChannels.push({
			name: '/user/queue/incidents/beginresult',
			callback: listResultCallback
		});
		stompChannels.push({
			name: '/user/queue/incidents/finishresult',
			callback: listResultCallback
		});
		stompChannels.push({
			name: '/user/queue/incidents/timebooked',
			callback: listResultCallback
		});

		subService.subscribeChannels('IncidentsController', stompChannels);

		// Unsubscribe from STOMP message channels when this scope is destroyed
		$scope.$on('$destroy', function() {
			subService.unsubscribeChannels('IncidentsController');
		});


		/**
		 * Sort the list of active incidents by date.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortActiveByDate = function(reverse) {
			if(reverse === true) {
				$scope.activeIncidents.sort(function(a, b) {
					var r = compareByDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r * -1;
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.dateRev = true;
			}
			else {
				$scope.activeIncidents.sort(function(a, b) {
					var r = compareByDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r;
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.date = true;
			}
		};


		/**
		 * Sort the list of active incidents by title.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortActiveByTitle = function(reverse) {
			if(reverse === true) {
				$scope.activeIncidents.sort(function(a, b) {
					return (compareByTitle(a, b) * -1);
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.titleRev = true;
			}
			else {
				$scope.activeIncidents.sort(compareByTitle);

				resetActiveSortingMarker();
				$scope.sortedBy.active.title = true;
			}
		};


		/**
		 * Sort the list of active incidents by status.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortActiveByStatus = function(reverse) {
			if(reverse === true) {
				$scope.activeIncidents.sort(function(a, b) {
					return (compareByStatus(a, b) * -1);
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.statusRev = true;
			}
			else {
				$scope.activeIncidents.sort(compareByStatus);

				resetActiveSortingMarker();
				$scope.sortedBy.active.status = true;
			}
		};


		/**
		 * Sort the list of active incidents by risk.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortActiveByRisk = function(reverse) {
			if(reverse === true) {
				$scope.activeIncidents.sort(function(a, b) {
					return (compareByRisk(a, b) * -1);
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.riskRev = true;
			}
			else {
				$scope.activeIncidents.sort(compareByRisk);

				resetActiveSortingMarker();
				$scope.sortedBy.active.risk = true;
			}
		};


		/**
		 * Sort the list of active incidents by due on date.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortActiveByDueOnDate = function(reverse) {
			if(reverse === true) {
				$scope.activeIncidents.sort(function(a, b) {
					var r = compareByDueOnDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r * -1;
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.dueOnRev = true;
			}
			else {
				$scope.activeIncidents.sort(function(a, b) {
					var r = compareByDueOnDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r;
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.dueOn = true;
			}
		};


		/**
		 * Sort the list of active incidents by owner.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortActiveByOwner = function(reverse) {
			if(reverse === true) {
				$scope.activeIncidents.sort(function(a, b) {
					return (compareByOwner(a, b) * -1);
				});

				resetActiveSortingMarker();
				$scope.sortedBy.active.ownerRev = true;
			}
			else {
				$scope.activeIncidents.sort(compareByOwner);

				resetActiveSortingMarker();
				$scope.sortedBy.active.owner = true;
			}
		};


		/**
		 * Sort the list of resolved incidents by date.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortResolvedByDate = function(reverse) {
			if(reverse === true) {
				$scope.resolvedIncidents.sort(function(a, b) {
					var r = compareByDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r * -1;
				});

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.dateRev = true;
			}
			else {
				$scope.resolvedIncidents.sort(function(a, b) {
					var r = compareByDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r;
				});

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.date = true;
			}
		};


		/**
		 * Sort the list of resolved incidents by date.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortResolvedByResDate = function(reverse) {
			if(reverse === true) {
				$scope.resolvedIncidents.sort(function(a, b) {
					var r = compareByResDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r * -1;
				});

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.resDateRev = true;
			}
			else {
				$scope.resolvedIncidents.sort(function(a, b) {
					var r = compareByResDate(a, b);

					if(r === 0) {
						r = compareById(a, b);
					}

					return r;
				});

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.resDate = true;
			}
		};


		/**
		 * Sort the list of resolved incidents by title.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortResolvedByTitle = function(reverse) {
			if(reverse === true) {
				$scope.resolvedIncidents.sort(function(a, b) {
					return (compareByTitle(a, b) * -1);
				});

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.titleRev = true;
			}
			else {
				$scope.resolvedIncidents.sort(compareByTitle);

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.title = true;
			}
		};


		/**
		 * Sort the list of resolved incidents by risk.
		 *
		 * @param {Boolean} reverse Reverse ordering or not
		 * @returns {undefined}
		 */
		$scope.sortResolvedByRisk = function(reverse) {
			if(reverse === true) {
				$scope.resolvedIncidents.sort(function(a, b) {
					return (compareByRisk(a, b) * -1);
				});

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.riskRev = true;
			}
			else {
				$scope.resolvedIncidents.sort(compareByRisk);

				resetResolvedSortingMarker();
				$scope.sortedBy.resolved.risk = true;
			}
		};


		/**
		 * Get the real name of a ticket owner or '---' if owner is 'Nobody'
		 *
		 * @param {Object} owner Ticket owner object
		 * @returns {String} Owner real name
		 */
		$scope.getTicketOwnerName = function(owner) {
			if(owner.username === 'Nobody') {
				return '---';
			}
			else {
				return owner.realName;
			}
		};


		/**
		 * Make the current user the owner of the specified incident.
		 * Only works for unclaimed incidents.
		 *
		 * @param {Integer} incId Incident ID
		 * @returns {undefined}
		 */
		$scope.takeIncident = function(incId) {
			$log.debug('Trying to take incident with ID: ' + incId);

			viewService.takeIncident(incId);
		};


		/**
		 * Translate the incident status into a normal text.
		 *
		 * @param {String} status The status text as returned from the backend
		 * @returns {String} The translated status string
		 */
		$scope.formatIncidentStatus = function(status) {
			switch(status) {
				case 'NEW':
					return 'Neu';
				case 'IN_PROGRESS':
					return 'In Bearbeitung';
				case 'UNKNOWN':
					return 'Unbekannt';
				case 'DONE':
					return 'Abgeschlossen';
				default:
					throw 'Invalid incident status!';
			}
		};


		/**
		 * Translate the incident threat level into a normal text.
		 *
		 * @param {String} level The threat level text as returned from the backend
		 * @returns {String} The translated threat level string
		 */
		$scope.formatThreatLevel = function(level) {
			switch(level) {
				case 'LOW':
					return 'niedrig';
				case 'MEDIUM':
					return 'mittel';
				case 'HIGH':
					return 'hoch';
				default:
					throw 'Invalid threat level!';
			}
		};


		/**
		 * Open the modal dialog for displaying incident details.
		 *
		 * @param {Integer} id Array index of the event
		 * @param {Boolean} active Use array of active incidents or not
		 * @returns {undefined}
		 */
		$scope.openIncidentDetails = function(id, active) {
			$log.debug('Open incident details for index: ' + id);

			$modal.open({
				templateUrl: 'templates/incidentdetails.html',
				controller: 'IncidentDetailsController',
				scope: $scope,
				size: 'lg',
				resolve: {
					details: function() {
						if(active) {
							return {
								incDetails: $scope.activeIncidents[id],
								incIndex: id,
								active: active
							};
						}
						else {
							return{
								incDetails: $scope.resolvedIncidents[id],
								incIndex: id,
								active: active
							};
						}
					}
				}
			});
		};


		/**
		 * Begin working on the specified incident. It will be marked as "IN_PROGRESS" after this operation.
		 * This operation can only be successfully completed if the active user is the owner of the ticket.
		 *
		 * @param {Integer} incId Incident ID
		 * @returns {undefined}
		 */
		$scope.beginWorkOnIncident = function(incId) {
			viewService.beginWorkOnIncident(incId);
		};


		/**
		 * Finish working on the specified incident. It will be marked as "DONE" after this operation.
		 * This operation can only be successfully completed if the active user is the owner of the ticket.
		 *
		 * @param {Integer} incId Incident ID
		 * @returns {undefined}
		 */
		$scope.finishWorkOnIncident = function(incId) {
			viewService.finishWorkOnIncident(incId);
		};


		$scope.$parent.navpath = 'incidents';

		viewService.requestActiveIncidents();
		viewService.requestResolvedIncidents();


		/**
		 * Callback for the STOMP message containing the list of active incidents.
		 * Stores the message contents in the $scope.
		 *
		 * @param {Object} data STOMP message data
		 * @returns {undefined}
		 */
		function activeIncidentsListCallback(data) {
			$log.debug('activeIncidentsListCallback, data:');
			$log.debug(data);

			var fetchedResult = JSON.parse(data.body);

			$log.debug('activeIncidentsListCallback, fetchedResult:');
			$log.debug(fetchedResult);

			$scope.activeIncidents = fetchedResult.actinclist;
			$scope.sortActiveByDate(false);
		}


		/**
		 * Callback for the STOMP message containing the list of resolved incidents.
		 * Stores the message contents in the $scope.
		 *
		 * @param {Object} data STOMP message data
		 * @returns {undefined}
		 */
		function resolvedIncidentsListCallback(data) {
			$log.debug('resolvedIncidentsListCallback, data:');
			$log.debug(data);

			var fetchedResult = JSON.parse(data.body);

			$log.debug('resolvedIncidentsListCallback, fetchedResult:');
			$log.debug(fetchedResult);

			$scope.resolvedIncidents = fetchedResult.resinclist;
			$scope.sortResolvedByDate(false);
		}


		/**
		 * Callback function which will process incoming list data.
		 * It updates the incident lists and restored former sorting.
		 *
		 * @param {Object} data Message data
		 * @returns {undefined}
		 */
		function listResultCallback(data) {
			$log.debug('listResultCallback, data:');
			$log.debug(data);

			var fetchedResult = JSON.parse(data.body);

			$log.debug('listResultCallback, fetchedResult:');
			$log.debug(fetchedResult);

			// Update list of active incidents
			$scope.activeIncidents = fetchedResult.actinclist;
			$scope.resolvedIncidents = fetchedResult.resinclist;

			// Restore last selected ordering for active incidents
			if($scope.sortedBy.active.date || $scope.sortedBy.active.dateRev) {
				$scope.sortActiveByDate($scope.sortedBy.active.dateRev);
			}
			else if($scope.sortedBy.active.title || $scope.sortedBy.active.titleRev) {
				$scope.sortActiveByTitle($scope.sortedBy.active.titleRev);
			}
			else if($scope.sortedBy.active.risk || $scope.sortedBy.active.riskRev) {
				$scope.sortActiveByRisk($scope.sortedBy.active.riskRev);
			}
			else if($scope.sortedBy.active.dueOn || $scope.sortedBy.active.dueOnRev) {
				$scope.sortActiveByDueOnDate($scope.sortedBy.active.riskRev);
			}
			else if($scope.sortedBy.active.status || $scope.sortedBy.active.statusRev) {
				$scope.sortActiveByStatus($scope.sortedBy.active.statusRev);
			}
			else if($scope.sortedBy.active.owner || $scope.sortedBy.active.ownerRev) {
				$scope.sortActiveByOwner($scope.sortedBy.active.ownerRev);
			}
			else {
				// Default ordering by date, should never happen
				$scope.sortActiveByDate(false);
				$log.debug('Something bad happened, default ordering after incident list response!');
			}

			// Restore last selected ordering for resolved incidents
			if($scope.sortedBy.resolved.date || $scope.sortedBy.resolved.dateRev) {
				$scope.sortResolvedByDate($scope.sortedBy.resolved.dateRev);
			}
			else if($scope.sortedBy.resolved.resDate || $scope.sortedBy.resolved.resDateRev) {
				$scope.sortResolvedByResDate($scope.sortedBy.resolved.resDateRev);
			}
			else if($scope.sortedBy.resolved.title || $scope.sortedBy.resolved.titleRev) {
				$scope.sortResolvedByTitle($scope.sortedBy.resolved.titleRev);
			}
			else if($scope.sortedBy.resolved.risk || $scope.sortedByresolvedactive.riskRev) {
				$scope.sortResolvedByRisk($scope.sortedBy.resolved.riskRev);
			}
			else {
				// Default ordering by date, should never happen
				$scope.sortResolvedByDate(false);
				$log.debug('Something bad happened, default ordering after incident list response!');
			}
		}


		/**
		 * Comparision function to sort incidents by ID.
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareById(incA, incB) {
			return (incA.id - incB.id);
		}


		/**
		 * Comparision function to sort incidents by timestamp.
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareByDate(incA, incB) {
			if(incA.timestamp.year === incB.timestamp.year) {
				if(incA.timestamp.dayOfYear === incB.timestamp.dayOfYear) {
					var secA = incA.timestamp.second;
					secA += incA.timestamp.minute * 60;
					secA += incA.timestamp.hour * 60 * 60;

					var secB = incB.timestamp.second;
					secB += incB.timestamp.minute * 60;
					secB += incB.timestamp.hour * 60 * 60;

					return (secA - secB);
				}
				else {
					return (incA.timestamp.dayOfYear - incB.timestamp.dayOfYear);
				}
			}
			else {
				return (incA.timestamp.year - incB.timestamp.year);
			}
		}


		/**
		 * Comparision function to sort incidents by resolved on date.
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareByResDate(incA, incB) {
			if(incA.ticket.resolvedOn.year === incB.ticket.resolvedOn.year) {
				if(incA.ticket.resolvedOn.dayOfYear === incB.ticket.resolvedOn.dayOfYear) {
					var secA = incA.ticket.resolvedOn.second;
					secA += incA.ticket.resolvedOn.minute * 60;
					secA += incA.ticket.resolvedOn.hour * 60 * 60;

					var secB = incB.ticket.resolvedOn.second;
					secB += incB.ticket.resolvedOn.minute * 60;
					secB += incB.ticket.resolvedOn.hour * 60 * 60;

					return (secA - secB);
				}
				else {
					return (incA.ticket.resolvedOn.dayOfYear - incB.ticket.resolvedOn.dayOfYear);
				}
			}
			else {
				return (incA.ticket.resolvedOn.year - incB.ticket.resolvedOn.year);
			}
		}


		/**
		 * Comparision function to sort incidents by title.
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareByTitle(incA, incB) {
			var titleA = incA.name;
			var titleB = incB.name;

			return titleA.localeCompare(titleB);
		}


		/**
		 * Comparision function to sort incidents by risk.
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareByRisk(incA, incB) {
			return (incA.risk - incB.risk);
		}


		/**
		 * Comparision function to sort incidents by due on date.
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareByDueOnDate(incA, incB) {
			if(incA.timestamp.year === incB.timestamp.year) {
				if(incA.timestamp.dayOfYear === incB.timestamp.dayOfYear) {
					var secA = incA.timestamp.second;
					secA += incA.timestamp.minute * 60;
					secA += incA.timestamp.hour * 60 * 60;

					var secB = incB.timestamp.second;
					secB += incB.timestamp.minute * 60;
					secB += incB.timestamp.hour * 60 * 60;

					return (secA - secB);
				}
				else {
					return (incA.timestamp.dayOfYear - incB.timestamp.dayOfYear);
				}
			}
			else {
				return (incA.timestamp.year - incB.timestamp.year);
			}
		}


		/**
		 * Comparision function to sort incidents by status.
		 * Status values and ordering are defined as stated in documentation for getStatusValue().
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareByStatus(incA, incB) {
			var statA = getStatusValue(incA.status);
			var statB = getStatusValue(incB.status);

			return (statA - statB);
		}


		/**
		 * Comparision function to sort incidents by owner real name.
		 *
		 * @param {Object} incA First incident
		 * @param {Object} incB Second incident
		 * @returns {Integer} Comparision result (negative: incA&lt;incB; 0: incA==incB; positive: incA&gt;incB)
		 */
		function compareByOwner(incA, incB) {
			var userA = $scope.getTicketOwnerName(incA.ticket.owner);
			var userB = $scope.getTicketOwnerName(incB.ticket.owner);

			return userA.localeCompare(userB);
		}


		/**
		 * Convert the string representation of a status into a number for comparision.
		 * Values are defined as follows:
		 * "NEW": 1
		 * "IN_PROGRESS": 2
		 * "UNKNOWN": 3
		 * "DONE": 4
		 * Which will produce the following ordering:
		 * "NEW" &lt; "IN_PROGRESS" &lt; "UNKNOWN" &lt; "DONE"
		 *
		 * Strings other than the ones given above will result in an exception.
		 *
		 * @param {String} status On of the above status strings
		 * @returns {Number}
		 */
		function getStatusValue(status) {
			switch(status) {
				case 'NEW':
					return 1;
				case 'IN_PROGRESS':
					return 2;
				case 'UNKNOWN':
					return 3;
				case 'DONE':
					return 4;
				default:
					throw 'Invalid incident status!';
			}
		}


		/**
		 * Reset markers for sorting the list of active incidents to default.
		 *
		 * @returns {undefined}
		 */
		function resetActiveSortingMarker() {
			$scope.sortedBy.active = {
				date: false,
				dateRev: false,
				title: false,
				titleRev: false,
				risk: false,
				riskRev: false,
				dueOn: false,
				dueOnRev: false,
				status: false,
				statusRev: false,
				owner: false,
				ownerRef: false
			};
		}


		/**
		 * Reset markers for sorting the list of resolved incidents to default.
		 *
		 * @returns {undefined}
		 */
		function resetResolvedSortingMarker() {
			$scope.sortedBy.resolved = {
				date: false,
				dateRev: false,
				resDate: false,
				resDateRev: false,
				title: false,
				titleRev: false,
				risk: false,
				riskRev: false
			};
		}
	}]);