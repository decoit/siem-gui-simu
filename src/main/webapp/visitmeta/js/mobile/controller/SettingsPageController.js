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

mobile.controller('SettingsPageController', ['$scope', '$log', '$settings', function ($scope, $log, $settings) {
    /**
     * Speichert die Einstellungen
     * @returns {void}
     */
    $scope.onSave = function() {
        $settings.setIdentifierColor($scope.identifierColor.spectrum("get"));
        $settings.setMetadataColor($scope.metadataColor.spectrum("get"));
        $settings.setSelectionColor($scope.selectionColor.spectrum("get"));
        $settings.setSearchColor($scope.searchColor.spectrum("get"));
        $settings.setNodeFont($scope.nodeFont.val());
        $settings.setNodeFontSize($scope.nodeFontSize.val());
        $settings.setNavigationTimeout($scope.navTimeout.val());
        $settings.setNavigationKeepOpen($scope.navKeepOpen.prop('checked'));

        $log.debug("Settings saved");
        $scope.changes = 1;
    };

    /**
     * Setzt die Einstellungen zurück
     * @returns {void}
     */
    $scope.onReset = function() {
        $settings.resetSettings();

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

        $scope.nodeFont.val($settings.getNodeFont()).attr('selected', true);
        $scope.nodeFont.selectmenu("refresh");

        $scope.nodeFontSize.val($settings.getNodeFontSize()).attr('selected', true);
        $scope.nodeFontSize.selectmenu("refresh");

        $scope.navTimeout.val($settings.getNavigationTimeout()).attr('selected', true);
        $scope.navTimeout.selectmenu("refresh");

        $scope.changes = 2;
        $log.debug("Settings reseted");
    };

    /**
     * Zeigt ein Status Popup an das Auskunft darüber gibt ob die Einstellungen gespeichert wurden
     * @param {string} text - Text mit der Beschreibung zur Statusmeldung
     * @returns {void}
     */
    $scope.showStatusPopup = function(text) {
        $scope.statusText.text(text);
        $scope.statusPopup.popup("open");
    }
}]);
