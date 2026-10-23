package com.hbm.machine.centrifuge;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;

/** Retains the original shared hbmMachines.json centrifuge keys without rewriting other machines. */
public final class CentrifugeConfig {
    public static int maxPower=100000,processingSpeed=200,baseConsumption=200;
    public static boolean enableLessBullshitMode=false,simpleCentrifuge=true;
    private CentrifugeConfig() {}
    public static boolean lbs() { return enableLessBullshitMode&&simpleCentrifuge; }
    public static void load() {
        Path file=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");
        try {
            Files.createDirectories(file.getParent()); JsonObject root=new JsonObject();
            if(Files.isRegularFile(file)) try(var reader=Files.newBufferedReader(file)) { root=JsonParser.parseReader(reader).getAsJsonObject(); }
            JsonObject machine=root.has("centrifuge")?root.getAsJsonObject("centrifuge"):new JsonObject();
            if(machine.has("I:powerCap")) maxPower=machine.get("I:powerCap").getAsInt();
            if(machine.has("I:timeToProcess")) processingSpeed=machine.get("I:timeToProcess").getAsInt();
            if(machine.has("I:consumption")) baseConsumption=machine.get("I:consumption").getAsInt();
            machine.addProperty("I:powerCap",maxPower); machine.addProperty("I:timeToProcess",processingSpeed); machine.addProperty("I:consumption",baseConsumption); root.add("centrifuge",machine);
            Files.writeString(file,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
            Path general=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmCentrifugeGeneral.json");
            if(Files.isRegularFile(general)) try(var reader=Files.newBufferedReader(general)) {
                var config=JsonParser.parseReader(reader).getAsJsonObject();
                if(config.has("enableLessBullshitMode")) enableLessBullshitMode=config.get("enableLessBullshitMode").getAsBoolean();
                if(config.has("LBSM_recipeSimpleCentrifuge")) simpleCentrifuge=config.get("LBSM_recipeSimpleCentrifuge").getAsBoolean();
            }
        } catch(Exception error) { LogUtils.getLogger().error("Cannot load original centrifuge machine parameters",error); }
    }
}
