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

//Namespace
var Ifmap = Ifmap || {};
Ifmap.Parser = Ifmap.Parser || {};
Ifmap.Parser.Json = Ifmap.Parser.Json || {};

/**
 *
 * @constructor
 */
Ifmap.Parser.Json.Delta = function () {
};

/**
 *
 * @param {Object} json
 * @returns {Delta}
 */
Ifmap.Parser.Json.Delta.prototype.parse = function (json) {
    var self = this;
    var updates = [];

    json.updates.forEach(function (update) {
        updates.push(self.parseChange(update));
    });

    var deletes = [];
    json.deletes.forEach(function (del) {
        deletes.push(self.parseChange(del));
    });

    return new Delta(updates, deletes);
};

/**
 *
 * @param {Object} json
 * @returns {Change}
 */
Ifmap.Parser.Json.Delta.prototype.parseChange = function (json) {
    return new Change(
        json.timestamp,
        this.parseLinks(json.links)
    );
};

/**
 *
 * @param {Object} links
 * @returns {Change[]}
 */
Ifmap.Parser.Json.Delta.prototype.parseLinks = function (links) {
    var parser = new Ifmap.Parser.Json.Link();
    var linksObjects = [];

    links.forEach(function (link) {
        linksObjects.push(parser.parse(link));
    });
    return linksObjects;
};
