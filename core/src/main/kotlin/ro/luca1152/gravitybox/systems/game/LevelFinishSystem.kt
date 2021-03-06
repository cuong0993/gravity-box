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
import com.badlogic.gdx.math.MathUtils
import ro.luca1152.gravitybox.MyGame
import ro.luca1152.gravitybox.components.game.*
import ro.luca1152.gravitybox.utils.kotlin.approxEqualTo
import ro.luca1152.gravitybox.utils.kotlin.getSingleton
import ro.luca1152.gravitybox.utils.ui.Colors

/** Handles what happens when a level is finished. */
class LevelFinishSystem(private val restartLevelWhenFinished: Boolean = false) : EntitySystem() {
    private lateinit var levelEntity: Entity
    private lateinit var playerEntity: Entity

    // The color scheme is the one that tells whether the level was finished: if the current color scheme
    // is the same as the dark color scheme, then it means that the level was finished. I should change
    // this in the future.
    private val colorSchemeIsFullyTransitioned
        get() = (Colors.useDarkTheme && Colors.gameColor.approxEqualTo(Colors.LightTheme.game57))
                || (!Colors.useDarkTheme && Colors.gameColor.approxEqualTo(Colors.DarkTheme.game95))
    private val levelIsFinished
        get() = playerEntity.player.isInsideFinishPoint && colorSchemeIsFullyTransitioned

    override fun addedToEngine(engine: Engine) {
        levelEntity = engine.getSingleton<LevelComponent>()
        playerEntity = engine.getSingleton<PlayerComponent>()
    }

    override fun update(deltaTime: Float) {
        if (!levelIsFinished)
            return
        handleLevelFinish()
    }

    private fun handleLevelFinish() {
        if (restartLevelWhenFinished)
            levelEntity.level.restartLevel = true
        else {
            Colors.hue = MathUtils.random(0, 360)
            deleteEntities()
            levelEntity.level.run {
                levelId = Math.min(levelId + 1, MyGame.LEVELS_NUMBER)
                loadMap = true
                forceUpdateMap = true
            }
            levelEntity.map.run {
                updateRoundedPlatforms = true
            }
        }
    }

    private fun deleteEntities() {
        engine.getEntitiesFor(
            Family.all(BodyComponent::class.java).exclude(
                PlayerComponent::class.java,
                FinishComponent::class.java,
                LevelComponent::class.java
            ).get()
        ).forEach {
            engine.removeEntity(it)
        }
    }
}