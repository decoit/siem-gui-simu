/*
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 *
 * =====================================================
 *
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 *
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 *
 * This file is part of VisITMeta Javascript GUI for SIMU project, version 0.1.0,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 *
 * Copyright (C) 2015 Trust@HsH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 * @author Soeren Grzanna
 */
'use strict';

app.factory('ChangesService', ['GraphFactory', '$log', '$interval', 'properties', function (GraphFactory, $log, $interval, properties) {
    /**
     *
     * @type {undefined}
     */
    var _run = undefined;

    /**
     *
     * @type {Array}
     */
    var _subscribes = [];

    /**
     *
     * @type {Array}
     */
    var _changes = [];

    /**
     *
     * @type {string|undefined}
     */
    var _connection = undefined;

    function polling() {
        if (_subscribes.length > 0) {
            GraphFactory.getChanges(_connection).success(function (data, status, headers, config) {
                $log.debug("result of polling changes: ", data);
                _changes = Object.keys(data);
                callSubscribe();
            });
        }
    }

    function callSubscribe() {
        angular.forEach(_subscribes, function (subscribe) {
            subscribe.call(_changes);
        });
    }

    return {
        start: function () {
            if (!this.isRunning()) {
                $log.info("start polling by interval of " + properties.changesPollingTime + " millisec");
                polling();
                _run = $interval(polling, properties.changesPollingTime);
            }
        },

        stop: function () {
            if (this.isRunning()) {
                $log.info("stop");
                $interval.cancel(_run);
                _run = undefined;
            }
        },

        isRunning: function () {
            return !angular.isUndefined(_run);
        },

        subscribe: function (callbackFunction) {
            $log.debug("add callback function");
            _subscribes.push({call: callbackFunction, data: null});
        },

        setConnection: function (connection) {
            _connection = connection;
        }


    };
}]);
