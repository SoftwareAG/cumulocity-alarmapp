//  Copyright (c) 2022 Software AG, Darmstadt, Germany and/or its licensors
//
//  SPDX-License-Identifier: Apache-2.0
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

import Combine
import CumulocityCoreLibrary
import IQKeyboardManagerSwift
import UIKit
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    private var cancellableSet = Set<AnyCancellable>()
    private var deviceToken: String?
    private var isSubcsribedForNotifications = false

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        M3Theme.setNavigationBarAppearance()
        IQKeyboardManager.shared.enable = true
        UNUserNotificationCenter.current().delegate = self
        return true
    }

    func registerForPushNotifications() {
        guard isSubcsribedForNotifications == false else {
            return
        }
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { _, _ in
            UNUserNotificationCenter.current().getNotificationSettings { settings in
                guard settings.authorizationStatus == .authorized else {
                    return
                }
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            }
        }
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        self.deviceToken = tokenParts.joined()
        self.subscribeOnTenant()
    }

    /// Called when globalFilter has been changed.
    func subscribeOnTenant() {
        if let token = self.deviceToken {
            let filter = SubscribedAlarmFilter.shared
            var tags: [String] = []
            if filter.filtersAllSeverity() {
                tags.append("severity:all")
            } else {
                for s in filter.severity {
                    tags.append("severity:\(s.rawValue.lowercased())")
                }
            }
            if filter.filtersAllStatus() {
                tags.append("status:all")
            } else {
                for s in filter.status {
                    tags.append("status:\(s.rawValue.lowercased())")
                }
            }
            tags.append("deviceId:\(filter.resolvedDeviceId ?? "all")")
            // can be comma separated!
            if let alarmType = filter.alarmType {
                for singleAlarmType in alarmType.components(separatedBy: ",") {
                    tags.append("type:\(singleAlarmType)")
                }
            } else {
                tags.append("type:all")
            }
            self.subscribeOnTenant(using: token, withTags: tags)
        }
    }

    private func subscribeOnTenant(using token: String, withTags tags: [String]?) {
        let bundleId = Bundle.main.bundleIdentifier ?? "test"
        var registration = DeviceRegistration()
        registration.userId = CumulocityApi.shared().userId
        registration.tags = tags
        registration.bundleId = bundleId
        registration.device = DeviceRegistration.Device()
        registration.device?.deviceToken = token
        registration.device?.deviceName = UIDevice.current.model

        let registrationsApi = RegistrationsApi(
            requestBuilder: Cumulocity.Core.shared.requestBuilder,
            withSession: Cumulocity.Core.shared.session
        )
        registrationsApi.subscribe(body: registration)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { _ in
                },
                receiveValue: { _ in
                    self.isSubcsribedForNotifications = true
                }
            )
            .store(in: &self.cancellableSet)
    }

    func unsubscribeOnTenant() {
        if let token = self.deviceToken {
            let registrationsApi = RegistrationsApi(
                requestBuilder: Cumulocity.Core.shared.requestBuilder,
                withSession: Cumulocity.Core.shared.session
            )
            registrationsApi.unsubscribe(userId: CumulocityApi.shared().userId, deviceToken: token)
                .receive(on: DispatchQueue.main)
                .sink(
                    receiveCompletion: { _ in
                    },
                    receiveValue: { _ in
                        self.isSubcsribedForNotifications = false
                    }
                )
                .store(in: &self.cancellableSet)
        }
    }

    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        // called when in foreground
        completionHandler(.newData)
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo
        let alarmId = userInfo["alarmId"]

        if let id = alarmId as? String {
            let alarmsApi = Cumulocity.Core.shared.alarms.alarmsApi
            alarmsApi.getAlarm(id: id)
                .receive(on: DispatchQueue.main)
                .sink(
                    receiveCompletion: { _ in
                    },
                    receiveValue: { value in
                        PushNotificationCenter.shared().receivedAlarm = value

                        let rootViewController = UIApplication.shared.keyWindow?.rootViewController
                        if let navigationController = rootViewController as? UINavigationController {
                            if let startController = navigationController.viewControllers[0] as? DashboardViewController
                            {
                                startController.performSegue(
                                    withIdentifier: UIStoryboardSegue.toAlarmDetails,
                                    sender: nil
                                )
                            }
                        }
                    }
                )
                .store(in: &self.cancellableSet)
        }
        completionHandler()
    }

    // MARK: UISceneSession Lifecycle

    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
}
