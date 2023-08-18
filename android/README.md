# How to build for Android

## Installation and Setup

The following tools are required to build the App:

- Android Studio Chipmunk or later
- Java 8

## Configuration

##### How to work with self-signed SSL certificates?
##### How to add support for languages?

## Build

The App can be built using the command line or using Android Studio. 

1) Goto project root directory and open command prompt and execute the command `gradlew build`.
2) Alternativly, open the App in Android Studio, it will automatically build the project. 

The created `APK` is located at this path `../app/build/outputs/apk`.

The latest release can be found [here](https://github.com/SoftwareAG/cumulocity-alarmapp/releases/latest).

**This build contains an unsigned APK.**

## Code Sign

Open the App in Android Studio:

- Find the `Build` menu and select `Generate signed bundle/APK.`,
- Select `Apk` and click `next`,
- In keystore path, choose keystore `path(.jks)`, 
-- Create a new keystore if its not present.
- Enter keystore password, key alias and key password,
- Click `next`, select `Release` and click `create`.

Alternativly, use the command line. Goto root project directory and update the `build.gradle` to include the below lines and update the keystore details:

```
android {
    signingConfigs {
        release {
            storeFile file('path/to/your/keystore.jks')
            storePassword 'your_keystore_password'
            keyAlias 'your_key_alias'
            keyPassword 'your_key_password'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

Open command prompt and execute the command `gradlew assembleRelease`.

This will create a signed apk at the path `../app/build/outputs/apk/release`.