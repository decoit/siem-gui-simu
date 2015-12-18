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

mobile.directive('d3GraphView', ['$window', '$timeout', '$log', '$rootScope', '$settings', '$layout', function ($window, $timeout, $log, $rootScope, $settings, $layout) {
    function init($scope, $element, $attrs) {
		var header = 2 * $('div[data-role="header"]').outerHeight();
		var width = $window.innerWidth;
		var height = $window.innerHeight - header;

    var node = null, edge = null, label = null;

		var lastNodeCount = 0;
		var lastEdgeCount = 0;

		var centerX = width / 2;
		var centerY = height / 2;
		var posX = 0, posY = 0, lastX = 0, lastY = 0;
		var currentScale = 1.0;

    var zoom = d3.behavior.zoom()
      .on("zoom", rescale);

    var drag = d3.behavior.drag()
      .origin(function(d) { return d; })
      .on("dragstart", dragstarted)
      .on("drag", dragged)
      .on("dragend", dragended);

		var svg = d3.select($element[0])
			.append("svg")
			.attr("width", width)
			.attr("height", height)
      .append("g")
      .call(zoom);

    var rect = svg.append("rect")
        .attr("width", width)
        .attr("height", height)
        .style("fill", "none")
        .style("pointer-events", "all");

		var container = svg.append("g");

    function rescale() {
      var trans = d3.event.translate;
      var scale = d3.event.scale;

      container.attr("transform",
      "translate(" + trans + ")"
      + " scale(" + scale + ")");
    }

    function dragstarted(d) {
      d3.event.sourceEvent.stopPropagation();
      d3.select(this).classed("dragging", true);
    }

    function dragged(d) {
      d3.select(this).attr("x", d.x = d3.event.x).attr("y", d.y = d3.event.y);
      $layout.resume();
    }

    function dragended(d) {
      d3.select(this).classed("dragging", false);
    }

    function linkArc(d) {
      var dx = d.target.x - d.source.x,
          dy = d.target.y - d.source.y,
          dr = Math.sqrt(dx * dx + dy * dy);
      return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
    }

    // add centering of graph with mouse right click
    svg.on("contextmenu", function () {
      resetGraph();

      zoom.translate([posX, posY]);
      zoom.scale(currentScale);

      //stop showing browser menu
      d3.event.preventDefault();
    });

		// registriert update callback
		$layout.onTick(update);

		/**
		 * Zeichnet Knoten, Kanten und Labels anhand der Daten des Layouts
		 * @return {void}
		 * */
		function draw() {
			var graph = $layout.getGraph();
			drawEdges(graph.edges);
			drawNodes(graph.nodes);
			drawNodeLabels(graph.nodes);
			$layout.start();
		}

		/**
		 * Zeichnet Kanten
		 * @param {...Object} edges - Kanten-Array
		 * @return {void}
		 * */
		function drawEdges(edges) {
      edge = container.selectAll(".edge").remove();

      var linkType = $settings.getLinkType();

			if (linkType == "curved") {
        edge = container.selectAll(".edge")
        .data(edges)
        .enter()
        .append("path")
        .attr("class", "edge");
      } else {
        edge = container.selectAll(".edge")
        .data(edges)
        .enter()
        .insert("line", ".node")
        .attr("class", "edge");
      }
		}

		/**
		 * Zeichnet Knoten
		 * @param {...Object} nodes - Knoten-Array
		 * @return {void}
		 * */
		function drawNodes(nodes) {
      container.selectAll(".node").remove();

			node = container.selectAll(".node")
        .data(nodes)
        .enter()
        .append("rect")
				.attr("class", "node")
				.attr("width", function (d) {
					return d.width;
				})
				.attr("height", function (d) {
					return d.height;
				})
				.style("fill", function (d) {
					return $settings.getNodeColor(d.data);
  			}).on("dblclick", function (d) {
          showProperties(d);
        }).on("contextmenu", function (d) {
          showProperties(d);
        }).call(drag);
		}

    function showProperties(d) {
      $scope.showProperties(d);

      //stop showing browser menu
      d3.event.preventDefault();
      d3.event.stopPropagation();
    }

		/**
		 * Zeichnet Beschriftung
		 * @param {...Object} nodes - Knoten-Array
		 * @return {void}
		 * */
		function drawNodeLabels(nodes) {
      container.selectAll(".node-label").remove();

			label = container.selectAll(".node-label")
        .data(nodes)
        .enter()
        .append("text")
				.attr("class", "node-label")
				.attr("font-family", $settings.getNodeFont())
				.attr("font-size", $settings.getNodeFontSize() + "px")
				.attr("text-anchor", "middle")
        .style("fill", "black")
				.text(function (d) {
					return d.label;
				});
		}

		/**
		 * Update-Funktion zum aktualsieren der Knoten-, Kanten- und Label-Positionen
		 * @return {void}
		 * */
		function update() {
      var linkType = $settings.getLinkType();
      if (linkType == "curved") {
          edge.attr("d", linkArc);
      }

      edge.attr("x1", function (d) { return d.source.x; })
				.attr("y1", function (d) { return d.source.y; })
				.attr("x2", function (d) { return d.target.x; })
				.attr("y2", function (d) { return d.target.y; });

			node.attr("x", function (d) { return d.x - (d.width / 2); })
				.attr("y", function (d) { return d.y - (d.height / 2); });

			label.attr("x", function (d) { return d.x; })
				.attr("y", function (d) { return d.y + (d.height / 5); });
		}

		/**
		 * Sucht nach Knoten und hebt diese farblich hervor
		 * @return {Object} seach - Such-Objekt enthält Typ und SuchBegriff
		 * @return {void}
		 * */
		function searchNode(search) {
			clearSearchNodes();
			if (search.value !== "") {
				var resultNode = filterBy(search.type, search.value);
				resultNode.style("fill", $settings.getSearchColor());
				resultNode.classed("result", true);
			}

			function filterBy(type, value) {
				return container.selectAll(".node").filter(function (d) {
					switch (type) {
						case "type":
							return d.type === value;
						case "name":
							return hasPropertyValue(d, "name", value);
						case "value":
							return hasPropertyValue(d, "value", value);
					}
				});
			}

			function hasPropertyValue(d, prop, value) {
				if (!d.data.hasProperty(prop)) {
					return false;
				}
				return d.data.getProperty(prop) === value;
			}
		}

		/**
		 * Löscht Suchmarkierung
		 * @return {void}
		 * */
		function clearSearchNodes() {
			container.selectAll(".result").each(function (d) {
				var target = d3.select(this);
				target.style("fill", $settings.getNodeColor(d.data));
				target.classed("result", false);
			});
		}

		/**
		 * Blendet Knoten anhand eines Faktors aus
		 * @param {number} opacity - Faktor der über die Sichtbarkeit der Knoten bestimmt
		 *  (0.0 = unsichtbar, 1.0 = sichtbar)
		 * @return {void}
		 * */
		function fadeNodes(opacity) {
			container.selectAll(".node").filter(function (d) {
				return !d.selected;
			}).transition().duration(200).style("opacity", opacity);
			container.selectAll(".node-label").filter(function (d) {
				return !d.selected;
			}).transition().duration(200).style("opacity", opacity);
			container.selectAll(".edge").transition().duration(200).style("opacity", opacity);
		}

		/**
		 * Skaliert den Graphen im Zentrum der Ansicht
		 * @param {number} scale - Skalierungsfaktor
		 * @return {void}
		 * */
		function centerScale(scale) {
			posX = -centerX * (scale - 1.0);
			posY = -centerY * (scale - 1.0);
			container.attr("transform", "translate(" + [posX, posY] + ") scale(" + [scale, scale] + ")");
		}

		/**
		 * Setz Graph auf den Ursprungszustand zurück, setzt Zoom-Faktor auf 1.0,
		 * Deselektiert und löst Fixierung aller Knoten. Das Einblenden unsichtbarer Knoten
		 * ist von der Funktion ausgenommen
		 * @return {void}
		 * */
		function resetGraph() {
      clearSearchNodes();
			centerScale(1.0);
		}

		// Diese Funktion wird ausgelöst wenn sich der Graph
		// geändert hat, wenn sich die Anzahl der Knoten oder Kanten
		// von der vorherigen unterscheidet, wird neugezeichnet
		$rootScope.$on('graph.transfer', function () {
			var currentNodeCount = $layout.getNodeCount();
			var currentEdgeCount = $layout.getEdgeCount();
			if (currentNodeCount != lastNodeCount || currentEdgeCount != lastEdgeCount) {
				$log.debug("Update Graph");
				draw();
				lastNodeCount = currentNodeCount;
				lastEdgeCount = currentEdgeCount;
			}
		});

		$scope.$watch(function () {
			return $window.innerWidth;
		}, function (value) {
			svg.attr("width", value);
		});

		$scope.$watch(function () {
			return $window.innerHeight;
		}, function (value) {
			svg.attr("height", value - header);
		});

		// überwacht ob ein Suche gestartet wurde
		// und ruft dann die entsprechende Funktion auf
		$scope.$watch('search', function (value) {
			console.log("Search: " + value);
			searchNode(value);
		}, true);

		// überwacht ob der Modus zum ausblenden von Knoten aktiv ist
		// und ruft dann die entsprechende Funktion auf
		$scope.$watch('nodeVisibility', function (value) {
			if (value) {
				fadeNodes(1.0);
			} else {
				fadeNodes(0.15);
			}
		});

		// überwacht ob der Graph zurückgesetzt werden soll
		// und ruft dann die entsprechende Funktion auf
		$scope.$watch('reset', function (value) {
			if (value) {
				resetGraph();
				draw();
			}
		});

		// überwacht ob sich Einstellungen geändert haben
		// und zeichnet dann den Graphen neu
		$scope.$watch('changes', function () {
			console.log("Settings have changed");
			draw();
		});

		// Workaround for wrong canvas size when #graph page is accessed directly
		// Not nice, but it finally works
		$('#graph').on('pageshow', function() {
			$log.debug('Event pageshow on #graph');

			var newHeader = 2 * $('#graph > div[data-role="header"]').outerHeight();
			var newHeight = $window.innerHeight - newHeader;

			$log.debug('newHeader: ' + newHeader);
			$log.debug('newHeight: ' + newHeight);

			svg.attr('height', newHeight);
		});
	}

  return {
      restrict: 'A',
      controller: 'D3GraphController',
      link: init
  };
}]);
