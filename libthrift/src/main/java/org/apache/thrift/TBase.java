/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.thrift;

import java.io.Serializable;
import java.util.Map;

import org.apache.thrift.meta_data.FieldMetaData;

/**
 * Generic base interface for generated Thrift objects.
 */
public interface TBase<T extends TBase<T, F>, F extends TFieldIdEnum> extends Comparable<T>,
        TSerializable, Serializable {

    /**
     * Get the F instance that corresponds to fieldId.
     */
    F fieldForId(int fieldId);

    /**
     * Check if a field is currently set or unset.
     *
     * @param field
     */
    boolean isSet(F field);

    /**
     * Get a field's value by field variable. Primitive types will be wrapped in
     * the appropriate "boxed" types.
     *
     * @param field
     */
    Object getFieldValue(F field);

    /**
     * Get all fields.
     *
     * @return fields
     */
    F[] getFields();

    /**
     * Get Fields meta data
     *
     * @return field meta data
     */
    Map<F, FieldMetaData> getFieldMetaData();

    /**
     * Set a field's value by field variable. Primitive types must be "boxed" in
     * the appropriate object wrapper type.
     *
     * @param field
     */
    void setFieldValue(F field, Object value);

    T deepCopy();

    /**
     * Return to the state of having just been initialized, as though you had just
     * called the default constructor.
     */
    void clear();
}
