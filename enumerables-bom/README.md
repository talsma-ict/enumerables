[![Released Version][maven-img]][maven] 

# Enumerables bill-of-materials

To make sure your project uses consistent versions of the 
various `enumerables` modules, you can import this
_bill of materials_ into your maven project in a `depencencyManagement`
section of your build.

## How to import this bill-of-materials

Add the following dependency import to the `dependencyManagement`
section in your maven `pom.xml`
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>nl.talsmasoftware.enumerables</groupId>
            <artifactId>enumerables-bom</artifactId>
            <version>[see maven-central version above]</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

This does **not** add any dependencies to your project.  
However, it will make sure that dependencies to any `enumerables` modules
that _are_ there (either explicit or transient) will be of the correct version.


  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware.enumerables%22
