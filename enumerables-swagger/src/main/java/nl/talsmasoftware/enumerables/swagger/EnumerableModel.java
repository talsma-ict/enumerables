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
package nl.talsmasoftware.enumerables.swagger;

import io.swagger.models.ExternalDocs;
import io.swagger.models.ModelImpl;
import nl.talsmasoftware.enumerables.Enumerable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Swagger {@code Model} implementation for {@link Enumerable} types.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableModel extends ModelImpl {
    private static final String WEBSITE_URL = "https://github.com/talsma-ict/enumerables";
    private static final int DESCRIPTION_MAX_KNOWN_VALUES = 25;

    private final Class<? extends Enumerable> enumerableType;

    private EnumerableModel(Class<? extends Enumerable> enumerableType) {
        if (enumerableType == null) throw new NullPointerException("Enumerable type is <null>.");
        this.enumerableType = enumerableType;
        super.setSimple(true);
        super.type("string").format("enumerable");
        super.setDescription(MessageFormat.format(
                ResourceBundle.getBundle(getClass().getName()).getString("description"),
                enumerableType.getSimpleName(),
                knownValues(enumerableType)));
        super.setExternalDocs(new ExternalDocs("Enumerables", WEBSITE_URL));
    }

    protected Collection<String> knownValues(Class<? extends Enumerable> enumerableType) {
        Enumerable[] enumerables = Enumerable.values(enumerableType);
        int size = Math.min(enumerables.length, DESCRIPTION_MAX_KNOWN_VALUES + 1);
        List<String> knownValues = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            knownValues.add(i < enumerables.length ? enumerables[i].getValue() : "...");
        }
        return knownValues;
    }

    public static EnumerableModel of(Class<? extends Enumerable> enumerableType) {
        return new EnumerableModel(enumerableType);
    }

    @Override
    public Object clone() {
        return new EnumerableModel(enumerableType);
    }

}
