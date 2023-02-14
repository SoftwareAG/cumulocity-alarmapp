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

class CommentItem: UITableViewCell {
    static var identifier = String(describing: CommentItem.self)

    static var nib: UINib {
        UINib(nibName: identifier, bundle: nil)
    }

    static func register(for tableView: UITableView) {
        tableView.register(nib, forCellReuseIdentifier: identifier)
    }

    @IBOutlet weak var cardView: MaterialCardView!
    @IBOutlet var titleLabel: MaterialLabel!
    @IBOutlet var timeLabel: MaterialLabel!
    @IBOutlet var valueLabel: MaterialLabel!

    override func layoutSubviews() {
        super.layoutSubviews()
        self.titleLabel.alpha = UIFont.TextEmphasis.full.rawValue
        self.titleLabel.font = UIFont.preferredFont(forTextStyle: .body).bold()
        self.timeLabel.alpha = UIFont.TextEmphasis.medium.rawValue
        self.valueLabel.alpha = UIFont.TextEmphasis.full.rawValue
        setCardBackgroundColor()
    }

    func bind(with comment: C8yComment?) {
        if let a = comment {
            self.titleLabel.text = "@\(a.user ?? "")"
            if let timestamp = a.time {
                self.timeLabel.text = CumulocityHelper.toReadableDate(timestamp)
            }
            self.valueLabel.text = a.text
        }
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if self.traitCollection.hasDifferentColorAppearance(comparedTo: previousTraitCollection) {
            setCardBackgroundColor()
        }
    }

    private func setCardBackgroundColor() {
        if self.traitCollection.userInterfaceStyle == .dark {
            self.cardView.cardBackgroundColor = nil
            self.cardView.shadowRadius = 0
        } else {
            self.cardView.cardBackgroundColor = .surface
            self.cardView.shadowRadius = 2
        }
    }
}
