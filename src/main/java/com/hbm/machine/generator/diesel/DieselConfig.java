package com.hbm.machine.generator.diesel;
import com.google.gson.*;
import com.hbm.fluid.FluidTraits.FuelGrade;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.EnumMap;
import net.neoforged.fml.loading.FMLPaths;
/** Exact dieselgen keys, including the original D[:efficiency array key and ordinal grade order. */
public final class DieselConfig {
    public static BigInteger maxPower=BigInteger.valueOf(50000);public static int fuelCapacity=16000;
    public static final EnumMap<FuelGrade,Double> efficiency=new EnumMap<>(FuelGrade.class);
    static{efficiency.put(FuelGrade.MEDIUM,.5D);efficiency.put(FuelGrade.HIGH,.75D);efficiency.put(FuelGrade.AERO,.1D);}
    public static void load(){Path path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");try{Files.createDirectories(path.getParent());JsonObject root=new JsonObject();if(Files.isRegularFile(path))try(var reader=Files.newBufferedReader(path)){root=JsonParser.parseReader(reader).getAsJsonObject();}var settings=root.has("dieselgen")?root.getAsJsonObject("dieselgen"):new JsonObject();if(settings.has("L:powerCap"))maxPower=settings.get("L:powerCap").getAsBigInteger();if(settings.has("I:fuelCap"))fuelCapacity=settings.get("I:fuelCap").getAsInt();if(settings.has("D[:efficiency")){var array=settings.getAsJsonArray("D[:efficiency");for(var grade:FuelGrade.values())efficiency.put(grade,array.get(grade.ordinal()).getAsDouble());}settings.addProperty("L:powerCap",maxPower);settings.addProperty("I:fuelCap",fuelCapacity);settings.addProperty("INFO","Fuel grades in order: LOW MEDIUM HIGH AERO GAS");var array=new JsonArray();for(var grade:FuelGrade.values())array.add(efficiency.getOrDefault(grade,0D));settings.add("D[:efficiency",array);root.add("dieselgen",settings);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}catch(Exception error){LogUtils.getLogger().error("Cannot load original diesel generator configuration",error);}}
    private DieselConfig(){}
}
