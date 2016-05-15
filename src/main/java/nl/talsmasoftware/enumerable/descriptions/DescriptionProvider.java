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
package nl.talsmasoftware.enumerable.descriptions;

import nl.talsmasoftware.enumerable.Enumerable;

/**
 * Provider interface for descriptions of {@link Enumerable} object instances.
 * <p>
 * A <code>DescriptionProvider</code> will automatically be used by the {@link Enumerable#getDescription()} method
 * if this provider has been defined as a constant within the {@link Enumerable} subtype itself
 * (i.e. as <code>static</code> and <code>final</code> member). The constant may have any visibility, including
 * <code>private</code>.
 * <p>
 * This 'automatic' registration is facilitated by the {@link DescriptionProviderRegistry}.
 * It is technically possible to register your own description provider at this registry programmatically,
 * defining a constant is generally preferable given the added simplicity.
 * <p>
 * An example of an automatically registered provider in an enumerable type:
 * <p>
 * <pre>
 * <code>
 *
 *  public static final class Wordpairs extends Enumerable {
 *      private static final DescriptionProvider provider = new ReverseDescriptionProvider();
 *
 *      public static final Wordpairs DESSERTS = new Wordpairs("desserts");
 *      public static final Wordpairs LIVED = new Wordpairs("lived");
 *      public static final Wordpairs EDIT = new Wordpairs("edit");
 *      public static final Wordpairs MAPS = new Wordpairs("maps");
 *      public static final Wordpairs STRAW = new Wordpairs("straw");
 *
 *      private Wordpairs(String value) {
 *          super(value);
 *      }
 *  }
 *
 * </code>
 * and:
 * <code>
 *
 *  public static final class ReverseDescriptionProvider implements DescriptionProvider {
 *      public String describe(Enumerable enumerable) {
 *          String description = Descriptions.defaultProvider().describe(enumerable);
 *          if (description == null || description.length() == 0) return description;
 *          StringBuilder reverse = new StringBuilder(description).reverse();
 *          reverse.setCharAt(reverse.length() - 1, Character.toLowerCase(reverse.charAt(reverse.length() - 1)));
 *          reverse.setCharAt(0, Character.toUpperCase(reverse.charAt(0)));
 *          return reverse.toString();
 *      }
 *  }
 * </code>
 * </pre>
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public interface DescriptionProvider {

    /**
     * This operation provides a human-readable description for the given {@link Enumerable} object instance.
     * <p>
     * Tip: The {@link Descriptions#defaultProvider() default provider} does not have any recursive dependencies and
     * may therefore always be used as a fallback implementation to rely on without having to worry about endless loops
     * and stack overflows.
     *
     * @param enumerable The enumerable object instance that requires a description value.
     * @return The description for the given value.
     */
    String describe(Enumerable enumerable);

}
