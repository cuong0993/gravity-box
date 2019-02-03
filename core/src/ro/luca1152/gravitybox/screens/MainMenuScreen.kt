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
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ro.luca1152.gravitybox.MyGame
import ro.luca1152.gravitybox.utils.ColorScheme
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MainMenuScreen(private val batch: Batch = Injekt.get(),
                     private val manager: AssetManager = Injekt.get()) : KtxScreen {
    private lateinit var uiStage: Stage
    private lateinit var skin: Skin
    private lateinit var root: Table

    override fun show() {
        uiStage = Stage(ExtendViewport(720f, 1280f), batch)
        skin = manager.get<Skin>("skins/uiskin.json")

        root = createRootTable().apply { uiStage.addActor(this) }
        root.add(createLogo()).expand().row()
        val bottomRow = Table().apply {
            add(createRateButton()).expandX().left()
            add(createSettingsButton()).right()
        }
        root.add(bottomRow).bottom().growX().pad(0f).padTop(-140f - 0f)

//        root.debug = true

        uiStage.addAction(sequence(
                fadeOut(0f),
                fadeIn(.5f))
        )

        Gdx.input.inputProcessor = uiStage
    }

    private fun createRootTable() = Table(skin).apply {
        pad(50f)
        setFillParent(true)
    }

    private fun createLogo(): Table {
        fun createPlayButton(): Button {
            val playButtonIcon = Image(skin, "play-button-empty").apply {
                color = ColorScheme.currentDarkColor
                addAction(forever(sequence(
                        delay(.5f),
                        fadeOut(1f),
                        fadeIn(1f)
                )))
            }
            val playButton = Button(skin, "big-button").apply {
                color = ColorScheme.currentDarkColor
                addListener(object : ClickListener() {
                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        color = ColorScheme.darkerDarkColor
                        playButtonIcon.color = ColorScheme.darkerDarkColor
                        return true
                    }

                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        color = ColorScheme.currentDarkColor
                        playButtonIcon.color = ColorScheme.currentDarkColor

                        if (isOver(this@apply, x, y)) {
                            LevelSelectorScreen.chosenlevel = 1
                            uiStage.addAction(sequence(
                                    fadeOut(.5f),
                                    run(Runnable { Injekt.get<MyGame>().setScreen<LevelSelectorScreen>() })
                            ))
                        }
                    }
                })
            }
            playButton.add(playButtonIcon)

            return playButton
        }

        fun createLogoImage() = Image(skin, "gravity-box").apply {
            color = ColorScheme.currentDarkColor
        }

        return Table().apply {
            add(createLogoImage()).row()
            add(createPlayButton()).padTop(-214f + 18f).padLeft(-4f)
        }
    }

    private fun createRateButton() = Button(skin, "small-button").apply {
        color = ColorScheme.currentDarkColor
        val heartIcon = Image(skin, "heart-icon").apply {
            color = ColorScheme.currentDarkColor
        }
        add(heartIcon)

        addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                color = ColorScheme.darkerDarkColor
                heartIcon.color = ColorScheme.darkerDarkColor
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                color = ColorScheme.currentDarkColor
                heartIcon.color = ColorScheme.currentDarkColor
            }
        })
    }

    private fun createSettingsButton() = Button(skin, "small-button").apply {
        color = ColorScheme.currentDarkColor
        val settingsIcon = Image(skin, "settings-icon").apply {
            color = ColorScheme.currentDarkColor
        }
        add(settingsIcon)

        addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                color = ColorScheme.darkerDarkColor
                settingsIcon.color = ColorScheme.darkerDarkColor
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                color = ColorScheme.currentDarkColor
                settingsIcon.color = ColorScheme.currentDarkColor
            }
        })
    }

    private fun update(delta: Float) {
        uiStage.act(delta)
    }

    override fun render(delta: Float) {
        update(delta)
        clearScreen(ColorScheme.currentLightColor.r, ColorScheme.currentLightColor.g, ColorScheme.currentLightColor.b)
        uiStage.draw()
    }
}