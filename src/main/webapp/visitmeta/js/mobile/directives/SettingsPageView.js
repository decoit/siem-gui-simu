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
 * @author Bastian Hellmann
 */
mobile.directive('settingsPageView', ['$log','$settings','$window',function ($log, $settings) {
    return {
        restrict: 'A',
        controller: 'SettingsPageController',
        link: function ($scope, $element, $attrs) {
            var settingsPage = $($element[0]);
            var fonts = ["Arial","Times","Verdana"];
            var fontSizes = [8, 9, 10, 11, 12, 14, 16, 18, 20, 22];
            var timeouts = [1000, 2000, 3000, 5000, 10000, 15000, 30000];
            var linkTypes = ["curved","straight"];

            // wird von der d3 graph view beobachtet um zu ermitteln ob Knoten neugezeichnet werden müssen
            // 0 = init, 1 = saved, 2 = reset
            $scope.changes = 0;
            $scope.save = $("#save-settings");
            $scope.reset = $("#reset-settings");
            $scope.navTimeout = $("#nav-timeout-select");
            $scope.navKeepOpen = $("#nav-keep-open");
            $scope.identifierColor = $("#identifier-color-select");
            $scope.metadataColor = $("#metadata-color-select");
            $scope.selectionColor = $("#selection-color-select");
            $scope.searchColor = $("#search-color-select");
            $scope.nodeFont = $("#node-font-select");
            $scope.nodeFontSize = $("#node-font-size-select");
            $scope.linkType = $("#edge-linktype");

            // Nach dem die Settings-Page angelegt wurde, wird das Lifecycle-Event
            // pagecreate aufgerufen, in diesem Zustand werden die folgenden UI-Widgets erstellt:
            settingsPage.on("pagecreate", function(event, ui ) {
                createStatusPopup();
                createGraphSettings();
                createInterfaceSettings();
            });

            // Nach dem die Settings-PPage sichtbar wird, wird das Lifecycle-Event
            // pageshow aufgerufen, werden Events für das speichern und reseten der Einstellungen angelegt
            settingsPage.on("pageshow", function(event, ui ) {
                $scope.save.on("click", function () {
                    $scope.onSave();
                    $scope.showStatusPopup("Settings saved.");
                });
                $scope.reset.on("click", function () {
                    $scope.onReset();
                    $scope.showStatusPopup("Settings reseted.");
                });
            });

            /**
             * Erstellt alle Einträge zu den Interface-Einstellungen
             * @returns {void}
             */
            function createInterfaceSettings() {
                // definiert den Schalter über den bestimmt werden kann ob die
                // Navigationsleiste dauerhaft zu sehen ist.
                $scope.navKeepOpen.prop("checked", $settings.getNavigationOpen());
                $scope.navKeepOpen.flipswitch( "refresh" );

                // erstellt Auswahloptionen zur Timeout-Option
                timeouts.forEach(function(timeout) {
                    $scope.navTimeout.append('<option value="' + timeout + '">' + (timeout / 1000) + ' sec. </option>');
                });
                $scope.navTimeout.val($settings.getNavigationTimeout()).attr('selected', true);
                $scope.navTimeout.selectmenu("refresh");
                $scope.navTimeout.selectmenu("disable");

                // deaktivert die Timeout-Option wenn eingestellt wurde das die
                // Navigationsleiste dauerheft sichbar sein soll
                $scope.navKeepOpen.on("change", function() {
                    var checked = $scope.navKeepOpen.prop("checked");
                    if(checked == true) {
                        $scope.navTimeout.selectmenu("disable");
                    } else {
                        $scope.navTimeout.selectmenu("enable");
                    }
                });

            }

            /**
             * Erstellt alle Einträge zu den Graph-Einstellungen
             * @returns {void}
             */
            function createGraphSettings() {
                // definiert Farbauswahl felder
                $scope.identifierColor.spectrum({
                    preferredFormat: "hex3",
                    color: $settings.getIdentifierColor()
                });
                $scope.metadataColor.spectrum({
                    preferredFormat: "hex3",
                    color: $settings.getMetadataColor()
                });
                $scope.selectionColor.spectrum({
                    preferredFormat: "hex3",
                    color: $settings.getSelectionColor()
                });
                $scope.searchColor.spectrum({
                    preferredFormat: "hex3",
                    color: $settings.getSearchColor()
                });

                // erstellt Auswahloptionen zur Schriftart und Schriftgröße
                fonts.forEach(function(font) {
                    $scope.nodeFont.append('<option value="' + font + '">' + font + '</option>');
                });
                $scope.nodeFont.val($settings.getNodeFont()).attr('selected', true);
                $scope.nodeFont.selectmenu("refresh");

                fontSizes.forEach(function(size) {
                    $scope.nodeFontSize.append('<option value="' + size + '">' + size + '</option>');
                });
                $scope.nodeFontSize.val($settings.getNodeFontSize()).attr('selected', true);
                $scope.nodeFontSize.selectmenu("refresh");

                // erstellt Auswahloption zur Darstellungsform von Kanten
                linkTypes.forEach(function(type) {
                  $scope.linkType.append('<option value="' + type + '">' + type + '</option>');
                });
                $scope.linkType.val($settings.getLinkType()).attr('selected', true);
                $scope.linkType.selectmenu("refresh");
            }

            /**
             * Erstellt das Popup das eine Statusmeldung anzeigt, ob die Daten gespeichert wurden
             * @returns {void}
             */
            function createStatusPopup() {
                $scope.statusPopup = $("#status");
                $scope.statusPopup.popup("option","positionTo", "origin");
                $scope.statusText = $("#status-text");
            }
        }
    }
}]);
