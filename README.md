Sling CoffeeScript Compiler

This project provides a tag lib and a service for compiling CoffeeScript into JavaScript on the fly in Sling.

Just include the following tag library in you JSP
<%@taglib prefix="coffee" uri="http://sling.apache.org/taglibs/sling/coffee/1.0.0"%>

Then add the tag to compile CoffeeScript to JavaScript from a specific path

<coffee:coffee path="/content/coffee/demo/demo.coffee" wrapWithTag="script"/>

# License

This project is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).