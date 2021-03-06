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
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.math.MathUtils
import ro.luca1152.gravitybox.components.editor.*
import ro.luca1152.gravitybox.components.game.*
import ro.luca1152.gravitybox.entities.editor.MovingMockPlatformEntity
import ro.luca1152.gravitybox.entities.game.CollectiblePointEntity
import ro.luca1152.gravitybox.entities.game.PlatformEntity
import ro.luca1152.gravitybox.screens.LevelEditorScreen
import ro.luca1152.gravitybox.utils.kotlin.getSingleton
import ro.luca1152.gravitybox.utils.kotlin.screenToWorldCoordinates
import ro.luca1152.gravitybox.utils.ui.button.ButtonType
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/** Places objects at touch when the place tool is used. */
class ObjectPlacementSystem(
    private val levelEditorScreen: LevelEditorScreen,
    private val inputMultiplexer: InputMultiplexer = Injekt.get()
) : EntitySystem() {
    private lateinit var undoRedoEntity: Entity
    private lateinit var inputEntity: Entity
    private lateinit var mapEntity: Entity

    private val inputAdapter = object : InputAdapter() {
        lateinit var placedObject: Entity
        var didPlaceObject = false

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (!placeToolIsUsed()) {
                didPlaceObject = false
                return false
            }

            createPlatformAt(screenX, screenY)
            didPlaceObject = true

            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            return if (didPlaceObject) {
                selectPlacedObject()
                didPlaceObject = false
                true
            } else false
        }

        private fun placeToolIsUsed() =
            inputEntity.input.toggledButton.get()?.type == ButtonType.PLACE_TOOL_BUTTON

        private fun createPlatformAt(screenX: Int, screenY: Int) {
            val coords = screenToWorldCoordinates(screenX, screenY)
            val platformWidth = 1f
            val id = engine.getEntitiesFor(Family.all(MapObjectComponent::class.java).get())
                .filter { !it.editorObject.isDeleted }.size
            placedObject = when (inputEntity.input.placeToolObjectType) {
                PlatformComponent::class.java, DestroyablePlatformComponent::class.java, MovingObjectComponent::class.java -> {
                    PlatformEntity.createEntity(
                        id,
                        MathUtils.floor(coords.x).toFloat() + .5f,
                        MathUtils.floor(coords.y).toFloat() + .5f,
                        platformWidth,
                        isDestroyable = inputEntity.input.placeToolObjectType == DestroyablePlatformComponent::class.java
                    )
                }
                CollectiblePointComponent::class.java -> {
                    CollectiblePointEntity.createEntity(
                        id,
                        MathUtils.floor(coords.x).toFloat() + .5f,
                        MathUtils.floor(coords.y).toFloat() + .5f,
                        blinkEndlessly = false
                    )
                }
                else -> error("placeToolObjectType was not recognized.")
            }
            placedObject.scene2D.color.a = LevelEditorScreen.OBJECTS_COLOR_ALPHA

            // Place the mock moving platform in the level editor

            if (inputEntity.input.placeToolObjectType == MovingObjectComponent::class.java) {
                val mockPlatform = MovingMockPlatformEntity.createEntity(
                    placedObject,
                    placedObject.scene2D.centerX + 1f, placedObject.scene2D.centerY + 1f,
                    placedObject.scene2D.width, placedObject.scene2D.rotation
                )
                placedObject.linkedEntity("mockPlatform", mockPlatform)
                placedObject.movingObject(mockPlatform.scene2D.centerX, mockPlatform.scene2D.centerY)
            }

            mapEntity.map.updateRoundedPlatforms = true
            undoRedoEntity.undoRedo.addExecutedCommand(AddCommand(placedObject, mapEntity))
        }

        private fun selectPlacedObject() {
            deselectAllPlatforms()
            placedObject.editorObject.isSelected = true
            useMoveTool()
        }

        private fun deselectAllPlatforms() {
            engine.getEntitiesFor(Family.all(EditorObjectComponent::class.java).get()).forEach {
                it.editorObject.isSelected = false
            }
        }

        private fun useMoveTool() {
            levelEditorScreen.run {
                placeToolButton.run {
                    isToggled = false
                    clickedOutsidePane()
                }
                moveToolButton.isToggled = true
                inputEntity.input.toggledButton.set(moveToolButton)
            }
        }
    }

    override fun addedToEngine(engine: Engine) {
        undoRedoEntity = engine.getSingleton<UndoRedoComponent>()
        inputEntity = engine.getSingleton<InputComponent>()
        mapEntity = engine.getSingleton<MapComponent>()
        inputMultiplexer.addProcessor(inputAdapter)
    }

    override fun removedFromEngine(engine: Engine?) {
        inputMultiplexer.removeProcessor(inputAdapter)
    }
}