package net.cmd.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CMDBlacklist {
    private final List<String> blocked = new ArrayList<>();

    public CMDBlacklist() { load(); }

    private void load() {
        try {
            if (!CMDConfigPaths.getBlacklistFile().exists()) {
                blocked.add("-pre-cim");
                blocked.add("-pre-cmd");
                saveDefaults();
                return;
            }
            try (FileReader reader = new FileReader(CMDConfigPaths.getBlacklistFile())) {
                JsonObject root = new Gson().fromJson(reader, JsonObject.class);
                if (root != null && root.has("blocked")) {
                    JsonArray array = root.getAsJsonArray("blocked");
                    for (int i = 0; i < array.size(); i++) blocked.add(array.get(i).getAsString());
                }
            }
            if (blocked.isEmpty()) {
                blocked.add("-pre-cim");
                blocked.add("-pre-cmd");
            }
        } catch (Exception e) {
            blocked.clear();
            blocked.add("-pre-cim");
            blocked.add("-pre-cmd");
        }
    }

    private void saveDefaults() {
        try {
            JsonObject root = new JsonObject();
            JsonArray array = new JsonArray();
            array.add("-pre-cim");
            array.add("-pre-cmd");
            root.add("blocked", array);
            try (FileWriter writer = new FileWriter(CMDConfigPaths.getBlacklistFile())) {
                new Gson().toJson(root, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean blocks(String name) {
        for (String entry : blocked) if (name.contains(entry)) return true;
        return false;
    }
}
