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

package ro.luca1152.gravitybox.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.load
import ktx.log.info
import ro.luca1152.gravitybox.MyGame
import ro.luca1152.gravitybox.utils.assets.Assets
import ro.luca1152.gravitybox.utils.assets.loaders.Text
import ro.luca1152.gravitybox.utils.assets.loaders.TextLoader
import ro.luca1152.gravitybox.utils.kotlin.UIStage
import ro.luca1152.gravitybox.utils.kotlin.clearScreen
import ro.luca1152.gravitybox.utils.kotlin.setScreen
import ro.luca1152.gravitybox.utils.ui.Colors
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class LoadingScreen(
    private val manager: AssetManager = Injekt.get(),
    private val game: MyGame = Injekt.get(),
    private val uiStage: UIStage = Injekt.get()
) : KtxScreen {
    private var loadingAssetsTimer = 0f
    private val finishedLoadingAssets
        get() = manager.update()

    override fun show() {
        loadGraphics()
        loadMaps()
    }

    private fun loadGraphics() {
        manager.run {
            load(Assets.uiSkin)
            load(Assets.tileset)
        }
    }

    private fun loadMaps() {
        manager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))
        loadGameMaps()
    }

    private fun loadGameMaps() {
        for (i in 1..MyGame.LEVELS_NUMBER) {
            manager.load<Text>("maps/game/map-$i.json")
        }
    }

    private fun loadEditorMaps() {
        Gdx.files.local("maps/editor").list().forEach {
            manager.load<Text>(it.path())
        }
    }

    override fun render(delta: Float) {
        update(delta)
        clearScreen(Colors.bgColor)
    }

    private var finishedLoadingOnce = false

    private fun update(delta: Float) {
        loadingAssetsTimer += delta
        uiStage.act()
        if (finishedLoadingAssets) {
            if (!finishedLoadingOnce) {
                finishedLoadingOnce = true
                loadEditorMaps()
                return
            }
            logLoadingTime()
            addScreens()
            game.setScreen(TransitionScreen(MainMenuScreen::class.java, false))
        }
    }

    private fun logLoadingTime() {
        info { "Finished loading assets in ${(loadingAssetsTimer * 100).toInt() / 100f}s." }
    }

    /**
     * Adds screens to the [KtxGame] so [KtxGame.setScreen] works.
     *
     * They are added here and not in [MyGame] because adding a screen automatically initializes it, initialization which
     * may use assets, such as [Skin]s or [Texture]s.
     */
    private fun addScreens() {
        game.run {
            addScreen(MainMenuScreen())
            addScreen(LevelEditorScreen())
            addScreen(LevelSelectorScreen())
            addScreen(PlayScreen())
        }
    }
}