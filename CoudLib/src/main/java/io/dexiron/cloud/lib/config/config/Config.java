package io.dexiron.cloud.lib.config.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.File;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;

public class Config {

    private JSONObject json;
    private LoadingCache<String, Object> cache;
    private File file;

    public Config(String path) {
        initCache();
        try {
            json = (JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        } catch (Exception ex) {
        }

        this.file = new File(path);
    }

    //<editor-fold defaultstate="collapsed" desc="initCache">
    private void initCache() {
        cache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(10, TimeUnit.MINUTES).build(new CacheLoader<String, Object>() {
            @Override
            public Object load(String key) throws Exception {
                String[] p = key.split("\\.");
                Object out = json;

                for (String p1 : p) {
                    out = ((Map) out).get(p1);
                }
                return out;
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get">
    public Object get(String path) {
        try {
            return cache.get(path);
        } catch (ExecutionException ex) {
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getString">
    public String getString(String path) {
        return (String) get(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getLong">
    public long getLong(String path) {
        return (long) get(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getInt">
    public int getInt(String path) {
        return (int) getLong(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getDouble">
    public double getDouble(String path) {
        return (double) get(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getFloat">
    public float getFloat(String path) {
        return (float) getDouble(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getBoolean">
    public boolean getBoolean(String path) {
        return (boolean) get(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getMap">
    public Map getMap(String path) {
        return (Map) get(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getList">
    public List getList(String path) {
        return (List) get(path);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="set">
    public void set(String path, Object... value) {
        String[] p = path.split("\\.");
        Map m = json;

        for(int i = 0; i < p.length - 1; i++) {
            Object o = m.get(p[i]);

            if(o == null) {
                m.put(p[i], new JSONObject());
                m = (Map)m.get(p[i]);
            } else if(o instanceof Map) {
                m = (Map) o;
            } else {
                return;
            }
        }

        if(value.length == 1) {
            m.put(p[p.length - 1], value[0]);
            return;
        }

        JSONArray a = new JSONArray();
        a.addAll(Arrays.asList(value));
        m.put(p[p.length - 1], a);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="save">
    public void save() {
        try {
            PrintWriter writer = new PrintWriter(this.file);
            writer.print(json.toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//</editor-fold>
}
