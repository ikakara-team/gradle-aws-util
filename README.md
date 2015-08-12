# gradle-aws-util

https://plugins.gradle.org/plugin/ikakara.awsutil.lambda

Description:
--------------
This gradle plugin is used to develop AWS Lambda functions and upload/manage them on AWS.

Development Setup:
--------------
1. Download and install Gradle 2.6 https://gradle.org/gradle-download/
  * Configure GRADLE_HOME
2. >gradle clean
3. >gradle -t build --info

Publishing Plugin:
--------------
Publish to mavenLocal()
```
> gradle install
```

Publish to gradle.plugin
```
> gradle publishPlugins
```

Plugin Usage:
--------------
Build script snippet for use in all Gradle versions:
```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
    mavenLocal()
  }
  dependencies {
    classpath "gradle.plugin.ikakara.awsutil:lambda:0.1.8"
  }
}

apply plugin: "ikakara.awsutil.lambda"
```

Build script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:
```
plugins {
  id "ikakara.awsutil.lambda" version "0.1.9"
}
```

Copyright & License:
--------------
Copyright 2014-2015 the original author or authors.

```
Apache 2 License - http://www.apache.org/licenses/LICENSE-2.0
```

History:
--------------
```
0.1.8 - first semi-working version
```
