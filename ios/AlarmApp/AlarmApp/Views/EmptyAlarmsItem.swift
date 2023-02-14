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

class EmptyAlarmsItem: UIView {
    static var identifier = String(describing: EmptyAlarmsItem.self)

    @IBOutlet var titleLabel: MaterialLabel!
    @IBOutlet var subTitleLabel: MaterialLabel!
    @IBOutlet var button: UIButton!
    @IBOutlet var icon: UIImageView!

    public var delegate: EmptyAlarmsDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }

    private func commonInit() {
        self.loadFromNib()
        self.titleLabel.alpha = UIFont.TextEmphasis.full.rawValue
        self.subTitleLabel.alpha = UIFont.TextEmphasis.medium.rawValue
        self.button.addTarget(self, action: #selector(onOpenFilterTapped(sender:)), for: .touchUpInside)
    }

    @objc
    private func onOpenFilterTapped(sender: UIButton) {
        delegate?.onOpenFilterTapped(sender)
    }
}

protocol EmptyAlarmsDelegate: AnyObject {
    func onOpenFilterTapped(_ sender: UIButton)
}
