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
import Foundation

/// Endpoint that is used to register physical devices at the backend
public class RegistrationsApi {
    private var requestBuilder: URLRequestBuilder
    private var session: URLSession

    init(requestBuilder: URLRequestBuilder, withSession session: URLSession) {
        self.requestBuilder = requestBuilder
        self.session = session
    }

    /// Allows to modify any property of the passed `URLRequestBuilder`.
    ///
    /// The default implementation merges the  properties  `schema`,` host`, `path`, `queryItems`, `requestHeaders`
    /// of the `requestBuilder` with the passed `URLRequestBuilder`.
    ///
    /// - Parameter builder: `URLRequestBuilder` to modify
    /// - Returns modified builder
    public func adapt(builder: URLRequestBuilder) -> URLRequestBuilder {
        let newBuilder = URLRequestBuilder(with: self.requestBuilder)
        return newBuilder.merge(with: builder)
    }

    /// Registers a device
    /// Registers a device at the tenant and Azure registration hub.
    /// The following table gives an overview of the possible response codes and their meanings.
    /// - Returns:
    /// 	- 201
    ///		  Created. Registering the device was successfull.
    /// 	- 406
    ///		  Not Acceptable. Errorous Request.
    /// - Parameters:
    /// 	- body
    public func subscribe(body: DeviceRegistration) -> AnyPublisher<Data, Swift.Error> {
        var encodedRequestBody: Data?
        do {
            encodedRequestBody = try JSONEncoder().encode(body)
        } catch {
            return Fail<Data, Error>(error: error).eraseToAnyPublisher()
        }
        let builder = URLRequestBuilder()
            .set(resourcePath: "/service/pushgateway/registrations")
            .set(httpMethod: "post")
            .add(header: "Content-Type", value: "application/json")
            .set(httpBody: encodedRequestBody)
        return self.session.dataTaskPublisher(
            for: adapt(builder: builder).build()
        )
        .tryMap { element -> Data in
            guard let httpResponse = element.response as? HTTPURLResponse else {
                throw URLError(.badServerResponse)
            }
            guard (200...299).contains(httpResponse.statusCode) else {
                throw BadResponseError(with: httpResponse)
            }
            return element.data
        }
        .eraseToAnyPublisher()
    }

    /// Removes a registered a device
    /// Removes a registered device from cumulocity and Azure Notification Hub.
    /// The following table gives an overview of the possible response codes and their meanings.
    /// - Returns:
    /// 	- 200
    ///		  OK. The user and device was removed successfully.
    /// 	- 406
    ///		  Not Acceptable. Errorous Request.
    /// - Parameters:
    /// 	- userId
    ///		  User ID
    /// 	- deviceToken
    ///		  the deviceToken specified when creating the registration
    public func unsubscribe(deviceToken: String) -> AnyPublisher<Data, Swift.Error> {
        let builder = URLRequestBuilder()
            .set(resourcePath: "/service/pushgateway/registrations/\(deviceToken)")
            .set(httpMethod: "delete")
        return self.session.dataTaskPublisher(
            for: adapt(builder: builder).build()
        )
        .tryMap { element -> Data in
            guard let httpResponse = element.response as? HTTPURLResponse else {
                throw URLError(.badServerResponse)
            }
            guard (200...299).contains(httpResponse.statusCode) else {
                throw BadResponseError(with: httpResponse)
            }
            return element.data
        }
        .eraseToAnyPublisher()
    }
}
