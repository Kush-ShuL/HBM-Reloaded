package com.hbm.item.defuser;
import com.hbm.armor.*;
import com.hbm.machine.pedestal.PedestalDefuser;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
/** Actual ItemModDefuser, all four armor slots' source extra-mod aura; no new armor-mod storage/runtime. */
public final class GoldenDefuserItem extends ArmorModItem {
    public GoldenDefuserItem(){super(ArmorModStorage.EXTRA,true,true,true,true);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.defuser_gold.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable("tooltip.hbm.golden_defuser.aura").withStyle(ChatFormatting.YELLOW));lines.add(Component.empty());super.appendHoverText(stack,context,lines,flags);}
    public Component installedDescription(ItemStack stack){return Component.translatable("tooltip.hbm.golden_defuser.installed",stack.getHoverName()).withStyle(ChatFormatting.YELLOW);}
    public static void register(){NeoForge.EVENT_BUS.addListener(GoldenDefuserItem::tick);}
    private static void tick(EntityTickEvent.Pre event){
        if(!(event.getEntity()instanceof LivingEntity wearer)||wearer.level().isClientSide||wearer.level().getGameTime()%20!=0)return;
        for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD}){
            var armor=wearer.getItemBySlot(slot);if(!ArmorModStorage.hasMods(armor)||!(ArmorModStorage.pryMod(armor,ArmorModStorage.EXTRA,wearer.registryAccess()).getItem()instanceof GoldenDefuserItem))continue;
            for(var creeper:wearer.level().getEntitiesOfClass(Creeper.class,wearer.getBoundingBox().inflate(5)))PedestalDefuser.defuse(creeper,wearer,true);
        }
    }
}
