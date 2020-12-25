package io.dexiron.cloud.server.npc.filter;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FilterManager {
    private ConcurrentHashMap<Player, List<Filter>> filters = new ConcurrentHashMap<>();

    public void addFilter(Player player, String group) {
        if(!filters.containsKey(player)) {
            filters.put(player, new ArrayList<>());
        }
        Filter filter = new Filter(player, group);
        List<Filter> fill = filters.get(player);
        fill.add(filter);
    }

    public ConcurrentHashMap<Player, List<Filter>> getFilters() {
        return filters;
    }


}
