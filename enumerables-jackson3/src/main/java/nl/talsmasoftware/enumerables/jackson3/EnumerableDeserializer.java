package nl.talsmasoftware.enumerables.jackson3;

import nl.talsmasoftware.enumerables.Enumerable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.deser.std.StdDeserializer;

public class EnumerableDeserializer extends StdDeserializer<Enumerable> {

    public EnumerableDeserializer() {
        super(Enumerable.class);
    }

    protected EnumerableDeserializer(JavaType valueType) {
        super(valueType);
    }

    @Override
    public Enumerable deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        return null;
    }

}
