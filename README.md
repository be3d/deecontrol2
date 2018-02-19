# DeeControl 2
The DeeControl 2 application is a tool to quickly and easily prepare 3D model to be printed over YSoft SafeQ print management system.
With integrated gcode viewer, user can see how the model will be printed, detect problem parts and eventually adjust print settings.

The application is written in Java based on Spring Framework and JavaFX. The slicing part of the process is done by CuraEngine developed by Ultimaker.

## License

The DeeControl 2 is released under terms of the AGPLv3 License. License text can be found at [https://www.gnu.org/licenses/agpl-3.0.en.html](https://www.gnu.org/licenses/agpl-3.0.en.html)

## Dependencies

- Gradle - build tool [installation guide](https://gradle.org/install/)
- [CuraEngine](https://github.com/Ultimaker/CuraEngine) v2.7 - slicing engine developed by Ultimaker
- Java 1.8_130 and higher - [download](https://java.com/en/download/)

## Prerequisities
- Put CuraEngine binary to `./bin/cura` folder

## Run from source code
```shell
gradle clean run
```

## Build application
```shell
gradle clean build
```

## Create portable zip
```shell
gradle clean createPortable
```
This command creates archive named `dctrl-<version>-portable.zip` in `build/distribution` folder with all needed libs and binaries.
The unzipped application can be run by a start up script in a bin folder.