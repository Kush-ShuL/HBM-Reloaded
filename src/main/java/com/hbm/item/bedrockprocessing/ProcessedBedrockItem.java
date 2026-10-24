package com.hbm.item.bedrockprocessing;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialStack;
import com.hbm.material.NtmMaterial;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** One immutable processing grade and ore type; raw vein composition remains on RawBedrockOreItem. */
public final class ProcessedBedrockItem extends Item {
    private final BedrockProcessingData.Grade grade;
    private final BedrockProcessingData.OreType oreType;
    public ProcessedBedrockItem(BedrockProcessingData.Grade grade,BedrockProcessingData.OreType oreType){super(new Properties());this.grade=grade;this.oreType=oreType;}
    public BedrockProcessingData.Grade grade(){return grade;}
    public BedrockProcessingData.OreType oreType(){return oreType;}
    public static ItemStack make(String grade,String type,int amount){return new ItemStack(BedrockProcessingContent.processed(grade,type),amount);}
    public static ItemStack make(String grade,String type){return make(grade,type,1);}
    public static ItemStack extract(BedrockProcessingData.Output output,double amount){return output.materialValue().make(AutogenContent.item("bedrock_ore_fragment"),Math.min((int)Math.ceil(output.amount()*amount),64));}
    public static MaterialStack toFluid(BedrockProcessingData.Output output,double amount){var material=output.materialValue();return material!=null&&material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE?new MaterialStack(material,(int)Math.ceil(HbmMaterials.shape("FRAGMENT").q(output.amount())*amount)):null;}
    @Override public String getDescriptionId(ItemStack stack){return "item.bedrock_ore";}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.bedrock_ore.grade."+grade.name().toLowerCase(Locale.US)+".name",Component.translatable("item.bedrock_ore.type."+oreType.suffix()+".name"));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(String trait:grade.traits())lines.add(Component.translatable("item.bedrock_ore.trait."+trait.toLowerCase(Locale.US)));}
    public int color(int pass){return pass==0?grade.tint():0xffffff;}
}
