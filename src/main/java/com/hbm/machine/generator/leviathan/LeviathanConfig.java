package com.hbm.machine.generator.leviathan;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import net.neoforged.fml.loading.FMLPaths;
/** Actual original steamturbineLeviathan section, preserving independent1e9 tank sizes and.85 efficiency. */
public final class LeviathanConfig {
    public static int inputTankSize=1000000000,outputTankSize=1000000000;
    public static double efficiency=.85D;
    public static void load(){Path path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");try{Files.createDirectories(path.getParent());JsonObject root=new JsonObject();if(Files.isRegularFile(path))try(var reader=Files.newBufferedReader(path)){root=JsonParser.parseReader(reader).getAsJsonObject();}String section="steamturbineLeviathan";var values=root.has(section)?root.getAsJsonObject(section):new JsonObject();if(values.has("I:inputTankSize"))inputTankSize=values.get("I:inputTankSize").getAsInt();if(values.has("I:outputTankSize"))outputTankSize=values.get("I:outputTankSize").getAsInt();if(values.has("D:efficiency"))efficiency=values.get("D:efficiency").getAsDouble();values.addProperty("INFO","leviathan steam turbine consumes all availible steam per tick");values.addProperty("I:inputTankSize",inputTankSize);values.addProperty("I:outputTankSize",outputTankSize);values.addProperty("D:efficiency",efficiency);root.add(section,values);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}catch(Exception error){LogUtils.getLogger().error("Cannot load original industrial steam turbine settings",error);}}
    private LeviathanConfig(){}
}
