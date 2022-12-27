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

class AlarmListItem: UITableViewCell {
    static var identifier = String(describing: AlarmListItem.self)

    static func register(for tableView: UITableView) {
        tableView.register(
            UINib(nibName: AlarmListItem.identifier, bundle: nil),
            forCellReuseIdentifier: AlarmListItem.identifier
        )
    }

    @IBOutlet weak var titleLabel: MaterialLabel!
    @IBOutlet weak var timeLabel: MaterialLabel!
    @IBOutlet weak var severityImage: DecoratedLabel!
    @IBOutlet weak var commentImage: DecoratedLabel!
    @IBOutlet weak var deviceLabel: MaterialLabel!
    @IBOutlet weak var statusImage: UIImageView!

    override func layoutSubviews() {
        super.layoutSubviews()
        self.selectedBackgroundView?.backgroundColor = UIColor.background.overlay(withColor: .primary, alpha: 0.12)
        self.statusImage.tintColor = UIColor.background.overlay(withColor: .onBackground, alpha: UIColor.Opacity.low)
        self.titleLabel.alpha = UIFont.TextEmphasis.full.rawValue
        self.timeLabel.alpha = UIFont.TextEmphasis.medium.rawValue
        self.deviceLabel.alpha = UIFont.TextEmphasis.full.rawValue
    }

    func bind(with alarm: C8yAlarm?, ignoreDevice: Bool = false) {
        if let a = alarm {
            self.titleLabel.text = a.text
            if let severity = a.severity {
                self.severityImage.bind(with: severity)
            }
            if let status = a.status {
                self.statusImage.image = status.icon()
            }
            self.statusImage.isHidden = a.status == .active
            if let timestamp = a.time {
                self.timeLabel.text = CumulocityHelper.toReadableDate(timestamp)
            }
            self.commentImage.bind(commentCount: (a[C8yComment.identifier] as? [Any])?.count)
            self.deviceLabel.text = ignoreDevice ? "" : a.source?.name
            self.deviceLabel.isHidden = ignoreDevice
        }
    }
}
