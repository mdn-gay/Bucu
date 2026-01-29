package com.x310.clarity.modules;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PacketDelay extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<Class<? extends Packet<?>>>> s2cPackets = sgGeneral.add(new PacketListSetting.Builder()
        .name("s2c-packets")
        .description("s2c packets to hold until module is disabled.")
        .filter(aClass -> PacketUtils.getS2CPackets().contains(aClass))
        .build()
    );

    private final Setting<Set<Class<? extends Packet<?>>>> c2sPackets = sgGeneral.add(new PacketListSetting.Builder()
        .name("c2s-packets")
        .description("c2s packets to hold until module is disabled.")
        .filter(aClass -> PacketUtils.getC2SPackets().contains(aClass))
        .build()
    );

    private final List<Packet<?>> delayedPackets = new ArrayList<>();

    public PacketDelay() {
        super(Main.CATEGORY, "packet-delay", "Holds packets then releases them when module is disabled");
    }

    @Override
    public void onDeactivate() {
        for (Packet<?> packet : delayedPackets) {
            if (PacketUtils.getS2CPackets().contains(packet.getClass())) {
                if (mc.getNetworkHandler() != null) {
                    @SuppressWarnings("unchecked")
                    Packet<ClientPlayPacketListener> s2cPacket = (Packet<ClientPlayPacketListener>) packet;
                    s2cPacket.apply(mc.getNetworkHandler());
                }
            } else if (PacketUtils.getC2SPackets().contains(packet.getClass())) {
                if (mc.getNetworkHandler() != null) {
                    mc.getNetworkHandler().sendPacket(packet);
                }
            }
        }
        delayedPackets.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onReceivePacket(PacketEvent.Receive event) {
        if (s2cPackets.get().contains(event.packet.getClass())) {
            delayedPackets.add(event.packet);
            event.cancel();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onSendPacket(PacketEvent.Send event) {
        if (c2sPackets.get().contains(event.packet.getClass())) {
            delayedPackets.add(event.packet);
            event.cancel();
        }
    }
}
