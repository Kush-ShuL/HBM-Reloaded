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
            JsonObject values = new JsonObject();
            var writtenHud = new JsonObject();
            var writtenGeiger = new JsonObject();
            writtenGeiger.addProperty("horizontalOffset", geigerHorizontal);
            writtenGeiger.addProperty("verticalOffset", geigerVertical);
            writtenHud.add("geiger", writtenGeiger);
            values.add("hud", writtenHud);
            Files.createDirectories(file.getParent());
            Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(values) + "\n");
        } catch (Exception error) {
            HbmReloaded.LOGGER.error("Cannot read original Geiger HUD client settings", error);
            geigerHorizontal = geigerVertical = 0;
        }
        loaded = true;
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
