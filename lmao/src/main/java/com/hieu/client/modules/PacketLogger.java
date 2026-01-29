package com.x310.clarity.modules;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

public class PacketLogger extends Module {
    private final SettingGroup sgGeneral = settings.createGroup("General");

    private final Setting<Boolean> incoming = sgGeneral.add(new BoolSetting.Builder()
        .name("incoming")
        .description("Logs incoming packets.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> outgoing = sgGeneral.add(new BoolSetting.Builder()
        .name("outgoing")
        .description("Logs outgoing packets.")
        .defaultValue(true)
        .build()
    );

    public PacketLogger() {
        super(Main.CATEGORY, "Packet Logger", "Logs incoming and outgoing packets.");
    }

    @EventHandler
    private void onReceive(PacketEvent.Receive event) {
        if (incoming.get()) ChatUtils.info("[Incoming] " + event.packet);
    }

    @EventHandler
    private void onSend(PacketEvent.Send event) {
        if (outgoing.get()) ChatUtils.info("[Outgoing] " + event.packet);
    }
}
