package com.hbm.machine.oilfracking;
import com.google.gson.*;
import java.nio.file.*;
import net.neoforged.fml.loading.FMLPaths;
/** All twelve actual source IConfigurableMachine keys; unrelated machine sections are preserved. */
public final class FrackingConfig {
    public record Values(int power,int consumption,int solution,int delay,int oil,int gasMin,int gasMax,double drain,int bedrockOil,int bedrockGasMin,int bedrockGasMax,int destructionRange){}
    private static Values values;
    public static synchronized Values get(){if(values==null)load();return values;}
    private static void load(){try{var path=FMLPaths.GAMEDIR.get().resolve("hbmConfig/hbmMachines.json");var root=Files.exists(path)?JsonParser.parseString(Files.readString(path)).getAsJsonObject():new JsonObject();var row=root.has("frackingtower")?root.getAsJsonObject("frackingtower"):new JsonObject();boolean changed=!root.has("frackingtower");String[] keys={"I:powerCap","I:consumption","I:solutionRequired","I:delay","I:oilPerDeposit","I:gasPerDepositMin","I:gasPerDepositMax","I:oilPerBedrockDeposit","I:gasPerBedrockDepositMin","I:gasPerBedrockDepositMax","I:destructionRange"};int[] defaults={5_000_000,5000,10,20,1000,100,500,100,10,50,75};for(int i=0;i<keys.length;i++)if(!row.has(keys[i])){row.addProperty(keys[i],defaults[i]);changed=true;}if(!row.has("D:drainChance")){row.addProperty("D:drainChance",.02);changed=true;}values=new Values(row.get(keys[0]).getAsInt(),row.get(keys[1]).getAsInt(),row.get(keys[2]).getAsInt(),row.get(keys[3]).getAsInt(),row.get(keys[4]).getAsInt(),row.get(keys[5]).getAsInt(),row.get(keys[6]).getAsInt(),row.get("D:drainChance").getAsDouble(),row.get(keys[7]).getAsInt(),row.get(keys[8]).getAsInt(),row.get(keys[9]).getAsInt(),row.get(keys[10]).getAsInt());root.add("frackingtower",row);if(changed){Files.createDirectories(path.getParent());Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}}catch(Exception error){throw new IllegalStateException("Cannot read actual fracking tower configuration",error);}}
    private FrackingConfig(){}
}
