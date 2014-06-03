## Overview

This Jackson extension handles reading and writing of data encoded in [Smile](http://wiki.fasterxml.com/SmileFormatSpec) data format ("binary JSON").
It extends standard Jackson streaming API (`JsonFactory`, `JsonParser`, `JsonGenerator`), and as such works seamlessly with all the higher level data abstractions (data binding, tree model, and pluggable extensions).

[![Build Status](https://fasterxml.ci.cloudbees.com/job/jackson-dataformat-smile-master/badge/icon)](https://fasterxml.ci.cloudbees.com/job/jackson-dataformat-smile-master/)

## Status

Module is fully usable, but depends on Jackson 2.0; older 1.x version is still available via Codehaus, and Jackson 1.x Download page.

## Maven dependency

To use this module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-smile</artifactId>
  <version>2.4.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

## Usage

Basic usage is by using `SmileFactory` in places where you would usually use `JsonFactory`:

```java
SmileFactory f = new SmileFactory();
// can configure instance with 'SmileParser.Feature' and 'SmileGenerator.Feature'
ObjectMapper mapper = new ObjectMapper(f);
// and then read/write data as usual
SomeType value = ...;
byte[] smileData = mapper.writeValueAsBytes(value);
SomeType otherValue = mapper.readValue(smileData, SomeType.class);
```

## Related

* [Smile-format discussion group](https://groups.google.com/forum/#!forum/smile-format-discussion)
