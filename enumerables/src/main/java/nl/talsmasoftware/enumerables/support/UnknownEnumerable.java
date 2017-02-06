/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package nl.talsmasoftware.enumerables.support;

import nl.talsmasoftware.enumerables.Enumerable;

/**
 * Special case for a deserialized Enumerable value for which the type is not known and therefore no 'known' constants
 * are specified either. This may happen if a deserialized object contains an attribute of type {@link Enumerable}
 * without any additional information.
 * In that case, when deserializing such attribute, an {@link UnknownEnumerable} instance will be returned.
 * Usually this is a sign of bad design with too little type information available,
 * but technically it should of course be possible to deserialize 'any' Enumerable object.
 *
 * @author Sjoerd Talsma
 */
public final class UnknownEnumerable extends Enumerable {

    private UnknownEnumerable(String value) {
        super(value);
    }

}
