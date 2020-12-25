package io.dexiron.cloud.lib.config.networking.registry;


import io.dexiron.cloud.lib.config.networking.Packet;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {
    private static final PacketRegistry packetRegistry = new PacketRegistry();

    public Packet getPacketById(final int id, final PacketDirection direction) throws IllegalAccessException, InstantiationException {
        return direction.getPackets().get(id).newInstance();
    }

    public int getIdByPacket(final Packet packet, final PacketDirection direction) {
        return direction.getPackets().entrySet().stream().filter(entry -> entry.getValue() == packet.getClass()).map(Map.Entry::getKey).findFirst().orElse(-1);
    }

    public enum PacketDirection {
        IN,
        OUT;
        private final HashMap<Integer, Class<? extends Packet>> packets = new HashMap<>();

        public void addPacket(final int id, final Class<? extends Packet> packet) {
            this.packets.put(id, packet);
        }

        public HashMap<Integer, Class<? extends Packet>> getPackets() {
            return packets;
        }
    }

    public static PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }
}
