package com.hbm.machine.reactor.dfc;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
/** Exact ArmorUtil.checkForHaz2 identity whitelist; no inferred protection from armour value or tag. */
final class DfcProtection {
    private static final String[] SUITS={"hazmat_paa","liquidator","euphemium","rpa","fau","dns"};
    private static final String[] PARTS={"helmet","plate","legs","boots"};
    private static final EquipmentSlot[] SLOTS={EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
    static boolean checkForHaz2(LivingEntity entity){for(var suit:SUITS){boolean matches=true;for(int i=0;i<4;i++){var id=ResourceLocation.parse("hbm:"+suit+"_"+PARTS[i]);var stack=entity.getItemBySlot(SLOTS[i]);if(stack.isEmpty()||!BuiltInRegistries.ITEM.containsKey(id)||stack.getItem()!=BuiltInRegistries.ITEM.get(id)){matches=false;break;}}if(matches)return true;}return false;}
    private DfcProtection(){}
}
