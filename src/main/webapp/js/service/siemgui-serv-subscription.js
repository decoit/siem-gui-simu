angular.module('SiemGui')
	.factory('StompSubscriptionService', ['$log', 'WebSocketService', function($log, wsService) {
		// We return this object to anything injecting our service
		var Service = {};
		var subscriptions = {};


		/**
		 * Private factory function for a subscription callback. The returned callback can be used for
		 * callbacks sent to WebSocketService.subscribe() to handle pending subscriptions.
		 *
		 * @param {String} serviceName Name of the service subscribing channels
		 * @returns {Function} Callback function
		 */
		function subscriptionCallbackFactory(serviceName) {
			if(typeof subscriptions[serviceName] === 'undefined') {
				subscriptions[serviceName] = {};
			}

			var cb = function(channel, sub) {
				$log.debug('Subsciption callback for: ' + serviceName + ' -> ' + channel);

				subscriptions[serviceName][channel] = sub;
			};

			return cb;
		}


		/**
		 * Public service function to subscribe to all listed channels and register callbacks for received messages.
		 * The channels array must be of the form [{name:'channelName',callback:messageCallback}, ...]
		 *
		 * @param {String} serviceName Name of the service subscribing channels
		 * @param {Array} channels Array containing channel names and callbacks
		 * @returns {undefined}
		 */
		Service.subscribeChannels = function(serviceName, channels) {
			$log.debug(serviceName + ' is subscribing to channels...');

			if(typeof subscriptions[serviceName] === 'undefined') {
				subscriptions[serviceName] = {};
			}

			angular.forEach(channels, function(channel) {
				var name = channel.name;
				var cb = channel.callback;
				var subCb = subscriptionCallbackFactory(serviceName);

				wsService.subscribe(name, cb, subCb);
			});

			$log.debug('...done!');
		};


		/**
		 * Public service function to unsubscribe from all previously registered channels.
		 *
		 * @param {String} serviceName Name of the service unsubscribing channels
		 * @returns {undefined}
		 */
		Service.unsubscribeChannels = function(serviceName) {
			$log.debug(serviceName + ' unsubscribing from channels...');

			angular.forEach(subscriptions[serviceName], function(sub) {
				sub.unsubscribe();
			});

			subscriptions[serviceName] = {};

			$log.debug('...done!');
		};


		return Service;
	}]);