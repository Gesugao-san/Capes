package me.cael.capes.render

import me.cael.capes.Capes
import me.cael.capes.handler.PlayerHandler
import me.cael.capes.mixins.AccessorEntityRenderManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.entity.player.PlayerSkinType
import net.minecraft.entity.player.SkinTextures
import net.minecraft.util.AssetInfo
import net.minecraft.util.Identifier
import kotlin.math.sqrt

object PlaceholderEntity {
    val gameProfile = MinecraftClient.getInstance().gameProfile

    private var skin: SkinTextures = DefaultSkinHelper.getSkinTextures(gameProfile)

    var slim = false

    var showBody = true
    var showElytra = false
    var capeLoaded = false
    var limbDistance = 0f
    var lastLimbDistance = 0f
    var limbAngle = 0f
    var yaw = 0f
    var prevYaw = 0f
    var x = 0.0
    var prevX = 0.0
    var renderer: PlaceholderEntityRenderer

    init {
        val ctx = EntityRendererFactory.Context(
            MinecraftClient.getInstance().entityRenderDispatcher,
            MinecraftClient.getInstance().itemModelManager,
            MinecraftClient.getInstance().mapRenderer,
            MinecraftClient.getInstance().blockRenderManager,
            MinecraftClient.getInstance().resourceManager,
            MinecraftClient.getInstance().loadedEntityModels,
            (MinecraftClient.getInstance().entityRenderDispatcher as AccessorEntityRenderManager).equipmentModelLoader,
            MinecraftClient.getInstance().atlasManager,
            MinecraftClient.getInstance().textRenderer,
            MinecraftClient.getInstance().playerSkinCache
        )
        renderer = PlaceholderEntityRenderer(ctx, slim)
        MinecraftClient.getInstance().skinProvider.fetchSkinTextures(gameProfile).thenAccept {
            skin = it.get()
            slim = skin.model == PlayerSkinType.SLIM
            renderer = PlaceholderEntityRenderer(ctx, slim)
        }
    }

    fun updateLimbs() {
        this.lastLimbDistance = this.limbDistance
        val d = this.x - this.prevX
        var g = sqrt(d * d).toFloat() * 4.0f
        if (g > 1.0f) {
            g = 1.0f
        }
        this.limbDistance += (g - this.limbDistance) * 0.4f
        this.limbAngle += this.limbDistance
    }

    fun getCapeTexture(): AssetInfo.TextureAsset? {
        if (!capeLoaded) {
            capeLoaded = true
            PlayerHandler.onLoadTexture(gameProfile)
        }
        val handler = PlayerHandler.fromProfile(gameProfile)
        return if (handler.hasCape) handler.getCape() else skin.cape
    }

    fun getElytraTexture(): AssetInfo.TextureAsset {
        val handler = PlayerHandler.fromProfile(gameProfile)
        val capeTexture = getCapeTexture()
        return if (handler.hasElytraTexture && Capes.CONFIG.enableElytraTexture && capeTexture != null) capeTexture
        else AssetInfo.TextureAssetInfo(Identifier.of("textures/entity/equipment/wings/elytra.png"),null);
    }

    fun getSkinTextures() : SkinTextures {
        return SkinTextures(skin.body, getCapeTexture(), getElytraTexture(), skin.model, skin.secure)
    }
}