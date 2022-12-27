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

@IBDesignable
class AlarmImage: UIView {
    @IBOutlet var severityImage: UIImageView!
    @IBOutlet var statusIcon: UIImageView!
    @IBOutlet var countItem: MaterialLabel!
    @IBOutlet var cardView: MaterialCardView!

    override init(frame: CGRect) {
        super.init(frame: frame)
        self.loadFromNib()
        self.cardView.cardBackgroundColor = .onBackground
        self.countItem.textColor = .background
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.loadFromNib()
        self.cardView.cardBackgroundColor = .onBackground
        self.countItem.textColor = .background
    }

    func bind(with alarm: C8yAlarm?) {
        if let a = alarm {
            self.statusIcon.isHidden = a.status == .active
            self.statusIcon.image = a.status?.icon()
            if let severity = a.severity {
                self.severityImage.image = severity.icon()
                self.severityImage.tintColor = severity.tint()
                self.statusIcon.tintColor = severity.tint()
            }
            if let count = a.count {
                self.countItem.text = "\(count)"
            }
        }
    }
}
