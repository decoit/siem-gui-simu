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
 * @param {Change[]} updates
 * @param {Change[]} deletes
 * @constructor
 */
function Delta(updates, deletes) {
    /**
     *
     * @type {Change[]}
     */
    this.updates = updates;

    /**
     *
     * @type {Change[]}
     */
    this.deletes = deletes;
}

/**
 *
 * @param {Change[]} updates
 */
Delta.prototype.setUpdates = function (updates) {
    this.updates = updates;
};

/**
 *
 * @param {Change[]} deletes
 */
Delta.prototype.setDeletes = function (deletes) {
    this.deletes = deletes;
};

/**
 *
 * @param {Change} update
 */
Delta.prototype.addUpdate = function (update){
    this.updates.push(update);
};

/**
 *
 * @param {Change} del
 */
Delta.prototype.addDelete = function (del){
    this.deletes.push(del);
};

/**
 *
 * @returns {Change[]}
 */
Delta.prototype.getDeletes = function (){
    return this.deletes;
};

/**
 *
 * @returns {Change[]}
 */
Delta.prototype.getUpdates = function (){
    return this.updates;
};
