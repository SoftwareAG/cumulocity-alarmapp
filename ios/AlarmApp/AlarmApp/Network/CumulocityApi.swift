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
import Foundation

class CumulocityApi {
    private static var sharedCumulocityApi: CumulocityApi = {
        C8yManagedObject.registerAdditionalProperty(typeName: C8yFragments.type.rawValue, for: String.self)
        C8yManagedObject.registerAdditionalProperty(typeName: C8yFragments.c8yHardware.rawValue, for: C8yHardware.self)
        C8yAlarm.registerAdditionalProperty(typeName: C8yComment.identifier, for: [C8yComment].self)

        return CumulocityApi()
    }()

    var userId: String = ""

    private init() {
        Cumulocity.Core.shared.session = URLSession(
            configuration: URLSessionConfiguration.default,
            delegate: Delegate(),
            delegateQueue: nil
        )
    }

    class func shared() -> CumulocityApi {
        sharedCumulocityApi
    }

    func initRequestBuilder(forCredentials credentials: Credentials) {
        guard let url = URL(string: credentials.tenant) else {
            return
        }
        if let host = url.host {
            Cumulocity.Core.shared.requestBuilder.set(host: host)
        }
        if let scheme = url.scheme {
            Cumulocity.Core.shared.requestBuilder.set(scheme: scheme)
        }
        Cumulocity.Core.shared.requestBuilder.set(
            authorization: credentials.userName,
            password: credentials.password
        )
    }
}

class Delegate: NSObject, URLSessionDelegate {
    let allowedDomains: [String]?

    override init() {
        let configuration = Bundle.main.cumulocityConfiguration()
        self.allowedDomains = configuration?["Allowed Domains"] as? [String]
    }

    func urlSession(
        _ session: URLSession,
        didReceive challenge: URLAuthenticationChallenge,
        completionHandler: (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
    ) {
        guard let serverTrust = challenge.protectionSpace.serverTrust else {
            completionHandler(.performDefaultHandling, nil)
            return
        }
        if allowedDomains?.contains(challenge.protectionSpace.host) ?? false {
            let credential = URLCredential(trust: serverTrust)
            completionHandler(.useCredential, credential)
        } else {
            completionHandler(.performDefaultHandling, nil)
        }
    }
}
