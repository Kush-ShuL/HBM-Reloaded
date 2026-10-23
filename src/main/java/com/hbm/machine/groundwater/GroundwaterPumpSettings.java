package com.hbm.machine.groundwater;

import com.google.gson.*;
import java.nio.file.Files;
import net.neoforged.fml.loading.FMLPaths;

/** Original IConfigurableMachine waterpump section, not an independent recipe or fluid system. */
public final class GroundwaterPumpSettings {
    public record Values(int groundHeight,int groundDepth,int steamSpeed,int electricSpeed){}
    private static volatile Values values=new Values(70,4,1000,10000);
    public static Values values(){return values;}
    /** Called on the existing common-setup main-thread queue, before real machine construction. */
    public static void load(){
        var path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmMachines.json");
        try{
            var root=Files.isRegularFile(path)?JsonParser.parseString(Files.readString(path)).getAsJsonObject():new JsonObject();
            var row=root.has("waterpump")?root.getAsJsonObject("waterpump"):new JsonObject();
            boolean changed=!root.has("waterpump");
            var names=new String[]{"I:groundHeight","I:groundDepth","I:steamSpeed","I:electricSpeed"};
            var defaults=new int[]{70,4,1000,10000};var actual=new int[4];
            for(int i=0;i<names.length;i++){if(!row.has(names[i])){row.addProperty(names[i],defaults[i]);changed=true;}actual[i]=row.get(names[i]).getAsInt();}
            values=new Values(actual[0],actual[1],actual[2],actual[3]);
            if(changed){root.add("waterpump",row);Files.createDirectories(path.getParent());Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
        }catch(Exception error){throw new IllegalStateException("Cannot read original waterpump settings from "+path,error);}
    }
    private GroundwaterPumpSettings(){}
}
