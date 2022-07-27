# liferay-spring-jndi-name Repository

So maybe you have seen my blog about using JNDI in Liferay: 
[Setting Up JNDI In Liferay 7.4](https://liferay.dev/blogs/-/blogs/setting-up-jndi-in-liferay-7-4)

In the blog, I show how to do some thread context class loader changes in order to perform
a JNDI lookup successfully as a module developer.

If you're a PortletMVC4Spring developer, though, you would probably love to use Spring's tags
to handle the lookup. They're super easy to use, it's basically just this in your context XML
file:

```xml
<jee:jndi-lookup id="myDataSource" jndi-name="java:comp/env/jdbc/TestDB" resource-ref="true"/>`
```

I mean, it just can't get any easier, right?

The problem, though, is that this tag just doesn't work under Liferay. The Spring implementation
just doesn't do the class loader manipulation that I pointed out from my blog post.

Well this repository solves that problem...

In this repository I have created a new Spring XML-compatible JNDI lookup tag. The tag is a little
more complicated to use, the new tag is:

```xml
<lrjee:jndi-lookup id="myDataSource" jndi-name="java:comp/env/jdbc/TestDB" resource-ref="true"/>`
```

It will likely take you forever to swap out the namespace and learn the new syntax, right? :wink:

## Building the Module

This builds into a single jar and it is using Maven for the artifact (sure, I could have used
Gradle, there's nothing special in the Maven pom.xml).

You may want to edit the properties in the pom.xml file to change the versions to match 
your target environment.

Use this jar as a dependency in your Spring portlet wars.

Your Spring context XML files will undergo a little change. From something like:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xmlns:jee="http://www.springframework.org/schema/jee" 
  xmlns:context="http://www.springframework.org/schema/context" 
  xsi:schemaLocation="http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/jee
      http://www.springframework.org/schema/jee/spring-jee.xsd
    http://www.springframework.org/schema/context
      http://www.springframework.org/schema/context/spring-context.xsd">
```

To the newer:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  xmlns:lrjee="http://www.liferay.com/schema/lrjee" 
  xmlns:context="http://www.springframework.org/schema/context" 
  xsi:schemaLocation="http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.liferay.com/schema/lrjee
      http://www.liferay.com/schema/lrjee/liferay-jee.xsd
    http://www.springframework.org/schema/context
      http://www.springframework.org/schema/context/spring-context.xsd">
```

Then it is just a matter of changing your `<jee:jndi-lookup />` tags to `<lrjee:jndi-lookup />` tags.

The new tag has all the same attributes as Spring's tag. In fact, the implementation (if you look at it)
is pretty much just an extension of Spring's version, but the modification uses the right class loader
for the lookup to work under Liferay.

## Gogo Testing

So I wanted to test my new lookup code before releasing it, so I built a handy Gogo command that
you can use if you deploy the module to liferay. Just drop the jar in the deploy folder and it
should get registered.

The command itself is pretty simple. To test a JNDI lookup, you just use:

```
jndi:lookup java:comp/env/jdbc/TestDB
```

And if it finds the match, it will report what type of object it found:

```
JNDI name java:comp/env/jdbc/TestDB found object 
  class org.apache.tomcat.dbcp.dbcp2.BasicDataSource
```

If it doesn't find the object, it will tell you so:

```
Error looking up java:/comp/env/jdbc/TestDB: Name [java:/comp/env/jdbc/TestDB] 
  is not bound in this Context. Unable to find [java:].
```

That's all there is to it!

Enjoy!
