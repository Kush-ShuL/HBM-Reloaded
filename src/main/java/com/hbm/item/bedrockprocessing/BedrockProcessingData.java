package com.hbm.item.bedrockprocessing;

import com.google.gson.Gson;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.HbmMaterials;
import com.hbm.material.NtmMaterial;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.world.item.ItemStack;

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
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static int sourceIndex(int metadata,int length){return Math.abs(metadata%length);}
    public static Grade grade(int metadata){return DATA.grades().get(sourceIndex(metadata>>4,DATA.grades().size()));}
    public static OreType type(int metadata){return DATA.types().get(sourceIndex(metadata&15,DATA.types().size()));}
    public static int canonical(int metadata){return DATA.grades().indexOf(grade(metadata))<<4|DATA.types().indexOf(type(metadata));}
    public static int encode(String grade,String type){return index(DATA.grades().stream().map(Grade::name).toList(),grade)<<4|index(DATA.types().stream().map(OreType::name).toList(),type);}
    private static int index(List<String> names,String value){int index=names.indexOf(value);if(index<0)throw new IllegalArgumentException("Unknown source bedrock enum "+value);return index;}
    private BedrockProcessingData(){}
}
