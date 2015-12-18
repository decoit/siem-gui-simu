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

/**
 *
 * @param {string} typeName
 * @param {object} properties
 * @constructor
 */
function Node(typeName, properties) {
    this.hashcode = undefined;
    this.setTypeName(typeName);
    this.setProperties(properties);
    this.hashCode();
}

/**
 *
 * @param {string} typeName
 * @return {void}
 */
Node.prototype.setTypeName = function (typeName) {
    this.name = typeName;
};

/**
 *
 * @return {string}
 */
Node.prototype.getTypeName = function () {
    return this.name;
};

/**
 *
 * @param {json} properties
 * @return {void}
 */
Node.prototype.setProperties = function (properties) {
    this.properties = properties;
};

/**
 *
 * @return {json}
 */
Node.prototype.getProperties = function () {
    return this.properties;
};

/**
 *
 * @param name
 * @return {string}
 */
Node.prototype.getProperty = function (name) {
    if (!this.hasProperty(name)) {
        return "UNDEFINED PROPERTY";
    }
    return this.properties[name];
};

/**
 *
 * @returns {Array}
 */
Node.prototype.getPropertiesKeys = function(){
    return Object.keys(this.getProperties());
};

/**
 *
 * @param {string} name
 * @return {boolean}
 */
Node.prototype.hasProperty = function (name) {
    return this.properties.hasOwnProperty(name);
};

/**
 *
 * @return {number}
 */
Node.prototype.hashCode = function () {
    if (this.hashcode === undefined) {
        var hash = 0;
        var string = JSON.stringify(this.getTypeName()) + JSON.stringify(this.getProperties());
        if (string.length == 0) return hash;
        for (var i = 0; i < string.length; i++) {
            var char = string.charCodeAt(i);
            hash = ((hash << 5) - hash) + char;
            hash = hash & hash; // Convert to 32bit integer
        }
        this.hashcode = hash;
    }
    return this.hashcode;
};
