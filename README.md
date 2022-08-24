# lucene-transcoding-analyzer

This library is a Lucene analyzer designated to detect Devanagari and transliterated strings, and transcode them to SLP1 transliteration scheme.

This library is using libraries copyrighted by The Sanskrit (http://sanskritlibrary.org) Library and distributed under the Creative Commons Attribution Non-Commercial Share Alike license available
in full at http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode, and summarized at http://creativecommons.org/licenses/by-nc-sa/3.0/.

## Requirements

-  Java `8`
-  Maven `3.8` (for building from source)
-  transcodeFile.zip (for building from source)

## Build

```
[ ! -f transcodeFile.zip ] && wget http://sanskritlibrary.org/software/transcodeFile.zip
unzip transcodeFile.zip
mvn install:install-file -Dfile=./TranscodeFile/dist/lib/SanskritLibrary.jar -DgroupId=org.sanskritlibrary -DartifactId=sl -Dversion=0.1 -Dpackaging=jar
cd ./lucene-transcoding-analyzer/
mvn clean install -DskipTests
```

## History

The module was used in the development of [SARIT](https://sarit.indology.info) via [sarit-existdb](https://github.com/sarit/sarit-existdb).
Historic links to this repo or its artifact might have used either `exc-asia-and-europe` as the org name for the source code repo, or `org.sanskritlibrary` as `<groupId>` for build dependencies pipelines.

Since its inception, the GitHub org was renamed to `HCTS` and the packages have been republished to the GitHub Packages registry attached to this repo. 
To declare a dependencie use:

```xml
<dependencies>
  <dependency>
    <groupId>de.uni-hd.hra</groupId>
    <artifactId>lucene-transcoding-analyzer</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

see [GH Apache Maven registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)