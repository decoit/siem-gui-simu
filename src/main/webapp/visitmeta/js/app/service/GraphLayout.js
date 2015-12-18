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
 *
 * @author Soeren Grzanna
 */
app.factory('GraphLayout', ['$log', function ($log) {
    var graph = new Springy.Graph();
    var layout = new Springy.Layout.ForceDirected(
        graph,
        500.0,  // Spring stiffness
        500.0,  // Node repulsion
        0.5     // Damping
    );


    /**
     *
     * @param node
     * @returns {Springy.Node}
     */
    function getNode(node) {
        var n = foundNode(node);
        if (angular.isUndefined(n)) {
            n = createNode(node);
        }
        return n;
    }

    /**
     *
     * @param {Node} node
     * @returns {Springy.Node|undefined}
     */
    function foundNode(node) {
        var n = undefined;
        graph.nodes.forEach(function (gn) {
            if (gn.id == node.hashCode()) {
                n = gn;
            }
        });
        return n;
    }

    /**
     *
     * @param {Node} node
     * @returns {Springy.Node}
     */
    function createNode(node) {
        return new Springy.Node(node.hashCode(), {
            node: node,
            label: createLabel(node)
        });
    }

    /**
     *
     * @param {Node} node
     */
    function createLabel(node) {
        if (node instanceof Identifier) {
            switch (node.getTypeName()) {
                case "ip-address":
                    return renderLabel(node.getTypeName(), node.getProperty("value"), node.getProperty("type"));
                case "access-request":
                case "device":
                    return renderLabel(node.getTypeName(), node.getProperty("name"));
                case "identity":
                    return renderLabel(node.getTypeName(), node.getProperty("name"),node.getProperty("type"));
                case "mac-address":
                    return renderLabel(node.getTypeName(), node.getProperty("value"));
                default :
                    return node.getTypeName();
            }
        } else {
            return node.getTypeName();
        }
    }

    function renderLabel(name, propertie, extra) {
        var str = name + ": " + propertie;
        if (extra !== undefined) {
            str += " (" + extra + ")";
        }
        return str;
    }

    /**
     *
     * @param {Springy.Node} node
     * @returns {Springy.Edge[]}
     */
    function getEdgesFromNode(node) {
        var edges = [];
        graph.edges.forEach(function (edge, index) {
            if (edge.source.id == node.id || edge.target.id == node.id) {
                edges.push(edge);
            }
        });
        return edges;
    }

    /**
     *
     * @param {Node} node
     * @returns {Springy.Node}
     */
    function addNode(node) {
        var n = getNode(node);
        graph.addNode(n);
        return n;
    }

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

    function createEdge(source, target) {
        var edges = graph.getEdges(source, target);
        if (graph.getEdges(source, target).length <= 0 && graph.getEdges(target, source).length <= 0) {
            graph.newEdge(source, target);
        }

    }

    /**
     *
     * @param {Metadata} metadata
     */
    function addMetadata(metadata) {
        //$log.info("add metadata: " + metadata.getTypeName() + " " + metadata.hashCode());
        return addNode(metadata);
    }

    /**
     *
     * @param {Identifier} identifier
     */
    function addIdentifier(identifier) {
        //$log.info("add identifier: " + identifier.getTypeName() + " " + identifier.hashCode());
        return addNode(identifier);
    }

    /**
     *
     * @param {Link} link
     */
    function removeLink(link) {
        link.getMetadatas().forEach(function (metadata) {
            removeNode(metadata);
        });

        /*
         link.getIdentifiers().forEach(function (identifier) {
         var adjacencyKeys = Object.keys(graph.adjacency);
         if (adjacencyKeys.indexOf(identifier.hashCode()) == -1) {
         var n = foundNode(identifier);
         if (angular.isDefined(n)) {
         graph.removeNode(n);
         }
         }
         });
         */

        link.getIdentifiers().forEach(function (identifier) {
            var n = foundNode(identifier);
            if (angular.isDefined(n)) {
                var edges = getEdgesFromNode(n);
                if (edges.length == 0) {
                    graph.removeNode(n);
                }
            }
        });

        console.log(graph);
    }

    /**
     *
     * @param {Node} node
     */
    function removeNode(node) {
        $log.debug("remove node", node);
        var n = foundNode(node);
        if (angular.isDefined(n)) {
            graph.detachNode(n);
            graph.removeNode(n);
        }
    }

    return {

        /**
         *
         * @param {Graph} graph
         */
        setGraph: function (graph) {
            this.clear();
            this.addGraph(graph);
        },

        addGraph: function (graph) {
            graph.getLinks().forEach(function (link) {
                addLink(link);
            });
        },

        /**
         *
         * @param {Delta} delta
         */
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

        clear: function () {
            graph.edges.forEach(function (edge) {
                graph.removeEdge(edge);
            });

            graph.nodes.forEach(function (node) {
                graph.removeNode(node);
            });

            graph.edges = [];
            graph.nodes = [];
            graph.adjacency = {};
            graph.nodeSet = {};
        },

        getGraph: function () {
            return graph;
        }
    };
}]);
