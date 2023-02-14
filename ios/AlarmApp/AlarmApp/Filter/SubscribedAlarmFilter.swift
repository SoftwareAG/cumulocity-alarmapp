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

import CumulocityCoreLibrary
import Foundation

class SubscribedAlarmFilter: AlarmFilter {
    private enum Keys: String {
        case severity
        case status
        case alarmType
        case deviceName
        case deviceId
    }

    static let shared = SubscribedAlarmFilter()

    var resolvedDeviceId: String?

    override private init() {
        super.init()
        if let severity = UserDefaults.standard.value(forKey: Keys.severity.rawValue) as? [String] {
            self.severity = severity.compactMap { C8yAlarm.C8ySeverity(rawValue: $0) }
        } else {
            self.severity = [C8yAlarm.C8ySeverity.critical]
        }
        if let status = UserDefaults.standard.value(forKey: Keys.status.rawValue) as? [String] {
            self.status = status.compactMap { C8yAlarm.C8yStatus(rawValue: $0) }
        } else {
            self.status = [C8yAlarm.C8yStatus.active]
        }
        self.alarmType = UserDefaults.standard.string(forKey: Keys.alarmType.rawValue)
        self.deviceName = UserDefaults.standard.string(forKey: Keys.deviceName.rawValue)
        self.resolvedDeviceId = UserDefaults.standard.string(forKey: Keys.deviceId.rawValue)
    }

    func persist() {
        if self.severity.isEmpty {
            UserDefaults.standard.removeObject(forKey: Keys.severity.rawValue)
        } else {
            UserDefaults.standard.set(self.severity.map { $0.rawValue }, forKey: Keys.severity.rawValue)
        }
        if self.status.isEmpty {
            UserDefaults.standard.removeObject(forKey: Keys.status.rawValue)
        } else {
            UserDefaults.standard.set(self.status.map { $0.rawValue }, forKey: Keys.status.rawValue)
        }
        if let alarmType = self.alarmType {
            UserDefaults.standard.set(alarmType, forKey: Keys.alarmType.rawValue)
        } else {
            UserDefaults.standard.removeObject(forKey: Keys.alarmType.rawValue)
        }
        if let deviceName = self.deviceName {
            UserDefaults.standard.set(deviceName, forKey: Keys.deviceName.rawValue)
        } else {
            UserDefaults.standard.removeObject(forKey: Keys.deviceName.rawValue)
        }
        if let resolvedDeviceId = resolvedDeviceId {
            UserDefaults.standard.set(resolvedDeviceId, forKey: Keys.deviceId.rawValue)
        } else {
            UserDefaults.standard.removeObject(forKey: Keys.deviceId.rawValue)
        }
    }

    func setToDefault() {
        self.severity = [C8yAlarm.C8ySeverity.critical]
        self.status = [C8yAlarm.C8yStatus.active]
        self.deviceName = nil
        self.resolvedDeviceId = nil
        self.alarmType = nil
        persist()
    }
}
