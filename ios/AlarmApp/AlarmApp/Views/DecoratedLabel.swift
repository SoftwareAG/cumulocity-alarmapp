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
class DecoratedLabel: UIView {
    @IBOutlet var severityImage: UIImageView!
    @IBOutlet var severityLabel: UILabel!

    override init(frame: CGRect) {
        super.init(frame: frame)
        self.loadFromNib()
        self.severityLabel.alpha = UIFont.TextEmphasis.full.rawValue
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.loadFromNib()
        self.severityLabel.alpha = UIFont.TextEmphasis.full.rawValue
    }

    func bind(with severity: C8yAlarm.C8ySeverity?) {
        if let severity = severity {
            self.severityImage.image = severity.icon()
            self.severityImage.tintColor = severity.tint()
            self.severityLabel.text = severity.localised()
        }
    }

    func bind(with status: C8yAlarm.C8yStatus?) {
        if let status = status {
            self.severityImage.image = status.icon()
            self.severityLabel.text = status.localised()
        }
    }

    func bind(commentCount: Int?) {
        self.severityImage.image = nil
        self.severityLabel.text = ""
        if let count = commentCount {
            self.severityImage.image = UIImage(systemName: "message.circle.fill")
            if count > 10 {
                self.severityLabel.text = "9+"
            } else {
                self.severityLabel.text = String(describing: count)
            }
        }
    }
}
