[![Released Version][maven-img]][maven]

# Parameter conversion in JDBI

Enumerable values can be used in [JDBI] relational databases.

## Usage

### Getting the module

Add the following dependency to your project or download it from 
[maven central](http://repo1.maven.org/maven2/nl/talsmasoftware/enumerables/enumerables-jdbi/).
```xml
<dependency>
    <groupId>nl.talsmasoftware.enumerables</groupId>
    <artifactId>enumerables-jdbi</artifactId>
    <version>[see maven-central badge]</version>
</dependency>
```

### Mapping Enumerable query parameters

The [EnumerableArgumentFactory] can convert any `Enumerable` method argument into a `String` query parameter.  

You can add this [ArgumentFactory] to a `DBI` configuration simply by calling `dbi.registerArgumentFactory(new EnumerableArgumentFactory())`.

Alternatively, you can also add it per-repository (or even per-method)
by using the `@RegisterArgumentFactory` annotation:
```java
    @RegisterArgumentFactory(EnumerableArgumentFactory.class)
    @RegisterMapper(MyValueObjectMapper.class) // define ResultSetMapper for MyValueObject
    public interface MyDao {

        @SqlQuery("select ... as obj from ...  where type = :type")
        List<MyValueObject> queryWithType( @Bind("type") MyTypeEnumerable type);

    }
```

### Mapping Enumerable query results

The [EnumerableColumnMapperFactory] can convert a single-column query result into an `Enumerable`.

For instance, the following returns a list of all `CarBrand` values by registering the [ColumnMapperFactory] with JDBI:
```java
    jdbi.registerColumnMapper(new EnumerablecolumnMapperFactory());

    try (Handle handle = jdbi.open()) {
        List<CarBrand> carBrands = handle
                .createQuery("select distinct brand from cars")
                .mapTo(CarBrand.class)
                .list();
    }
```


  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware.enumerables%22
  [jdbi]: http://jdbi.org
  
  [EnumerableArgumentFactory]: src/main/java/nl/talsmasoftware/enumerables/jdbi/EnumerableArgumentFactory.java
  [EnumerableColumnMapperFactory]: src/main/java/nl/talsmasoftware/enumerables/jdbi/EnumerableColumnMapperFactory.java
  [argumentfactory]: http://jdbi.org/#_argumentfactory
  [columnmapperfactory]: http://jdbi.org/#_columnmapperfactory

