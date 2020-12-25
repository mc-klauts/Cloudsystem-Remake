package io.dexiron.cloud.server.npc.npc;

import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NPCManager {
    private File file = new File("plugins/ServerNPC/npcs.json");
    private Map<Integer, NPC> npcs = new HashMap<>();

    public NPCManager() {

        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.loadNPCS();
    }

    public void loadNPCS() {

        JSONArray array = getNPCSFromFile();
        if (array != null) {
            for (Object object : array) {

                JSONObject json = (JSONObject) object;
                Location loc = locationFromJson((JSONObject) json.get("location"));
                NPC npc = new NPC(loc, (String) json.get("displayName"), (String) json.get("skin"));
                npc.setGroup((String) json.get("group"));
                npcs.put(npc.getId(), npc);
            }
        }
    }

    public void addNPC(Player player, String skin, String group, Location loc, String displayName) {
        NPC npc = new NPC(loc, displayName, skin);
        npc.setGroup(group);
        npcs.put(npc.getId(), npc);
        saveNPCS();
    }

    public NPC getNPC(String name) {
        return this.npcs.get(name);
    }

    public void removeNPC() {

    }

    public JSONArray getNPCSFromFile() {
        try {
            if (file.exists()) {
                String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
                if (fileContent != null) {
                    JSONObject object = (JSONObject) new JSONParser().parse(fileContent);
                    return (JSONArray) object.get("npcs");
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void saveNPCS() {
        try {
            System.out.println("try to save npc");
            JSONArray array = new JSONArray();
            for (NPC npc : npcs.values()) {
                JSONObject json = new JSONObject();
                json.put("location", locationToJson(npc.getLocation()));
                json.put("group", npc.getGroup());
                json.put("displayName", npc.getName());
                json.put("skin", npc.getSkin());

                array.add(json);
            }

            JSONObject object = new JSONObject();
            object.put("npcs", array);

            FileWriter fileWriter = new FileWriter(file, false);
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(object);
            fileWriter.write(json);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Save npc");
    }

    public JSONObject locationToJson(Location location) {
        JSONObject object = new JSONObject();
        object.put("world", location.getWorld().getName());
        object.put("x", location.getX());
        object.put("y", location.getY());
        object.put("z", location.getZ());
        object.put("yaw", location.getYaw());
        object.put("pitch", location.getPitch());

        return object;
    }

    public static Location locationFromJson(JSONObject jsonObject) {
        World world = Bukkit.getWorld((String) jsonObject.get("world"));
        if (world == null) {
            return null;
        }
        return new Location(world, (double) jsonObject.get("x"), (double) jsonObject.get("y"), (double) jsonObject.get("z"), (float) ((Double) jsonObject.get("yaw")).doubleValue(), (float) ((Double) jsonObject.get("pitch")).doubleValue());
    }

    public Map<Integer, NPC> getNpcs() {
        return npcs;
    }

}
