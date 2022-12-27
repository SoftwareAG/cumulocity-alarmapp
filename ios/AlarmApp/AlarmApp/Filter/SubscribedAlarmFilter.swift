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
        let rawSeverity =
            UserDefaults.standard.string(forKey: Keys.severity.rawValue) ?? C8yAlarm.C8ySeverity.critical.rawValue
        self.severity = C8yAlarm.C8ySeverity(rawValue: rawSeverity)
        let rawStatus = UserDefaults.standard.string(forKey: Keys.status.rawValue) ?? C8yAlarm.C8yStatus.active.rawValue
        self.status = C8yAlarm.C8yStatus(rawValue: rawStatus)
        self.alarmType = UserDefaults.standard.string(forKey: Keys.alarmType.rawValue)
        self.deviceName = UserDefaults.standard.string(forKey: Keys.deviceName.rawValue)
        self.resolvedDeviceId = UserDefaults.standard.string(forKey: Keys.deviceId.rawValue)
    }

    func persist() {
        if let severity = self.severity?.rawValue {
            UserDefaults.standard.set(severity, forKey: Keys.severity.rawValue)
        } else {
            UserDefaults.standard.set("all", forKey: Keys.severity.rawValue)
        }
        if let status = self.status?.rawValue {
            UserDefaults.standard.set(status, forKey: Keys.status.rawValue)
        } else {
            UserDefaults.standard.set("all", forKey: Keys.status.rawValue)
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
        self.severity = C8yAlarm.C8ySeverity.critical
        self.status = C8yAlarm.C8yStatus.active
        self.deviceName = nil
        self.resolvedDeviceId = nil
        self.alarmType = nil
        persist()
    }

    func isDefaultFilter() -> Bool {
        self.severity == C8yAlarm.C8ySeverity.critical
            && self.status == C8yAlarm.C8yStatus.active
            && self.alarmType == nil
            && self.deviceName == nil
    }
}
