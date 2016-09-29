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

import nl.talsmasoftware.enumerables.Enumerable;

import java.util.Locale;

import static nl.talsmasoftware.enumerables.descriptions.Descriptions.capitalize;

/**
 * Default implementation for providing descriptions of {@link Enumerable} objects in a human-readable way.
 *
 * @author Sjoerd Talsma
 */
final class DefaultDescriptionProvider implements DescriptionProvider {

    /**
     * Reusable instance of the default description provider.
     *
     * @see Descriptions#defaultProvider()
     */
    static final DescriptionProvider INSTANCE = new DefaultDescriptionProvider();

    /**
     * Private constructor to avoid other instances than INSTANCE.
     */
    private DefaultDescriptionProvider() {
    }

    /**
     * Provides the standard description for any enumerable instance.
     * <p>
     * <ol>
     * <li>Enumerable <code>null</code> results in description <code>null</code>.</li>
     * <li>Otherwise, the {@link Enumerable#name() constant name} is used as base of the description.</li>
     * <li>In case the value is no constant, the actual {@link Enumerable#getValue() value} will be the base of the
     * description.</li>
     * <li>The result is then transformed into {@link String#toLowerCase(Locale) lowercase} characters.</li>
     * <li>All occurrances of the underscore character ('_') are {@link String#replace(char, char) replaced}
     * by whitespace characters.</li>
     * <li>Finally, the first character of the resulting string is {@link Descriptions#capitalize(String) capitalized}.</li>
     * </ol>
     * <p>
     * This will lead for example to a description of <code>"Some value"</code> for constant <code>SOME_VALUE</code>.
     *
     * @param enumerable The enumerable object value to provide a description for.
     * @return The 'standaard' description according to the above algorithm
     * or <code>null</code> if the <code>enumerable</code> was <code>null</code> itself.
     */
    public String describe(Enumerable enumerable) {
        String description = null;
        if (enumerable != null) {
            description = enumerable.name();
            if (description == null) {
                description = enumerable.getValue();
                if (description == null)
                    throw new IllegalStateException("The value of an enumerable object instance may never be null!");
            }
            description = capitalize(description.toLowerCase(Locale.ENGLISH)).replace('_', ' ');
        }
        return description;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return getClass().isInstance(other);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
