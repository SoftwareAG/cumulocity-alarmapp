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

import UIKit

class FilterChip: UICollectionViewCell {
    static var identifier = String(describing: FilterChip.self)

    static var nib: UINib {
        UINib(nibName: identifier, bundle: nil)
    }

    static func register(for collectionView: UICollectionView) {
        collectionView.register(nib, forCellWithReuseIdentifier: identifier)
    }

    @IBOutlet weak var label: UIButton! {
        didSet {
            self.label.configuration?.buttonSize = .mini
        }
    }
}
