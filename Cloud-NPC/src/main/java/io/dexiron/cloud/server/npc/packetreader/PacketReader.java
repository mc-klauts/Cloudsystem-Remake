package io.dexiron.cloud.server.npc.packetreader;


import io.dexiron.cloud.server.npc.events.PlayerAttackNPCEvent;
import io.dexiron.cloud.server.npc.events.PlayerInteractNPCEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public class PacketReader {

   public static Channel channel;


    public static void inject(Player player) {
        CraftPlayer cPlayer = (CraftPlayer) player;
        channel = cPlayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) throws Exception {
                arg2.add(packet);
                readPacket(packet, player);
            }
        });
    }

    public static void uninject() {
        if (channel.pipeline().get("PacketInjector") != null) {
            channel.pipeline().remove("PacketInjector");
        }
    }


    public static void readPacket(Packet<?> packet, Player player) {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            int id = (Integer) getValue(packet, "a");

            System.out.println(getValue(packet, "action").toString());

            if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
                Bukkit.getPluginManager().callEvent(new PlayerAttackNPCEvent(player, id));
            } else if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) {
                Bukkit.getPluginManager().callEvent(new PlayerInteractNPCEvent(player, id));

            }


        }
    }


    public static void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
        }
    }

    public static Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
        }
        return null;
    }

}