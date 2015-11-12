[![Build Status](https://travis-ci.org/hrytsenko/w3c-dom-facade.svg?branch=master)](https://travis-ci.org/hrytsenko/w3c-dom-facade)

# Summary

Facade for reading XML files using standard Java API.

This facade uses following main features: collections, optional values and runtime exceptions.
Therefore, it can be easily integrated with modern applications.

# Example

For example, we have the list of newsletters in XML format:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<fonts>
    <newsletter subject="Technology">
        <recipient email="john@any.org" name="John" />
        <recipient email="james@any.org" name="James" />
    </newsletter>
    <newsletter subject="Healthcare">
        <recipient email="john@any.org" name="John" />
        <recipient email="mark@any.org" name="Mark" />
        <recipient email="robert@any.org" name="Robert" />
    </newsletter>
</fonts>
```

Following code gets the email addresses for 'Healthcare':

```java
XmlElement newsletters = XmlElement.rootOf(newslettersXml);

List<String> emails = newsletters.findAll("//newsletter[@subject='Healthcare']/recipient").stream()
        .map(r -> r.attr("email")).collect(Collectors.toList());
```
