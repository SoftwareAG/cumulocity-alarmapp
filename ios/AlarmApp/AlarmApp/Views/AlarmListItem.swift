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

import CumulocityCoreLibrary
import UIKit

class AlarmListItem: UITableViewCell {
    static var identifier = String(describing: AlarmListItem.self)

    static var nib: UINib {
        UINib(nibName: identifier, bundle: nil)
    }

    static func register(for tableView: UITableView) {
        tableView.register(nib, forCellReuseIdentifier: identifier)
    }

    @IBOutlet weak var deviceLabel: MaterialLabel!
    @IBOutlet weak var messageLabel: MaterialLabel!
    @IBOutlet weak var commentContainer: DecoratedLabel!
    @IBOutlet weak var severityContainer: DecoratedLabel!
    @IBOutlet weak var statusContainer: DecoratedLabel!
    @IBOutlet weak var timeLabel: MaterialLabel!

    override func layoutSubviews() {
        super.layoutSubviews()
        self.selectedBackgroundView?.backgroundColor = M3Theme().elevationOverlayColor(elevation: 1)
        self.messageLabel.alpha = UIFont.TextEmphasis.full.rawValue
        self.deviceLabel.alpha = UIFont.TextEmphasis.full.rawValue
        self.timeLabel.alpha = UIFont.TextEmphasis.full.rawValue
    }

    func bind(with alarm: C8yAlarm?, ignoreDevice: Bool = false) {
        if let alarm = alarm {
            self.messageLabel.text = alarm.text
            self.deviceLabel.text = ignoreDevice ? "" : alarm.source?.name
            self.deviceLabel.isHidden = ignoreDevice
            self.statusContainer.bind(with: alarm.status)
            self.severityContainer.bind(with: alarm.severity)
            if let time = alarm.time {
                self.timeLabel.text = CumulocityHelper.toReadableDate(time)
            }
            self.commentContainer.bind(commentCount: (alarm[C8yComment.identifier] as? [Any])?.count)
        }
    }
}
