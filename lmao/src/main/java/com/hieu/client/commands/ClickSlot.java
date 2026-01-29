package com.x310.clarity.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.slot.SlotActionType;

public class ClickSlot extends Command {
    public ClickSlot() {
        super("clickslot", "clickslot shit yk");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer(0))
            .then(argument("btn", IntegerArgumentType.integer(0, 1))
                .then(argument("count", IntegerArgumentType.integer(1))
                    .then(argument("type", StringArgumentType.word())
                        .executes(ctx -> {
                            int slot = IntegerArgumentType.getInteger(ctx, "slot");
                            int btn = IntegerArgumentType.getInteger(ctx, "btn");
                            int count = IntegerArgumentType.getInteger(ctx, "count");
                            String type = StringArgumentType.getString(ctx, "type");
                            ClientPlayerEntity player = mc.player;
                            if (player != null && mc.interactionManager != null && player.currentScreenHandler != null && slot < player.currentScreenHandler.slots.size()) {
                                try {
                                    SlotActionType action = SlotActionType.valueOf(type.toUpperCase());
                                    mc.execute(() -> {
                                        for (int i = 0; i < count; i++) {
                                            mc.interactionManager.clickSlot(player.currentScreenHandler.syncId, slot, btn, action, player);
                                        }
                                    });
                                } catch (IllegalArgumentException ignored) {}
                            }
                            return SINGLE_SUCCESS;
                        })))));
    }
}
