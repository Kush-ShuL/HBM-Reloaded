package com.hbm.item.tool;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.fluid.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

/** Source gasoline/acetylene tools, fuel initialization, fifty-mB fill limit and exact per-use costs. */
public final class BlowtorchItem extends Item implements FillableItem {
    public final boolean acetylene;
    public BlowtorchItem(boolean acetylene){super(new Item.Properties().stacksTo(1));this.acetylene=acetylene;}
    @Override public boolean acceptsFluid(HbmFluidType type,ItemStack stack){return acetylene?type==HbmFluids.UNSATURATEDS||type==HbmFluids.OXYGEN:type==HbmFluids.GAS;}
    @Override public int tryFill(HbmFluidType type,int amount,ItemStack stack){if(!acceptsFluid(type,stack))return amount;int fill=Math.min(Math.min(amount,50),getMaxFill(type)-getFill(stack,type));setFill(stack,type,getFill(stack,type)+fill);return amount-fill;}
    public int getFill(ItemStack stack,HbmFluidType type){if(!stack.has(DataComponents.CUSTOM_DATA))initNBT(stack);var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();String name=HbmFluids.toNameCompat(type);if(tag.contains(name)){int fill=tag.getInt(name);tag.remove(name);tag.putInt(Integer.toString(type.getID()),fill);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return fill;}return tag.getInt(Integer.toString(type.getID()));}
    public int getMaxFill(HbmFluidType type){return type==HbmFluids.GAS?4000:type==HbmFluids.UNSATURATEDS?8000:type==HbmFluids.OXYGEN?16000:0;}
    public void setFill(ItemStack stack,HbmFluidType type,int fill){if(!stack.has(DataComponents.CUSTOM_DATA))initNBT(stack);var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();tag.putInt(Integer.toString(type.getID()),fill);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    public void initNBT(ItemStack stack){stack.set(DataComponents.CUSTOM_DATA,CustomData.of(new CompoundTag()));if(acetylene){setFill(stack,HbmFluids.UNSATURATEDS,getMaxFill(HbmFluids.UNSATURATEDS));setFill(stack,HbmFluids.OXYGEN,getMaxFill(HbmFluids.OXYGEN));}else setFill(stack,HbmFluids.GAS,getMaxFill(HbmFluids.GAS));}
    public static ItemStack getEmptyTool(Item item){var tool=(BlowtorchItem)item;var stack=new ItemStack(item);if(tool.acetylene){tool.setFill(stack,HbmFluids.UNSATURATEDS,0);tool.setFill(stack,HbmFluids.OXYGEN,0);}else tool.setFill(stack,HbmFluids.GAS,0);return stack;}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();if(player==null||!(level.getBlockState(pos).getBlock() instanceof ToolableBlock block))return InteractionResult.PASS;var stack=context.getItemInHand();
        if(acetylene?(getFill(stack,HbmFluids.UNSATURATEDS)<20||getFill(stack,HbmFluids.OXYGEN)<10):getFill(stack,HbmFluids.GAS)<250)return InteractionResult.PASS;
        var point=context.getClickLocation().subtract(pos.getX(),pos.getY(),pos.getZ());if(!block.onScrew(level,player,pos,context.getClickedFace(),(float)point.x,(float)point.y,(float)point.z,ToolType.TORCH))return InteractionResult.PASS;
        if(!level.isClientSide){if(acetylene){setFill(stack,HbmFluids.UNSATURATEDS,getFill(stack,HbmFluids.UNSATURATEDS)-20);setFill(stack,HbmFluids.OXYGEN,getFill(stack,HbmFluids.OXYGEN)-10);}else setFill(stack,HbmFluids.GAS,getFill(stack,HbmFluids.GAS)-250);player.inventoryMenu.broadcastChanges();WeldingParticlePacket.send(level,context.getClickLocation(),10,false);}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    public double fuelFraction(ItemStack stack){return acetylene?Math.min((double)getFill(stack,HbmFluids.UNSATURATEDS)/getMaxFill(HbmFluids.UNSATURATEDS),(double)getFill(stack,HbmFluids.OXYGEN)/getMaxFill(HbmFluids.OXYGEN)):(double)getFill(stack,HbmFluids.GAS)/getMaxFill(HbmFluids.GAS);}
    @Override public boolean isBarVisible(ItemStack stack){return 1-fuelFraction(stack)>0;}
    @Override public int getBarWidth(ItemStack stack){return (int)Math.round(13*fuelFraction(stack));}
    @Override public int getBarColor(ItemStack stack){return Mth.hsvToRgb((float)fuelFraction(stack)/3,1,1);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){if(acetylene){tooltip.add(gauge(stack,HbmFluids.UNSATURATEDS).withStyle(ChatFormatting.YELLOW));tooltip.add(gauge(stack,HbmFluids.OXYGEN).withStyle(ChatFormatting.AQUA));}else tooltip.add(gauge(stack,HbmFluids.GAS).withStyle(ChatFormatting.YELLOW));}
    private net.minecraft.network.chat.MutableComponent gauge(ItemStack stack,HbmFluidType type){return Component.translatable("tooltip.hbm.blowtorch.fuel",type.displayName(),String.format(Locale.US,"%,d",getFill(stack,type)),String.format(Locale.US,"%,d",getMaxFill(type)));}
    @Override public boolean providesFluid(HbmFluidType type,ItemStack stack){return false;}
    @Override public int tryEmpty(HbmFluidType type,int amount,ItemStack stack){return amount;}
    @Override public HbmFluidType getFirstFluidType(ItemStack stack){return null;}
    @Override public int getFill(ItemStack stack){return 0;}
}
