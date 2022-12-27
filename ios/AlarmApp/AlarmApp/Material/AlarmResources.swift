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
import UIKit

extension C8yAlarm.C8yStatus {
    func icon() -> UIImage? {
        switch self {
        case .acknowledged:
            return UIImage(named: "ic_alarm_acknowledged")

        case .cleared:
            return UIImage(named: "ic_alarm_cleared")

        default:
            return UIImage(named: "ic_alarm_active")
        }
    }

    func tint() -> UIColor? {
        switch self {
        case .acknowledged:
            return .alarmActionAcknowledge

        case .cleared:
            return .alarmActionClear

        default:
            return .primary
        }
    }

    func name() -> String? {
        switch self {
        case .acknowledged:
            return "Acknowledge"

        case .cleared:
            return "Clear"

        default:
            return "Active"
        }
    }
}

extension C8yAlarm.C8ySeverity {
    func icon() -> UIImage? {
        switch self.rawValue {
        case C8yAlarm.C8ySeverity.major.rawValue:
            return UIImage(named: "ic_alarm_major")

        case C8yAlarm.C8ySeverity.minor.rawValue:
            return UIImage(named: "ic_alarm_minor")

        case C8yAlarm.C8ySeverity.warning.rawValue:
            return UIImage(named: "ic_alarm_warning")

        default:
            return UIImage(named: "ic_alarm_critical")
        }
    }

    func tint() -> UIColor? {
        switch self.rawValue {
        case C8yAlarm.C8ySeverity.major.rawValue:
            return UIColor(named: "color_major") ?? .black

        case C8yAlarm.C8ySeverity.minor.rawValue:
            return UIColor(named: "color_minor") ?? .black

        case C8yAlarm.C8ySeverity.warning.rawValue:
            return UIColor(named: "color_warning") ?? .black

        default:
            return UIColor(named: "color_critical") ?? .black
        }
    }
}
