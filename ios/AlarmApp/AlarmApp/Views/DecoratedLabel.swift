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
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.loadFromNib()
    }

    func bind(with severity: C8yAlarm.C8ySeverity?) {
        if let s = severity {
            self.severityImage.image = s.icon()
            self.severityImage.tintColor = s.tint()
            self.severityLabel.tintColor = s.tint()
            self.severityLabel.text = s.rawValue.capitalized
            self.severityLabel.textColor = s.tint()
            self.severityLabel.alpha = UIFont.TextEmphasis.full.rawValue
        }
    }

    func bind(commentCount: Int?) {
        self.severityImage.isHidden = commentCount == nil
        self.severityLabel.isHidden = self.severityImage.isHidden
        if let count = commentCount {
            self.severityImage.tintColor = UIColor.background.overlay(withColor: .onBackground, alpha: UIColor.Opacity.low)
            self.severityImage.image = UIImage(systemName: "message.fill")
            self.severityLabel.text = String(describing: count)
            self.severityLabel.textColor = .onBackground
            self.severityLabel.alpha = UIFont.TextEmphasis.medium.rawValue
        }
    }
}
