# Cumulocity IoT Alarm App

The Cumulocity IoT Alarm App brings Alarms on your mobile phone. You'll see an overview about all Alarms raised on your Cumulocity IoT tenant, browse or filter the list of Alarms, collaborate by adding comments or modify the Alarm's severity or status.

Update or create an Alarm on your Cumulocity IoT tenant to trigger a push notification to be sent to your mobile phone. Open the notification to quickly see the Alarm on the Alarm App. A common use case is to inform certain users when problems occur at certain devices. 

The Cumulocity IoT Alarm App is available for Android and iOS. Open the app on your mobile phone, enter a Cumulocity IoT tenant along with your user credentials and start exploring.

<img src="./ios/screenshots/dashboard.png" width="180" /> <img src="./ios/screenshots/alarm_list.png" width="180" /> <img src="./ios/screenshots/alarm_details.png" width="180" /> <img src="./ios/screenshots/device_details.png" width="180" />

Enabling push notifications requires dedicated microservices to be deployed on your Cumulocity IoT tenant.

## Getting Started

The latest release can be found [here](https://github.com/SoftwareAG/cumulocity-alarmapp/releases/latest).

###### What does it contain?

The release contains binaries for Android and iOS:

- An unsigned `APK` file, which can be distributed to devices,
- An unsigned `IPA` file, which can be distributed, but must be signed before deployed to devices.

It does not contain microservices because certain properties need to be configured before you build them.

###### How to build the individual parts?

- [How to build for iOS?](./ios/README.md)
- [How to build for Android?](./android/README.md)
- [How to build and deploy the microservices?](./microservices/README.md)

## Contact

For all questions, please contact IoT-Analytics-Mobile-Accelerator@softwareag.com.

## Contribution

If you've spotted something that doesn't work as you'd expect, or if you have a new feature you'd like to add, we're happy to accept contributions and bug reports.

For bug reports, please raise an issue directly in this repository by selecting the `issues` tab and then clicking the `new issue` button. Please ensure that your bug report is as detailed as possible and allows us to reproduce your issue easily.

In the case of new contributions, please create a new branch from the latest version of `main`. When your feature is complete and ready to evaluate, raise a new pull request.

---

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.
