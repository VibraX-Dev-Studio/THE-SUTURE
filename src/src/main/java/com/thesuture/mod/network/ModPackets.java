package com.thesuture.mod.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier JUMPSCARE_PACKET_ID = new Identifier("the_suture", "jumpscare_event");
    public static final Identifier BLOOD_EFFECT_PACKET_ID = new Identifier("the_suture", "blood_effect_event");

    public static void sendJumpscare(ServerPlayerEntity player, double x, double y, double z) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        ServerPlayNetworking.send(player, JUMPSCARE_PACKET_ID, buf);
    }

    public static void sendBloodProximity(ServerPlayerEntity player, float bloodIntensity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeFloat(bloodIntensity);
        ServerPlayNetworking.send(player, BLOOD_EFFECT_PACKET_ID, buf);
    }
}
