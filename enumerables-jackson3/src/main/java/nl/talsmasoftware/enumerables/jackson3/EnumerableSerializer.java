package nl.talsmasoftware.enumerables.jackson3;

import nl.talsmasoftware.enumerables.Enumerable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class EnumerableSerializer extends StdSerializer<Enumerable> {

    public EnumerableSerializer() {
        super(Enumerable.class);
    }

    public void serialize(Enumerable value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(Enumerable.print(value));
        }
    }

}
