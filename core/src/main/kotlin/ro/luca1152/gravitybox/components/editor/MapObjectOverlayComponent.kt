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

package ro.luca1152.gravitybox.components.editor

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool.Poolable
import ro.luca1152.gravitybox.utils.components.ComponentResolver

/** Contains the elements that will be shown in the overlay when the object is selected. */
class MapObjectOverlayComponent : Component, Poolable {
    var showMovementButtons = true
    var showRotationButton = true
    var showResizingButtons = true
    var showDeletionButton = true

    fun set(
        showMovementButtons: Boolean, showRotationButton: Boolean,
        showResizingButtons: Boolean, showDeletionButton: Boolean
    ) {
        this.showMovementButtons = showMovementButtons
        this.showRotationButton = showRotationButton
        this.showResizingButtons = showResizingButtons
        this.showDeletionButton = showDeletionButton
    }

    override fun reset() {
        showMovementButtons = true
        showRotationButton = true
        showResizingButtons = true
        showDeletionButton = true
    }

    companion object : ComponentResolver<MapObjectOverlayComponent>(MapObjectOverlayComponent::class.java)
}

val Entity.mapObjectOverlay: MapObjectOverlayComponent
    get() = MapObjectOverlayComponent[this]