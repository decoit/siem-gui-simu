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

mobile.controller('GraphPageController', ['$window', '$scope', '$log', '$timeout', '$settings', function ($window, $scope, $log, $timeout, $settings) {
    $scope.navigationVisiblity = false;
    $scope.nodeVisibility = true;

    /**
     * Gibt an ob die Live-Aktualisierung Schalter aktiviert bzw. dekativert ist
     * @returns {boolean} true wenn der Live-Aktualisierung Schalter aktiviert  andernfalls false
     */
    $scope.isLiveOn = function () {
        return $scope.liveSwitch.prop("checked");
    };

    /**
    * aktiviert bzw. deaktivert das Hervorheben von Knoten
    * aktualsiert das nodeVisibility-Property des scope-Objekts,
    * benachrichtigt watch-Funktionen die das nodeVisibility-Property beobachten
    * @returns {void}
    */
    $scope.toggleVisibility = function () {
        $scope.nodeVisibility = !$scope.nodeVisibility;
    };

    /**
     * zeigt die Navigationsleiste an setzt das navigationVisiblity-Property
     * des scope-Objekts auf true, signalisiert das die Navigationsleiste momentan sichtbar ist,
     * benachrichtigt watch-Funktionen die das navigationVisiblity- beobachten
     * @returns {void}
     */
    $scope.showNavigation = function () {
        $scope.navBar.panel("open");
        $scope.navigationVisiblity = true;
    };

    /**
     * schließt die Navigationsleiste nach einer gewissen Zeit (Standard: 15 Sekunden, kann in den Einstellungen geändert werden)
     * an setzt das navigationVisiblity-Property des scope-Objekts auf false,
     * signalisiert das die Navigationsleiste momentan nicht sichtbar ist,
     * benachrichtigt watch-Funktionen die das navigationVisiblity- beobachten
     * @returns {void}
     */
    $scope.hideNavigation = function () {
        var time = $settings.getNavigationTimeout();
        $timeout(function () {
            $scope.navBar.panel("close");
        }, time);
        $scope.navigationVisiblity = false;
    };

    /**
     * Gibt an ob Navigationsleiste momentan sichtbar sit
     * @returns {boolean} true wenn sichtbar, andernfalls false
     */
    $scope.isNavigationVisible = function () {
        return $scope.navigationVisiblity;
    };

    /**
     * Erstellt das Eigenschaften-Fenster anhand des ausgewählten Knotens und zeigt es an
     * @param {Object} node - Knoten von dem die Eigenschaften angezeigt werden sollen
     * @returns {void}
     */
    $scope.showProperties = function (node) {
        var nodeData = node.data;
        var title;

        if (nodeData instanceof Identifier) {
          if (isExtendedIdentifer(nodeData)) {
            title = "Extended Identifier: " + getExtendedIdentifierTypeName(nodeData);
          } else {
            title = "Identifier: " + nodeData.getTypeName();
          }
        } else {
            title = "Metadata: " + nodeData.getTypeName();
        }

        $scope.propertyList.empty();
        $scope.propertyList.append("<thead>");
        $scope.propertyList.append("<tr class=\"th-groups\"><th colspan=\"2\" style=\"text-align:center\">" + title + "</th></tr>");
        $scope.propertyList.append("<tr class=\"ui-bar-d\"><th>Property</th><th>Value</th></tr>");
        $scope.propertyList.append("</thead>");
        $scope.propertyList.append("<tbody>");

        if (isExtendedIdentifer(nodeData)) {
          appendExtendedIdentifierProperties(nodeData);
        } else {
          appendProperties(nodeData);
        }

        $scope.propertyList.append("</tbody>");

        $scope.propertyPopup.popup("open", {
            x: ($window.innerWidth / 2),
            y: $window.innerHeight
        });
    };

    function appendProperties(data) {
      for (var property in data.getProperties()) {
        if (data.hasProperty(property)) {
            appendProperty(property, data.getProperty(property));
        }
      }
    }

    function isExtendedIdentifer(data) {
      if ((data.getTypeName() == "identity") && (data.getProperty("type") == "other")) {
          return true;
      } else {
        return false;
      }
    }

    function getExtendedIdentifierTypeName(data) {
      var name = data.getProperty("name");
      var idxFirstSemicolon = name.indexOf(";");
      if (idxFirstSemicolon != -1) {
        return name.substring(name.indexOf(";") + 1, name.indexOf(" "));
      } else {
        return data.getTypeName();
      }
    }

    function appendExtendedIdentifierProperties(data) {
      var type = data.getProperty("type");
      var otherTypeDefinition = data.getProperty("other-type-definition");
      var name = data.getProperty("name");

      var idxFirstSemicolon = name.indexOf(";");
      if (idxFirstSemicolon != -1) {
        var xmlString = deescapeXml(name);
        var xmlDoc = parseXml(xmlString);

        var root = xmlDoc.documentElement;

        treeWalk(root, 0);
      } else {
        appendProperty("type", name);
        appendProperty("other-type-definition", otherTypeDefinition);
      }
    }

    function appendProperty(key, value) {
      $scope.propertyList.append("<tr><td>" + key + "</td><td>" + value + "</td></tr>");
    }

    function treeWalk(node, level) {
      var childNode;
      var value;

      var children = node.childNodes;
      if (children.length > 0) {
        level++;
        for (var i = 0; i < children.length; i++) {
          childNode = node.children[i];

          if (childNode.nodeType == "#text") {
            value = childNode.nodeValue;
            if (value) {
              appendProperty(childNode.nodeName, value);
            }
          } else {
            handleAttributes(childNode);
            treeWalk(childNode, level);
          }
        }
      } else {
        handleAttributes(node);
      }
    }

    function handleAttributes(node) {
      var attributes = node.attributes;
      var attribute;
      if (attributes.length > 0) {
        for (var i = 0; i < attributes.length; i++) {
          attribute = attributes[i];
          appendProperty(attribute.nodeName, attribute.nodeValue);
        }
      }
    }

    function deescapeXml(escapedXML) {
      var result = escapedXML;

      var unwanted = ["&amp;", "&lt;", "&gt;", "&quot;", "&apos;"];
      var replaceBy = ["&", "<", ">", "\"", "'"];

      for (var i = 0; i < unwanted.length; i++) {
        result = result.replace(new RegExp(unwanted[i], 'g'), replaceBy[i]);
      }

      return result;
    }

    function parseXml(xmlString) {
      if (window.DOMParser)
      {
        parser = new DOMParser();
        return xmlDoc = parser.parseFromString(xmlString, "text/xml");
      }
      else // code for IE
      {
        xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async = false;
        return xmlDoc.loadXML(xmlString);
      }
    }

    /**
     * Zeigt das Fenster mit Zeitpunktsauswahl an
     * @returns {void}
     */
    $scope.showTime = function () {
        $scope.timePopup.popup("open", {
            x: ($window.innerWidth / 2),
            y: $window.innerHeight
        });
    };

    /**
     * Schließt das Fenster mit Zeitpunktsauswahl
     * @returns {void}
     */
    $scope.closeTime = function () {
        $scope.timePopup.popup("close");
    };

    /**
     * Zeigt das Fenster mit der Suche an
     * @returns {void}
     */
    $scope.showSearch = function () {
        $scope.searchPopup.popup("open", {
            x: ($window.innerWidth / 2),
            y: 200
        });
    };

    /**
     * Schließt das Fenster mit der Suche
     * @returns {void}
     */
    $scope.closeSearch = function () {
        $scope.searchPopup.popup("close");
    };

    /**
     * Zeigt den Dialog mit der Sicherheitsfrage zum
     * Reseteten des Graphen an
     * @returns {void}
     */
    $scope.showDialog = function () {
        $scope.dialogPopup.popup("open");
    };

    /**
     * watch-Funktion welche dafür zuständig ist die Navigationsleiste nach einer gewissen Zeit
     * zu schlißen falls diese sichtbar ist und die Option zum dauerhaften Anzeigen deaktiviert ist
     * @returns {void}
     */
    $scope.$watch('navigationVisiblity', function (visible) {
        if ((visible == true) && !$settings.getNavigationOpen()) {
            $scope.hideNavigation();
        }
    });

}]);
