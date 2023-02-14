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

class M3Theme {
    func elevationOverlayColor(elevation: Int) -> UIColor {
        elevationOverlayColor(color: .primary, elevation: elevation)
    }

    func elevationOverlayColor(color: UIColor, elevation: Int) -> UIColor {
        // SEE https://m3.material.io/styles/color/the-color-system/color-roles
        var opacity: CGFloat
        switch elevation {
        case 1:
            opacity = 0.05

        case 2:
            opacity = 0.08

        case 3:
            opacity = 0.11

        case 4:
            opacity = 0.12

        default:
            opacity = 0.14
        }
        return UIColor { (_: UITraitCollection) -> UIColor in
            .surface.overlay(withColor: color, alpha: opacity)
        }
    }
}

// MARK: NavigationBar appearance

extension M3Theme {
    static func setNavigationBarAppearance() {
        let theme = M3Theme()
        let appearance = UINavigationBarAppearance()
        appearance.backgroundColor = theme.elevationOverlayColor(elevation: 2)
        appearance.titleTextAttributes = [.foregroundColor: UIColor.onSurface]
        appearance.largeTitleTextAttributes = [.foregroundColor: UIColor.onSurface]

        UINavigationBar.appearance().tintColor = UIColor.onSurface
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
}

// MARK: Card appearance

extension M3Theme {
    static func applyTheme(card: MaterialCardView) {
        let theme = M3Theme()
        if let backgroundColor = card.cardBackgroundColor {
            card.layer.backgroundColor = backgroundColor.cgColor
        } else {
            card.layer.backgroundColor = theme.elevationOverlayColor(elevation: 1).cgColor
        }
    }
}

// MARK: Dialog appearance

extension M3Theme {
    static func applyTheme(view: UIView) {
        let theme = M3Theme()
        view.backgroundColor = theme.elevationOverlayColor(elevation: 3)
    }
}

// MARK: Segmented Control appearance

extension M3Theme {
    static func applyTheme(view: UISegmentedControl) {
        view.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.onPrimary], for: .selected)
        view.setTitleTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.onSurface], for: .normal)
        let theme = M3Theme()
        // background color is overlayed with black color and 6% opacity, so we use a darker background ourselves
        view.backgroundColor = theme.elevationOverlayColor(elevation: 5)
        view.selectedSegmentTintColor = .primary
    }
}
