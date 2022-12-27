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

import Combine
import CumulocityCoreLibrary
import UIKit

class AlarmDetailsViewController: UIViewController, AlarmUpdate {
    @IBOutlet var textLabel: MaterialLabel!
    @IBOutlet var openDeviceButton: UIButton!
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var newCommentButton: UIButton!

    private var cancellableSet = Set<AnyCancellable>()
    private var summaryController: UIViewController?

    @Published
    var alarm: C8yAlarm?

    func receivedUpdatedAlarm(alarm: C8yAlarm?) {
        self.alarm = alarm
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        M3Theme.applyTheme(view: self.segmentedControl)
        self.newCommentButton.layer.shadowOpacity = 0.4
        self.newCommentButton.layer.shadowRadius = 2
        self.newCommentButton.layer.shadowOffset = CGSize(width: 2.0, height: 2.0)
        self.$alarm.sink { value in
            if let a = value {
                self.textLabel.text = a.text
                self.openDeviceButton.setTitle(a.source?.name, for: [])
            }
        }
        .store(in: &self.cancellableSet)
    }

    // MARK: - Actions

    /*
    @IBAction func onCommentTyped(_ sender: UITextField) {
        if let a = self.alarm, let id = a.id {
            var alarm = C8yAlarm()
            var newComment = C8yComment()
            newComment.text = sender.text
            newComment.user = CumulocityApi.shared().userId
            
            var comments = a[C8yComment.identifier] as? [C8yComment] ?? []
            comments.append(newComment)
            alarm.customFragments = [C8yComment.identifier: comments]
            self.updateAlarm(alarm, id)
            
            sender.text = nil
        }
    }
    */

    @IBAction func onContentsChanged(_ sender: UISegmentedControl) {
        if let child = summaryController {
            child.willMove(toParent: nil)
            child.removeFromParent()
            child.view.removeFromSuperview()
            summaryController = nil
        }
        if sender.selectedSegmentIndex == 1 {
            let child = UIStoryboard.createCommentListViewController()
            if let vc = child {
                vc.data = self.alarm?[C8yComment.identifier] as? [C8yComment] ?? []
                self.containerView.addSubview(vc.view)
                self.addChild(vc)
                vc.didMove(toParent: self)
                summaryController = vc
            }
        } else {
            self.performSegue(withIdentifier: UIStoryboardSegue.embedAlarmSummary, sender: sender)
        }
    }

    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == UIStoryboardSegue.toDeviceDetails {
            let destination = segue.destination as? DeviceDetailsViewController
            destination?.source = self.alarm?.source
        } else if segue.identifier == UIStoryboardSegue.embedAlarmSummary {
            let destination = segue.destination as? AlarmSummaryViewController
            destination?.alarm = self.alarm
            destination?.delegate = self
            self.summaryController = destination
        }
    }
}

protocol AlarmUpdate {
    func receivedUpdatedAlarm(alarm: C8yAlarm?)
}
