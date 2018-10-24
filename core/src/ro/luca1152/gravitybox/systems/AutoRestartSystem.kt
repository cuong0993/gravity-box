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

package ro.luca1152.gravitybox.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.systems.IteratingSystem
import ro.luca1152.gravitybox.components.PhysicsComponent
import ro.luca1152.gravitybox.components.PlayerComponent
import ro.luca1152.gravitybox.components.physics
import ro.luca1152.gravitybox.events.GameEvent

/**
 * Restarts the level when the player is off-screen.
 */
class AutoRestartSystem(private val gameEventSignal: Signal<GameEvent>) : IteratingSystem(Family.all(PlayerComponent::class.java, PhysicsComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.physics.body.worldCenter.y < -10f)
            gameEventSignal.dispatch(GameEvent.LEVEL_RESTART)
    }
}