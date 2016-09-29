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
package nl.talsmasoftware.enumerables.descriptions.resourcebundle;

import java.util.Locale;

/**
 * An interface to be able to supply the {@link ResourceBundleDescriptionProvider} of a dynamic {@link Locale},
 * different from the platform-default locale that is specified for the entire JVM.
 * <p>
 * This allows for a per-user or per-thread setting that can dynamically be configured base on an externally-managed
 * <code>ThreadLocal</code> value.
 *
 * @author Sjoerd Talsma
 */
public interface LocaleProvider {

    /**
     * Constant for the default LocaleProvider that supplies the JVM default locale.
     */
    LocaleProvider DEFAULT = new LocaleProvider() {
        /** @return The default Locale for the entire JVM. */
        public Locale provideLocale() {
            return Locale.getDefault();
        }
    };

    /**
     * @return The locale that must be used to obtain the <code>description</code> from the resource bundle.
     */
    Locale provideLocale();

}
