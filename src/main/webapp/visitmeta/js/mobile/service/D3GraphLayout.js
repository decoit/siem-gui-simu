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
mobile.factory('$layout', ['$log', '$window','$settings', function ($log, $window, $settings) {
    var width = $window.innerWidth;
    var height = $window.innerHeight;

    var nodes = [];
    var edges = [];

    var charge = -2000;
    var linkDistance = 200;

    // legt ein Dummy Element an welches zu Berechnung der Text-Dimensionen
    // bei den Knoten Labels benötigt wird
    var text = d3.select('body')
        .append("svg")
        .append("svg:text");

    // legt das d3 force directed layout an
    var layout = d3.layout.force()
        .nodes(nodes)
        .links(edges)
        .size([width, height])
        .linkDistance(linkDistance)
        .charge(charge);

    /**
     * Erstellt ein Knoten für das D3 Graph Layout anhand der IF-MAP Daten
     * legt nur einen Knoten an, wenn dieser noch nicht existiert
     * @param {Object} data - kann entweder IF-MAP Metadate oder IF-MAP Identidier sein
     * @return {Object} node - gibt das erstellte Knoten-Objekt zurück
     * */
    function addNode(data) {
        var i = nodes.map(function (d) {
            return d.id;
        }).indexOf(data.hashCode());

        if (i == -1) {
            var nodeType = data.getTypeName();
            if (isExtendedIdentifer(data)) {
              nodeType = getExtendedTypeName(data);
            }

            var nodeLabel = createLabel(data);
            var dimension = precalcDimension(nodeLabel);
            var node = {
                id: data.hashCode(),
                label: nodeLabel,
                width: dimension.paddingWidth,
                height: dimension.paddingHeight,
                data: data,
                type: nodeType
            };
            nodes.push(node);
            return node;
        } else {
            return nodes[i];
        }
    }

  function isExtendedIdentifer(data) {
    if ((data.getTypeName() == "identity") && (data.getProperty("type") == "other")) {
        return true;
    } else {
      return false;
    }
  }

	function createLabel(data) {
		var ifmapTypeName = data.getTypeName();

    if (data instanceof Metadata) {
      return ifmapTypeName;
    }

    var properties = data.getProperties();

    switch (ifmapTypeName) {
      case "access-request":
        return ifmapTypeName + ": " + data.getProperty("name");
      case "mac-address":
        return ifmapTypeName + ": " + data.getProperty("value");
      case "ip-address":
        return ifmapTypeName + ": " + data.getProperty("value") + " (" + data.getProperty("type") + ")";
      case "device":
        return ifmapTypeName + ": " + data.getProperty("name");
      case "identity":
        var type = data.getProperty("type");
        var name = data.getProperty("name");
        if (isExtendedIdentifer(data)) {
          var otherTypeDefinition = data.getProperty("other-type-definition");
          var extIdentifierName = getExtendedTypeName(data);
          if (extIdentifierName != "") {
            var content = getFurtherInformation(name);
            if (content != "") {
              return extIdentifierName + ": " + content;
            } else {
              return extIdentifierName;
            }
          } else {
            return ifmapTypeName + ": " + data.getProperty("name") + "(" + otherTypeDefinition + ")";
          }
        } else {
          return ifmapTypeName + ": " + name + " (" + type + ")";
        }
      default:
        return ifmapTypeName;
     }
	}

  function getExtendedTypeName(data) {
    if (data.hasProperty("name")) {
      var name = data.getProperty("name");

      var idxFirstSemicolon = name.indexOf(";");
      if (idxFirstSemicolon != -1) {
        return name.substring(name.indexOf(";") + 1, name.indexOf(" "));
      }
    }

    return "";
  }

  function getFurtherInformation(innerData) {
    var xmlString = deescapeXml(innerData);
    var xmlDoc = parseXml(xmlString);

    var name = extractSingleInformation(xmlDoc, "name");
    var type = extractSingleInformation(xmlDoc, "type");
    var value = extractSingleInformation(xmlDoc, "value");

    var result = name;
    if (type != "") {
      result = result + " (" + type;
      if (value != "") {
        result = result + ", " + value;
      }
       result = result + ")";
    }

    return result;
  }

  function extractSingleInformation(xmlDoc, key) {
    var documentElement = xmlDoc.documentElement;
    var children = xmlDoc.childNodes;

    var result = "";
    for (var i = 0; i < children.length; i++) {
      var node = children[i];
      if (node == key) {
        return node.nodeName;
      }
    }

    if (result == "") {
      result = documentElement.getAttribute(key);
    }

    if (result == null) {
      return "";
    } else {
      return result;
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
     * Erstellt eine ungerichtete Kanten für das D3 Graph Layout,
     * legt nur eine Kante an wenn diese noch nicht exisitiert, dabei werden folgende
     * Kobination geprüft: source-target,target-source
     * @param {Object} source - Quell-Knoten
     * @param {Object} target - Ziel-Knoten
     * @return {void}
     * */
    function createEdge(source, target) {
        var linking1 = edges.map(function (d) {
            return d.source.id + "-" + d.target.id;
        }).indexOf(source.id + "-" + target.id);

        var linking2 = edges.map(function (d) {
            return d.target.id + "-" + d.source.id;
        }).indexOf(source.id + "-" + target.id);

        if (linking1 == -1 && linking2 == -1) {
            $log.debug(source.label + "-" + target.label);
            edges.push({source: source, target: target});
        }7
    }


    /**
     * Erstell aus einen IF-MAP Link, entsprechende Kanten und Knoten des D3 Graph Layours
     * @param {Object} link - IF-MAP Link
     * @return {void}
     * */
    function addLink(link) {
        var identifers = link.getIdentifiers();
        var metadatums = link.getMetadatas();

        var node1 = addIdentifier(identifers[0]);
        var node2 = undefined;

        if (identifers.length == 2) {
            node2 = addIdentifier(identifers[1]);
        }

        metadatums.forEach(function (metadata) {
            var node = addMetadata(metadata);
            createEdge(node1, node);
            if (!angular.isUndefined(node2)) {
                createEdge(node, node2);
            }
        });
    }

    /**
     * Erstell einen Metadata-Knoten
     * @param {Object} metadata - IF-MAP Metadata
     * @return {void}
     * */
    function addMetadata(metadata) {
        $log.info("add metadata: " + metadata.getTypeName() + " " + metadata.hashCode());
        return addNode(metadata);
    }

    /**
     * Erstell einen Identifier-Knoten
     * @param {Object} identifier - IF-MAP Identifier
     * @return {void}
     * */
    function addIdentifier(identifier) {
        $log.info("add identifier: " + identifier.getTypeName() + " " + identifier.hashCode());
        return addNode(identifier);
    }

    /**
     * Funktion zur Berechnung der Text-Dimensionen
     * @param {string} string - Zeichkette zur der die Text-Höhe und Text-Breite bestimmt werden soll
     * @return {Object} gibt ein Objekt mit den Text-Dimensionen zurück, mit ursprüngglichen Dimensionen und mit einem festen Offer
     * */
    function precalcDimension(string) {
        text.attr("font-family", $settings.getNodeFont())
            .attr("font-size", ""+$settings.getNodeFontSize() + "px")
            .attr("text-anchor", "middle")
            .text(string);
        var bBox = text.node().getBBox();
        return {
            width: bBox.width,
            height: bBox.height,
            paddingWidth: bBox.width + 40,
            paddingHeight: bBox.height + 15
        };
    }

    function removeLink(link) {

    }

    return {
        /**
         * Ersetzt einen vorhanden IF-MAP Graph durch einen neuereren
         * @param {Object} graph - IF-MAP Graph Datan
         * @return {void}
         * */
        setGraph: function (graph) {
            this.clear();
            this.addGraph(graph);
        },

        /**
         * Fügt einen vorhanden IF-MAP Graph einem bestehenden hinzu
         * @param {Object} graph - IF-MAP Graph Datan
         * @return {void}
         * */
        addGraph: function (graph) {
            graph.getLinks().forEach(function (link) {
                addLink(link);
            });
        },

        /**
         * Aktualisiert den Graphen anhand der updates und deletes des Deltas
         * @param {Object} delta - Enthält updates und deletes zum IF-MAP Graphen
         * @return {void}
         * */
        addDelta: function (delta) {
            delta.getUpdates().forEach(function (change) {
                change.getLinks().forEach(function (link) {
                    addLink(link);
                });
            });

            delta.getDeletes().forEach(function (change) {
                change.getLinks().forEach(function (link) {
                    removeLink(link);
                });
            });
        },

        /**
         * Gibt den aktuellen D3 Graph zurück
         * @return {Object} graph - enthält die Daten zu den Knoten und Kanten des Grapghen
         * */
        getGraph: function() {
            return {nodes: nodes, edges: edges};
        },

        /**
         * Gibt die Anzahl der im D3 Graphen vorhanden Knoten zurück
         * @return {number} node count - Anzahl der Knoten
         * */
        getNodeCount: function() {
            return nodes.length;
        },

        /**
         * Gibt die Anzahl der im D3 Graphen vorhanden Kanten zurück
         * @return {number} node count - Anzahl der Kanten
         * */
        getEdgeCount: function() {
            return edges.length;
        },

        /**
         * Gibt das D3 Graphen Layout zurück
         * @return {Object} layout - D3 Graphen Layout Objekt
         * */
        getForceLayout: function() {
            return layout;
        },

        /**
         * Löscht alle Knoten und Kanten des D3 Graphen Layout
         * */
        clear: function () {
            nodes = [];
            edges = [];
            layout.nodes(nodes);
            layout.links(edges);
        },

        /**
         * Registiert eine Handler-Funktion die pro Simulations Schritt des Force-Directed Layout ausgeführt wird
         * @param {Object} handler - Funktion welcher pro Simulations Schritt des Force-Directed Layout ausgeführt wird
         * */
        onTick: function (handler) {
            layout.on("tick", handler);
        },

        /**
         * Startet das D3 Graphen Layout
         * */
        start: function () {
            layout.start();
        },

        /**
         * Hält  das D3 Graphen Layout an
         * */
        stop: function () {
            layout.stop();
        },

        /**
         * Startet das D3 Graphen Layout neu
         * */
        resume: function () {
            layout.resume();
        }

    };
}]);
