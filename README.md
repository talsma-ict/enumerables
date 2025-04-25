[![Maven Version][maven-img]][maven]
[![Javadoc][javadoc-img]][javadoc]
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=talsma-ict_enumerables&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=talsma-ict_enumerables)
[![Coverage Status][coveralls-img]][coveralls]

# Enumerables

Enumerables are similar to standard Java `Enum` types with the added ability 
to parse 'yet unknown' values.  
They are invaluable if you attempt to build a stable API.

## Getting the library

Add the following dependency to your project or download it 
[from maven central](https://repo1.maven.org/maven2/nl/talsmasoftware/enumerables/enumerables/).
```xml
<dependency>
    <groupId>nl.talsmasoftware.enumerables</groupId>
    <artifactId>enumerables</artifactId>
    <version>[see maven-central badge]</version>
</dependency>
```

## Example

A working example of an `Enumerable` type:

```java
public final class CarBrand extends Enumerable {
    public static final CarBrand ASTON_MARTIN = new CarBrand("Aston martin");
    public static final CarBrand JAGUAR = new CarBrand("Jaguar");
    public static final CarBrand TESLA = new CarBrand("Tesla");
    // We all know there are more CarBrands than the ones we identified here... 
    // Not a good fit for a java.lang.Enum, but suitable for Enumerable.

    private CarBrand(String value) { super(value); }
}
```

We can interact with `CarBrand` similar to `Enum`: 
 - `CarBrand[] knownCarBrands = Enumerable.values(CarBrand.class);`
 - `assert CarBrand.ASTON_MARTIN == Enumerable.valueOf(CarBrand.class, "ASTON_MARTIN");`
 - `assert CarBrand.JAGUAR.ordinal() == 1;`
 - `assert CarBrand.TESLA.name().equals("TESLA");`
 
But you can also:
 - `assert CarBrand.ASTON_MARTIN.getValue().equals("Aston martin");`
 - Parse another 'unknown' car brand: `CarBrand porsche = Enumerable.parse(CarBrand.class, "Porsche");`
 - Compare all `CarBrand` instances with each-other.  
   Constants sort in declaration order before 'unknown' values,
   which in turn sort alphabetically, case-insensitive.
 - Consistent with the sorting order, `ordinal()` of non-constants are _by definition_:  
   `assert porsche.ordinal() == Integer.MAX_VALUE;`
 - As they aren't defined by a constant, `name()` of non-constants _always_ returns `null`:  
   `assert porsche.name() == null;`

## Parsing and printing

```java
public static <E extends Enumerable> E parse(Class<E> type, CharSequence value);
```

The parse method first compares the given value with the values of all known constants 
for the specified `Enumerable` type. This results in a constant reference in most cases.
A new object instance is only created using the `String` constructor for non-constant values.

The counterpart of parsing, _printing_ is also covered which simply returns the value:

```java
public static String print(Enumerable enumerable);
```

## Enum-like behaviour

### Enumerable.valueOf()

```java
public static <E extends Enumerable> E valueOf(Class<E> type, CharSequence name)
        throws ConstantNotFoundException;
```

This method is comparable with the `Enum.valueOf(Class, String)` method.

This method returns the constant with the specified `name`.
If there is no constant within the requested `type` found by that `name`,
the method will throw a `ConstantNotFoundException`.

Please note that this method looks at the constant `name` and **not** the String `value` 
of this enumerable object. To obtain an enumerable object from a specific `value`, 
please use the `parse(Class, CharSequence)` method instead.
That method will not throw any exceptions for yet-unknown values, but returns a
new enumerable instance containing the `value` instead.

### Enumerable.values()

```java
public static <E extends Enumerable> E[] values(Class<E> enumerableType);
```

This method is comparable with the `Enum.values()` method.

It finds all public constants of the requested `enumerableType`
that have been declared in the class of the enumerable itself.

These constants must be **public static final** fields of the own Enumerable type.

### Enumerable.ordinal()

```java
public int ordinal();
```

Returns the 'enum ordinal' for an enumerable value.

For a constant value, this method returns the index of the constant within the `Enumerable.values()` array.

For non-constant values, this method will always return `Integer.MAX_VALUE`.
There are various reasons for this choice, but the most obvious one is that the `#compareTo(Enumerable)`
implementation is greatly influenced by this `ordinal` value, 
automatically sorting all constants before any non-constant parsed values.

### Enumerable.name()

```java
public String name();
```

Returns the 'enum constant name' for an enumerable value.

For a constant value, this method returns the `name` of the defined constant as defined in the code (not its `value`).

For non-constant values, this method will always return `null`.

## Serialization / deserialization

_Serialization_: The `Enumerable` implements `Serializable`. 
This means that a concrete subclass is serializable if it does not contain 
any non-serializable and non-transient fields. 
Note that additional class fields should be either specified in the 
constant declaration or be deducable from the `String` constructor 
if they carry meaning after deserialization.

_Deserialization_: Similar to parsing, a deserialized `Enumerable` object 
resolves back to a listed constant reference if its value matches the constant.  
Only unanticipated values will result in new objects.

## Add-on modules

### Validation

The [enumerables-validation](enumerables-validation) module provides several annotations as
`javax.validation` constraints.

### JAX-RS

The [enumerables-jaxrs](enumerables-jaxrs) module provides an 
[enumerable parameter converter provider](enumerables-jaxrs/src/main/java/nl/talsmasoftware/enumerables/jaxrs/EnumerableParamConverterProvider.java)
for JAX-RS.

### JSON serialization

The [enumerables-jackson2](enumerables-jackson2) and [enumerables-gson](enumerables-gson)
modules provide serialization and deserialization functionality to and from [json].  
[Jackson] also supports other common formats such as [yaml].

### Swagger documentation

The [enumerables-swagger](enumerables-swagger) module provides [Swagger] API model documentation
for `Enumerable` types, including examples.

## Background

The `Enumerable` superclass is **very** similar to a standard Java `Enum` type
but allows representing _currently unknown_ values as well.

### Why represent 'currently unknown' values?

Have you ever had to support an API that returns an actual `Enum` value?  
Can you remember what happened when the product owner decided that
an additional value was needed for something?  
\***Bang!**\* there goes your API compatibility: 
You will either have to tell all your existing customers _"sorry, the api is now broken"_ 
or create a new version **beside** the existing API and declare the old one deprecated.  
Even with a new API version, you still have to think about handling the new value in the old version
or create a special exception for this case.  
This is nasty in any conceivable scenario!  

### Change the `Enum` to a `String`, problem solved?  

You still want to document all those constant values that you _do_ know, right?  
So now there's documentation or best-case a list of constants informing users which values are _special_.
You also lose all other `Enum` advantages.

### Conclusion 

That is exactly the reason we've created the `Enumerable` type.
It is similar to `Enum` in that it represents a number of _known constants_ 
that can be validated, iterated over and used as constants from code.
By providing a way to parse  _yet-unknown additional values_ it allows API
designers to make a slightly lighter promise to users:  
I _currently_ know of these possible values which have meaning in the API,
but please be prepared to receive any 'other' value as well (handle as you feel fit).  
Introducing a new value in a new API release does not break the
existing contract since the consumer should have already anticipated unknown values.

## License

[Apache 2.0 license](LICENSE)

  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables
  [maven]: https://mvnrepository.com/artifact/nl.talsmasoftware.enumerables
  [javadoc-img]: https://www.javadoc.io/badge/nl.talsmasoftware.enumerables/enumerables.svg
  [javadoc]: https://www.javadoc.io/doc/nl.talsmasoftware.enumerables/enumerables
  [coveralls-img]: https://coveralls.io/repos/github/talsma-ict/enumerables/badge.svg
  [coveralls]: https://coveralls.io/github/talsma-ict/enumerables

  [json]: https://www.w3schools.com/js/js_json_intro.asp
  [yaml]: https://yaml.org/
  [jackson]: https://github.com/FasterXML/jackson
  [swagger]: https://swagger.io/
