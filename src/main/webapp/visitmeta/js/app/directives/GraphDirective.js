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
app.directive('graphDirective', function ($window) {
    function init($scope, element, attrs, $window) {
        var canvas = element[0];

        var stiffness = 400.0;
        var repulsion = 400.0;
        var damping = 0.5;
        var minEnergyThreshold = 0.00001;
        var graph = $scope.graph;

        var layout = new Springy.Layout.ForceDirected(graph, stiffness, repulsion, damping, minEnergyThreshold);
        render(canvas, layout, $scope);
        resize($scope, canvas);
    }

    function render(canvas, layout, $scope) {
        var nodeFont = "16px Verdana, sans-serif";
        var nodeSelected = null;

        var backgroundColorIdentifier = "blue";
        var backgroundColorMetadata = "red";
        var fontColorIdentifier = "white";
        var fontColorMetadata = "white";
        var selectedNodeBlurColor = "yellow";
        var selectedNodeBlur = 50;
        var nearestNodeBlurColor = "orange";
        var nearestNodeBlur = 50;

        var ctx = canvas.getContext("2d");

        // calculate bounding box of graph layout.. with ease-in
        var currentBB = layout.getBoundingBox();
        var targetBB = {bottomleft: new Springy.Vector(-2, -2), topright: new Springy.Vector(2, 2)};

        // auto adjusting bounding box
        Springy.requestAnimationFrame(function adjust() {
            targetBB = layout.getBoundingBox();
            // current gets 20% closer to target every iteration
            currentBB = {
                bottomleft: currentBB.bottomleft.add(targetBB.bottomleft.subtract(currentBB.bottomleft)
                    .divide(10)),
                topright: currentBB.topright.add(targetBB.topright.subtract(currentBB.topright)
                    .divide(10))
            };

            Springy.requestAnimationFrame(adjust);
        });

        // convert to/from screen coordinates
        var toScreen = function (p) {
            var size = currentBB.topright.subtract(currentBB.bottomleft);
            var sx = p.subtract(currentBB.bottomleft).divide(size.x).x * canvas.width / scale;
            var sy = p.subtract(currentBB.bottomleft).divide(size.y).y * canvas.height / scale;
            return new Springy.Vector(sx, sy);
        };

        var fromScreen = function (s) {
            var size = currentBB.topright.subtract(currentBB.bottomleft);
            var px = (s.x / canvas.width / scale) * size.x + currentBB.bottomleft.x;
            var py = (s.y / canvas.height / scale) * size.y + currentBB.bottomleft.y;
            return new Springy.Vector(px, py);
        };

        // half-assed drag and drop
        var selected = null;
        var nearest = null;
        var dragged = null;

        var scale = 1;
        var originx = 0;
        var originy = 0;

        canvas.onmousewheel = function (event){
            console.log("zoom");
            var mousex = event.clientX - canvas.offsetLeft;
            var mousey = event.clientY - canvas.offsetTop;
            var wheel = event.wheelDelta/120;//n or -n


            //according to Chris comment
            var zoom = Math.pow(1 + Math.abs(wheel)/2 , wheel > 0 ? 1 : -1);

            ctx.translate(
                originx,
                originy
            );
            ctx.scale(zoom,zoom);
            ctx.translate(
                -( mousex / scale + originx - mousex / ( scale * zoom ) ),
                -( mousey / scale + originy - mousey / ( scale * zoom ) )
            );

            originx = ( mousex / scale + originx - mousex / ( scale * zoom ) );
            originy = ( mousey / scale + originy - mousey / ( scale * zoom ) );
            scale *= zoom;
        };
/*
        jQuery(canvas).mousedown(function (e) {
            //
            switch (e.which) {
                case 1: //Left button
                    var pos = jQuery(this).offset();
                    var p = fromScreen({x: e.pageX - pos.left, y: e.pageY - pos.top});
                    selected = nearest = dragged = layout.nearest(p);

                    if (selected.node !== null) {
                        dragged.point.m = 10000.0;

                        if (nodeSelected) {
                            nodeSelected(selected.node);
                        }
                    }

                    $scope.properties = selected.node.data.node.getProperties();

                    break;
                case 3: //Right button
                    selected = nearest = dragged = null;
                    $scope.properties = null;
                    break;
            }

            renderer.start();
        });

        jQuery(canvas).mousemove(function (e) {
            var pos = jQuery(this).offset();
            var p = fromScreen({x: e.pageX - pos.left, y: e.pageY - pos.top});
            nearest = layout.nearest(p);

            if (dragged !== null && dragged.node !== null) {
                dragged.point.p.x = p.x;
                dragged.point.p.y = p.y;
            }

            renderer.start();
        });

        jQuery(window).bind('mouseup', function (e) {
            dragged = null;
        });

        jQuery(canvas).bind('contextmenu', '#right', function (e) {
            e.preventDefault();
            return false;
        });
*/
        var getTextWidth = function (node) {
            var text = node.data.label;

            ctx.save();
            ctx.font = nodeFont;
            var width = ctx.measureText(text).width;
            ctx.restore();

            node._width || (node._width = {});
            node._width[text] = width;

            return width;
        };

        var getTextHeight = function (node) {
            return 16;
        };

        Springy.Node.prototype.getHeight = function () {
            return getTextHeight(this);
        };

        Springy.Node.prototype.getWidth = function () {
            return getTextWidth(this);
        };

        var renderer = this.renderer = new Springy.Renderer(layout,
            function clear() {
                ctx.clearRect(originx, originy, canvas.width / scale, canvas.height / scale);
            },
            function drawEdge(edge, p1, p2) {
                var x1 = toScreen(p1).x;
                var y1 = toScreen(p1).y;
                var x2 = toScreen(p2).x;
                var y2 = toScreen(p2).y;

                ctx.strokeStyle = '#000000';
                ctx.beginPath();
                ctx.moveTo(x1, y1);
                ctx.lineTo(x2, y2);
                ctx.stroke();

            },
            function drawNode(node, p) {
                var s = toScreen(p);

                ctx.save();

                // Pulled out the padding aspect sso that the size functions could be used in multiple places
                // These should probably be settable by the user (and scoped higher) but this suffices for now
                var paddingX = 6;
                var paddingY = 6;

                var contentWidth = node.getWidth();
                var contentHeight = node.getHeight();
                var boxWidth = contentWidth + paddingX;
                var boxHeight = contentHeight + paddingY;

                // clear background
                ctx.clearRect(s.x - boxWidth / 2, s.y - boxHeight / 2, boxWidth, boxHeight);

                // set style
                ctx.lineWidth = 1;
                ctx.strokeStyle = 'black';

                // set shadow blur for nearsted node
                if( nearest !== null && nearest.node !== null && nearest.node.id === node.id ){
                    ctx.shadowBlur = nearestNodeBlur;
                    ctx.shadowColor = nearestNodeBlurColor;
                }

                // set shadow for selected node
                if (selected !== null && selected.node !== null && selected.node.id === node.id) {
                    ctx.shadowBlur = selectedNodeBlur;
                    ctx.shadowColor = selectedNodeBlurColor;
                }

                // fill background
                if (node.data.node instanceof Identifier) {
                    ctx.fillStyle = backgroundColorIdentifier;
                } else {
                    ctx.fillStyle = backgroundColorMetadata;
                }

                ctx.fillRect(s.x - boxWidth / 2, s.y - boxHeight / 2, boxWidth / scale , boxHeight  / scale);
                ctx.strokeRect(s.x - boxWidth / 2, s.y - boxHeight / 2, boxWidth  / scale, boxHeight  / scale);


                //Label
                ctx.textAlign = "left";
                ctx.textBaseline = "top";
                ctx.font = nodeFont;
                if (node.data.node instanceof Identifier) {
                    ctx.fillStyle = fontColorIdentifier;
                } else {
                    ctx.fillStyle = fontColorMetadata;
                }

                var text = node.data.label;
                ctx.fillText(text, s.x - contentWidth / 2  / scale, s.y - contentHeight / 2 / scale);

                ctx.restore();
            }
        );

        renderer.start();
    }

    function resize($scope, canvas) {
        var navHeight = 76;

        $scope.$watch(function () {
            return $window.innerWidth;
        }, function (value) {
            $(canvas).attr('width', value);
        });

        $scope.$watch(function () {
            return $window.innerHeight;
        }, function (value) {
            $(canvas).attr('height', value - navHeight + 20);
        });
        /*
         $scope.$watch(function () {
         return $(table).height();
         }, function (value) {
         var wh = $window.innerHeight;
         $(canvas).attr('height', wh - value - navHeight);
         });
         */
    }

    return {
        restrict: 'A',
        link: init
    };
});
