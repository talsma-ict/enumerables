package nl.talsmasoftware.enumerables.jackson2.bug25;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.talsmasoftware.enumerables.jackson2.EnumerableModule;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_STRING;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kroon.r on 28-06-2017.
 */
public class DeserializationEqualityTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new EnumerableModule(AS_STRING));
//            .registerModule(new WaardenlijstJackson2Module(WaardenlijstSerialisatieWijze.ALS_STRING));

    @Test
    public void testAsList() throws IOException {
        TypeWithEnumerableList original = new TypeWithEnumerableList();
        original.stringValue = "Een String";
        original.teammembers = Arrays.asList(Team.RICHARD);
        String asString = MAPPER.writeValueAsString(original);

        TypeWithEnumerableList deserialized = MAPPER.readValue(asString, TypeWithEnumerableList.class);

        System.out.println("Serialized: " + asString);

        assertThat("deserialized representatie", deserialized, equalTo(original));
    }

    @Test
    public void testAsWrappedList() throws IOException {
        TypeWithListContainingNestedEnumerable original = new TypeWithListContainingNestedEnumerable();
        original.stringValue = "Een String";

        TypeWithEnumerable wrappedWaardelijst = new TypeWithEnumerable();
        wrappedWaardelijst.teammember = Team.RICHARD;
        wrappedWaardelijst.otherValue = "Een String";

        original.teammembers = Arrays.asList(wrappedWaardelijst);

        String asString = MAPPER.writeValueAsString(original);

        TypeWithListContainingNestedEnumerable deserialized = MAPPER.readValue(asString, TypeWithListContainingNestedEnumerable.class);

        System.out.println("Serialized: " + asString);

        assertThat("deserialized representatie", deserialized, equalTo(original));
    }

}
