package me.cael.capes.menu

import me.cael.capes.Capes
import me.cael.capes.render.PlaceholderEntity
import me.cael.capes.render.PlaceholderEntityRenderState
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.option.GameOptions
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.joml.Vector3f

class SelectorMenu(parent: Screen, gameOptions: GameOptions) : MainMenu(parent, gameOptions) {

    var lastTime = 0L

    override fun init() {
        super.init()

        var buttonW = 200
        val config = Capes.CONFIG

        addDrawableChild(ButtonWidget.builder(config.clientCapeType.getText()) {
            config.clientCapeType = config.clientCapeType.cycle()
            config.save()
            it.message = config.clientCapeType.getText()
            PlaceholderEntity.capeLoaded = false
        }.position((width / 2) - (buttonW / 2), 60).size(buttonW, 20).build())

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) {
            client!!.setScreen(parent)
        }.position((width / 2) - (buttonW / 2), 220).size(buttonW, 20).build())

        buttonW = 100

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.capes.selector.player")) {
            PlaceholderEntity.showBody = !PlaceholderEntity.showBody
        }.position((width / 4) - (buttonW / 2), 145).size(buttonW, 20).build())

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.capes.selector.elytra")) {
            PlaceholderEntity.showElytra = !PlaceholderEntity.showElytra
        }.position((width / 4) - (buttonW / 2), 120).size(buttonW, 20).build())

        addDrawableChild(ButtonWidget.builder(Text.literal("DO NOT ASK WHY THIS EXISTS")) {
        }.size(0, 0).build())

    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val playerX = (width/2) - 50
        val playerY = 65

        val time = System.currentTimeMillis()

        val entity = PlaceholderEntity

        if (time > lastTime + (1000 / 60)) {
            lastTime = time
            entity.prevX = entity.x + 0.025
            entity.updateLimbs()
        }

        drawEntity(context, playerX, playerY, playerX + 100, playerY + 300, PlaceholderEntity);
    }

    fun drawEntity(context: DrawContext, x1: Int, y1: Int, x2: Int, y2: Int, entity: PlaceholderEntity) {
        context.enableScissor(x1, y1, x2, y2)

        val entityRenderer = PlaceholderEntity.renderer
        val entityRenderState: PlaceholderEntityRenderState = entityRenderer.getAndUpdatePlaceholderRenderState(entity)

        context.addEntity(entityRenderState, 69f, Vector3f(0.0f, 0.0f, 0.0f), Quaternionf().rotateZ(Math.PI.toFloat()), null, x1, y1, x2, y2)
        context.disableScissor()
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        PlaceholderEntity.prevYaw = PlaceholderEntity.yaw
        PlaceholderEntity.yaw -= deltaX.toFloat()
        return true
    }

}