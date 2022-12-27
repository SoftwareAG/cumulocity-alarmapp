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

class AuditRecordItem: UITableViewCell {
    static var identifier = String(describing: AuditRecordItem.self)

    static func register(for tableView: UITableView) {
        tableView.register(
            UINib(nibName: AuditRecordItem.identifier, bundle: nil),
            forCellReuseIdentifier: AuditRecordItem.identifier
        )
    }

    @IBOutlet var valueLabel: MaterialLabel!
    @IBOutlet var timeLabel: MaterialLabel!

    override func layoutSubviews() {
        super.layoutSubviews()
        self.backgroundColor = .clear
        self.valueLabel.alpha = UIFont.TextEmphasis.full.rawValue
        self.timeLabel.alpha = UIFont.TextEmphasis.medium.rawValue
    }

    func bind(with auditRecord: C8yAuditRecord?) {
        if let a = auditRecord {
            let text = NSMutableAttributedString()

            if let changes = a.changes {
                let authorText = NSMutableAttributedString(string: "@\(a.user ?? "") ", attributes: boldAttributes())
                text.append(authorText)
                for change in changes {
                    if let representation = change.toString() {
                        let titleText = NSMutableAttributedString(
                            string: representation,
                            attributes: defaultAttributes()
                        )
                        text.append(titleText)
                    } else {
                        let titleText = NSMutableAttributedString(
                            string: a.activity ?? "",
                            attributes: defaultAttributes()
                        )
                        text.append(titleText)
                    }
                }
            } else {
                let titleText = NSMutableAttributedString(string: a.activity ?? "", attributes: defaultAttributes())
                text.append(titleText)
            }

            self.valueLabel.attributedText = text
            if let timestamp = a.creationTime {
                self.timeLabel.text = CumulocityHelper.toReadableDate(timestamp)
            }
        }
    }

    func bind(with comment: C8yComment?) {
        if let a = comment {
            let text = NSMutableAttributedString()
            let authorText = NSMutableAttributedString(string: "@\(a.user ?? "") ", attributes: boldAttributes())
            text.append(authorText)
            let titleText = NSMutableAttributedString(string: comment?.text ?? "", attributes: defaultAttributes())
            text.append(titleText)

            self.valueLabel.attributedText = text
            if let timestamp = a.time {
                self.timeLabel.text = CumulocityHelper.toReadableDate(timestamp)
            }
        }
    }

    private func defaultAttributes() -> [NSAttributedString.Key: Any] {
        [NSAttributedString.Key.foregroundColor: UIColor.onSurface]
    }

    private func boldAttributes() -> [NSAttributedString.Key: Any] {
        [
            NSAttributedString.Key.font: UIFont.preferredFont(forTextStyle: .body).bold(),
            NSAttributedString.Key.foregroundColor: UIColor.onSurface,
        ]
    }
}

extension C8yAuditRecord.C8yChanges {
    func toString() -> String? {
        if let previous = self.previousValue as? String, let new = self.newValue as? String {
            return "\(previous.capitalized) to \(new.capitalized)"
        }
        return nil
    }
}
