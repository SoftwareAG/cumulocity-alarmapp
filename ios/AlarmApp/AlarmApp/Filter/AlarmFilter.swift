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

class AlarmFilter {
    var severity: C8yAlarm.C8ySeverity?
    var status: C8yAlarm.C8yStatus? = .active
    var deviceName: String? {
        didSet {
            if let value = self.deviceName {
                if value.isEmpty {
                    self.deviceName = nil
                }
            }
        }
    }
    var alarmType: String? {
        didSet {
            if let value = self.alarmType {
                if value.isEmpty {
                    self.alarmType = nil
                }
            }
        }
    }
}
