[![Released Version][maven-img]][maven]

# Mapping Enumerables using Jackson

Enumerable values can be mapped to JSON and other formats using [Jackson] data processing tools.

## Usage

### Getting the module

Add the following dependency to your project or download it from 
[maven central](http://repo1.maven.org/maven2/nl/talsmasoftware/enumerables/enumerables-jackson2/).
```xml
<dependency>
    <groupId>nl.talsmasoftware.enumerables</groupId>
    <artifactId>enumerables-jackson2</artifactId>
    <version>[see maven-central badge]</version>
</dependency>
```

### Enabling the module in the Jackson ObjectMapper

The `EnumerableModule` for jackson can be automatically discovered, 
so registering it in an `ObjectMapper` is easy:
```java
ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
```

Or, if you like to do so manually:
```java
ObjectMapper objectMapper = new ObjectMapper().registerModule(new EnumerableModule());
```

`Enumerable` objects will now automatically be parsed and serialized by Jackson.

### Customizing the serialization method

There are two [serialization methods][SerializationMethod], as `String` value or as JSON `Object`.
By default, the module serializes to `String` values.
The serialization method can be customized as follows:

Reconfiguring an entire `ObjectMapper`:
```java
// Reconfigure the mapper to serialize Enumerables as JSON object, except CarBrand as String.
objectMapper = objectMapper.setConfig(objectMapper.getSerializationConfig()
        .with(ContextAttributes.getEmpty()
            .withSharedAttribute(
                    SerializationMethod.class.getName(),
                    SerializationMethod.AS_OBJECT.except(CarBrand.class)
        )));
```

Or an individual `Writer`:
```java
// Create a writer to serialize Enumerables as String, except CarBrand as JSON objects.
ObjectWriter writer = objectMapper.writer(ContextAttributes.getEmpty()
        .withSharedAttribute(
                SerializationMethod.class.getName(),
                SerializationMethod.AS_STRING.except(CarBrand.class)
        ));
```

### Mapping Enumerable objects to JSON

Given the following Java classes:
```Java
public final class CarBrand extends Enumerable {
    public static final CarBrand ASTON_MARTIN = new CarBrand("Aston martin");
    public static final CarBrand JAGUAR = new CarBrand("Jaguar");
    public static final CarBrand TESLA = new CarBrand("Tesla");
    // We all know there are more CarBrands than the ones we identified here... 
    // Not a good fit for a java.lang.Enum, but suitable for Enumerable.

    private CarBrand(String value) { super(value); }
}

public class Car {
    public CarBrand brand;
    public String type;
}    
```

### Parsing

The following JSON can be parsed using the [EnumerableModule]:
```json
{"brand": "Aston martin", "type": "DB-7"}
{"brand": "Porsche", "type": "911"}
```

But also:
```json
{"brand": {"value": "Aston martin"}, "type": "DB-7"}
{"brand": {"value": "Porsche"}, "type": "911"}
```

### Generating JSON

Serializing `Enumerable` subtypes will yield comparable results, depending on the chosen [SerializationMethod].
When omitted, the default [SerializationMethod] will be `AS_STRING` which produces the following JSON:
```json
{"brand": "Aston martin", "type": "DB-7"}
```

Choosing `AS_OBJECT` as [SerializationMethod] renders the `Enumerable` as a regular JSON object,
(including any public properties): 
```json
{"brand": {"value": "Aston martin"}, "type": "DB-7"}
```

You can even define exceptions to the chosen serialization method: `SerializationMethod.AS_STRING.except(CarBrand.class)`.
This serializes `CarBrand` enumerable values as JSON objects, while all other enumerables will be serialized as String.


  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware.enumerables%22

  [jackson]: https://github.com/FasterXML/jackson
  [json]: https://www.w3schools.com/js/js_json_intro.asp
  [EnumerableModule]: src/main/java/nl/talsmasoftware/enumerables/jackson2/EnumerableModule.java
  [SerializationMethod]: src/main/java/nl/talsmasoftware/enumerables/jackson2/SerializationMethod.java
