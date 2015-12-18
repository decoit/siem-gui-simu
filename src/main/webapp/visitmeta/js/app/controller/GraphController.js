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

app.controller('GraphController', ['GraphFactory', 'GraphLayout', 'ChangesService', '$scope', '$interval', '$log', 'properties', '$rootScope', function (GraphFactory, GraphLayout, ChangesService, $scope, $interval, $log, properties, $rootScope) {
    var self = this;
    var _connectionName = properties.connection;
    var _lastUpdateTime = 0;
    var _timestamps = [];
    var _run = undefined;

    $rootScope.$on('live.on', function () {
        startLive();
    });

    $rootScope.$on('live.off', function () {
        stopLive();
        setGraph(_timestamps[_timestamps.length - 1]);
        GraphLayout.clear();
    });

    $rootScope.$on('timestamp.change', function (data, timestamp) {
        GraphLayout.clear();
        console.log(data, timestamp);
        setGraph(timestamp);
    });

    $rootScope.$on('connection.change', function (data, connection) {
        _timestamps = [];
        _lastUpdateTime = 0;
        _connectionName = connection;
        ChangesService.setConnection(connection);
        GraphLayout.clear();
    });

    this.graph = GraphLayout.getGraph();
    $scope.graph = this.graph;


    /**
     *
     * @returns {void}
     */
    function init() {
        ChangesService.subscribe(updateChangesSuccess);
        ChangesService.setConnection(_connectionName);
        ChangesService.start();
    }

    /**
     *
     * @returns {void}
     */
    function stopLive() {
        $log.info("stop live mode");
        $interval.cancel(_run);
    }

    /**
     *
     * @returns {void}
     */
    function startLive() {
        $log.info("start live mode");
        setCurrentGraph();
        _run = $interval(liveIntervalLoop, properties.graphUpateTime);
    }

    /**
     *
     * @returns {boolean}
     */
    function isLive(){
        return !angular.isUndefined(_run);
    }

    function liveIntervalLoop() {
        if (_timestamps.length > 0 && _timestamps[_timestamps.length - 1] > _lastUpdateTime) {
            setDelta(_lastUpdateTime, _timestamps[_timestamps.length - 1]);
        }
    }

    function updateChangesSuccess(data) {
        $log.debug("call updateChangesSuccess from GraphController");
        _timestamps = data;
    }

    function setCurrentGraph() {
        GraphFactory.getCurrent(_connectionName).success(transferGraph);
    }

    function setGraph(at) {
        GraphFactory.getAt(_connectionName, at).success(transferGraph);
    }

    function setDelta(from, to) {
        var parser = new Ifmap.Parser.Json.Delta();
        GraphFactory.getDelta(_connectionName, from, to).success(function (response) {
            _lastUpdateTime = to;
            GraphLayout.addDelta(parser.parse(response));
        });
    }

    function transferGraph(response) {
        var parser = new Ifmap.Parser.Json.Graph();
        var graphs = [];
        response.forEach(function (graph) {
            graphs.push(parser.parse(graph));
        });

        if (graphs.length > 0) {
            _lastUpdateTime = graphs[0].getTimestamp();
            GraphLayout.setGraph(graphs[0]);
        }
    }

    init();
}]);
