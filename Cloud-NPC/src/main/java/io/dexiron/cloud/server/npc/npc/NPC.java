package io.dexiron.cloud.server.npc.npc;


import com.mojang.authlib.GameProfile;
import io.dexiron.cloud.server.npc.NPCPlugin;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.apache.commons.io.Charsets;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NPC extends Reflections {


    private UUID uuid;
    private int id;
    private GameProfile profile;
    private Location location;
    private String name, skin;
    private String group;

    public NPC(Location location, String name, String skin) {
        this.id = (int) Math.ceil(Math.random() * 1000) + 2000;
        this.uuid = UUID.nameUUIDFromBytes(("FakePlayer-" + id).getBytes(Charsets.UTF_8));
        this.location = location;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.skin = skin;
    }

    public void spawn(Player player) {
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn();

        setValue(spawn, "a", this.getId());
        setValue(spawn, "b", this.uuid);
        setValue(spawn, "c", (int) Math.floor(location.getX() * 32.0D));
        setValue(spawn, "d", (int) Math.floor(location.getY() * 32.0D));
        setValue(spawn, "e", (int) Math.floor(location.getZ() * 32.0D));
        setValue(spawn, "f", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        setValue(spawn, "g", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        setValue(spawn, "h", 0);

        DataWatcher w = new DataWatcher(null);
        w.a(10, (byte) 127);
        w.a(6, (float) 20);
        setValue(spawn, "i", w);

        GameProfile f = ((CraftPlayer)player).getProfile();
        this.profile = new GameProfile(uuid, ChatColor.translateAlternateColorCodes('&', name));
        this.profile.getProperties().removeAll("textures");
        this.profile.getProperties().putAll("textures", f.getProperties().get("textures"));

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(profile, 0, EnumGamemode.SURVIVAL, CraftChatMessage.fromString(ChatColor.translateAlternateColorCodes('&', name))[0]);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        players.add(data);

        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        setValue(packet, "b", players);

        sendPacket(packet, player);

        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
                setValue(packet, "b", Arrays.asList(packet.new PlayerInfoData(profile, 0, null, null)));
                sendPacket(packet, player);
            }
        }.runTaskLater(NPCPlugin.getInstance(), 20);
        sendPacket(spawn, player);
        sendLookPacket(player, location.getYaw());

    }

    public void sendLookPacket(Player player, double yaw) {
        PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        setValue(packet, "a", this.id);
        setValue(packet, "b", (byte) (int) (yaw * 256.0F / 360.0F));

        sendPacket(packet, player);
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getSkin() {
        return skin;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public GameProfile getProfile() {
        return profile;
    }



    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}