package me.cael.capes.mixins;

import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderManager.class)
public interface AccessorEntityRenderManager {
    @Accessor
    EquipmentModelLoader getEquipmentModelLoader();
}
