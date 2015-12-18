angular.module('SiemGui')
	.factory('WebSocketService', ['$log', '$rootScope', function($log, $rootScope) {
		// We return this object to anything injecting our service
		var Service = {};

		var ws;
		var stompClient;
		var isConnected = false;
		var pendingSubscriptions = [];
		var pendingMessages = [];


		/**
		 * Private function to register any message channel subscriptions that were sent prior to establishing the WebSocket connection
		 *
		 * @returns {undefined}
		 */
		function processPendingSubscriptions() {
			if(isConnected) {
				angular.forEach(pendingSubscriptions, function(pendSubscription) {
					var sub = stompClient.subscribe(pendSubscription.channel, pendSubscription.callback, pendSubscription.headers);

					pendSubscription.subcallback(pendSubscription.channel, sub);
				});

				pendingSubscriptions = [];
			}
		}


		/**
		 * Private function to send any messages that were ordered to be sent prior to establishing the WebSocket connection
		 *
		 * @returns {undefined}
		 */
		function processPendingMessages() {
			if(isConnected) {
				angular.forEach(pendingMessages, function(pendMessage) {
					stompClient.send(pendMessage.channel, pendMessage.headers, pendMessage.data);
				});

				pendingMessages = [];
			}
		}


		/**
		 * Public function to connect to a websocket on the given endpoint. The SockJS library will be used to wrap the WebSocket,
		 * so the endpoint must be SockJS compatible.
		 *
		 * @param {String} wsEndpoint WebSocket endpoint to be used
		 * @param {Function} callback Callback function to be called on successful connect
		 * @param {Object} [headers] Custom headers to send with connect message
		 * @returns {undefined}
		 */
		Service.connect = function(wsEndpoint, callback, headers) {
			if(!isConnected) {
				var custHeaders = {};

				if(headers) {
					custHeaders = headers;
				}

				ws = new SockJS(wsEndpoint);
				stompClient = Stomp.over(ws);

				// Disable logging of stomp.js
				//stompClient.debug = function(){};

				stompClient.connect(custHeaders,
					function(frame) {
						$log.debug(frame);

						$log.info('WebSocket connected');
						$log.debug('WebSocket endpoint used: ' + wsEndpoint);

						isConnected = true;

						// Make sure to process subscriptions and messages that were stored as pending
						processPendingSubscriptions();
						processPendingMessages();

						callback();
					},
					function(error) {
						$log.error('WebSocket connection failed');
						$log.debug('Error: ' + error);

						ws = null;
						stompClient = null;
						isConnected = false;
					}
				);
			}
		};


		/**
		 * Public function to disconnect from the websocket.
		 *
		 * @returns {undefined}
		 */
		Service.disconnect = function() {
			if(isConnected) {
				stompClient.disconnect(function() {
					$log.info('WebSocket disconnected');

					ws = null;
					stompClient = null;
					isConnected = false;
				});
			}
		};


		/**
		 * Public function to return whether the WebSocket is connected or not
		 *
		 * @returns {Boolean} WebSocket is connected or not
		 */
		Service.isConnected = function() {
			return isConnected;
		};


		/**
		 * Public function to subscribe to a STOMP message channel. If the connection to the WebSocket is not yet established,
		 * the subscription will be stored as pending and processed once the connection was established.
		 *
		 * @param {String} channel The message channel
		 * @param {Function} callback The callback function to be called on incoming data
		 * @param {Function} subscriptionCallback The callback function to be called when subscription completed
		 * @param {Object} [headers] Custom headers to send with subscription message
		 * @returns {undefined}
		 */
		Service.subscribe = function(channel, callback, subscriptionCallback, headers) {
			// Wrap the callback in $apply to make sure Angular is aware of the scope changes
			var cbFunc = function(data) {
				$rootScope.$apply(function() {
					callback(data);
				});
			};

			if(isConnected) {
				$log.debug('Subscription to channel: ' + channel);

				var sub = stompClient.subscribe(channel, cbFunc, headers);
				subscriptionCallback(channel, sub);
			}
			else {
				$log.debug('Pending subscription to channel: ' + channel);

				// Store pending subscription for later use
				var pendSub = {
					channel: channel,
					callback: cbFunc,
					headers: headers,
					subcallback: subscriptionCallback
				};

				pendingSubscriptions.push(pendSub);
			}
		};


		/**
		 * Public function to unsubscribe from a STOMP message channel. Requires the subscription object returned by subscribe().
		 *
		 * @param {Object} subscription Subscription object returned by subscribe()
		 * @returns {undefined}
		 */
		Service.unsubscribe = function(subscription) {
			if(subscription && typeof subscription.unsubscribe === 'function') {
				subscription.unsubscribe();

				$log.debug('Successful unsubscribed from subscription: ' + subscription.id);
			}
			else {
				$log.warn('Object without unsubscribe() function provided');
			}
		};


		/**
		 * Public function to send a message on a STOMP message channel. If the connection to the WebSocket is not yet established,
		 * the mesasge will be stored as pending and sent once the connection was established.
		 *
		 * @param {String} channel The message channel
		 * @param {Object|Array} data The data to send, will be converted to JSON prior to sending
		 * @param {Object} [headers] Custom headers to send with message
		 * @returns {undefined}
		 */
		Service.send = function(channel, data, headers) {
			var json = JSON.stringify(data);
			var custHeaders = {};

			if(headers) {
				custHeaders = headers;
			}

			if(isConnected) {
				$log.debug('Sending data to channel: ' + channel);
				$log.debug('Custom headers sent: ' + custHeaders);
				$log.debug('Data sent: ' + json);

				stompClient.send(channel, custHeaders, json);
			}
			else {
				$log.debug('Pending message to channel: ' + channel);

				// Store pending message for later use
				var pendMsg = {
					channel: channel,
					headers: headers,
					data: json
				};

				pendingMessages.push(pendMsg);
			}


		};


		return Service;
	}]);