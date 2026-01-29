package com.x310.clarity.hud;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class Logo extends HudElement {
    private final Identifier TEXTURE = Identifier.of("clarity", "textures/icon.png");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> size = sgGeneral.add(new IntSetting.Builder()
        .name("Size")
        .description("size in ohio 💀💀💀")
        .defaultValue(100)
        .min(1)
        .max(256)
        .sliderMax(256)
        .build()
    );

    public static final HudElementInfo<Logo> INFO = new HudElementInfo<>(
        Main.HUD_GROUP, "Logo", "Renders the Clarity logo", Logo::new);

    public Logo() {
        super(INFO);
    }


    @Override
    public void render(HudRenderer renderer) {
        setSize(renderer.textWidth("Test"), 60);
        renderer.texture(TEXTURE, x, y, size.get(), size.get(), Color.WHITE);
        renderer.text("clarity.gay", x + ((double) size.get() / 2) - (renderer.textWidth("clarity.gay") / 2), y + size.get(), Color.WHITE, true);
    }
}
