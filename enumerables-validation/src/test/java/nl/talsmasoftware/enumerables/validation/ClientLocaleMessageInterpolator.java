/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.enumerables.validation;

import javax.validation.MessageInterpolator;
import java.util.Locale;

/**
 * @author Sjoerd Talsma
 */
public class ClientLocaleMessageInterpolator implements MessageInterpolator {

    private final MessageInterpolator delegate;

    public ClientLocaleMessageInterpolator(MessageInterpolator delegate) {
        if (delegate == null) throw new IllegalArgumentException("Delegate message interpolator is required!");
        this.delegate = delegate;
    }

    public String interpolate(String message, Context context) {
        return interpolate(message, context, null);
    }

    public String interpolate(String message, Context context, Locale locale) {
        return delegate.interpolate(message, context, locale != null ? locale : ClientLocaleHolder.get());
    }

}
