/*
 * Copyright 2016-2025 Talsma ICT
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
package nl.talsmasoftware.enumerables.jackson2.bug25;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.talsmasoftware.enumerables.jackson2.EnumerableModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_STRING;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by kroon.r on 28-06-2017.
 */
class DeserializationEqualityTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new EnumerableModule(AS_STRING));

    @Test
    void testAsList() throws IOException {
        TypeWithEnumerableList original = new TypeWithEnumerableList();
        original.stringValue = "A String value";
        original.teammembers = asList(Team.RICHARD);
        String asString = MAPPER.writeValueAsString(original);

        TypeWithEnumerableList deserialized = MAPPER.readValue(asString, TypeWithEnumerableList.class);
        assertThat(deserialized).as("Deserialized representation").isEqualTo(original);
    }

    @Test
    void testAsWrappedList() throws IOException {
        TypeWithListContainingNestedEnumerable original = new TypeWithListContainingNestedEnumerable();
        original.stringValue = "A String value";

        TypeWithEnumerable wrappedWaardelijst = new TypeWithEnumerable();
        wrappedWaardelijst.teammember = Team.RICHARD;
        wrappedWaardelijst.otherValue = "Another String value";

        original.teammembers = asList(wrappedWaardelijst);

        String asString = MAPPER.writeValueAsString(original);

        TypeWithListContainingNestedEnumerable deserialized = MAPPER.readValue(asString, TypeWithListContainingNestedEnumerable.class);
        assertThat(deserialized).as("Deserialized representation").isEqualTo(original);
    }

}
