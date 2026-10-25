package com.hbm.client.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.HbmReloaded;
import java.nio.file.Files;
import net.neoforged.fml.loading.FMLPaths;

/** Client HUD settings in the current nested schema. */
public final class LegacyClientConfig {
    private static int geigerHorizontal, geigerVertical;
    private static boolean loaded;
    private LegacyClientConfig() {}

    public static int geigerHorizontal() { if (!loaded) reload(); return geigerHorizontal; }
    public static int geigerVertical() { if (!loaded) reload(); return geigerVertical; }

    public static synchronized void reload() {
        var file = FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmClient.json");
        JsonObject input = new JsonObject();
        try {
            if (Files.isRegularFile(file)) input = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            var hud = object(input, "hud");
            var geiger = object(hud, "geiger");
            geigerHorizontal = integer(geiger, "horizontalOffset");
            geigerVertical = integer(geiger, "verticalOffset");
            // Write this config's current schema only; obsolete fields are not imported or retained.
            save(geigerHorizontal, geigerVertical);
        } catch (Exception error) {
            HbmReloaded.LOGGER.error("Cannot read original Geiger HUD client settings", error);
            geigerHorizontal = geigerVertical = 0;
        }
        loaded = true;
    }

    public static synchronized void setGeigerOffset(boolean horizontal, int value) throws java.io.IOException {
        if (!loaded) reload();
        int x = horizontal ? value : geigerHorizontal, y = horizontal ? geigerVertical : value;
        save(x, y);
        geigerHorizontal = x;
        geigerVertical = y;
    }

    private static void save(int horizontal, int vertical) throws java.io.IOException {
        var file = FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmClient.json");
        var values = new JsonObject();
        var hud = new JsonObject();
        var geiger = new JsonObject();
        geiger.addProperty("horizontalOffset", horizontal);
        geiger.addProperty("verticalOffset", vertical);
        hud.add("geiger", geiger);
        values.add("hud", hud);
        Files.createDirectories(file.getParent());
        Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(values) + "\n");
    }

    private static JsonObject object(JsonObject values,String name) {
        return values.has(name) && values.get(name).isJsonObject() ? values.getAsJsonObject(name) : new JsonObject();
    }

    private static int integer(JsonObject values, String name) {
        if (!values.has(name)) return 0;
        try { return values.get(name).getAsInt(); }
        catch (RuntimeException error) { HbmReloaded.LOGGER.warn("Invalid client setting {}; using original zero offset", name); return 0; }
    }
}
