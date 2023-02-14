//
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

prefix operator %
extension String {
    static prefix func % (string: String) -> String {
        String(localized: String.LocalizationValue(string))
    }

    func isTranslationKey() -> Bool {
        self.starts(with: "%")
    }
}

protocol Localizeable {
}

extension Localizeable {
    func resolveTranslationKey(for value: String?) -> String? {
        if let text = value, text.isTranslationKey() {
            return %"\(text.replacingCharacters(in: ...text.startIndex, with: ""))"
        }
        return value
    }
}
