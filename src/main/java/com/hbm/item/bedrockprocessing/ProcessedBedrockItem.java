package com.hbm.item.bedrockprocessing;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialStack;
import com.hbm.material.NtmMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Real ItemBedrockOreNew: grade in high bits, six ore types in low nibble. */
public final class ProcessedBedrockItem extends Item {
    public ProcessedBedrockItem(){super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static ItemStack make(String grade,String type,int amount){var stack=new ItemStack(BedrockProcessingContent.ORE.get(),amount);stack.set(HbmHazards.LEGACY_METADATA.get(),BedrockProcessingData.encode(grade,type));return stack;}
    public static ItemStack make(String grade,String type){return make(grade,type,1);}
    public static ItemStack extract(BedrockProcessingData.Output output,double amount){return output.materialValue().make(AutogenContent.item("bedrock_ore_fragment"),Math.min((int)Math.ceil(output.amount()*amount),64));}
    public static MaterialStack toFluid(BedrockProcessingData.Output output,double amount){var material=output.materialValue();return material!=null&&material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE?new MaterialStack(material,(int)Math.ceil(HbmMaterials.shape("FRAGMENT").q(output.amount())*amount)):null;}
    @Override public String getDescriptionId(ItemStack stack){return "item.bedrock_ore";}
    @Override public Component getName(ItemStack stack){int meta=BedrockProcessingData.metadata(stack);return Component.translatable("item.bedrock_ore.grade."+BedrockProcessingData.grade(meta).name().toLowerCase(Locale.US)+".name",Component.translatable("item.bedrock_ore.type."+BedrockProcessingData.type(meta).suffix()+".name"));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(String trait:BedrockProcessingData.grade(BedrockProcessingData.metadata(stack)).traits())lines.add(Component.translatable("item.bedrock_ore.trait."+trait.toLowerCase(Locale.US)));}
    public int color(ItemStack stack,int pass){return pass==0?BedrockProcessingData.grade(BedrockProcessingData.metadata(stack)).tint():0xffffff;}
    public List<ItemStack> variants(){var result=new ArrayList<ItemStack>();for(var type:BedrockProcessingData.DATA.types())for(var grade:BedrockProcessingData.DATA.grades())result.add(make(grade.name(),type.name()));return List.copyOf(result);}
}
