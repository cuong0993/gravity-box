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

package ro.luca1152.gravitybox.systems.editor

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.input.GestureDetector.GestureAdapter
import ro.luca1152.gravitybox.components.editor.InputComponent
import ro.luca1152.gravitybox.components.editor.input
import ro.luca1152.gravitybox.components.game.pixelsToMeters
import ro.luca1152.gravitybox.utils.kotlin.GameCamera
import ro.luca1152.gravitybox.utils.ui.button.ButtonType
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/** Handles pan gestures. */
class PanningSystem(
    private val gameCamera: GameCamera = Injekt.get(),
    private val inputMultiplexer: InputMultiplexer = Injekt.get()
) : EntitySystem() {
    private lateinit var inputEntity: Entity
    private val gestureDetector = GestureDetector(object : GestureAdapter() {
        override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
            if (!moveToolIsUsed())
                return false

            inputEntity.input.isPanning = true
            panCamera(deltaX, deltaY)

            return true
        }

        private fun moveToolIsUsed() = inputEntity.input.toggledButton.get()?.type == ButtonType.MOVE_TOOL_BUTTON

        private fun panCamera(deltaX: Float, deltaY: Float) {
            gameCamera.position.add(
                -deltaX.pixelsToMeters * gameCamera.zoom,
                deltaY.pixelsToMeters * gameCamera.zoom,
                0f
            )
        }
    })

    override fun addedToEngine(engine: Engine) {
        inputEntity = engine.getEntitiesFor(Family.all(InputComponent::class.java).get()).first()
        inputMultiplexer.addProcessor(gestureDetector)
    }

    override fun removedFromEngine(engine: Engine?) {
        inputMultiplexer.removeProcessor(gestureDetector)
    }
}