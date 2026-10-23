package com.hbm.item.toolability;

import com.hbm.armor.LegacyArmorDurabilityRules;
import com.hbm.fluid.FillableItem;
import com.hbm.fluid.HbmFluidType;
import com.hbm.machine.storage.CrateStackData;
import java.util.HashSet;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Actual source fueled subclass of the ONE existing ability tool, using native private fluids and fuel NBT. */
public class ItemToolAbilityFueled extends ItemToolAbility implements FillableItem {
    protected final int maxFuel,consumption,fillRate;
    protected final HashSet<HbmFluidType> acceptedFuels=new HashSet<>();
    private static final class FuelProperties extends SourceProperties {
        @Override public Item.Properties durability(int ignored) { return super.durability(1); }
    }
    public ItemToolAbilityFueled(String sourceName,float damage,double movement,Tier material,Type type,
                                AvailableAbilities abilities,int maxFuel,int consumption,int fillRate,HbmFluidType... fuels) {
        super(sourceName,damage,movement,material,type,abilities,new FuelProperties());
        this.maxFuel=maxFuel;this.consumption=consumption;this.fillRate=fillRate;
        for(var fuel:fuels) acceptedFuels.add(fuel);
    }
    @Override public int getFill(ItemStack stack) {
        if(!stack.has(DataComponents.CUSTOM_DATA)) { setFill(stack,maxFuel);return maxFuel; }
        return CrateStackData.data(stack).getInt("fuel");
    }
    public void setFill(ItemStack stack,int fill) { var tag=CrateStackData.data(stack);tag.putInt("fuel",fill);CrateStackData.data(stack,tag); }
    @Override public boolean canOperate(ItemStack stack) { return getFill(stack)>=consumption; }
    @Override public void setDamage(ItemStack stack,int damage) { setFill(stack,Math.max(getFill(stack)-damage*consumption,0)); }
    // The source setter consumes fuel and never changes raw item damage. Preserve the same old Unbreaking RNG,
    // without a modern pre-setDamage break check accidentally destroying this maxDamage1 tool.
    private void wear(ItemStack stack,int amount,LivingEntity owner) {
        if(!(owner.level() instanceof ServerLevel level)||owner instanceof Player player&&player.getAbilities().instabuild)return;
        setDamage(stack,stack.getDamageValue()+LegacyArmorDurabilityRules.damageAfterUnbreaking(level,stack,amount,owner.getRandom()));
    }
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker) { wear(stack,1,attacker); }
    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity owner) {
        if(state.getDestroySpeed(level,pos)!=0) wear(stack,1,owner);return true;
    }
    @Override public boolean acceptsFluid(HbmFluidType type,ItemStack stack) { return acceptedFuels.contains(type); }
    @Override public int tryFill(HbmFluidType type,int amount,ItemStack stack) {
        if(!acceptsFluid(type,stack))return amount;
        int fill=Math.min(Math.min(amount,fillRate),maxFuel-getFill(stack));setFill(stack,getFill(stack)+fill);return amount-fill;
    }
    @Override public boolean providesFluid(HbmFluidType type,ItemStack stack) { return false; }
    @Override public int tryEmpty(HbmFluidType type,int amount,ItemStack stack) { return amount; }
    @Override public HbmFluidType getFirstFluidType(ItemStack stack) { return null; }
    public static ItemStack getEmptyTool(ItemToolAbilityFueled item) { var stack=new ItemStack(item);item.setFill(stack,0);return stack; }
    @Override public boolean isBarVisible(ItemStack stack) { return getFill(stack)<maxFuel; }
    @Override public int getBarWidth(ItemStack stack) { return (int)Math.round(13D*getFill(stack)/maxFuel); }
    @Override public int getBarColor(ItemStack stack) { return Mth.hsvToRgb(Math.max(0,(float)((double)getFill(stack)/maxFuel))/3F,1,1); }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.hbm.chainsaw.fuel",getFill(stack),maxFuel).withStyle(ChatFormatting.GOLD));
        for(var fuel:acceptedFuels)tooltip.add(Component.literal("- ").append(fuel.displayName()).withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack,context,tooltip,flag);
    }
}
