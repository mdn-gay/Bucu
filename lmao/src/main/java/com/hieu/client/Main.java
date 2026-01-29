package com.x310.clarity;

import com.x310.clarity.commands.*;
import com.x310.clarity.hud.Arraylist;
import com.x310.clarity.hud.ClarityChan;
import com.x310.clarity.hud.Logo;
import com.x310.clarity.hud.Watermark;
import com.mojang.logging.LogUtils;
import com.x310.clarity.modules.*;
import com.x310.clarity.modules.crashers.*;
import com.x310.clarity.utils.payload.PaperCustomPayload;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;

import java.util.ArrayList;

public class Main extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Clarity Addon");
    public static final Category CRASH_GROUP = new Category("Clarity Crash");
    public static final HudGroup HUD_GROUP = new HudGroup("Clarity");
    public static final ArrayList<String> delayedMessages = new ArrayList<>();

    @Override
    public void onInitialize() {
        LOG.info("Initializing Clarity");

        // Modules
        Modules.get().add(new ClarityNametags());
        Modules.get().add(new PacketCharge());
        Modules.get().add(new OhioCrash());
        Modules.get().add(new RecipeCrash());
        Modules.get().add(new ZlibBufferCrasher());
        Modules.get().add(new NettyVarIntCrasher());
        Modules.get().add(new SkillCrash());
        Modules.get().add(new PacketDelay());
        Modules.get().add(new VelocityCrash());
        Modules.get().add(new BypassSpam());
        Modules.get().add(new ChatBypass());
        Modules.get().add(new ChannelFetch());
        Modules.get().add(new PacketLogger());
        Modules.get().add(new SkillCrash2());
        Modules.get().add(new BungeeGuard());
        Modules.get().add(new BetterBoatFly());
        Modules.get().add(new BoatUAV());
        Modules.get().add(new BoatPlace());

        // Commands
        Commands.add(new GetAccessToken());
        Commands.add(new ChangeUsername());
        Commands.add(new ClickSlot());
        Commands.add(new SafeDisconnect());


        // HUD
        Hud.get().register(Watermark.INFO);
        Hud.get().register(Arraylist.INFO);
        Hud.get().register(Logo.INFO);
        Hud.get().register(ClarityChan.INFO);

		// Payload register
        PayloadTypeRegistry.playC2S().register(PaperCustomPayload.ID, PaperCustomPayload.CODEC);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
        Modules.registerCategory(CRASH_GROUP);
    }

    @Override
    public String getPackage() {
        return "com.x310.clarity";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("ck-clarity", "addon");
    }
}
