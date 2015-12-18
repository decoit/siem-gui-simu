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

mobile.directive('searchView', ['$log', function ($log) {
    return {
        restrict: 'A',
        controller: "SearchController",
        link: function ($scope, $element, $attrs) {
            var searchPopup = $($element[0]);
            var types = ["type","name","value"];
            $scope.search = {value: "", type: ""};

            // Nach dem das Popup angelegt wurde, wird das Lifecycle-Event
            // popupcreate aufgerufen, in diesem Zustand werden die UI-Widgets für
            // das Suchfeld und den Suchtyp angelegt
            searchPopup.on("popupcreate", function( event, ui ) {
                createSearchType();
                createSearchInput();
            });
            // Nach dem das Popup sichtbar wird, wird das Lifecycle-Event
            // popupafteropen aufgerufen,
            searchPopup.on("popupafteropen", function( event, ui ) {

            });

            /**
             * Legt ein Suchfeld für die Eingabe des Suchkriterium an und registriert ein event
             * welches ausgelöst wird, wenn etwas in das Suchfeld eigegeben wird
             * @return {void}
             * */
            function createSearchInput() {
                var searchInput = $("#search-input");
                var searchType = $("#search-type");
                searchInput.on("change", function() {
                    var value = searchInput.val();
                    var type = searchType.val();
                    if(value !== "") {
                        $scope.onSearch(value, type);

                        // schliesst die Virtuelle Tastatur nach der Eingabe
                        searchInput.blur();
                        $scope.closeSearch();
                    }

                });
            }

            /**
             * Legt ein Auswahl-Menü mit dem Suchtyp an
             * legt für jeden Suchtype ein option-Element an und fügt
             * es dem Auswahl-Menü hinzu
             * @return {void}
             * */
            function createSearchType() {
                var searchType = $("#search-type");
                types.forEach(function(type) {
                    searchType.append('<option value="' + type + '">' + type + '</option>');
                });
                searchType.selectmenu("refresh");
            }
        }
    }
}]);
