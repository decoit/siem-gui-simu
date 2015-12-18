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

app.factory('PollingProvider', ['CallbackProvider', '$log', '$interval', function (CallbackProvider, $log, $interval) {
    var PollingService = function () {
        this.run = undefined;
    };

    PollingService.prototype.poll = function () {
        throw new Error("update function must be implemented");
    };

    PollingService.prototype.start = function (millisecs) {
        if (!this.isRunning()) {
            $log.info("start polling by interval of " + millisecs + " millisec");
            this.poll();
            this.run = $interval(this.poll, millisecs);
        }
    };

    PollingService.prototype.stop = function () {
        if (this.isRunning()) {
            $log.info("stop");
            $interval.cancel(this.run);
            this.run = undefined;
        }
    };

    PollingService.prototype.isRunning = function () {
        return !angular.isUndefined(this.run);
    };

    PollingService = angular.extend(PollingService,CallbackProvider.getNewInstance());

    console.log(PollingService,CallbackProvider);

    return {
        getNewInstance: function () {
            return new PollingService();
        }
    }
}]);

app.factory('PollingService', ['CallbackProvider', function (CallbackProvider) {
    return CallbackProvider.getNewInstance();
}]);
