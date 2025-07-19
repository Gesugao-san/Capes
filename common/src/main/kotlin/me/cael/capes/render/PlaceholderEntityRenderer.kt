package me.cael.capes.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.entity.state.PlayerEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerModelPart
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier

class PlaceholderEntityRenderer(ctx: EntityRendererFactory.Context, slim: Boolean) :
    LivingEntityRenderer<LivingEntity, PlayerEntityRenderState, PlayerEntityModel>(
        ctx,
        PlayerEntityModel(ctx.getPart(if (slim) EntityModelLayers.PLAYER_SLIM else EntityModelLayers.PLAYER), slim),
        0.5f
    ) {

    val placeholderState = createRenderState()

    init {
        this.addFeature(CapeFeatureRenderer(this, ctx.entityModels, ctx.equipmentModelLoader))
        this.addFeature(HeadFeatureRenderer<PlayerEntityRenderState?, PlayerEntityModel?>(this, ctx.entityModels))
        this.addFeature(
            ElytraFeatureRenderer<PlayerEntityRenderState?, PlayerEntityModel?>(
                this,
                ctx.entityModels,
                ctx.equipmentRenderer
            )
        )
    }

    override fun render(livingEntityRenderState: PlayerEntityRenderState?, matrixStack: MatrixStack?, vertexConsumerProvider: VertexConsumerProvider?, i: Int) {
        this.model.parts.forEach { it.visible = PlaceholderEntity.showBody }
        super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i)
    }

    override fun getTexture(playerEntityRenderState: PlayerEntityRenderState): Identifier? {
        return playerEntityRenderState.skinTextures.texture()
    }

    override fun scale(playerEntityRenderState: PlayerEntityRenderState?, matrixStack: MatrixStack) {
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f)
    }

    override fun createRenderState(): PlaceholderEntityRenderState {
        return PlaceholderEntityRenderState()
    }

    fun getAndUpdatePlaceholderRenderState(entity: PlaceholderEntity): PlaceholderEntityRenderState {
        val entityRenderState = placeholderState
        updateRenderState(entity, entityRenderState)
        return entityRenderState
    }

    fun updateRenderState(placeholderEntity: PlaceholderEntity, playerEntityRenderState: PlayerEntityRenderState) {
        playerEntityRenderState.bodyYaw = placeholderEntity.yaw

        playerEntityRenderState.limbSwingAnimationProgress = placeholderEntity.limbAngle
        playerEntityRenderState.limbSwingAmplitude = placeholderEntity.limbDistance

        val options = MinecraftClient.getInstance().options
        playerEntityRenderState.leftArmPose = BipedEntityModel.ArmPose.EMPTY
        playerEntityRenderState.rightArmPose = BipedEntityModel.ArmPose.EMPTY
        playerEntityRenderState.skinTextures = placeholderEntity.getSkinTextures()
        playerEntityRenderState.hatVisible = options.isPlayerModelPartEnabled(PlayerModelPart.HAT)
        playerEntityRenderState.jacketVisible = options.isPlayerModelPartEnabled(PlayerModelPart.JACKET)
        playerEntityRenderState.leftPantsLegVisible = options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_PANTS_LEG)
        playerEntityRenderState.rightPantsLegVisible = options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_PANTS_LEG)
        playerEntityRenderState.leftSleeveVisible = options.isPlayerModelPartEnabled(PlayerModelPart.LEFT_SLEEVE)
        playerEntityRenderState.rightSleeveVisible = options.isPlayerModelPartEnabled(PlayerModelPart.RIGHT_SLEEVE)
        playerEntityRenderState.capeVisible = true

        playerEntityRenderState.equippedChestStack = if (placeholderEntity.showElytra) ItemStack(Items.ELYTRA) else ItemStack.EMPTY
        playerEntityRenderState.leftWingRoll = -(Math.PI / 12).toFloat()
        playerEntityRenderState.leftWingPitch = (Math.PI / 12).toFloat()

        playerEntityRenderState.name = placeholderEntity.gameProfile.name
    }
}
