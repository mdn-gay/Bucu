package com.hieu.client.hud;

import com.hieu.client.Main;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.network.PlayerListEntry;
import meteordevelopment.meteorclient.systems.modules.Modules;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Arraylist extends HudElement {
    private final SettingGroup sgScale = settings.createGroup("Scale");
    private final SettingGroup sgColor = settings.createGroup("Color");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> outlines = sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
        .name("outlines"))
        .description("Yes/no render outlines"))
        .defaultValue(false))
        .build()
    );

    SettingGroup var10001 = this.sgGeneral;
    Setting<Boolean> var10003 = this.outlines;
    DoubleSetting.Builder var5 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
        .name("scale"))
        .description("Custom scale.");

    private final Setting<Double> scale = var10001.add(((DoubleSetting.Builder)var5.visible(var10003::get)).defaultValue((double)1.0F).min((double)0.5F).sliderRange((double)0.5F, (double)3.0F).build());;
    private final Setting<Boolean> customScale = sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
        .name("custom-scale"))
        .description("scale shit ts ts pmo icl"))
        .defaultValue(false))
        .build()
    );

    private static int ping() {
        if (mc.getNetworkHandler() == null || mc.player == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return playerListEntry != null ? playerListEntry.getLatency() : 0;
    }

    public static final HudElementInfo<Arraylist> INFO = new HudElementInfo<>(Main.HUD_GROUP, "Arraylist", "Arraylist", Arraylist::new);

    public Arraylist() {
        super(INFO);
    }

    private double getScale() {
        return (Boolean)this.customScale.get() ? (Double)this.scale.get() : Hud.get().getTextScale();
    }

    @Override
    public void render(HudRenderer renderer) {
        int paddingX = 6;
        int paddingY = 3;

        var activeModules = Modules.get().getActive();

        activeModules.sort((module1, module2) -> Integer.compare(module2.title.length(), module1.title.length()));

        float totalHeight = 0;
        for (int i = 0; i < activeModules.size(); i++) {
            String moduleName = activeModules.get(i).title;
            float moduleWidth = (float) renderer.textWidth(moduleName);
            totalHeight += (float) renderer.textHeight(true, getScale());

            float fadeFactor = (float) (System.currentTimeMillis() % 1000) / 1000;

            int red = (int) (177 + (117 - 177) * Math.sin(fadeFactor * Math.PI * 2));
            int green = (int) (205 + (168 - 205) * Math.sin(fadeFactor * Math.PI * 2));
            int blue = (int) (255 * Math.sin(fadeFactor * Math.PI * 2));

            Color moduleColor = new Color(red, green, blue, 255);

            float xPos = x + getWidth() - moduleWidth - paddingX;
            renderer.text(moduleName, xPos, y + paddingY + (i * 15), moduleColor, true);
        }

        setSize(renderer.textWidth("Test"), totalHeight);
    }
}
