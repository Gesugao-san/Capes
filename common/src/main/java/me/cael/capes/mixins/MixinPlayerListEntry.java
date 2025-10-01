package me.cael.capes.mixins;

import com.mojang.authlib.GameProfile;
import me.cael.capes.CapeConfig;
import me.cael.capes.Capes;
import me.cael.capes.handler.PlayerHandler;
import me.cael.capes.util.UtilsKt;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {
    @Shadow @Final private GameProfile profile;

    @Inject(method = "texturesSupplier", at = @At("HEAD"))
    private static void loadTextures(GameProfile profile, CallbackInfoReturnable<Supplier<SkinTextures>> cir) {
        if (UtilsKt.isValidProfile(profile)) {
            PlayerHandler.Companion.onLoadTexture(profile);
        }
    }

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
        if (!UtilsKt.isValidProfile(profile)) return;
        PlayerHandler handler = PlayerHandler.Companion.fromProfile(profile);
        if (handler.getHasCape()) {
            CapeConfig config = Capes.INSTANCE.getCONFIG();
            SkinTextures oldTextures = cir.getReturnValue();
            AssetInfo.TextureAsset capeTexture = handler.getCape();
            AssetInfo.TextureAsset elytraTexture = handler.getHasElytraTexture() && config.getEnableElytraTexture() ? capeTexture : new AssetInfo.TextureAssetInfo(Identifier.of("textures/entity/equipment/wings/elytra.png"),null);
            SkinTextures newTextures = new SkinTextures(
                    oldTextures.body(),
                    capeTexture, elytraTexture,
                    oldTextures.model(), oldTextures.secure());
            cir.setReturnValue(newTextures);
        }
    }

}
