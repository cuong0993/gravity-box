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

package ro.luca1152.gravitybox.entities.game

import com.badlogic.gdx.assets.AssetManager
import ro.luca1152.gravitybox.components.editor.editorObject
import ro.luca1152.gravitybox.components.editor.json
import ro.luca1152.gravitybox.components.editor.overlay
import ro.luca1152.gravitybox.components.editor.snap
import ro.luca1152.gravitybox.components.game.*
import ro.luca1152.gravitybox.utils.assets.Assets
import ro.luca1152.gravitybox.utils.kotlin.addToEngine
import ro.luca1152.gravitybox.utils.kotlin.newEntity
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

object CollectiblePointEntity {
    private const val WIDTH = 1f
    private const val HEIGHT = 1f

    fun createEntity(
        id: Int,
        centerX: Float, centerY: Float,
        rotation: Float = 0f,
        blinkEndlessly: Boolean = true,
        manager: AssetManager = Injekt.get()
    ) = newEntity().apply {
        scene2D(manager.get(Assets.tileset).findRegion("collectible-point"), centerX, centerY, WIDTH, HEIGHT, rotation)
        color(ColorType.DARK)
        if (blinkEndlessly) {
            fadeInFadeOut(scene2D)
        }
        collectiblePoint()
        editorObject()
        mapObject(id)
        collisionBox(WIDTH, HEIGHT)
        polygon(scene2D)
        snap()
        json(this)
        overlay(
            showMovementButtons = true, showRotationButton = true, showDeletionButton = true,
            showResizingButtons = false, showSettingsButton = false
        )
        addToEngine()
    }
}