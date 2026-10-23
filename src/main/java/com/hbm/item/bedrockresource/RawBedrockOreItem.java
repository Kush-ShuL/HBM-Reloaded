package com.hbm.item.bedrockresource;

import com.hbm.worldgen.bedrockresource.BedrockDensity;
import com.hbm.worldgen.bedrockresource.BedrockOreType;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

/** Real ItemBedrockOreBase: six source NBT doubles, coordinate sampling and original tooltip. */
public final class RawBedrockOreItem extends Item {
    public static Supplier<BlockPos> CREATIVE_POSITION=()->null;
    public RawBedrockOreItem(){super(new Properties());}
    public static double getOreAmount(ItemStack stack,BedrockOreType type){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getDouble(type.suffix);}
    public static void setOreAmount(ItemStack stack,int x,int z,double multiplier){CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->{for(var type:BedrockOreType.values())tag.putDouble(type.suffix,BedrockDensity.getOreLevel(x,z,type)*multiplier);});}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        for(var type:BedrockOreType.values()){
            double amount=getOreAmount(stack,type);
            lines.add(Component.translatable("item.bedrock_ore.type."+type.suffix+".name").append(": "+BedrockDensity.displayed(amount)+" (").append(Component.translatable(BedrockDensity.translateDensity(amount)).withStyle(BedrockDensity.getColor(amount))).append(Component.literal(")").withStyle(ChatFormatting.GRAY)));
        }
    }
    public ItemStack creativeStack(){var stack=new ItemStack(this);BlockPos pos=CREATIVE_POSITION.get();if(pos!=null)setOreAmount(stack,pos.getX(),pos.getZ(),1D);return stack;}
}
