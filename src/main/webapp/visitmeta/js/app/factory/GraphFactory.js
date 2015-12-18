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

app.factory('GraphFactory', function ($http, properties) {

    function getGraphUrl(connectionName) {
        return properties.dataserviceUrl + "/" + connectionName + "/graph";
    }

    return {
        /**
         *
         * @param connectionName
         * @returns {*}
         */
        getCurrent: function (connectionName) {
            return $http.get(getGraphUrl(connectionName) + "/current");
            return $http.get("data/json_test_files/current.json");
        },
        /**
         *
         * @param connectionName
         * @returns {*}
         */
        getInitial: function (connectionName) {
            return $http.get(getGraphUrl(connectionName) + "/initial");
            return $http.get("data/json_test_files/initial.json");
        },
        /**
         *
         * @param connectionName
         * @param at
         * @returns {*}
         */
        getAt: function (connectionName, at) {
            return $http.get(getGraphUrl(connectionName) + "/" + at);
            return $http.get("data/json_test_files/" + at);
        },
        /**
         *
         * @param connectionName
         * @param from
         * @param to
         * @returns {*}
         */
        getDelta: function (connectionName, from, to) {
            return $http.get(getGraphUrl(connectionName) + "/" + from + "/" + to);
            return $http.get("data/json_test_files/" + from + "-" + to);
        },
        /**
         *
         * @param connectionName
         * @returns {*}
         */
        getChanges: function (connectionName) {
            return $http.get(getGraphUrl(connectionName) + "/changes");
            return $http.get("data/json_test_files/changes.json");
        }
    };
});
