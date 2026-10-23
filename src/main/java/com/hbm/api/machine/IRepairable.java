package com.hbm.api.machine;

import com.hbm.item.tool.BlowtorchItem;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.recipe.generic.GenericIngredient;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Original material consumption is planned on copies and committed only if all requirements match. */
public interface IRepairable {
    boolean isDamaged();
    List<GenericIngredient> getRepairMaterials();
    void repair(Player player);
    void tryExtinguish(Level level,BlockPos pos,EnumExtinguishType type);
    enum EnumExtinguishType {WATER,FOAM,SAND,CO2}
    static List<GenericIngredient> getRepairMaterials(Level level,BlockPos pos,DummyableBlock dummy,Player player){if(!(player.getMainHandItem().getItem() instanceof BlowtorchItem))return null;var core=dummy.findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof IRepairable machine&&machine.isDamaged()?machine.getRepairMaterials():null;}
    static boolean tryRepairMultiblock(Level level,BlockPos pos,DummyableBlock dummy,Player player){
        var core=dummy.findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof IRepairable machine)||!machine.isDamaged())return false;
        var materials=machine.getRepairMaterials();if(materials==null||materials.isEmpty()||consume(player,materials)){if(!level.isClientSide)machine.repair(player);return true;}return false;
    }
    private static boolean consume(Player player,List<GenericIngredient> materials){
        var original=player.getInventory().items;var inventory=original.stream().map(ItemStack::copy).toList();var changed=new boolean[original.size()];
        for(var ingredient:materials){int required=ingredient.count();for(int slot=0;slot<inventory.size();slot++){var stack=inventory.get(slot);if(ingredient.matches(stack,true)){int amount=Math.min(required,stack.getCount());required-=amount;stack.shrink(amount);changed[slot]=true;if(required<=0)break;}}if(required>0)return false;}
        for(int slot=0;slot<inventory.size();slot++)if(changed[slot])original.set(slot,inventory.get(slot));return true;
    }
}
