package com.x310.clarity.netty;

import com.x310.clarity.Main;
import com.x310.clarity.modules.ChannelFetch;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.network.PacketByteBuf;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class ClientPayloadPacketDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (Modules.get().get(ChannelFetch.class).isActive()) {
            PacketByteBuf b = new PacketByteBuf(ctx.alloc().buffer().writeBytes(in));
            if (b.readVarInt() == 25) {
                decodePayload(b);
            }
        }
        out.add(in.resetReaderIndex().retain());
    }

    public void decodePayload(PacketByteBuf b) {
        String channel = b.readString();
        byte[] data = new byte[b.readableBytes()];
        b.readBytes(data);

        if (Objects.equals(channel, "REGISTER") || Objects.equals(channel, "minecraft:register")) {
            String channels = new String(data, Charset.defaultCharset());
            String[] channelArray = channels.split("\000");
            for (String s : channelArray) {
                Main.delayedMessages.add("Register: " + s);
            }
            return;
        }
        Main.delayedMessages.add("Channel: " + channel + " | Data: " + new String(data, Charset.defaultCharset()));
    }
}
