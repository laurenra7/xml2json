# xml2json
Convert XML to JSON using the [FasterXML Jackson library](https://github.com/FasterXML/jackson).

Because the FasterXML Jackson library doesn't fully represent all kinds of XML documents, there are some quirks.

### Arrays do not convert properly
FasterXML doesn't distinguish between objects and arrays so if the XML has repeating objects it treats them as duplicates of the same object and only grabs the last one. For this reason, XML arrays don't convert properly. Only the last item in the array is converted. (Other parsers recognize that repeating XML elements at the same node level are an array of elements and parse them as such.) See the [author's response to Issue 187](https://github.com/FasterXML/jackson-dataformat-xml/issues/187).

### Build

Build with [Maven](https://maven.apache.org/).

```
mvn clean install
```

Produces an executable .jar file

```
/target/xml2json.jar
```


### Run

```
java -jar xml2json.jar
```


### Options

```
usage: java -jar xml2json.jar ['XML to convert'] [-f <filename>] [-h] [-o <filename>]
       [-v]

Convert XML to JSON

 -f,--file <filename>     XML file to convert
 -h,--help                Show this help
 -o,--output <filename>   output file
 -v,--verbose             show processing messages

Examples:

  java -jar xml2json.jar -f movie.xml

  java -jar xml2json.jar -f movie.xml -o movie.json

  java -jar xml2json.jar -o movie.json '<movie><title>Ant Man</title></movie>'

  java -jar xml2json.jar '<?xml version="1.0"?><movie><title>Ant Man</title></movie>'
```
