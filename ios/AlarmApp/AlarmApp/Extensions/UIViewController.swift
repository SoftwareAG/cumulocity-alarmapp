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

extension UIViewController {
    func presentAs(
        bottomSheet: UIViewController,
        detents: [UISheetPresentationController.Detent] = [.medium()],
        isModal: Bool = false
    ) {
        bottomSheet.view.backgroundColor = .surface
        let navigationController = UINavigationController(rootViewController: bottomSheet)
        navigationController.modalPresentationStyle = .pageSheet
        navigationController.navigationBar.isHidden = true
        navigationController.isModalInPresentation = isModal
        if let sheet = navigationController.sheetPresentationController {
            sheet.detents = detents
            sheet.prefersGrabberVisible = true
            if isModal {
                sheet.largestUndimmedDetentIdentifier = .medium
            }
        }
        present(navigationController, animated: true, completion: nil)
    }
}
