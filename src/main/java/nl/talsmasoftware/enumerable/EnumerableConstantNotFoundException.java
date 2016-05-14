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
package nl.talsmasoftware.enumerable;

/**
 * Exception that is thrown by the {@link Enumerable#valueOf(Class, CharSequence)} method when an enumerable
 * constant is requested that could not be found.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableConstantNotFoundException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor. Creates a new exception telling the caller that a bad constant name was requested.
     *
     * @param enumerableType The type of Enumerable that was requested.
     * @param constantName   The name of the Enumerable constant that was requested.
     */
    public EnumerableConstantNotFoundException(Class<? extends Enumerable> enumerableType, CharSequence constantName) {
        this(String.format("No Enumerable constant \"%s.%s\" found.",
                enumerableType == null ? null : enumerableType.getSimpleName(), constantName), null);
    }

    /**
     * General exception constructor left in place in case anybody wishes to subclass us.
     *
     * @param message The message for this exception.
     * @param cause   The cause of this exception.
     */
    protected EnumerableConstantNotFoundException(String message, Throwable cause) {
        super(message);
        if (cause != null) super.initCause(cause); // Allows for initCause() later on if cause == null.
    }

}
