/*
 * This file is part of Gravity Box.
 *
 * Gravity Box is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gravity Box is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gravity Box.  If not, see <https://www.gnu.org/licenses/>.
 */

package ro.luca1152.gravitybox.utils.assets.json

class PaddingPrototype {
    var left = 0
    var right = 0
    var top = 0
    var bottom = 0
}

class PositionPrototype {
    var x = 0f
    var y = 0f
}

class PlayerPrototype {
    var id = 0
    var position = PositionPrototype()
    var rotation = 0
}

class FinishPrototype {
    var id = 0
    var position = PositionPrototype()
    var rotation = 0
}

class MovingToPrototype {
    var x = Float.POSITIVE_INFINITY
    var y = Float.POSITIVE_INFINITY
}

class ObjectPrototype {
    var type = ""
    var id = 0
    var position = PositionPrototype()
    var movingTo = MovingToPrototype()
    var width = 0f
    var rotation = 0
    var isDestroyable = false
    var isRotating = false
}