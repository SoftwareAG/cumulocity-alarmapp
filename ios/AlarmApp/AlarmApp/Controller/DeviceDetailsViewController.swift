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

class DeviceDetailsViewController: UIViewController {
    @IBOutlet var nameItem: DetailsItem!
    @IBOutlet var systemIdItem: DetailsItem!
    @IBOutlet var typeItem: DetailsItem!
    @IBOutlet var serialNumberItem: DetailsItem!
    @IBOutlet var externalIdItem: DetailsItem!

    private var cancellableSet = Set<AnyCancellable>()
    var source: C8yAlarm.C8ySource?
    @Published var device: C8yManagedObject?

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        self.serialNumberItem.valueLabel.text = "N/A"
        self.serialNumberItem.isHidden = true
        self.typeItem.valueLabel.text = "N/A"
        self.typeItem.isHidden = true
        self.externalIdItem.valueLabel.text = "Not defined"
        self.externalIdItem.isHidden = true

        self.$device.sink { value in
            self.nameItem.valueLabel.text = value?.name
            self.systemIdItem.valueLabel.text = value?.id
            if let hardware = value?[C8yFragments.c8yHardware.rawValue] {
                if let c8yHardware = hardware as? C8yHardware {
                    self.serialNumberItem.valueLabel.text = c8yHardware.serialNumber
                    self.serialNumberItem.isHidden = false
                }
            }
            if let deviceType = value?[C8yFragments.type.rawValue] {
                self.typeItem.valueLabel?.text = deviceType as? String
                self.typeItem.isHidden = false
            }
            self.fetchExternalId(for: value?.id)
        }
        .store(in: &self.cancellableSet)

        fetchDevice()
    }

    private func fetchDevice() {
        let managedObjectsApi = Cumulocity.Core.shared.inventory.managedObjectsApi
        if let deviceSource = source?.id {
            managedObjectsApi.getManagedObject(id: deviceSource)
                .receive(on: DispatchQueue.main)
                .sink(
                    receiveCompletion: { _ in
                    },
                    receiveValue: { data in
                        self.device = data
                    }
                )
                .store(in: &self.cancellableSet)
        }
    }

    private func fetchExternalId(for deviceId: String?) {
        let externalIdService = Cumulocity.Core.shared.identity.externalIDsApi
        if let id = deviceId {
            externalIdService.getExternalIds(id: id)
                .receive(on: DispatchQueue.main)
                .sink(
                    receiveCompletion: { _ in
                    },
                    receiveValue: { data in
                        let c8ySerial = data.externalIds?.first { externalId in
                            externalId.type?.compare("c8y_Serial", options: .caseInsensitive) == .orderedSame
                        }
                        if let serial = c8ySerial {
                            self.externalIdItem.valueLabel.text = serial.externalId
                            self.externalIdItem.isHidden = false
                        }
                    }
                )
                .store(in: &self.cancellableSet)
        }
    }

    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == UIStoryboardSegue.embedDeviceAlarms {
            if let destination = segue.destination as? DeviceAlarmsViewController {
                destination.source = self.source
            }
        }
    }
}
