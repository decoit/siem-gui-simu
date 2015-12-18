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

mobile.directive('timeView', ['$log','$rootScope','$filter', function ($log, $rootScope, $filter) {
    return {
        restrict: 'A',
        controller: "TimeController",
        link: function ($scope, $element, $attrs) {
            var timePopup = $($element[0]);
            var value = 0;

            // Nach dem das Popup angelegt wurde, wird das Lifecycle-Event
            // popupcreate aufgerufen, in diesem Zustand werden die UI-Widgets für
            // den Schieberegler angelegt
            timePopup.on("popupcreate", function( event, ui ) {
                createTimeSlider();
            });
            // Nach dem das Popup sichtbar wird, wird das Lifecycle-Event
            // popupafteropen aufgerufen, dabei wird der Schieberegler aktualisiert
            timePopup.on("popupafteropen", function( event, ui ) {
                refreshSlider();
            });

            /**
             * Aktualisert den Schieberegler
             * @return {void}
             * */
            function refreshSlider() {
                var max = $scope.getTimestampMax();
                $scope.timeSlider.prop({
                    min: 0,
                    max: max,
                    step: 1,
                    value: value
                }).slider("refresh");
            }

            /**
             * Erstellt einen Schieberegler zur Zeitpunkts-Auswahl und eine Anzeige für die
             * Darstellung der Zeitpunkte
             * @return {void}
             * */
            function createTimeSlider() {
                $scope.timeSlider = $("#time-slider");
                $scope.timestamp = $("#timestamp");
                $scope.timeSlider.on("change",function() {
                    value = $scope.timeSlider.val();
                    $scope.onTimestampChanged(value);
                });
            }

        }
    }
}]);
