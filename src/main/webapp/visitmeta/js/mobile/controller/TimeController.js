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

mobile.controller('TimeController', ['$scope', '$log', '$rootScope', 'ChangesService','properties', function ($scope, $log, $rootScope, ChangesService, properties) {
    var self = this;
    var _timestamps = [];

    /**
     * Initialisiertung: registriert updateSuccess-Funktion bei ChangesService und startet diesen
     * im Anschluss
     * */
    this.init = function () {
        ChangesService.subscribe(this.updateSuccess);
        ChangesService.setConnection(properties.connection);
        ChangesService.start();
    };

    /**
     * Aktualsiert die Liste mit Zeitpunkten an den Änderungen am IF-MAP Graphen durchgeführt wurden
     * @param {...number} timestamps - Liste neuen mit Zeitpunkten
     * */
    this.updateSuccess = function (timestamps) {
        $log.debug("call updateSuccess from TimestampSliderController");
        _timestamps = timestamps;
        $log.debug("timestamps are updated");
    };

    /**
     * Gibt den letzten Zeitpunkt, aus der Liste der vorhanden Zeitpunkte, wieder
     * @return {number} timestamps - letzter/neuster verfügbarer Zeitpunkt
     * */
    $scope.getTimestampMax = function() {
        return _timestamps.length - 1;
    };

    /**
     * Gibt die Liste mit Zeitpunkten zurück
     * @return {...number} timestamps - Liste mit allen verfügbaren Zeitpunkten
     * */
    $scope.getTimestamps = function() {
        return _timestamps;
    };

    /**
     * Funktion wird ausgeführt wenn der Schieberegeler zur Zeitpunkts auswahl bewegt wird
     * anhand der Schieberegeler Position wird ein Zeitpunkt ausgewählt: z. B. 0 = Schiebereglerposition (ganz links)
     * Auswahl des ersten Zeitpunks aus der Liste mit den Zeitpunkten: timestamps[0]
     * Löst ein broadcast-event aus welches bescheid gibt, das sich der ausgewählte Zeitpunkt geändert hat
     * @param {number} value - enthält die aktulle Position des Schiebereglers
     * @return {void}
     * */
    $scope.onTimestampChanged = function(value) {
        $log.debug("Timestamp: "+value);
        var timestamp = _timestamps[value];
        updateTimestamp(timestamp);
        $rootScope.$broadcast('timestamp.change', timestamp);
    };

    // deaktiviert den Schieberegler zur Zeitpunktsauswahl, wenn die Live-Aktualisierung aktivier ist
    $rootScope.$on('live.on', function () {
        $scope.timeSlider.slider('disable');
    });

    // aktivier den Schieberegler zur Zeitpunktsauswahl, wenn die Live-Aktualisierung deaktiviert ist
    $rootScope.$on('live.off', function () {
        $scope.timeSlider.slider('enable');
    });

    /**
     * Aktualisiert das UI-Widget zum Anzeigen des gewählten Zeitpunkts
     * @param {number} timestamp - Zeitpunkt als Unix Timestamp repräsentation
     * @return {void}
     * */
    function updateTimestamp(timestamp) {
        var dateTime = new Date(+timestamp);
        $scope.timestamp.val(dateTime.toLocaleDateString() + " " +dateTime.toLocaleTimeString());
    }

    this.init();
}]);
