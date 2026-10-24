package com.hbm.item.bedrockprocessing;

import com.google.gson.Gson;
import com.hbm.material.HbmMaterials;
import com.hbm.material.NtmMaterial;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Original source enum order, colors, material quantities and processing traits. */
public final class BedrockProcessingData {
    public static final Definitions DATA;
    static {
        try (var stream=BedrockProcessingData.class.getResourceAsStream("/hbm/bedrock-processing-data.json")) {
            if(stream==null)throw new IllegalStateException("Missing real bedrock processing definitions");
            DATA=new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Definitions.class);
        } catch(java.io.IOException error){throw new ExceptionInInitializerError(error);}
    }
    public record Definitions(List<OldOre> oldOres,List<Byproduct> byproducts,List<OreType> types,List<Grade> grades){}
    public record OldOre(String name,String oreName,int color,List<String> byproducts){}
    public record Byproduct(String name,int color){}
    public record Output(String material,int amount){public NtmMaterial materialValue(){return material==null?null:HbmMaterials.bySymbol(material);}}
    public record OreType(String name,int light,int dark,String suffix,List<Output> outputs){}
    public record Grade(String name,int tint,String prefix,List<String> traits){}
    private BedrockProcessingData(){}
}
