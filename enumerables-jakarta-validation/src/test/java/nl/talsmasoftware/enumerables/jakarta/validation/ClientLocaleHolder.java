/*
 * Copyright 2016-2023 Talsma ICT
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
package nl.talsmasoftware.enumerables.jakarta.validation;

import java.util.Locale;

/**
 * @author Sjoerd Talsma
 */
public class ClientLocaleHolder {
    public static final Locale DUTCH = new Locale("nl", "NL"), FRISIAN = new Locale("fy", "NL");

    private static final ThreadLocal<Locale> CLIENT_LOCALE = new ThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return Locale.getDefault();
        }
    };

    public static Locale get() {
        return CLIENT_LOCALE.get();
    }

    public static void set(Locale clientLocale) {
        CLIENT_LOCALE.set(clientLocale);
    }

}
