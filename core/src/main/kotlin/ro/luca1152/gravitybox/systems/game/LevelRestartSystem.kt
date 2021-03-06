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

package ro.luca1152.gravitybox.systems.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.BodyDef
import ro.luca1152.gravitybox.components.editor.EditorObjectComponent
import ro.luca1152.gravitybox.components.editor.editorObject
import ro.luca1152.gravitybox.components.game.*
import ro.luca1152.gravitybox.entities.game.PlatformEntity
import ro.luca1152.gravitybox.utils.kotlin.getSingleton
import ro.luca1152.gravitybox.utils.kotlin.removeAndResetEntity
import ro.luca1152.gravitybox.utils.kotlin.tryGet

/** Handles what happens when a level is marked as to be restarted. */
class LevelRestartSystem : EntitySystem() {
    private lateinit var levelEntity: Entity

    override fun addedToEngine(engine: Engine) {
        levelEntity = engine.getSingleton<LevelComponent>()
    }

    override fun update(deltaTime: Float) {
        if (!levelEntity.level.restartLevel)
            return
        restartTheLevel()
    }

    private fun restartTheLevel() {
        resetBodiesToInitialState()
        resetMovingPlatforms()
        resetDestroyablePlatforms()
        resetCollectiblePoints()
        removeBullets()
        levelEntity.level.restartLevel = false
    }

    private fun removeBullets() {
        engine.getEntitiesFor(Family.all(BulletComponent::class.java).get()).forEach {
            engine.removeAndResetEntity(it)
        }
    }

    private fun resetDestroyablePlatforms() {
        engine.getEntitiesFor(Family.all(DestroyablePlatformComponent::class.java).get()).forEach {
            if (it.tryGet(EditorObjectComponent) == null || !it.editorObject.isDeleted) {
                it.run {
                    if (destroyablePlatform.isRemoved) {
                        destroyablePlatform.isRemoved = false
                        scene2D.isVisible = true
                        val bodyType =
                            if (tryGet(DestroyablePlatformComponent) == null) BodyDef.BodyType.StaticBody else BodyDef.BodyType.KinematicBody
                        val categoryBits = PlatformEntity.CATEGORY_BITS
                        val maskBits = PlatformEntity.MASK_BITS
                        body(scene2D.toBody(bodyType, categoryBits, maskBits), categoryBits, maskBits)
                    }
                }
            }
        }
    }

    private fun resetCollectiblePoints() {
        engine.getEntitiesFor(Family.all(CollectiblePointComponent::class.java).get()).forEach {
            if (it.tryGet(EditorObjectComponent) == null || !it.editorObject.isDeleted) {
                it.run {
                    if (collectiblePoint.isCollected) {
                        collectiblePoint.isCollected = false
                        scene2D.isVisible = true
                    }
                }
            }
        }
    }

    private fun resetBodiesToInitialState() {
        engine.getEntitiesFor(Family.all(BodyComponent::class.java).exclude(CombinedBodyComponent::class.java).get())
            .forEach {
                if (it.tryGet(BodyComponent) != null) {
                    it.body.resetToInitialState()
                    it.scene2D.run {
                        centerX = it.body.body.worldCenter.x
                        centerY = it.body.body.worldCenter.y
                    }
                }
            }
    }

    private fun resetMovingPlatforms() {
        engine.getEntitiesFor(Family.all(MovingObjectComponent::class.java).get()).forEach {
            it.movingObject.run {
                isMovingTowardsEndPoint = true
                moved(it, it.linkedEntity.get("mockPlatform"))
            }
        }
    }
}