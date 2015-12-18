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
 * @author Daniel Huelse
 */
'use strict';

mobile.controller('D3GraphController', ['GraphFactory', '$layout', 'ChangesService', '$scope', '$interval', '$log', 'properties', '$rootScope', function (GraphFactory, $layout, ChangesService, $scope, $interval, $log, properties, $rootScope) {
    var self = this;
    var _connectionName = properties.connection;
    var _lastUpdateTime = 0;
    var _timestamps = [];
    var _run = undefined;

    // startet die Live-Aktualsierung
    $rootScope.$on('live.on', function () {
        startLive();
    });

    // beendet die Live-Aktualsierung
    $rootScope.$on('live.off', function () {
        stopLive();
        setGraph(_timestamps[_timestamps.length - 1]);
        $layout.clear();
    });

    // aktualsiert das Graphen Layout bei Änderung eines Zeitpunkts
    $rootScope.$on('timestamp.change', function (data, timestamp) {
        $layout.clear();
        console.log(data, timestamp);
        setGraph(timestamp);
    });

    // aktualsiert das Graphen Layout bei der Änderung der Connections
    $rootScope.$on('connection.change', function (data, connection) {
        _timestamps = [];
        _lastUpdateTime = 0;
        _connectionName = connection;
        ChangesService.setConnection(connection);
        $layout.clear();
    });

	// Manage static graph data transferred from an incident to vis-mobile
	if(window.loadDefer !== undefined) {
		$log.debug('window.loadDefer !== undefined, static graph data incoming');
		window.dataTransferDefer.promise.then(function(msg) {
			$log.debug('dataTransferDefer resolved, calling transferGraph: ' + msg);

			transferGraph($rootScope.dataFromSiemGui);

			$rootScope.dataFromSiemGui.splice(0, $rootScope.dataFromSiemGui.length);
		});
	}

    /**
     * Initialisiert den D3 Graph Controller
     * @returns {void}
     */
    function init() {
        ChangesService.subscribe(updateChangesSuccess);
        ChangesService.setConnection(_connectionName);
        ChangesService.start();
    }

    /**
     * Beendet die Live-Aktualsierung der Graph Daten
     * @returns {void}
     */
    function stopLive() {
        $log.info("stop live mode");
        $interval.cancel(_run);
    }

    /**
     * Startet die Live-Aktualsierung der Graph Daten, erfragt im Abstand von 1000 ms (Standardwert)
     * den Zustand des IF-MAP Graphen zum aktuellen Zeitpunkt
     * @returns {void}
     */
    function startLive() {
        $log.info("start live mode");
        setCurrentGraph();
        _run = $interval(liveIntervalLoop, properties.graphUpateTime);
    }

    /**
     * Gibt an ob die Live-Aktualisierung aktiv ist
     * @returns {boolean} true wenn Live-Aktualisierung aktiv, andernfalls false
     */
    function isLive(){
        return !angular.isUndefined(_run);
    }

    /**
     * Intervallfunktion wird in regelmäßigen Abständen aufgerufen,
     * benachrichtigt per broadcast-event die D3 Graph View, damit der Visualisierung der Graphen
     * aktualisiert wird
     * @returns {void}
     */
    function liveIntervalLoop() {
        if (_timestamps.length > 0 && _timestamps[_timestamps.length - 1] > _lastUpdateTime) {
            setDelta(_lastUpdateTime, _timestamps[_timestamps.length - 1]);
        }
        $rootScope.$broadcast('graph.transfer');
    }

    /**
     * Aktualsiert die Liste mit Zeitpunkten an den Änderungen am IF-MAP Graphen durchgeführt wurden
     * @param {...number} data - Liste neuen mit Zeitpunkten
     * */
    function updateChangesSuccess(data) {
        $log.debug("call updateChangesSuccess from D3GraphController");
        _timestamps = data;
    }

    /**
     * Stellt eine Anfrage an die REST-Schnittstelle des VisITMeta Dataservices um
     * die Daten des IF-MAP Graphen zum aktuellen Zeitpunkt zu erhalten
     * @returns {void}
     */
    function setCurrentGraph() {
        GraphFactory.getCurrent(_connectionName).success(transferGraph);
    }

    /**
     * Stellt eine Anfrage an die REST-Schnittstelle des VisITMeta Dataservices um
     * die Daten des IF-MAP Graphen zu einem bestimmten Zeitpunkt zu erhalten
     * @param {number} at - Zeitpunkt zu dem die Daten des IF-MAP Graphen aberufen werden
     * @returns {void}
     */
    function setGraph(at) {
        GraphFactory.getAt(_connectionName, at).success(transferGraph);
    }

    /**
     * Stellt eine Anfrage an die REST-Schnittstelle des VisITMeta Dataservices um
     * die Daten des IF-MAP Graphen in einem Zeit-Intervall zu erhalten, liefert ein Delta,
     * mit einer Liste von update und deletes
     * @param {number} from - Zeitpunkt zum Beginn des Intervalls
     * @param {number} to   - Zeitpunkt zum Ende des Intervalls
     * @returns {void}
     */
    function setDelta(from, to) {
        var parser = new Ifmap.Parser.Json.Delta();
        GraphFactory.getDelta(_connectionName, from, to).success(function (response) {
            $layout.addDelta(parser.parse(response));
        });
    }

    /**
     * Aktualisiert das Graph Layout anhand aus der REST-Schnittstelle
     * erfragten Daten zum IF-MAP Graphen
     * @returns {void}
     */
    function transferGraph(response) {
        var parser = new Ifmap.Parser.Json.Graph();
        var graphs = [];

        response.forEach(function (graph) {
            graphs.push(parser.parse(graph));
        });

        if (graphs.length > 0) {
            _lastUpdateTime = graphs[0].getTimestamp();
            $layout.setGraph(graphs[0]);
        }
        $rootScope.$broadcast('graph.transfer');
    }

    init();
}]);
