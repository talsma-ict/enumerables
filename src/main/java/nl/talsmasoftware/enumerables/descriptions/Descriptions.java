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
package nl.talsmasoftware.enumerables.descriptions;

import static java.lang.Character.isTitleCase;
import static java.lang.Character.toTitleCase;

/**
 * @author Sjoerd Talsma
 */
public final class Descriptions {

    /**
     * Private constructor to prevent instances of this static utility class.
     */
    private Descriptions() {
        throw new UnsupportedOperationException("Utility class may not be initialized.");
    }

    /**
     * Factory method to provide a reference to the default {@link DescriptionProvider}.
     *
     * @return The default <code>DescriptionProvider</code>
     */
    public static DescriptionProvider defaultProvider() {
        return DefaultDescriptionProvider.INSTANCE;
    }

    /**
     * Utility method to make the first character of a String an uppercase character.
     * The method was included here to avoid unncecessary dependencies to other (more complex) libraries.
     *
     * @param value The string to be capitalized.
     * @return The capitalized string with the first character as an uppercase character.
     */
    public static String capitalize(String value) {
        if (value == null || value.length() == 0 || isTitleCase(value.charAt(0))) return value;
        char[] chars = value.toCharArray();
        chars[0] = toTitleCase(chars[0]);
        return new String(chars);
    }

}
