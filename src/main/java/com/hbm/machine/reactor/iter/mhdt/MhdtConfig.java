package com.hbm.machine.reactor.iter.mhdt;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import net.neoforged.fml.loading.FMLPaths;
/** Source IConfigurableMachine section mhd-turbine, exact mutable minimumPlasma threshold. */
public final class MhdtConfig {
 public static long minimumPlasma=5000000L;
 public static void load(){var path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");try{Files.createDirectories(path.getParent());JsonObject root=new JsonObject();if(Files.isRegularFile(path))try(var reader=Files.newBufferedReader(path)){root=JsonParser.parseReader(reader).getAsJsonObject();}var section=root.has("mhd-turbine")?root.getAsJsonObject("mhd-turbine"):new JsonObject();if(section.has("L:minimumPlasma"))minimumPlasma=section.get("L:minimumPlasma").getAsLong();section.addProperty("L:minimumPlasma",minimumPlasma);root.add("mhd-turbine",section);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}catch(Exception error){LogUtils.getLogger().error("Cannot load original mhd-turbine settings",error);}}
 private MhdtConfig(){}
}
