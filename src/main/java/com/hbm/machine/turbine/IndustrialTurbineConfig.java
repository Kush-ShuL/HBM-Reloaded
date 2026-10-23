package com.hbm.machine.turbine;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import net.neoforged.fml.loading.FMLPaths;
/** Actual IConfigurableMachine section; FLYWHEEL_MAX_ENERGY was not a source config setting. */
public final class IndustrialTurbineConfig {
    public static int inputTankSize=750000,outputTankSize=3000000;
    public static double efficiency=1D;
    public static void load(){Path path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");try{Files.createDirectories(path.getParent());JsonObject root=new JsonObject();if(Files.isRegularFile(path))try(var reader=Files.newBufferedReader(path)){root=JsonParser.parseReader(reader).getAsJsonObject();}String section="steamturbineIndustrialMk2";var values=root.has(section)?root.getAsJsonObject(section):new JsonObject();if(values.has("I:inputTankSize"))inputTankSize=values.get("I:inputTankSize").getAsInt();if(values.has("I:outputTankSize"))outputTankSize=values.get("I:outputTankSize").getAsInt();if(values.has("D:efficiency"))efficiency=values.get("D:efficiency").getAsDouble();values.addProperty("INFO","industrial steam turbine consumes 20% of available steam per tick");values.addProperty("I:inputTankSize",inputTankSize);values.addProperty("I:outputTankSize",outputTankSize);values.addProperty("D:efficiency",efficiency);root.add(section,values);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}catch(Exception error){LogUtils.getLogger().error("Cannot load original industrial steam turbine settings",error);}}
    private IndustrialTurbineConfig(){}
}
