package com.hbm.machine.converter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import net.neoforged.fml.loading.FMLPaths;

/** Original hbmMachines.json converter sections, with the user's mandatory lossy-cycle constraint. */
public final class ConverterConfig {
    public record Settings(long heInput,long rfOutput,double heInputDecay,long rfInput,long heOutput,double rfInputDecay) {
        public Settings {
            if(heInput<=0||rfOutput<=0||rfInput<=0||heOutput<=0)throw new IllegalArgumentException("Converter input/output ratios must be positive");
            if(!Double.isFinite(heInputDecay)||heInputDecay<0||heInputDecay>1||!Double.isFinite(rfInputDecay)||rfInputDecay<0||rfInputDecay>1)throw new IllegalArgumentException("Converter inputDecay2 must be within0..1");
            if(BigInteger.valueOf(rfOutput).multiply(BigInteger.valueOf(heOutput)).compareTo(BigInteger.valueOf(heInput).multiply(BigInteger.valueOf(rfInput)))>=0)throw new IllegalArgumentException("HE/RF converters must lose energy over a round trip");
        }
    }
    public static final Settings SOURCE_DEFAULTS=new Settings(5,1,0,2,5,0);
    private static volatile Settings settings=SOURCE_DEFAULTS;
    private ConverterConfig(){}
    public static Settings settings(){return settings;}
    public static void configure(Settings values){settings=java.util.Objects.requireNonNull(values);}
    public static void load(){
        var path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");
        try{
            Files.createDirectories(path.getParent());JsonObject root=new JsonObject();
            if(Files.isRegularFile(path))try(var reader=Files.newBufferedReader(path,StandardCharsets.UTF_8)){root=JsonParser.parseReader(reader).getAsJsonObject();}
            JsonObject he=root.has("HEToRFConverter")?root.getAsJsonObject("HEToRFConverter"):new JsonObject(),rf=root.has("RFToHEConverter")?root.getAsJsonObject("RFToHEConverter"):new JsonObject();
            Settings values=new Settings(longValue(he,"L:HE_Used",5),longValue(he,"L:RF_Created",1),doubleValue(he,"D:inputDecay2",0),longValue(rf,"L:RF_Used2",2),longValue(rf,"L:HE_Created2",5),doubleValue(rf,"D:inputDecay2",0));
            configure(values);
            he.addProperty("L:HE_Used",values.heInput);he.addProperty("L:RF_Created",values.rfOutput);he.addProperty("D:inputDecay2",values.heInputDecay);
            rf.addProperty("L:RF_Used2",values.rfInput);rf.addProperty("L:HE_Created2",values.heOutput);rf.addProperty("D:inputDecay2",values.rfInputDecay);
            root.add("HEToRFConverter",he);root.add("RFToHEConverter",rf);
            Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n",StandardCharsets.UTF_8);
        }catch(Exception error){settings=SOURCE_DEFAULTS;LogUtils.getLogger().error("Cannot load lossy original HE/RF converter configuration; keeping source defaults5HE:1RF /2RF:5HE",error);}
    }
    private static long longValue(JsonObject object,String name,long fallback){return object.has(name)?object.get(name).getAsLong():fallback;}
    private static double doubleValue(JsonObject object,String name,double fallback){return object.has(name)?object.get(name).getAsDouble():fallback;}
}
