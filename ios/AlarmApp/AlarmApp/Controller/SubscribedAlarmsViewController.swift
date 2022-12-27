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

class SubscribedAlarmsViewController: UITableViewController, SubscribedAlarmListReloadDelegate {
    private var data = C8yAlarmCollection()
    private var selectedAlarm: C8yAlarm?
    private var cancellableSet = Set<AnyCancellable>()

    public var openFilterDelegate: EmptyAlarmsDelegate?

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        UITableViewController.prepareForAlarms(with: self.tableView, delegate: openFilterDelegate)
        self.view.backgroundColor = .clear
        reload()
    }

    func reload() {
        let filter = SubscribedAlarmFilter.shared
        fetchAlarms(
            bySeverity: filter.severity,
            byDeviceId: filter.resolvedDeviceId,
            byStatus: filter.status,
            byAlarmType: filter.alarmType
        )
    }

    private func fetchAlarms(
        bySeverity severity: C8yAlarm.C8ySeverity?,
        byDeviceId deviceId: String?,
        byStatus status: C8yAlarm.C8yStatus?,
        byAlarmType type: String?
    ) {
        let alarmsApi = Cumulocity.Core.shared.alarms.alarmsApi
        var publisher: AnyPublisher<C8yAlarmCollection, Error>?
        if let type = type {
            publisher = alarmsApi.getAlarms(
                pageSize: 50,
                severity: severity?.rawValue,
                source: deviceId,
                status: status?.rawValue,
                type: [type]
            )
        } else {
            publisher = alarmsApi.getAlarms(
                pageSize: 50,
                severity: severity?.rawValue,
                source: deviceId,
                status: status?.rawValue
            )
        }
        publisher?.receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { _ in
                },
                receiveValue: { collection in
                    self.data = collection
                    self.tableView.reloadData()
                }
            )
            .store(in: &self.cancellableSet)
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        let alarmCount = data.alarms?.count ?? 0
        tableView.backgroundView?.isHidden = alarmCount > 0
        return alarmCount > 0 ? 1 : 0
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        data.alarms?.count ?? 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(
            withIdentifier: AlarmListItem.identifier,
            for: indexPath
        ) as? AlarmListItem {
            cell.bind(with: data.alarms?[indexPath.item])
            return cell
        }
        fatalError("Cannot create AlarmListItem")
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        self.selectedAlarm = data.alarms?[indexPath.item]
        performSegue(withIdentifier: UIStoryboardSegue.toAlarmDetails, sender: self)
    }

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        if let view = tableView.dequeueReusableHeaderFooterView(
            withIdentifier: ListViewHeaderItem.identifier
        ) as? ListViewHeaderItem {
            let filter = SubscribedAlarmFilter.shared
            if filter.isDefaultFilter() {
                view.separator.titleText = "ACTIVE, CRITICAL ALARMS"
            } else {
                view.separator.titleText = "MY FILTER"
            }
            return view
        }
        fatalError("Cannot create ListViewHeaderItem")
    }

    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == UIStoryboardSegue.toAlarmDetails {
            if let destination = segue.destination as? AlarmDetailsViewController {
                destination.alarm = self.selectedAlarm
            }
        }
    }
}

protocol SubscribedAlarmListReloadDelegate: AnyObject {
    func reload()
}
