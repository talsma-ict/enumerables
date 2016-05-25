/*
 * Copyright (C) 2016 Talsma ICT
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
package nl.talsmasoftware.enumerables.support.json.jackson2;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Compatibility class for using various Jackson 2.x versions and features that were added after 2.0.x.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
final class Compatibility {
    private static final Logger LOGGER = Logger.getLogger(Compatibility.class.getName());

    private static final ConcurrentMap<String, Object> RESOLVED_METHODS = new ConcurrentHashMap<String, Object>();

    private Compatibility() {
        throw new UnsupportedOperationException();
    }

    private static Method method(Class<?> type, String method) throws NoSuchMethodException {
        final String key = type.getName() + "." + method;
        Object resolved = RESOLVED_METHODS.get(key);
        if (resolved == null) {
            try {
                RESOLVED_METHODS.putIfAbsent(key, type.getMethod(method));
            } catch (NoSuchMethodException nsme) {
                RESOLVED_METHODS.putIfAbsent(key, nsme);
            }
            resolved = RESOLVED_METHODS.get(key);
        }
        if (resolved instanceof NoSuchMethodException) {
            throw (NoSuchMethodException) resolved;
        }
        return (Method) resolved;
    }

//    static JavaType getContextualType(DeserializationContext ctxt) {
//        try {
//            return (JavaType) method(ctxt.getClass(), "getContextualType").invoke(ctxt);
//        } catch (ReflectiveOperationException reflectieFout) {
//            LOGGER.log(Level.FINEST, "Geen contextual type; wordt Jackson versie < 2.5 gebruikt?", reflectieFout);
//            return null;
//        }
//    }

//    static Object getTypeId(JsonParser jsonParser) {
//        try {
//            return method(jsonParser.getClass(), "getTypeId").invoke(jsonParser);
//        } catch (ReflectiveOperationException reflectieFout) {
//            LOGGER.log(Level.FINEST, "Geen typeId; wordt Jackson versie < 2.3 gebruikt?", reflectieFout);
//            return null;
//        }
//    }

}
