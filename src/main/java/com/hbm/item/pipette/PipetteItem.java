package com.hbm.item.pipette;

import com.hbm.fluid.FillableItem;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** Source ItemPipette, using the one existing native-fluid item-loading contract. */
public final class PipetteItem extends Item implements FillableItem {
    public enum Kind {
        GLASS("pipette",1000,50), BORON("pipette_boron",1000,50), LABORATORY("pipette_laboratory",50,1);
        public final String sourceName;
        public final int maximum,step;
        Kind(String sourceName,int maximum,int step){this.sourceName=sourceName;this.maximum=maximum;this.step=step;}
    }
    private final Kind kind;
    public PipetteItem(Kind kind){super(new Properties().durability(1));this.kind=kind;}
    public Kind kind(){return kind;}
    public short getMaxFill(){return (short)kind.maximum;}
    private CompoundTag data(ItemStack stack){
        if(!stack.has(DataComponents.CUSTOM_DATA))initNBT(stack);
        return stack.get(DataComponents.CUSTOM_DATA).copyTag();
    }
    /** Exact source short fields, including replacing the empty legacy tag on first access. */
    public void initNBT(ItemStack stack){
        var tag=new CompoundTag();
        tag.putShort("type",(short)HbmFluids.NONE.getID());tag.putShort("fill",(short)0);
        tag.putShort("capacity",getMaxFill());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
    }
    public HbmFluidType getType(ItemStack stack){return HbmFluids.fromID(data(stack).getShort("type"));}
    public short getCapacity(ItemStack stack){return data(stack).getShort("capacity");}
    public void setFill(ItemStack stack,HbmFluidType type,short fill){
        var tag=data(stack);tag.putShort("type",(short)type.getID());tag.putShort("fill",fill);
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
    }
    @Override public int getFill(ItemStack stack){return data(stack).getShort("fill");}
    @Override public boolean acceptsFluid(HbmFluidType type,ItemStack stack){
        return (type==getType(stack)||getFill(stack)==0)&&!type.isAntimatter();
    }
    @Override public int tryFill(HbmFluidType type,int amount,ItemStack stack){
        if(!acceptsFluid(type,stack))return amount;
        if(getFill(stack)==0)setFill(stack,type,(short)0);
        int request=getCapacity(stack)-getFill(stack),toFill=Math.min(request,amount);
        setFill(stack,type,(short)(getFill(stack)+toFill));
        if(getFill(stack)>0&&willFizzle(type))stack.setCount(0);
        return amount-toFill;
    }
    public boolean willFizzle(HbmFluidType type){return kind==Kind.GLASS&&type.isCorrosive()&&type!=HbmFluids.PEROXIDE;}
    @Override public boolean providesFluid(HbmFluidType type,ItemStack stack){return getType(stack)==type;}
    @Override public int tryEmpty(HbmFluidType type,int amount,ItemStack stack){
        if(providesFluid(type,stack)){
            int toUnload=Math.min(amount,getFill(stack));setFill(stack,type,(short)(getFill(stack)-toUnload));
            if(getFill(stack)==0)setFill(stack,HbmFluids.NONE,(short)0);
            return toUnload;
        }
        // Source mismatch result is the requested amount; the real loader checks providesFluid first.
        return amount;
    }
    @Override public HbmFluidType getFirstFluidType(ItemStack stack){return getType(stack);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);data(stack);
        if(!level.isClientSide){
            if(getFill(stack)==0){
                int capacity=player.isShiftKeyDown()?Math.max(getCapacity(stack)-kind.step,kind.step):Math.min(getCapacity(stack)+kind.step,kind.maximum);
                var tag=data(stack);tag.putShort("capacity",(short)capacity);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
                player.displayClientMessage(Component.literal(capacity+"/"+getMaxFill()+"mB"),false);
            }else player.displayClientMessage(Component.translatable("desc.item.pipette.noEmpty"),false);
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+kind.sourceName+".name");}
    @Override public boolean isValidRepairItem(ItemStack stack,ItemStack material){return false;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        if(kind==Kind.LABORATORY){tooltip.add(Component.translatable("desc.item.pipette.corrosive"));tooltip.add(Component.translatable("desc.item.pipette.laboratory"));}
        if(kind==Kind.BORON)tooltip.add(Component.translatable("desc.item.pipette.corrosive"));
        if(kind==Kind.GLASS)tooltip.add(Component.translatable("desc.item.pipette.noCorrosive"));
        tooltip.add(Component.translatable("tooltip.hbm.pipette.fluid",getType(stack).displayName()));
        tooltip.add(Component.translatable("tooltip.hbm.pipette.amount",getFill(stack),getCapacity(stack),getMaxFill()));
    }
    public int color(ItemStack stack,int pass){int color=pass==0?0xffffff:getType(stack).getColor();return 0xff000000|(color<0?0xffffff:color);}
}
