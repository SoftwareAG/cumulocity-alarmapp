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

class SubscribedAlarmFilterViewController: UIViewController {
    @IBOutlet var severityFilter: UISegmentedControl!
    @IBOutlet var statusFilter: UISegmentedControl!
    @IBOutlet var deviceNameTextfield: UITextField!
    @IBOutlet var alarmTypeTextfield: UITextField!

    var filter = AlarmFilter()
    var delegate: AlarmListReloadDelegate?

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        delegate?.fetchAlarms()
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        self.deviceNameTextfield.text = self.filter.deviceName
        self.alarmTypeTextfield.text = self.filter.alarmType
        M3Theme.applyTheme(view: self.severityFilter)
        M3Theme.applyTheme(view: self.statusFilter)
        self.severityFilter.bind(with: self.filter.severity)
        self.statusFilter.bind(with: self.filter.status)
    }

    @IBAction func onSeverityChanged(_ sender: UISegmentedControl) {
        sender.storeSeverity(on: self.filter)
    }

    @IBAction func onStatusChanged(_ sender: UISegmentedControl) {
        sender.storeSatus(on: self.filter)
    }

    @IBAction func onDeviceNameChanged(_ sender: UITextField) {
        self.filter.deviceName = sender.text
    }

    @IBAction func onAlarmTypeChanged(_ sender: UITextField) {
        self.filter.alarmType = sender.text
    }
}
