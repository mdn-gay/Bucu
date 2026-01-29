package com.x310.clarity.modules;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import java.util.Random;

public class BoatPlace extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final Random random = new Random();

    public BoatPlace() {
        super(Main.CATEGORY, "BoatPlace", "Bypass for placing boats on 6b6t.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerInteractBlockC2SPacket originalPacket)) return;
        if (mc.player == null || mc.world == null || mc.player.networkHandler == null) {
            ChatUtils.error("BoatPlace", "p/w/n handler is null.");
            return;
        }

        Item mainHandItem = mc.player.getMainHandStack().getItem();
        if (!isBoatItem(mainHandItem)) return;

        event.cancel();
        mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, 0, -90));
    }

    private boolean isBoatItem(Item item) {
        return item == Items.OAK_BOAT || item == Items.SPRUCE_BOAT || item == Items.BIRCH_BOAT ||
            item == Items.JUNGLE_BOAT || item == Items.ACACIA_BOAT || item == Items.DARK_OAK_BOAT ||
            item == Items.MANGROVE_BOAT || item == Items.CHERRY_BOAT || item == Items.BAMBOO_RAFT;
    }

}
