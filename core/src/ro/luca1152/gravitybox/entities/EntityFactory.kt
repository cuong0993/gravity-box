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

package ro.luca1152.gravitybox.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import ktx.actors.minus
import ktx.assets.getAsset
import ro.luca1152.gravitybox.components.*
import ro.luca1152.gravitybox.utils.ColorScheme
import ro.luca1152.gravitybox.utils.EntityCategory
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get


object EntityFactory {
    fun createBullet(playerEntity: Entity,
                     world: World = Injekt.get(),
                     manager: AssetManager = Injekt.get(),
                     engine: PooledEngine = Injekt.get()) = engine.createEntity().apply label@{
        // BulletComponent
        add(engine.createComponent(BulletComponent::class.java))

        // PhysicsComponent
        add(engine.createComponent(PhysicsComponent::class.java))
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            bullet = true
            position.set(playerEntity.physics.body.worldCenter)
        }
        val fixtureDef = FixtureDef().apply {
            shape = PolygonShape().apply { setAsBox(.15f, .15f) }
            density = .2f
            filter.categoryBits = EntityCategory.BULLET.bits
            filter.maskBits = EntityCategory.OBSTACLE.bits
        }
        val body = world.createBody(bodyDef).apply {
            createFixture(fixtureDef)
            gravityScale = .5f
            userData = this@label
        }
        fixtureDef.shape.dispose()
        this.physics.set(body)

        // ImageComponent
        add(engine.createComponent(ImageComponent::class.java))
        this.image.set(manager.getAsset("graphics/bullet.png"), body.worldCenter)

        // ColorComponent
        add(engine.createComponent(ColorComponent::class.java))
        this.color.colorType = ColorType.DARK
        this.image.color = ColorScheme.currentDarkColor

        engine.addEntity(this)
    }!!

    fun createExplosion(position: Vector2,
                        manager: AssetManager = Injekt.get(),
                        engine: PooledEngine = Injekt.get()) = engine.createEntity().apply {
        // ExplosionComponent
        add(engine.createComponent(ExplosionComponent::class.java))

        // ImageComponent
        add(engine.createComponent(ImageComponent::class.java))
        this.image.set(manager.getAsset("graphics/circle.png"), position)
        this.image.img.run {
            setScale(1f)
            addAction(Actions.sequence(
                    Actions.parallel(
                            Actions.scaleBy(3f, 3f, .25f),
                            Actions.fadeOut(.25f, Interpolation.exp5)
                    ),
                    Actions.run {
                        stage - this
                        engine.removeEntity(this@apply)
                    },
                    Actions.removeActor()
            ))
        }

        // ColorComponent
        add(engine.createComponent(ColorComponent::class.java))
        this.color.set(ColorType.DARK)
        this.image.color = ColorScheme.currentDarkColor

        engine.addEntity(this)
    }!!

    fun createFinish(body: Body,
                     manager: AssetManager = Injekt.get(),
                     engine: PooledEngine = Injekt.get()) = engine.createEntity().apply {
        // FinishComponent
        add(engine.createComponent(FinishComponent::class.java))

        // PhysicsComponent
        add(engine.createComponent(PhysicsComponent::class.java))
        this.physics.set(body)

        // CollisionBoxComponent
        add(engine.createComponent(CollisionBoxComponent::class.java))
        this.collisionBox.set(2f)

        // ImageComponent
        add(engine.createComponent(ImageComponent::class.java))
        this.image.set(manager.getAsset("graphics/finish.png"), physics.body.worldCenter)
        this.image.img.run {
            addAction(RepeatAction().apply {
                action = Actions.sequence(
                        Actions.fadeOut(1f),
                        Actions.fadeIn(1f)
                )
                count = RepeatAction.FOREVER
            })
        }

        // ColorComponent
        add(engine.createComponent(ColorComponent::class.java))
        this.color.set(ColorType.DARK)
        this.image.color = ColorScheme.currentDarkColor

        engine.addEntity(this)
    }!!

    fun createMap(levelNumber: Int,
                  engine: PooledEngine = Injekt.get()) = engine.createEntity().apply {
        // MapComponent
        add(engine.createComponent(MapComponent::class.java))
        this.map.set(levelNumber)
    }!!

    fun createPlatform(mapObject: MapObject, isDynamic: Boolean, body: Body, engine: PooledEngine = Injekt.get()) = engine.createEntity().apply {
        // MapObjectComponent
        add(engine.createComponent(MapObjectComponent::class.java))
        this.mapObject.set(mapObject)

        // PlatformComponent
        add(engine.createComponent(PlatformComponent::class.java))
        this.platform.isDynamic = isDynamic

        // PhysicsComponent
        add(engine.createComponent(PhysicsComponent::class.java))
        this.physics.set(body)

        engine.addEntity(this)
    }!!

    fun createPlayer(body: Body,
                     manager: AssetManager = Injekt.get(),
                     engine: PooledEngine = Injekt.get()) = engine.createEntity().apply {
        // PlayerComponent
        add(engine.createComponent(PlayerComponent::class.java))

        // PhysicsComponent
        add(engine.createComponent(PhysicsComponent::class.java))
        this.physics.set(body)

        // CollisionBoxComponent
        add(engine.createComponent(CollisionBoxComponent::class.java))
        this.collisionBox.set(1f)

        // ImageComponent
        add(engine.createComponent(ImageComponent::class.java))
        this.image.set(manager.getAsset("graphics/player.png"), physics.body.worldCenter)

        // ColorComponent
        add(engine.createComponent(ColorComponent::class.java))
        this.color.set(ColorType.DARK)
        this.image.color = ColorScheme.currentDarkColor

        engine.addEntity(this)
    }!!

    fun createCollectible(body: Body,
                          engine: PooledEngine = Injekt.get()) = engine.createEntity().apply {
        // CollectibleComponent
        add(engine.createComponent(CollectibleComponent::class.java))

        // PhysicsComponent
        add(engine.createComponent(PhysicsComponent::class.java))
        this.physics.set(body)

        // CollisionBoxComponent
        add(engine.createComponent(CollisionBoxComponent::class.java))
        this.collisionBox.set(1f)

        engine.addEntity(this)
    }!!
}