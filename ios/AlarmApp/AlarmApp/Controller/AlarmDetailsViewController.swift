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

import Combine
import CumulocityCoreLibrary
import UIKit

class AlarmDetailsViewController: UIViewController, AlarmUpdate {
    @IBOutlet var textLabel: MaterialLabel!
    @IBOutlet var openDeviceButton: UIButton!
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    @IBOutlet weak var newCommentButton: UIButton!

    private var cancellableSet = Set<AnyCancellable>()
    private var summaryController: AlarmSummaryViewController?
    private var commentsController: CommentListViewController?

    @Published
    var alarm: C8yAlarm?

    func receivedUpdatedAlarm(alarm: C8yAlarm?) {
        self.alarm = alarm
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationItem.title = %"alarm_details_title"
        M3Theme.applyTheme(view: self.segmentedControl)
        self.segmentedControl.setTitle(%"alarm_details_details", forSegmentAt: 0)
        self.segmentedControl.setTitle(%"alarm_details_comments", forSegmentAt: 1)
        self.newCommentButton.layer.shadowOpacity = 0.4
        self.newCommentButton.layer.shadowRadius = 2
        self.newCommentButton.layer.shadowOffset = CGSize(width: 2.0, height: 2.0)
        self.$alarm.sink { [self] value in
            if let a = value {
                self.textLabel.text = a.text
                self.openDeviceButton.setTitle(a.source?.name, for: [])
                // update the comment list when a new comment was submitted
                if let commentController = self.commentsController {
                    commentController.data = a[C8yComment.identifier] as? [C8yComment] ?? []
                    commentController.tableView.reloadData()
                }
            }
        }
        .store(in: &self.cancellableSet)
    }

    // MARK: - Actions

    @IBAction func onContentsChanged(_ sender: UISegmentedControl) {
        if sender.selectedSegmentIndex == 1 {
            self.commentsController?.view.superview?.isHidden = false
            self.summaryController?.view.superview?.isHidden = true
        } else {
            self.summaryController?.view.superview?.isHidden = false
            self.commentsController?.view.superview?.isHidden = true
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
        } else if segue.identifier == UIStoryboardSegue.embedCommentList {
            let destination = segue.destination as? CommentListViewController
            destination?.data = self.alarm?[C8yComment.identifier] as? [C8yComment] ?? []
            self.commentsController = destination
        } else if segue.identifier == UIStoryboardSegue.toNewComment {
            let destination = segue.destination as? NewCommentViewController
            destination?.alarm = self.alarm
            destination?.delegate = self
        }
    }
}

protocol AlarmUpdate {
    func receivedUpdatedAlarm(alarm: C8yAlarm?)
}
