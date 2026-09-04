package nl.talsmasoftware.enumerables.jackson3;

import nl.talsmasoftware.enumerables.Enumerable;
import tools.jackson.databind.module.SimpleModule;

public class EnumerableModule extends SimpleModule {

    public EnumerableModule() {
        super.addSerializer(new EnumerableSerializer());
        super.addDeserializer(Enumerable.class, new EnumerableDeserializer());
    }

}
