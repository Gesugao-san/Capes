package me.cael.capes.mixins;

import me.cael.capes.render.PlaceholderEntity;
import me.cael.capes.render.PlaceholderEntityRenderState;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

    @Inject(method = "getRenderer(Lnet/minecraft/client/render/entity/state/EntityRenderState;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <S extends EntityRenderState> void getPlaceholderRenderer(S state, CallbackInfoReturnable<EntityRenderer<?, ? super S>> cir) {
        if (state instanceof PlaceholderEntityRenderState) {
            cir.setReturnValue((EntityRenderer<?, ? super S>) PlaceholderEntity.INSTANCE.getRenderer());
        }
    }
}