[![Released Version][maven-img]][maven]

# Creating proper Swagger Models for Enumerable objects

Enumerable values can be part of your [Swagger] API.  
By default, [swagger] documents `Enumerable` values as a JSON object model, while
normally these will be serialized as `String`.
Furthermore, an important point of Enumerable values is documenting the known values.
These should therefore be described in the swagger model.

## Usage

### Getting the module

Add the following dependency to your project or download it from 
[maven central](http://repo1.maven.org/maven2/nl/talsmasoftware/enumerables/enumerables-swagger/).
```xml
<dependency>
    <groupId>nl.talsmasoftware.enumerables</groupId>
    <artifactId>enumerables-swagger</artifactId>
    <version>[see maven-central badge]</version>
</dependency>
```

### Enabling the module in your Swagger API

Swagger configuration is static in nature,
therefore there is only _one_ configuration for your entire application.    
Register the `EnumerableModelConverter` to your application initialization to enable
swagger models for Enumerable objects:

```java
import io.swagger.converter.ModelConverters;
import nl.talsmasoftware.enumerables.swagger.EnumerableModelConverter;

// Somewhere in your application initialization:
ModelConverters.getInstance().addConverter(new EnumerableModelConverter());
```

That should be all there is to it.


  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware.enumerables%22

  [swagger]: https://swagger.io/
  [jackson]: https://github.com/FasterXML/jackson
  [json]: https://www.w3schools.com/js/js_json_intro.asp
  [EnumerableModule]: src/main/java/nl/talsmasoftware/enumerables/jackson2/EnumerableModule.java
  [SerializationMethod]: src/main/java/nl/talsmasoftware/enumerables/jackson2/SerializationMethod.java
