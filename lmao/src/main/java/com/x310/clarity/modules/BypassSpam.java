package com.x310.clarity.modules;

import com.x310.clarity.Main;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.mixin.ClientPlayNetworkHandlerAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class BypassSpam extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<SpamMode> mode = sgGeneral.add(new EnumSetting.Builder<SpamMode>()
        .name("mode")
        .description("Bypass spam mode")
        .defaultValue(SpamMode.NORMAL)
        .build()
    );

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("Messages to send")
        .defaultValue(new ArrayList<>())
        .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("Amount of messages to send")
        .defaultValue(1)
        .min(1)
        .max(100)
        .build()
    );

    private final Setting<String> bypassCommand = sgGeneral.add(new StringSetting.Builder()
        .name("bypass-command")
        .description("Command prefix for bypass")
        .defaultValue("/msg")
        .build()
    );

    private final Setting<Boolean> disableOnLeave = sgGeneral.add(new BoolSetting.Builder()
        .name("disable-on-leave")
        .description("Disable when leaving game")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> disableOnDisconnect = sgGeneral.add(new BoolSetting.Builder()
        .name("disable-on-disconnect")
        .description("Disable on disconnect screen")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> randomize = sgGeneral.add(new BoolSetting.Builder()
        .name("randomize")
        .description("Randomize message selection")
        .defaultValue(false)
        .build()
    );

    private int messageIndex = 0;

    public BypassSpam() {
        super(Main.CATEGORY, "BypassSpam", "Bypass spam filters");
    }

    @Override
    public void onActivate() {
        messageIndex = 0;
    }

   @EventHandler
   private void onScreenOpen(OpenScreenEvent event) {
        if (disableOnDisconnect.get()) {
            toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (disableOnLeave.get()) {
            toggle();
        }
    }

    @EventHandler
    private void onTick(Post event) {
        if (messages.get().isEmpty() || mc.player == null || mc.getNetworkHandler() == null) return;

        ClientPlayNetworkHandler handler = mc.getNetworkHandler();

        for (int times = 0; times < amount.get(); times++) {
            String message = getNextMessage();
            String fullMessage = bypassCommand.get() + " " + message;

            try {
                Instant instant = Instant.now();
                long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
                LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = 
                    ((ClientPlayNetworkHandlerAccessor) handler).getLastSeenMessagesCollector().collect();
                MessageSignatureData messageSignatureData = 
                    ((ClientPlayNetworkHandlerAccessor) handler).getMessagePacker().pack(
                        new MessageBody(fullMessage, instant, l, lastSeenMessages.lastSeen()));

                handler.sendPacket(new ChatMessageC2SPacket(fullMessage, instant, l, messageSignatureData, lastSeenMessages.update()));
            } catch (Exception e) {
                error("Failed to send message: " + e.getMessage());
            }
        }
    }

    private String getNextMessage() {
        if (randomize.get()) {
            return messages.get().get(Utils.random(0, messages.get().size()));
        } else {
            if (messageIndex >= messages.get().size()) {
                messageIndex = 0;
            }
            return messages.get().get(messageIndex++);
        }
    }

    public enum SpamMode {
        NORMAL("Normal"),
        MATRIX("Matrix"),
        RANDOM("Random");

        private final String name;

        SpamMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
