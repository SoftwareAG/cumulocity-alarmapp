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
import UIKit

/// Example usage:  UIColor.surface.overlay(withColor: .onSurface, alpha: UIColor.Opacity.medium)
extension UIColor {
    public enum Opacity: CGFloat {
        case full = 1.0
        case medium = 0.54
        case disabled = 0.38
        case low = 0.32
        case disabledLow = 0.12
        case overlayAlpha = 0.04
    }

    // MARK: Theme colors

    static var primary = UIColor(named: "primary") ?? .black
    static var onPrimary = UIColor(named: "onPrimary") ?? .black
    static var primaryContainer = UIColor(named: "primaryContainer") ?? .black
    static var onPrimaryContainer = UIColor(named: "onPrimaryContainer") ?? .black
    static var secondary = UIColor(named: "secondary") ?? .black
    static var onSecondary = UIColor(named: "onSecondary") ?? .black
    static var background = UIColor(named: "background") ?? .black
    static var onBackground = UIColor(named: "onBackground") ?? .black
    static var surface = UIColor(named: "surface") ?? .black
    static var onSurface = UIColor(named: "onSurface") ?? .black
    static var alarmActionClear = UIColor(named: "color_alarm_clear") ?? .black
    static var alarmActionAcknowledge = UIColor(named: "color_alarm_acknowledge") ?? .black

    func overlay(withColor color: UIColor, alpha: Opacity) -> UIColor {
        overlay(withColor: color, alpha: alpha.rawValue)
    }

    func overlay(withColor color: UIColor, alpha: CGFloat) -> UIColor {
        let color = color.withAlphaComponent(alpha)
        return UIColor.blendColor(color, withBackgroundColor: self)
    }

    private static func blendColor(_ color: UIColor, withBackgroundColor backroundColor: UIColor) -> UIColor {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        var bRed: CGFloat = 0.0
        var bGreen: CGFloat = 0.0
        var bBlue: CGFloat = 0.0
        var bAlpha: CGFloat = 0.0
        backroundColor.getRed(&bRed, green: &bGreen, blue: &bBlue, alpha: &bAlpha)
        return UIColor(
            red: blendColorChannel(red, bRed, alpha, bAlpha),
            green: blendColorChannel(green, bGreen, alpha, bAlpha),
            blue: blendColorChannel(blue, bBlue, alpha, bAlpha),
            alpha: alpha + bAlpha * (1 - alpha)
        )
    }

    private static func blendColorChannel(_ value: CGFloat, _ bValue: CGFloat, _ alpha: CGFloat, _ bAlpha: CGFloat)
        -> CGFloat
    {
        ((1 - alpha) * bValue * bAlpha + alpha * value) / (alpha + bAlpha * (1 - alpha))
    }
}
