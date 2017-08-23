[![Released Version][maven-img]][maven]

# Parameter conversion in JAX-RS

Enumerable values can be used in [JAX-RS] rest service parameters
annotated by `@PathParam`, `@QueryParam`, `@MatrixParam`, `@CookieParam` and `@HeaderParam`.  
For a less technical description of [JAX-RS] please see the [corresponding wikipedia article][wikipedia].

## Usage

### Obtaining the module

Add the `enumerables-jaxrs` dependency to your application.  
For example, in maven you can add the following dependency:
```xml
<dependency>
    <groupId>nl.talsmasoftware.enumerables</groupId>
    <artifactId>enumerables-jaxrs</artifactId>
    <version>[see maven-central badge]</version>
</dependency>
```

### Registering the provider

The [`EnumerableParamConverterProvider`][provider-source]
is annotated with `@Provider` so if it is managed by a Java-EE loader 
it should be automatically picked up by your [JAX-RS] implementation.  
In non-Java-EE environments it may be necessary to explicitly register the 
[provider class][provider-source] to the [JAX-RS] implementation you use.


  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware.enumerables%22
  [JAX-RS]: http://docs.oracle.com/javaee/6/tutorial/doc/giepu.html
  [wikipedia]: https://en.wikipedia.org/wiki/Java_API_for_RESTful_Web_Services

  [provider-source]: src/main/java/nl/talsmasoftware/enumerables/jaxrs/EnumerableParamConverterProvider.java
