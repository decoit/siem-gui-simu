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

mobile.directive('graphPageView', ['$log', '$settings', '$timeout', '$rootScope', function ($log, $settings, $timeout, $rootScope) {
    return {
        restrict: 'A',
        controller: 'GraphPageController',
        link: function ($scope, $element, $attrs) {
            var graphPage = $($element[0]);
            $scope.navBar = $("#navigation");
            $scope.toggleNavBar = $("#toggle-navigation");
            $scope.propertyPopup = $("#properties");
            $scope.propertyList = $("#property-list");
            $scope.propertyHeader = $("#property-header");
            $scope.searchPopup = $("#search");
            $scope.timePopup = $("#time");
            $scope.dialogPopup = $("#dialog");
            $scope.dialogHeader= $("#dialog-header");
            $scope.dialogText = $("#dialog-text");
            $scope.dialogYes = $("#dialog-yes");
            $scope.dialogNo = $("#dialog-no");

            // Nach dem die Graph-Page angelegt wurde, wird das Lifecycle-Event
            // pagecreate aufgerufen, in diesem Zustand werden die folgenden UI-Widgets erstellt:
            graphPage.on("pagecreate", function (event, ui) {
                createDialog();
                createTime();
                createSearch();
                createProperties();
                createNavigationBar();
                createNavigationToggle();
            });
            // Nach dem die Page sichtbar wird, wird das Lifecycle-Event
            // pageshow aufgerufen, daraufhin wird die Navigationsleiste eingeblendet
            graphPage.on("pageshow", function (event, ui) {
                $scope.showNavigation();
                if(!$settings.getNavigationOpen()) {
                    $scope.hideNavigation()
                }
            });

            /**
             * Erstellt das Button der die Navigationsleiste wieder sichbar werden lässt
             * @returns {void}
             */
            function createNavigationToggle() {
                var toggleNavBar = $("#toggle-navigation");
                toggleNavBar.on("click", function () {
                    $scope.showNavigation();
                });
            }

            /**
             * Erstellt das Panel für die Navigationsleiste
             * @returns {void}
             */
            function createNavigationBar() {
                $scope.navBar.panel({
                    position: "left"
                });

//                var home = $("#to-home");
                var time = $("#to-time");
                var visible = $("#to-visibility");
                var help = $("#to-help");
                var search = $("#to-search");
                var settings = $("#to-settings");

//                home.on("click", function () {
//                    changePage("#home", "fade");
//                });
                settings.on("click", function () {
                    changePage("#settings", "pop");
                });
                help.on("click", function () {
                    changePage("#help", "pop");
                });
                visible.on("click", function () {
                    visible.toggleClass("ui-btn-active");
                    $scope.toggleVisibility();
                });
                time.on("click", function () {
                    $scope.showTime();
                });
                search.on("click", function () {
                    $scope.showSearch();
                });
            }

            /**
             * Erstellt das Popup-Fenster für das Anzeigen der Knoten-Eigenschaften
             * @returns {void}
             */
            function createProperties() {
                $scope.propertyPopup.popup({
                    dismissible: true, // workaround: set to "false" prevents instant popup close on android 4.4.2
                    corners: false,
                    transition: "slideup"
                });


            }

            /**
             * Erstellt den Dialog mit der Sicherheitsabfrage zum Reset des Graphens
             * @returns {void}
             */
            function createDialog() {
                $scope.dialogHeader.text("Reset graph?");
                $scope.dialogText.text("Do you want to reset the graph?");

                $scope.dialogPopup.popup({
                    dismissible: true,
                    corners: false,
                    history: false,
                    transition: "pop"
                });

                $scope.dialogPopup.on("popupafteropen", function( event, ui ) {
                    $scope.reset = false;
                });

                $scope.dialogYes.on("click", function() {
                    $scope.reset = true;
                    $scope.dialogPopup.popup("close");
                });

                $scope.dialogNo.on("click", function() {
                    $scope.reset = false;
                    $scope.dialogPopup.popup("close");
                });
            }

            /**
             * Erstellt das Popup-Fenster zur Suche
             * @returns {void}
             */
            function createSearch() {
                $scope.searchPopup.popup({
                    dismissible: true,
                    corners: false,
                    history: false,
                    transition: "slidedown"
                });
            }

            /**
             * Erstellt das Popup-Fenster zur Zeitpunktsauswahl
             * @returns {void}
             */
            function createTime() {
                $scope.timePopup.popup({
                    dismissible: true,
                    corners: false,
                    history: false,
                    transition: "slideup"
                });
            }

            /**
             * Funktion die einen Page wechsel veranlasst
             * @param {Object} page - Seite zu der gewechselt werden soll
             * @param {Object} transition - Animation die abgespielt werden soll, wenn die Page gewchselt wird
             * @returns {void}
             */
            function changePage(page, transition) {
                $.mobile.pageContainer.pagecontainer("change", page, {transition: transition});
            }

        }
    }
}]);
