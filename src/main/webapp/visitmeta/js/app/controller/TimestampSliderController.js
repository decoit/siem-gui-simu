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
'use strict';

app.controller('TimestampSliderController', ['ChangesService', '$log', '$scope', '$filter', '$rootScope', function (ChangesService, $log, $scope, $filter, $rootScope) {
    var self = this;
    var _timestamps = [];
    var _slider;

    $rootScope.$on('live.on', function () {
        _slider.slider('disable');
    });

    $rootScope.$on('live.off', function () {
        _slider.slider('enable');
    });

    $rootScope.$on('connection.change', function (data, connection) {
        var isEnabled = _slider.slider('isEnabled');
        _timestamps = [];
        _slider.slider('destroy');
        self.initSlider();
        _slider.slider((isEnabled ? 'enable' : 'disable'));
    });


    this.init = function () {
        this.initSlider();
        ChangesService.subscribe(this.updateSuccess);
        ChangesService.start();
    };

    this.updateSuccess = function (timestamps) {
        $log.debug("call updateSuccess from TimestampSliderController");
        _timestamps = timestamps;
        $log.debug("timestamps are updated");
        _slider.slider('setAttribute', 'max', _timestamps.length - 1);

        var value = _slider.slider('getValue');

        if (_timestamps.length - 1 > value && !_slider.slider('isEnabled')) {
            value = _timestamps.length - 1;
            console.log("set timestamp slider to", value);
        }
        _slider.slider('setValue', value);
    };

    this.initSlider = function () {
        $log.debug("init timestamp slider");

        _slider = $("#timestamp-slider input").slider({
            min: 0,
            max: 0,
            step: 1,
            value: 0,
            formatter: function (value) {
                if (value < _timestamps.length) {
                    return $filter('date')(_timestamps[value], 'medium');
                }
                return "NT";
            }
        });

        _slider.slider('on', 'slideStop', function (event) {
            var timestampIndex = event.value;
            var timestamp = _timestamps[timestampIndex];
            $rootScope.$broadcast('timestamp.change', timestamp);
        });

    };

    this.init();
}]);
