package io.dexiron.cloud.wrapper.config;

import com.google.gson.GsonBuilder;
import io.dexiron.cloud.lib.config.CloudWrapperConfig;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CloudWrapperConfigAdapter {


    private final File file = new File("config/wrapper.json");

    public CloudWrapperConfigAdapter() {
        if (!file.exists()) {
            try {
                file.createNewFile();
                saveWrapperConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveWrapperConfig() {
        JSONObject json = new JSONObject();
        json.put("name", "Wrapper-1");
        json.put("host", "127.0.0.1");
        json.put("port", 76713);
        json.put("ram", 1024);
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            String json1 = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            fileWriter.write(json1);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CloudWrapperConfig getCloudWrapperConfig() {
        if (file.exists()) {
            try {
                String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
                if (fileContent != null) {
                    JSONObject object = (JSONObject) new JSONParser().parse(fileContent);
                    return new CloudWrapperConfig((String) object.get("name"), (String) object.get("host"), (Long) object.get("port"), (Long) object.get("ram"));
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
