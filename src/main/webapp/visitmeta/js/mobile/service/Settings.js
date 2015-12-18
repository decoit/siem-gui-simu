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
'use strict';

mobile.factory('$settings', ['$window', function($window) {
    var identColor = "#9999FF";
    var metaColor = "#FF9966";
    var selColor = "#CEB100";
    var searchColor = "#88A538";
    var navTimeout = 15000;
    var navKeepOpen = true;
    var nodeFont = "Verdana";
    var nodeFontSize = 12;
    var linkType = "curved";
    var publisherIds = {};

    var dhcp = "dhcp-4ebd6876-f83e-4541-9e71-18f60a6026b8";
    var pdp = "pdp-9bd857dd-68e8-4547-a882-64a88c607abf";
    var test = "test-d0537336-41c5-46e2-a06e-f00abd4fdc08";
    var openvas = "";
    var nmap = "";
    var irongpm = "";
    var radius = "";
    var snort = "";
    var iptables = "";

    // http://www.colourlovers.com/palette/168661/Where,_Oh,_Where
    // http://www.colourlovers.com/palette/519318/Crazy_People
    // http://www.colourlovers.com/palette/650547/Mymesis

    setColorForPublisherId(dhcp, "#F7C2AB");
    setColorForPublisherId(pdp, "#F7F4AD");
    setColorForPublisherId(test, "#96D6CE");

    function setColorForPublisherId(publisherId, color) {
        publisherIds[publisherId] = color;
    }

    return {
        /** Getter */

        /**
         * Gibt Farbwert für Identifier zurück, Rückgabe als Hexadezimal String z. B. '#FFFFFF'
         * @return {string} Farbwert für Identifier
         * */
        getIdentifierColor: function() {
            var color = localStorage.getItem("mobile:identifier:color");
            if(color) {
                return color;
            }
            return identColor;
        },
        /**
         * Gibt Farbwert für Metadata zurück, Rückgabe als Hexadezimal String z. B. '#FFFFFF'
         * @return {string} Farbwert für Metadata
         * */
        getMetadataColor: function(data) {
            if (data != undefined) {
              var publisherId = data.getProperty("ifmap-publisher-id");
              if (publisherId in publisherIds) {
                return publisherIds[publisherId];
              }
            }

            var color = localStorage.getItem("mobile:metadata:color");
            if (color) {
                return color;
            }

            return metaColor;
        },

        /**
         * Farbwet bestimmt sich über angegebene Knoten
         * @param {Object} data - kann entweder vom Typ IF-MAP Identifier oder IF-MAP Metadata sein
         * @return {string} Farbwert für Identifier (falls der data von Typ IF-MAP Identifier ist),
         *              ansonsten rückgabe des Farbwerts für Metadata
         * */
        getNodeColor: function(data) {
            if (data instanceof Identifier) {
                return this.getIdentifierColor();
            }
            return this.getMetadataColor(data);
        },

        /**
         * Gibt Farbwert für ausgewählte Knoten zurück, Rückgabe als Hexadezimal String z. B. '#FFFFFF'
         * @return {string} Farbwert für ausgewählte Knoten
         * */
        getSelectionColor: function() {
            var color = localStorage.getItem("mobile:selection:color");
            if(color) {
                return color;
            }
            return selColor;
        },

        /**
         * Gibt Farbwert für gefundene Knoten bei einer Suchanfrage, Rückgabe als Hexadezimal String z. B. '#FFFFFF'
         * @return {string} Farbwert für gefundene Knoten bei einer Suchanfrage
         * */
        getSearchColor: function() {
            var color = localStorage.getItem("mobile:search:color");
            if(color) {
                return color;
            }
            return searchColor;
        },

        /**
         * Gibt die Schriftart zurück, welche zur Beschriftung der Knoten genutzt wird
         * @return {string} Schriftart z. B. Arial
         * */
        getNodeFont: function() {
            var font = localStorage.getItem("mobile:node:font");
            if(font) {
                return font;
            }
            return nodeFont;
        },

        /**
         * Gibt die Schriftgröße verwendeten der Schriftart zurück, welche zur beschriftung der Knoten genutzt wird
         * @return {number} Schriftgröße
         * */
        getNodeFontSize: function() {
            var fontSize = localStorage.getItem("mobile:node:size");
            if(fontSize) {
                return parseInt(fontSize);
            }
            return nodeFontSize;
        },

        /**
         * Gibt Zeit bis zum Ausblenden der Navigationleisre zurück
         * @return {number} timeout (in ms)
         * */
        getNavigationTimeout: function() {
            var timeout = localStorage.getItem("mobile:nav:timeout");
            if(timeout) {
                return parseInt(timeout);
            }
            return navTimeout;
        },

        /**
         * Gibt an ob die Navigationsleiste dauerhaft sichtbar sein soll oder nicht
         * @return {boolean} kann true oder false sein
         * */
        getNavigationOpen: function() {
            var keep = localStorage.getItem("mobile:nav:keepopen");
            if(keep) {
                return keep == "true";
            }
            return navKeepOpen;
        },

        /**
         * Gibt an wie Kanten gezeichnet werden sollen
         * @return {string} Darstellungstyp der Kanten
         * */
        getLinkType: function() {
          var type = localStorage.getItem("mobile:edge:linktype");
          if(type) {
              return type;
          }
          return linkType;
        },

        /** Setter */

        /**
         * Setzt den Farbwert für Identifier, Angabe als Hexadezimal String z. B. '#FFFFFF'
         * @param {string} color - Farbwert für Identifier
         * @return {void}
         * */
        setIdentifierColor: function(color) {
            localStorage.setItem("mobile:identifier:color", color);
        },

        /**
         * Setzt den Farbwert für Metadata, Angabe als Hexadezimal String z. B. '#FFFFFF'
         * @param {string} color - Farbwert für Metadata
         * @return {void}
         * */
        setMetadataColor: function(color) {
            localStorage.setItem("mobile:metadata:color", color);
        },

        /**
         * Setzt den Farbwert für ausgewählte Knoten, Angabe als Hexadezimal String z. B. '#FFFFFF'
         * @param {string} color - Farbwert für ausgewählte Knoten
         * @return {void}
         * */
        setSelectionColor: function(color) {
            localStorage.setItem("mobile:selection:color", color);
        },

        /**
         * Setzt den Farbwert für gefundene Knoten einer Suchanfrage, Angabe als Hexadezimal String z. B. '#FFFFFF'
         * @param {string} color - Farbwert für gefundene Knoten einer Suchanfrage
         * @return {void}
         * */
        setSearchColor: function(color) {
            localStorage.setItem("mobile:search:color", color);
        },

        /**
         * Setzt die Schriftart, welche zur beschriftung der Knoten genutzt wird
         * @param {string} font - Schriftart z. B. Arial
         * */
        setNodeFont: function(font) {
            localStorage.setItem("mobile:node:font", font);
        },

        /**
         * Setzt die Schriftgröße verwendeten der Schriftart, welche zur Beschriftung der Knoten genutzt wird
         * @param {number} size - Schrifgröße
         * */
        setNodeFontSize: function(size) {
            localStorage.setItem("mobile:node:size", size.toString());
        },

        /**
         * Setzt die Zeit bis zum Ausblenden der Navigationleiste
         * @param {number} timeout - Zeit bis zur Aublendung der Navigationleiste (in ms)
         * */
        setNavigationTimeout: function(timeout) {
            localStorage.setItem("mobile:nav:timeout",timeout.toString());
        },

        /**
         * Setzt ein Flag welches darüber Auskunft gibt ob die Navigationsleiste dauerhaft zu sehen ist
         * @param {boolean} keep - wenn false wird Navigationsleiste ausgeblendet, andernfals bleibt sie sichtbar
         * */
        setNavigationKeepOpen: function(keep) {
            localStorage.setItem("mobile:nav:keepopen",keep.toString());
        },

        /**
         * Setzt den Darstellungstyp der Kanten
         * @param {string} linktype - Darstellungstyp, z.B. curved
         * */
        setNavigationKeepOpen: function(linktype) {
            localStorage.setItem("mobile:edge:linktype",linktype);
        },

        /**
         * Setzt die Einstellungen auf ihre Standardwerte zurück
         * @return {void}
         * */
        resetSettings: function() {
            localStorage.setItem("mobile:identifier:color", identColor);
            localStorage.setItem("mobile:metadata:color", metaColor);
            localStorage.setItem("mobile:selection:color", selColor);
            localStorage.setItem("mobile:search:color", searchColor);
            localStorage.setItem("mobile:nav:timeout", navTimeout.toString());
            localStorage.setItem("mobile:node:font", nodeFont);
            localStorage.setItem("mobile:node:size", nodeFontSize.toString());
            localStorage.setItem("mobile:nav:keepopen",navKeepOpen.toString());
            localStorage.setItem("mobile:edge:linktype", linkType.toString());
        }
    };
}]);
