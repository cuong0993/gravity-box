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

import com.badlogic.ashley.core.EntitySystem
import ro.luca1152.gravitybox.utils.kotlin.GameStage
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ImageRenderingSystem(private val gameStage: GameStage = Injekt.get()) : EntitySystem() {
    override fun update(deltaTime: Float) {
        gameStage.act()
        gameStage.draw()
    }
}