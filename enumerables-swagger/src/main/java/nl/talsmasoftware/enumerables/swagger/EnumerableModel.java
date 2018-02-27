/*
 * Copyright 2016-2018 Talsma ICT
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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.models.AbstractModel;
import io.swagger.models.ExternalDocs;
import io.swagger.models.properties.Property;
import nl.talsmasoftware.enumerables.Enumerable;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

@XmlType(propOrder = {"type", "required", "discriminator", "properties"})
@JsonPropertyOrder({"type", "required", "discriminator", "properties"})
public class EnumerableModel extends AbstractModel {
    private static final String WEBSITE_URL = "https://github.com/talsma-ict/enumerables";
    private static final String DEFAULT_DESCRIPTION =
            "Known %s values. However, unknown values must also be supported.";

    private String type, name, description;
    private boolean simple = true;
    private Map<String, Property> properties;
    private List<String> knownValues;
    private Object example;

    private EnumerableModel(Class<? extends Enumerable> enumerableType) {
        if (enumerableType != null) { // Can only be null for cloning
            this.type = "string";
            this.name = enumerableType.getSimpleName();
            this.description = String.format(DEFAULT_DESCRIPTION, name);
            Enumerable[] values = Enumerable.values(enumerableType);
            this.knownValues = knownValuesOf(values);
            if (values.length > 0) example = values[0].getValue();
            super.setExternalDocs(new ExternalDocs("Enumerables", WEBSITE_URL));
        }
    }

    public static EnumerableModel of(Class<? extends Enumerable> enumerableType) {
        if (enumerableType == null) throw new NullPointerException("Enumerable type is <null>.");
        return new EnumerableModel(enumerableType);
    }

    private static List<String> knownValuesOf(Enumerable[] values) {
        List<String> result = null;
        if (values != null && values.length > 0) {
            List<String> knownValues = new ArrayList<String>(values.length);
            for (Enumerable value : values) knownValues.add(value.getValue());
            result = unmodifiableList(knownValues);
        }
        return result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEnum() {
        return knownValues;
    }

    public void setEnum(List<String> knownValues) {
        this.knownValues = knownValues == null ? null : unmodifiableList(new ArrayList<String>(knownValues));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties == null ? null : unmodifiableMap(new LinkedHashMap<String, Property>(properties));
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public Object getExample() {
        return example;
    }

    public void setExample(Object example) {
        this.example = example;
    }

    @Override
    public Object clone() {
        EnumerableModel copy = new EnumerableModel(null);
        this.cloneTo(copy);
        return copy;
    }

    @Override
    public void cloneTo(Object clone) {
        if (clone instanceof AbstractModel) super.cloneTo(clone);
        if (clone instanceof EnumerableModel) {
            EnumerableModel other = (EnumerableModel) clone;
            other.type = this.type;
            other.name = this.name;
            other.description = this.description;
            other.simple = this.simple;
            other.properties = this.properties;
            other.knownValues = this.knownValues;
            other.example = this.example;
        }
    }

    @Override
    public int hashCode() {
        return hash(super.hashCode(), type, name, description, simple, properties, knownValues, example);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EnumerableModel
                && super.equals(other)
                && equals(type, ((EnumerableModel) other).type)
                && equals(name, ((EnumerableModel) other).name)
                && equals(description, ((EnumerableModel) other).description)
                && equals(simple, ((EnumerableModel) other).simple)
                && equals(properties, ((EnumerableModel) other).properties)
                && equals(knownValues, ((EnumerableModel) other).knownValues)
                && equals(example, ((EnumerableModel) other).example)
        );
    }

    // Unfortunately we're not in Java 7 land yet.

    private static int hash(Object... objs) {
        int hash = 0;
        for (Object obj : objs) {
            hash *= 31;
            if (obj != null) hash += obj.hashCode();
        }
        return hash;
    }

    private static boolean equals(Object obj1, Object obj2) {
        return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    }
}
