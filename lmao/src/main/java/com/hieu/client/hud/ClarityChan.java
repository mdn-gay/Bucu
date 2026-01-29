package com.hieu.client.hud;

import com.hieu.client.Main;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Identifier;

public class ClarityChan extends HudElement {
    private final Identifier TEXTURE = Identifier.of("PrimeClient", "textures/PrimeClient-chan.png");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> size = sgGeneral.add(new IntSetting.Builder()
        .name("Size")
        .description("size in ohio 💀💀💀")
        .defaultValue(100)
        .min(1)
        .max(800)
        .sliderMax(800)
        .build()
    );

    public static final HudElementInfo<ClarityChan> INFO = new HudElementInfo<>(
        Main.HUD_GROUP, "PrimeClientChan", "Renders PrimeClient-chan", ClarityChan::new);

    public ClarityChan() {
        super(INFO);
    }


    @Override
    public void render(HudRenderer renderer) {
        setSize(renderer.textWidth("Test"), 60);
        renderer.texture(TEXTURE, x, y, size.get() * 1.5, size.get(), Color.WHITE);
    }
}
