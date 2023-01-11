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

import UIKit

class ListViewHeaderItem: UITableViewHeaderFooterView {
    static var identifier: String = "sectionHeader"

    static func register(for tableView: UITableView) {
        tableView.register(ListViewHeaderItem.self, forHeaderFooterViewReuseIdentifier: identifier)
    }

    let separator = SeparatorItem()

    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        configureContents()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        configureContents()
    }

    func configureContents() {
        self.separator.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(self.separator)
        NSLayoutConstraint.activate([
            self.separator.leadingAnchor.constraint(equalTo: self.contentView.safeAreaLayoutGuide.leadingAnchor),
            self.separator.trailingAnchor.constraint(equalTo: self.contentView.safeAreaLayoutGuide.trailingAnchor),
            self.separator.topAnchor.constraint(equalTo: self.contentView.topAnchor),
            self.separator.bottomAnchor.constraint(equalTo: self.contentView.bottomAnchor),
        ])
    }
}
