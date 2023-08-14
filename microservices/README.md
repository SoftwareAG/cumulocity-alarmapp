# Cumulocity IoT Push Gateway

## Introduction

The Cumulocity IoT Push Gateway is a microservice extension to the Cumulocity IoT platform. It enables mobile applications to receive push notifications sent from Cumulocity IoT.

> :information_source: Push notifications in general target delivery of information from a software application to a mobile client application. In contrast to client-server communication, push notifications are sent without a specific request from the client. A mobile application is identified by a unique `device token`, which needs to be obtained from a mobile platform notification provider: Either Apple Push Notification Service (APNS) for iOS devices or Firebase for Android devices.

Our key goal is to support push notifications with Cumulocity IoT for iOS and Android devices. Strictly speaking, the typical use case is supporting a Cumulocity IoT application sending a push notification to a user’s device. Additional requirements are:

- Users may register more than one device (e.g. iPhone & iPad & Android phone),
- Sending of messages, sounds as well as custom payloads,
- Support managing mobile devices and its user association outside of Cumulocity IoT Device Management.

###### Integrating Microsoft Azure

The Cumulocity IoT Push Gateway integrates [Microsoft Azure Notification Hub](https://azure.microsoft.com/de-de/services/notification-hubs/): A scalable push engine that enables you to send push notifications to any platform from any backend. The Notification Hub supports one particular mobile application, identified by it's `bundle id` and stores device tokens associated with this mobile application. 
*An external system to manage devices and send push notifications will be named `notification provider` in this document.*

> **Limitation:** The Push Gateway only connects to one notification hub and thus only one mobile application is supported.

Sending a device token to the Notification Hub is called an installation (or registration). Any installation supports a list of `tags` - basically named topics. The Notification Hub supports up to `60` tags per installation. Please refer to the Azure documentation about [tags](https://learn.microsoft.com/en-us/azure/notification-hubs/notification-hubs-tags-segment-push-message) to learn more about it.

###### Using tags to target users

Tags can be used to send push notifications to a list of certain users: A boolean expression using tags will be evaluated by Azure. Tags can be chained to so called `tag expressions` supporting common operators like `AND (&&)`, `OR (||)`, and `NOT (!)`.

> **Limitation:** Tag expressions can contain all Boolean operators, such as AND (&&), OR (||), and NOT (!). They can also contain parentheses. Tag expressions are limited to 20 tags if they contain only ORs; otherwise they are limited to 6 tags.

###### Triggering Push Notifications

One requirement to send push notifications is to configure the connection to the Notification Hub: A security token as well as the name of the connection hub is required. Both pieces of information are stored within the tenant options and described below in this document.

On a second note, the Push Gateway itself does not automatically trigger push notifications. Any Cumulocity IoT client application (a microservice, Apama app, webMethods.io workflow, or other third party tool) may use the RESTful API of the Push Gateway to trigger push notifications.

The repository contains an example microservice (see [Push Message Emitter](./c8y-push-message-emitter)), which receives updates on `Alarm` objects and forwards those updates to the Push Gateway in order to trigger a push notification for the updated `Alarm`. This microservice can be deployed with a filter configuration, e.g. to only forward the `Alarm` if a certain type, severity or status is matched. The filter is implemented by using `tag` `expressions`.

###### OpenAPI

An OpenAPI specification of all RESTful services provided by the Push Gateway can be found [here](./c8y-push-api/openapi.yml). 

## Build

Building the Push Gateway requires a Notification Hub to be existing first. Copy the `Manage,Listen,Send` permission as well as the `name` of the Hub to the `application-prod.properties` file located within the [push-bundle](./c8y-push-gateway/push-bundle) module. 

```
defaultsettings.hub=<Name of your Notification Hub>
defaultsettings.connection=<Value of the Managed,Listen,Send permission>
```

Afterwards execute the Maven goal `install` on the root directory at `./c8y-push-gateway`.

```
mvn clean install
```

Now, build the [Push Message Emitter](./c8y-push-message-emitter) by calling the `install` goal.

> Make sure the following tools [Docker](https://www.docker.com/) and [Maven](https://maven.apache.org/) are installed on your machine.

## Deploy

Deploy the microservice in the following order:

1. [Push Gateway](./c8y-push-gateway)
2. [Push Message Emitter](./c8y-push-message-emitter)

## Workflow

### Obtaining device tokens

Mobile applications need to register itself to a mobile platform notification provider: A secure and efficient service to propagate information to iOS and Android devices, using the APNS or Firebase, respectively. The registration process results in a device token which is used to identify the mobile application.

Detailed information about obtaining the device token can be found below:

- [Firebase registration](https://firebase.google.com/docs/cloud-messaging/android/client),
- [APNS registration](https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_token-based_connection_to_apns).

### Register mobile applications to the Push Gateway

Once the device token is obtained, the mobile application can be registered to the Push Gateway using a RESTful service. Typically, the mobile application uses this service directly to register itself to the push gateway:

`POST /registrations`
```json
{
    "userId": "string",
    "bundleId": "string"
    "tags": [
        "string"
    ],
    "device": {
        "deviceToken": "APNS or firebase token",
        "platform": "iOS or Android"
    }
}
```

In addition to the `device token`, the `bundle id` and mobile `platform` name is required as well as the Cumulocity IoT `user id`. Each registration is forwarded and stored in the notification hub. The device token can then be found in your Cumulocity IoT user profile or obtained via REST API.

The meaning of `tags` is by design not specified. `tags` are used to add additional information about the registration, e.g. severity or status of Alarms. The client application may filter registrations by `tag` to deliver a push notification to the correct registrations. Using Microsoft Azure as notification provider for example allows to chain tags using boolean operators. `tags` are stored within the device installation on your Azure cloud.

Additional RESTful services to manage registrations:

- `GET /registrations?tag=string` Lists all registrations. Can be optionally filtered by a tag.
- `GET /registrations/{userId}` Lists all registrations for a Cumulocity IoT user id 
- `DELETE /registrations` Removes all registrations
- `DELETE /registrations/{deviceToken}` Remove the registration for a particular installation

### Send push notifications to mobile devices

Use the following service to trigger push notifications. Each request will be forwarded to the Azure notification hub.

- `POST /notification` Create a send request for a new push notification
    ```json
    {
        "receiver": [
        	"deviceTokens": [ 
                "string"
            ], 
            "userIds": [ 
                "Cumulocity IoT user id"
            ]
        ],
        "tags": [
            "string"
        ],
        "message": {
            "title": "string",
            "body": {
                "alarmId": "string"
            }
        }
    }
    ```
    
A push notification is sent to each registrations as long as `deviceTokens` or `userIds` are not set explicitly. The use of `deviceTokens` takes precedence over `userIds`. `tags` can be used to filter push notifications. If specified, receivers registrations do need to specify the same tags.

`message` is a required property and must contain one `alarmId`. The Push Gateway will load the Alarm by it's id and hand it over as payload of the push notification.

### Configure Azure notification hub

The authorization keys for connecting to the Azure notification hub must be configured within the `application.properties` file once before deploying the microservice.

```
defaultsettings.hub=string
defaultsettings.connection=string
```

Once the microservice is deployed, the configuration can be found within the tenant properties. At runtime, the Azure connection can be obtained and configured via the following RESTful API:

- `GET /configuration` Get the current Azure notification hub authorization keys
- `PUT /configuration` Updates the Azure notification hub authorization keys
    ```json
    {
        "hub": "string",
        "connection": "string"
    }
    ```
    
The notification hub needs to authorise against a push notification provider: Register for Firebase Cloud Messaging and add the `API key` within `Settings` > `Google (GCM/FCM)`. To authorise against APNS, create a `p8` token on the Apple Developer Console and add within `Settings` > `Appke (APNS)`.

## Local deployment

When deploying the Push Gateway in a local environment, make sure to specify the properties listed below in your `application-dev.properties` file:

```
C8Y.baseURL=tenant url
C8Y.bootstrap.user=string
C8Y.bootstrap.password=string
C8Y.bootstrap.register=true
C8Y.bootstrap.tenant=tenant id
C8Y.service.user=string
C8Y.service.password=string
```

And make sure to reference the active profile in your `application.properties` file like:

```
# Available profiles: dev, test, prod
spring.profiles.active=dev
```

###### Nice to know

- bootstrap credentials can be accessed using `GET /application/applications/{id}/bootstrapUser`
	- make sure to have a deployed version on your tenant to get the application id
- service user credentials can be accessed using `GET /application/currentApplication/subscriptions`
	- the bootstrap user must be used for basic authentication 
