package com.x310.clarity.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;

public class SafeDisconnect extends Command {
    public SafeDisconnect() {
        super("safedisconnect", "Disconnect by sending a keepalive packet with -1 value");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ClientPlayNetworkHandler handler = mc.getNetworkHandler();
            assert handler != null;
            handler.sendPacket(new KeepAliveC2SPacket(-1));
            return SINGLE_SUCCESS;

        });
    }
}
