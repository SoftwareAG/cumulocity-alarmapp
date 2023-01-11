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
import Foundation
import UIKit

class LoginViewController: UIViewController, UITextFieldDelegate {
    private var cancellableSet = Set<AnyCancellable>()
    @IBOutlet var tenantTextfield: UITextField!
    @IBOutlet var usernameTextfield: UITextField!
    @IBOutlet var passwordTextfield: UITextField!
    var tenant: String?
    var userName: String?
    var password: String?
    var showPasswordButton: UIButton?

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        let defaultCredentials = Credentials.defaults()
        self.tenant = defaultCredentials.tenant
        self.userName = defaultCredentials.userName
        self.tenantTextfield.text = self.tenant
        self.usernameTextfield.text = self.userName
        // configure passwort textfield to provide capability to show password
        self.showPasswordButton = UIButton(configuration: .borderless())
        self.showPasswordButton?.configuration?.image = getPasswordDecoration()
        self.showPasswordButton?.configuration?.preferredSymbolConfigurationForImage = UIImage.SymbolConfiguration(scale: .medium)
        self.showPasswordButton?.tintColor = .primary
        self.showPasswordButton?.addTarget(self, action: #selector(didPressedPasswordButton), for: .touchUpInside)
        self.passwordTextfield.rightViewMode = .whileEditing // required otherwise clear button is shown
        self.passwordTextfield.rightView = showPasswordButton
    }

    @objc
    private func didPressedPasswordButton() {
        self.passwordTextfield.isSecureTextEntry.toggle()
        self.showPasswordButton?.configuration?.image = getPasswordDecoration()
    }

    private func getPasswordDecoration() -> UIImage? {
        let image = self.passwordTextfield.isSecureTextEntry ? UIImage(systemName: "eye.fill") : UIImage(systemName: "eye.slash.fill")
        return image
    }

    // MARK: Actions

    @IBAction func onTenantEntered(_ sender: UITextField) {
        self.tenant = sender.text
    }

    @IBAction func onUserNameEntered(_ sender: UITextField) {
        self.userName = sender.text
    }

    @IBAction func onPasswordEntered(_ sender: UITextField) {
        self.password = sender.text
    }

    @IBAction func onLoginButttonTapped(_ sender: UIButton) {
        guard let user = self.userName, let password = self.password, let tenant = self.tenant else {
            return
        }

        let credentials = Credentials(forUser: user, password: password, tenant: tenant)
        CumulocityApi.shared().initRequestBuilder(forCredentials: credentials)
        sender.configuration?.showsActivityIndicator = true

        let usersApi = Cumulocity.Core.shared.users.currentUserApi
        usersApi.getCurrentUser()
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { completion in
                    sender.configuration?.showsActivityIndicator = false
                    if (try? completion.error()) != nil {
                        credentials.remove()
                        self.presentAuthenticationError()
                    }
                },
                receiveValue: { value in
                    guard let userId = value.id else {
                        return
                    }
                    CumulocityApi.shared().userId = userId
                    credentials.persist(for: userId)

                    if let window = UIApplication.shared.keyWindow {
                        let onboardingViewController = UIStoryboard.createRootViewController()
                        window.rootViewController?.dismiss(animated: true) {
                            window.rootViewController = onboardingViewController
                        }
                    }
                }
            )
            .store(in: &self.cancellableSet)
    }

    private func presentAuthenticationError() {
        let alertController = UIAlertController(
            title: %"login_authentication_error_title",
            message: %"login_authentication_error_message",
            preferredStyle: UIAlertController.Style.alert
        )
        let alertAction = UIAlertAction(
            title: %"login_error_action",
            style: UIAlertAction.Style.default,
            handler: nil
        )
        alertController.addAction(alertAction)
        present(alertController, animated: true, completion: nil)
        alertController.view.tintColor = .primary
    }
}
