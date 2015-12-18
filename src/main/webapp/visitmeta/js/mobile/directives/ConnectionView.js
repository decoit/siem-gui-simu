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

mobile.directive('connectionView', ['$log', function ($log) {
    return {
        restrict: 'A',
        controller: "ConnectionController",
        link: function ($scope, $element, $attrs) {
            var connectionsSelect = $($element[0]);

            // legt für jede verfügbare Connection aus der Liste mit Connections, ein option-Element an
            // und fügt es dem Auswahl-Menü der ConnectionView hinzu
            connectionsSelect.append('<option disabled selected>Connections</option>');
            if($scope.connections.length == 0) {
                connectionsSelect.append('<option value="' + $scope.connection + '">' + $scope.connection + '</option>');
            } else {
                $scope.connections.forEach(function(connection) {
                    connectionsSelect.append('<option value="' + connection + '">' + connection + '</option>');
                });
            }

            // registriert ein event, wird ausgelöst wenn die Connection geändert wird
            // übergibt die gewählte Connection der changeConnection-Funktion
            connectionsSelect.on("change",function() {
                var value = connectionsSelect.val();
                $scope.changeConnection(value);
            });
        }
    }
}]);
