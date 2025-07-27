package me.cael.capes.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.cael.capes.CapeConfig;
import me.cael.capes.Capes;
import me.cael.capes.handler.PlayerHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {
    @Shadow @Final private GameProfile profile;

    @Inject(method = "texturesSupplier", at = @At("HEAD"))
    private static void loadTextures(GameProfile profile, CallbackInfoReturnable<Supplier<SkinTextures>> cir) {
        if (capes$isValidProfile(profile)) {
            PlayerHandler.Companion.onLoadTexture(profile);
        }
    }

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
        if (!capes$isValidProfile(profile)) return;
        PlayerHandler handler = PlayerHandler.Companion.fromProfile(profile);
        if (handler.getHasCape()) {
            CapeConfig config = Capes.INSTANCE.getCONFIG();
            SkinTextures oldTextures = cir.getReturnValue();
            Identifier capeTexture = handler.getCape();
            Identifier elytraTexture = handler.getHasElytraTexture() && config.getEnableElytraTexture() ? capeTexture : Identifier.of("textures/entity/equipment/wings/elytra.png");
            SkinTextures newTextures = new SkinTextures(
                    oldTextures.texture(), oldTextures.textureUrl(),
                    capeTexture, elytraTexture,
                    oldTextures.model(), oldTextures.secure());
            cir.setReturnValue(newTextures);
        }
    }

    @Unique
    private static boolean capes$isValidProfile(GameProfile profile) {
        Optional<Property> property = profile.getProperties().get("textures").stream().findFirst();
        if (property.isEmpty()) return false;

        String textures = property.get().value();
        String json = new String(Base64.getDecoder().decode(textures), StandardCharsets.UTF_8);
        JsonElement root;
        try {
            root = JsonParser.parseString(json);
        } catch (JsonSyntaxException e) {
            return false;
        }
        if (!root.isJsonObject()) return false;
        JsonElement element = root.getAsJsonObject().get("profileName");
        if (element == null || element.isJsonNull()) return false;

        String profileName = element.getAsString();
        return profile.getId().version() == 4 || (profile.getId().version() == 2 && profile.getName().equals(profileName));
    }

}
