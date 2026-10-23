package com.hbm.machine.reactor.icf;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import net.neoforged.fml.loading.FMLPaths;

/** Original icfLaser section and unrestricted integer settings, preserving every other machine section. */
public final class IcfConfig {
    public static int capacitorPower=2500000,turboPower=5000000;
    public static void load() {
        var file=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");
        try {
            Files.createDirectories(file.getParent());
            var root=Files.isRegularFile(file)?JsonParser.parseString(Files.readString(file)).getAsJsonObject():new JsonObject();
            var values=root.has("icfLaser")?root.getAsJsonObject("icfLaser"):new JsonObject();
            if(values.has("I:capacitorPower"))capacitorPower=values.get("I:capacitorPower").getAsInt();
            if(values.has("I:turboPower"))turboPower=values.get("I:turboPower").getAsInt();
            values.addProperty("I:capacitorPower",capacitorPower);
            values.addProperty("I:turboPower",turboPower);
            root.add("icfLaser",values);
            Files.writeString(file,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original ICF laser settings",error);}
    }
    private IcfConfig(){}
}
