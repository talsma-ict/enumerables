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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
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

    @SuppressWarnings("unchecked")
    private static <T> T call(Object target, String method) throws NoSuchMethodException {
        try {

            return (T) method(target.getClass(), method).invoke(target);

        } catch (IllegalAccessException iae) {
            NoSuchMethodException nsme = new NoSuchMethodException(
                    String.format("Not allowed to call method \"%s\": %s", method, iae.getMessage()));
            nsme.initCause(iae);
            throw nsme;
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause == null) cause = ite; // shouldn't happen!
            throw cause instanceof RuntimeException ? (RuntimeException) cause
                    : new RuntimeException(cause.getMessage(), cause);
        }
    }

    /**
     * Attempts to call <code>JsonParser.getTypeId()</code>.
     * However, this method was only added in Jackson version 2.3,
     * so it may not be possible to call it before then.
     * Therefore we anticipate this method not being available.
     *
     * @param jsonParser The json parser to call <code>getTypeId()</code> on.
     * @return The result of the call, or <code>null</code> if the method was not yet defined.
     */
    static Object getTypeId(JsonParser jsonParser) {
        if (jsonParser != null) try {
            return call(jsonParser, "getTypeId");
        } catch (NoSuchMethodException nsme) {
            LOGGER.log(Level.FINEST, "No getTypeId() method; is Jackson version less than 2.3 ?", nsme);
        }
        return null;
    }

    static JavaType getContextualType(DeserializationContext ctxt) {
        if (ctxt != null) try {
            return call(ctxt, "getContextualType");
        } catch (NoSuchMethodException nsme) {
            LOGGER.log(Level.FINEST, "No getContextualType() method; is Jackson version less than 2.5 ?", nsme);
        }
        return null;
    }


}
