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
Ifmap.Parser.Json.Link = function () {
    this.propParser = new Ifmap.Parser.Json.Properties();
};


/**
 *
 * @param json
 */
Ifmap.Parser.Json.Link.prototype.parse = function (json) {
    var link = new Link([], []);
    var self = this;

    if (Array.isArray(json.identifiers)) {
        json.identifiers.forEach(function (i) {
            link.addIdentifier(new Identifier(i.typename, self.propParser.parse(i.properties)));
        })
    } else if (json.identifiers instanceof Object) {
        link.addIdentifier(new Identifier(json.identifiers.typename, self.propParser.parse(json.identifiers.properties)));
    } else {
        console.error("no identifier", json);
    }

    if (angular.isArray(json.metadata)) {
        json.metadata.forEach(function (m) {
            link.addMetadata(new Metadata(m.typename, self.propParser.parse(m.properties)));
        });
    } else if (json.metadata instanceof Object) {
        link.addMetadata(new Metadata(json.metadata.typename, self.propParser.parse(json.metadata.properties)));
    } else {
        console.error("no metadata", JSON.stringify(json));
    }

    return link;
};
