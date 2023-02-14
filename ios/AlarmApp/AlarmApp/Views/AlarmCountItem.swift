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

@IBDesignable
class AlarmCountItem: UIView {
    @IBOutlet var titleLabel: MaterialLabel!
    @IBOutlet var countLabel: MaterialLabel!

    @IBInspectable var titleText: String = "" {
        didSet {
            self.titleLabel?.text = titleText
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        self.commonInit()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.commonInit()
    }

    private func commonInit() {
        self.loadFromNib()
        self.titleLabel.alpha = UIFont.TextEmphasis.medium.rawValue
        self.countLabel.alpha = UIFont.TextEmphasis.full.rawValue
    }
}
