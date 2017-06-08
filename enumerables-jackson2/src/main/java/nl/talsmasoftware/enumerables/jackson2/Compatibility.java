/*
 * Copyright 2016-2017 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.enumerables.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compatibility class to overcome API differences between various Jackson-2 versions.
 *
 * @author Sjoerd Talsma
 */
final class Compatibility {
    private static final Logger LOGGER = Logger.getLogger(Compatibility.class.getName());

    private static boolean supportsContextualType = true;
    private static boolean supportsTypeId = true;

    /**
     * {@link DeserializationContext#getContextualType()} exists since Jackson 2.5
     */
    static JavaType getContextualType(DeserializationContext ctxt) {
        if (supportsContextualType) try {
            return ctxt.getContextualType();
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "DeserializationContext.getContextualType() unavailable. Using Jackson < 2.5?", le);
            supportsContextualType = false;
        }
        return null;
    }

    /**
     * {@link JsonParser#getTypeId()} exists since Jackson 2.3
     */
    public static Object getTypeId(JsonParser jp) throws IOException {
        if (supportsTypeId) try {
            return jp.getTypeId();
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "JsonParser.getTypeId() unavailable. Using Jackson < 2.3?", le);
            supportsTypeId = false;
        }
        return null;
    }

}
