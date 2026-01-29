package com.hieu.client.mixin;

import com.hieu.client.Main;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        Main.delayedMessages.forEach(ChatUtils::info);
        Main.delayedMessages.clear();
    }
}
