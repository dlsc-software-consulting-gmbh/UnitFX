[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/unitfx)

# UnitFX

UnitFX is a lightweight framework for creating textfield input controls based on a "unit of measure" UOM.
Currently the only documentation is the source code itself. Please take a look at the file `DemoApp.java` to find
out how to use the framework.

`QuantityInputField` will perform validation and conversion between different units out-of-the-box. A "base" unit can be 
set on a textfield and the field will be highlighted when the user changes the textfield's unit to something
different than the base unit.

*At least **JDK 11** is required.*

![screenshot of demo_app](docs/images/demo.png) 

### Building

**JDK 17** is required to build this project. Use the provided Maven wrapper to launch the build

```
$ ./mvnw verify
```
