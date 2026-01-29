package com.hieu.client.utils.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PaperCustomPayload(byte[] data) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, PaperCustomPayload> CODEC = CustomPayload.codecOf(PaperCustomPayload::write, PaperCustomPayload::new);
    public static final CustomPayload.Id<PaperCustomPayload> ID = new Id<>(Identifier.of("paper", "issue"));

    public PaperCustomPayload(byte[] data) {
        this.data = data;
    }

    public PaperCustomPayload(PacketByteBuf buf) {
        this(buf.readByteArray());
    }

    private void write(PacketByteBuf buf) {
        buf.writeBytes(data);
    }

    @Override
    public CustomPayload.Id<? extends  CustomPayload> getId() {
        return ID;
    }

}
