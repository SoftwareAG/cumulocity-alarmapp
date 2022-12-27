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

extension UISegmentedControl {
    func bind(with severity: C8yAlarm.C8ySeverity?) {
        if let s = severity {
            switch s.rawValue {
            case C8yAlarm.C8ySeverity.critical.rawValue:
                self.selectedSegmentIndex = 0

            case C8yAlarm.C8ySeverity.major.rawValue:
                self.selectedSegmentIndex = 1

            case C8yAlarm.C8ySeverity.minor.rawValue:
                self.selectedSegmentIndex = 2

            case C8yAlarm.C8ySeverity.warning.rawValue:
                self.selectedSegmentIndex = 3

            default:
                self.selectedSegmentIndex = 4
            }
        } else {
            self.selectedSegmentIndex = 4
        }
    }

    func storeSeverity(on filter: AlarmFilter) {
        switch selectedSegmentIndex {
        case 0:
            filter.severity = C8yAlarm.C8ySeverity.critical

        case 1:
            filter.severity = C8yAlarm.C8ySeverity.major

        case 2:
            filter.severity = C8yAlarm.C8ySeverity.minor

        case 3:
            filter.severity = C8yAlarm.C8ySeverity.warning

        default:
            filter.severity = nil
        }
    }

    func bind(with status: C8yAlarm.C8yStatus?) {
        if let s = status {
            switch s.rawValue {
            case C8yAlarm.C8yStatus.active.rawValue:
                self.selectedSegmentIndex = 0

            case C8yAlarm.C8yStatus.acknowledged.rawValue:
                self.selectedSegmentIndex = 1

            case C8yAlarm.C8yStatus.cleared.rawValue:
                self.selectedSegmentIndex = 2

            default:
                self.selectedSegmentIndex = 3
            }
        } else {
            self.selectedSegmentIndex = 3
        }
    }

    func storeSatus(on filter: AlarmFilter) {
        switch selectedSegmentIndex {
        case 0:
            filter.status = C8yAlarm.C8yStatus.active

        case 1:
            filter.status = C8yAlarm.C8yStatus.acknowledged

        case 2:
            filter.status = C8yAlarm.C8yStatus.cleared

        default:
            filter.status = nil
        }
    }
}
