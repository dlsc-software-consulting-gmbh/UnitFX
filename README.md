[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=bugs)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=code_smells)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=ncloc)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=alert_status)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=security_rating)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=sqale_index)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=dlsc-software-consulting-gmbh_UnitFX&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=dlsc-software-consulting-gmbh_afterburner.fx)
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