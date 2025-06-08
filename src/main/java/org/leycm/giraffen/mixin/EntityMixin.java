package org.leycm.giraffen.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.leycm.giraffen.module.modules.esp.EntityEspModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract boolean isGlowing();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract boolean equals(Object o);


    @Shadow public abstract void emitGameEvent(RegistryEntry<GameEvent> event);

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    public void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!EntityEspModule.getInstance().getData("esp.type", String.class, "glowing").equals("glowing") || !EntityEspModule.getInstance().isRunning()) return;
        Entity entity = (Entity)(Object)this;
        boolean shouldGlow = EntityEspModule.getInstance().shouldGlow(entity);

        if (shouldGlow) cir.setReturnValue(true);

    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    public void onGetTeamColorValue(@NotNull CallbackInfoReturnable<Integer> cir) {
        if (!EntityEspModule.getInstance().getData("esp.type", String.class, "glowing").equals("glowing") || !EntityEspModule.getInstance().isRunning()) return;

        Entity entity = (Entity)(Object)this;
        int color = EntityEspModule.getInstance().getColor(entity);


        cir.setReturnValue(color);
    }
}