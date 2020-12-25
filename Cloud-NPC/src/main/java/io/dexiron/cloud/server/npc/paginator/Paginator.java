package io.dexiron.cloud.server.npc.paginator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Paginator<ServerInfo> extends ArrayList<ServerInfo> {
    private int pageSize;

    public Paginator(int pageSize) {
        this(pageSize, new ArrayList<ServerInfo>());
    }

    @SafeVarargs
    public Paginator(int pageSize, ServerInfo... objects) {
        this(pageSize, Arrays.asList(objects));
    }

    public Paginator(int pageSize, List<ServerInfo> objects) {
        this.pageSize = pageSize;
        addAll(objects);
    }

    public int pageSize() {
        return pageSize;
    }

    public int totalPages() {
        return (int) Math.ceil((double) size() / pageSize);
    }

    public boolean exists(int page) {
        return !(page < 0) && page < totalPages();
    }

    public List<ServerInfo> getPage(int page) {
        if (page < 0 || page >= totalPages()) {
            throw new IndexOutOfBoundsException("Index: " + page + ", Size: " + totalPages());
        }

        List<ServerInfo> objects = new ArrayList<>();

        int min = page * pageSize;
        int max = ((page * pageSize) + pageSize);

        if (max > size()) {
            max = size();
        }

        for (int i = min; max > i; i++) {
            objects.add(get(i));
        }

        return objects;
    }
}
