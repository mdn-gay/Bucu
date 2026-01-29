package com.x310.clarity.modules;

import com.x310.clarity.Main;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class BoatUAV extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Boolean> autoTrack = sgGeneral.add(new BoolSetting.Builder()
        .name("AutoTrack")
        .description("Automatically tracks the closest player's X and Z coordinates.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> charge = sgGeneral.add(new IntSetting.Builder()
        .name("charge")
        .description("How long to charge the bow before releasing in ticks.")
        .defaultValue(5)
        .range(5, 20)
        .sliderRange(5, 20)
        .build()
    );

    public final Setting<Double> maxYDelta = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-y-delta")
        .description("Maximum vertical movement per tick.")
        .defaultValue(5.0)
        .range(0.1, 100.0)
        .sliderRange(0.1, 100.0)
        .build()
    );

    public final Setting<Double> maxXZDelta = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-xz-delta")
        .description("Maximum horizontal movement per tick.")
        .defaultValue(0.5)
        .range(0.05, 15.0)
        .sliderRange(0.05, 15.0)
        .build()
    );

    public final Setting<Integer> bowSpamDelay = sgGeneral.add(new IntSetting.Builder()
        .name("bow-spam-delay")
        .description("Delay between bow releases in ticks.")
        .defaultValue(1)
        .range(1, 20)
        .sliderRange(1, 20)
        .build()
    );

    public final Setting<Integer> xzUpdateDelay = sgGeneral.add(new IntSetting.Builder()
        .name("xz-delay")
        .description("Number of ticks between XZ movement updates.")
        .defaultValue(2)
        .range(1, 20)
        .sliderRange(1, 20)
        .build()
    );


    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private enum State { MOVING_UP_Y_1, MOVING_UP_Y_2, MOVING_DOWN_Y }
    private State currentState = State.MOVING_UP_Y_1;
    private PlayerEntity targetPlayer = null;
    private int xzTickCounter = 0;
    private Vec3d lastSentPos = null;
    private double yTarget = 0.0;
    private boolean wasBow = false;
    private int bowTickCounter = 0;

    public BoatUAV() {
        super(Main.CATEGORY, "BoatUAV", "BOAT GO CRAZYYYYYY WOOHOOOO VELOCITY KILLA 3000");
    }

    @Override
    public void onActivate() {
        if (mc.player == null || mc.world == null || mc.player.networkHandler == null) {
            ChatUtils.error("p/w/n handler is null, deactivating.");
            toggle();
            return;
        }

        if (!(mc.player.getVehicle() instanceof BoatEntity)) {
            ChatUtils.error("You must be in a boat to activate.");
            toggle();
            return;
        }

        targetPlayer = findClosestPlayer();
        if (targetPlayer == null) {
            ChatUtils.error("No target player found, deactivating.");
            toggle();
            return;
        }

        currentState = State.MOVING_UP_Y_1;
        lastSentPos = mc.player.getPos();
        yTarget = targetPlayer.getY() + 50;
        wasBow = false;
        bowTickCounter = 0;
    }

    @Override
    public void onDeactivate() {
        targetPlayer = null;
        currentState = State.MOVING_UP_Y_1;
        lastSentPos = null;
        yTarget = 0.0;
        setPressed(false);
        wasBow = false;
        bowTickCounter = 0;
        ChatUtils.info("Module deactivated.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null || mc.world == null || mc.player.networkHandler == null) {
            ChatUtils.error("p/w/n handler is null, deactivating.");
            toggle();
            return;
        }

        if (!(mc.player.getVehicle() instanceof BoatEntity)) {
            ChatUtils.error("Not in a boat, deactivating.");
            toggle();
            return;
        }

        BoatEntity boat = (BoatEntity) mc.player.getVehicle();
        if (lastSentPos == null) {
            lastSentPos = boat.getPos();
        }

        if (targetPlayer == null || !targetPlayer.isAlive() || !mc.world.getPlayers().contains(targetPlayer)) {
            targetPlayer = findClosestPlayer();
            if (targetPlayer == null) {
                ChatUtils.error("No target player found, deactivating.");
                toggle();
                return;
            }

            if (currentState == State.MOVING_UP_Y_1) {
                yTarget = targetPlayer.getY() + 50;
            }
        }

        if (!autoTrack.get()) return;

        Vec3d targetPos = targetPlayer.getPos();
        double finalYTarget = currentState == State.MOVING_UP_Y_2 ? targetPos.y + 80 : targetPos.y + 10;

        handleMovement(boat, targetPos, finalYTarget);

        if (xzTickCounter++ >= xzUpdateDelay.get()) {
            updateXZ(boat, targetPos);
            xzTickCounter = 0;
        }

        if (currentState == State.MOVING_DOWN_Y) {
            if (!mc.player.getAbilities().creativeMode && !InvUtils.find(itemStack -> itemStack.getItem() instanceof ArrowItem).found()) {
                ChatUtils.error("No arrows found in inventory, stopping bow spam.");
                toggle();
                return;
            }

            boolean isBow = mc.player.getMainHandStack().getItem() == Items.BOW;
            if (!isBow && wasBow) setPressed(false);

            wasBow = isBow;
            if (!isBow) return;

            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                mc.player.getYaw(), 90.0f, mc.player.isOnGround(), false
            ));

            if (bowTickCounter > 0) {
                bowTickCounter--;
                return;
            }

            if (mc.player.getItemUseTime() >= charge.get()) {
                mc.interactionManager.stopUsingItem(mc.player);
                bowTickCounter = bowSpamDelay.get();
            } else {
                setPressed(true);
            }
        } else {
            setPressed(false);
        }
    }

    private void updateXZ(BoatEntity boat, Vec3d targetPos) {
        double xDelta = targetPos.x - lastSentPos.x;
        double zDelta = targetPos.z - lastSentPos.z;

        double moveX = Math.max(-maxXZDelta.get(), Math.min(maxXZDelta.get(), xDelta));
        double moveZ = Math.max(-maxXZDelta.get(), Math.min(maxXZDelta.get(), zDelta));

        Vec3d newXZPos = new Vec3d(lastSentPos.x + moveX, lastSentPos.y, lastSentPos.z + moveZ);
        sendMovePacket(boat, newXZPos);
    }


    private void handleMovement(BoatEntity boat, Vec3d targetPos, double targetY) {
        double currentY = lastSentPos.y;
        double yDelta = (currentState == State.MOVING_UP_Y_1 ? yTarget : targetY) - currentY;
        double moveY = Math.max(-maxYDelta.get(), Math.min(maxYDelta.get(), yDelta));

        Vec3d newPos = new Vec3d(lastSentPos.x, currentY + moveY, lastSentPos.z);

        if (Math.abs(yDelta) < maxYDelta.get()) {
            switch (currentState) {
                case MOVING_UP_Y_1 -> {
                    currentState = State.MOVING_UP_Y_2;
                    yTarget = targetPos.y + 80;
                }
                case MOVING_UP_Y_2 -> currentState = State.MOVING_DOWN_Y;
                case MOVING_DOWN_Y -> {
                    currentState = State.MOVING_UP_Y_1;
                    yTarget = targetPos.y + 40;
                }
            }
        }

        sendMovePacket(boat, newPos);
    }


    private void sendMovePacket(BoatEntity boat, Vec3d newPos) {
        mc.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(newPos, boat.getYaw(), boat.getPitch(), false));
        lastSentPos = newPos;
    }

    private PlayerEntity findClosestPlayer() {
        if (mc.world == null || mc.player == null) return null;
        PlayerEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player && !player.isSpectator() && player.isAlive() && Friends.get().shouldAttack(player)) {
                double distance = mc.player.getPos().distanceTo(player.getPos());
                if (distance < closestDistance) {
                    closest = player;
                    closestDistance = distance;
                }
            }
        }
        return closest;
    }

    private void setPressed(boolean pressed) {
        mc.options.useKey.setPressed(pressed);
    }
}
