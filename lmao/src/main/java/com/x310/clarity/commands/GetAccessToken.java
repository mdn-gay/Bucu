package com.x310.clarity.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class GetAccessToken extends Command {
    public GetAccessToken() {
        super("getaccesstoken", "Get access token.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            String accessToken = mc.getSession().getAccessToken();
            Text accessTokenText = Text.literal("Access Token: " + "[Click here to copy]")
                .setStyle(Text.literal("").getStyle()
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, accessToken))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Copy to clipboard"))));
            info(accessTokenText);
            return SINGLE_SUCCESS;
        });
    }
}
