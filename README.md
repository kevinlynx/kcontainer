kcontainer is a lightweight container sample.

1. import project into eclipse (existing maven project)
2. cd sample && compile.bat, compile test1 & test2 sample bundle and copy to `kcontainer/bundle` directory, make sure `bundle` directory exists
3. run kcontainer `Main` in eclipse

## Bundle

```
.
|-- bundle
    |-- test1
        |-- test1.prop
        |-- lib
        |   |-- abc.jar (if contains inner jar, unpack it)
        |   |-- def.jar
        |-- classes
|-- lib
```

