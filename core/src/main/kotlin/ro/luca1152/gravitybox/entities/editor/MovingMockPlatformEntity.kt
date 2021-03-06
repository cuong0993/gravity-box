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

package ro.luca1152.gravitybox.entities.editor

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.NinePatch
import ro.luca1152.gravitybox.components.editor.*
import ro.luca1152.gravitybox.components.game.*
import ro.luca1152.gravitybox.entities.game.PlatformEntity
import ro.luca1152.gravitybox.utils.assets.Assets
import ro.luca1152.gravitybox.utils.kotlin.addToEngine
import ro.luca1152.gravitybox.utils.kotlin.newEntity
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * An object placed in the level editor which indicates the target position of a moving platform.
 * It doesn't have any functionality beside that, so it is used only in the level editor.
 */
object MovingMockPlatformEntity {
    fun createEntity(
        realPlatform: Entity,
        x: Float, y: Float,
        width: Float, rotation: Float,
        manager: AssetManager = Injekt.get()
    ) = newEntity().apply {
        scene2D(
            NinePatch(
                manager.get(Assets.tileset).findRegion("moving-platform"),
                PlatformEntity.PATCH_LEFT, PlatformEntity.PATCH_RIGHT,
                PlatformEntity.PATCH_TOP, PlatformEntity.PATCH_BOTTOM
            ), x, y, width, PlatformEntity.HEIGHT, rotation
        )
        polygon(scene2D)
        editorObject()
        mockMapObject()
        linkedEntity("platform", realPlatform)
        snap()
        color(ColorType.DARK)
        overlay(
            showMovementButtons = true, showRotationButton = false, showDeletionButton = true,
            showResizingButtons = false, showSettingsButton = false
        )
        extendedTouch(this, 0f, 1f - PlatformEntity.HEIGHT)
        addToEngine()
    }
}