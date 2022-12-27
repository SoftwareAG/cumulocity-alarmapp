# Cumulocity IoT Alarming App

## Installation and Setup

To build and run Alarming App, the following setup and tools are required:

1. Xcode 13.2+

### Cumulocity Swift Client SDK

The Cumulocity Swift Client SDK is integrated as a SwiftPackage from it's repository at [https://github.com/SoftwareAG/cumulocity-clients-swift](https://github.com/SoftwareAG/cumulocity-clients-swift). For SwiftPackage it is required to configure a [github.com](https://github.com) user account with access to the repository. The user account must be using a developer token for authentication.

It's recommended to setup the user account in Xcode before opening the project the first time in Xcode.

### Build

`Build` github worlflow creates an unsigned ipa. This build can be distributed, but must be signed before deployed to devices.

To sign the ipa, it is recommended to use [Fastlane](https://fastlane.tools). See documentation for [Fastlane resign](https://docs.fastlane.tools/actions/resign/) for more infos and config options, for example to change version number, display name, etc. when resigning.

```bash
> fastlane run resign ipa:./AlarmApp.ipa signing_identity:"<Keychain Identity of certificate>" provisioning_profile:<path provisioning profile>
```

> Only provide unsigned builds to customers. They have to sign the ipa using their own certificates and provisioning profiles.

## Contact

For all questions, please contact IoT-Analytics-Mobile-Accelerator@softwareag.com

## Contribution

If you've spotted something that doesn't work as you'd expect, or if you have a new feature you'd like to add, we're happy to accept contributions and bug reports.

For bug reports, please raise an issue directly in this repository by selecting the `issues` tab and then clicking the `new issue` button. Please ensure that your bug report is as detailed as possible and allows us to reproduce your issue easily.

In the case of new contributions, please create a new branch from the latest version of `main`. When your feature is complete and ready to evaluate, raise a new pull request.

---

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.
