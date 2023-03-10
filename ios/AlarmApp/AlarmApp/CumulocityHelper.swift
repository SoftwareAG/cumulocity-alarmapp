//  Copyright (c) 2023 Software AG, Darmstadt, Germany and/or its licensors
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

import UIKit

enum CumulocityHelper {
    static var alarmDateFormatter = DateFormatter(format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    static var printFormatter = DateFormatter(format: %"alarm_time_printformat")

    static func toReadableDate(_ s: String) -> String {
        if let convertedDate = alarmDateFormatter.date(from: s) {
            return printFormatter.string(from: convertedDate)
        } else {
            return s
        }
    }

    static func toReadableDate(_ date: Date) -> String {
        printFormatter.string(from: date)
    }

    /// creates an URL encoded query to search for a deviceName
    /// to be used for querying managed objects by it's name
    static func queryBy(deviceName: String) -> String {
        let encodedDeviceName = deviceName.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)
        return "$filter=name eq \(encodedDeviceName ?? "")"
    }
}

extension DateFormatter {
    convenience init(format: String, locale: Locale = Locale(identifier: %"alarm_time_locale")) {
        self.init()
        self.dateFormat = format
        self.locale = locale
    }
}
