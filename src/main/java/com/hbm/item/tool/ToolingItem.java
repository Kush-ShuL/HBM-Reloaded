package com.hbm.item.tool;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.api.tool.ToolableBlock.ToolType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.EventHooks;

/** Original ItemTooling uses the already shared native ToolableBlock callback, preserving block-before-item ordering. */
public class ToolingItem extends CraftingDegradationItem implements com.hbm.multiblock.MultiblockTool {
    public final ToolType type;
    public ToolingItem(ToolType type,int durability){this(type,durability,new Properties());}
    protected ToolingItem(ToolType type,int durability,Properties properties){super(durability,properties);this.type=type;}
    public void registerSourceType(){type.register(new ItemStack(this));}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var player=context.getPlayer();var pos=context.getClickedPos();if(player==null||!(level.getBlockState(pos).getBlock() instanceof ToolableBlock block))return InteractionResult.PASS;
        var hit=context.getClickLocation().subtract(pos.getX(),pos.getY(),pos.getZ());if(!block.onScrew(level,player,pos,context.getClickedFace(),(float)hit.x,(float)hit.y,(float)hit.z,type))return InteractionResult.PASS;
        if(!level.isClientSide){wear(context);player.inventoryMenu.broadcastChanges();}return InteractionResult.sidedSuccess(level.isClientSide);
    }
    /** For source blocks that perform onScrew inside their modern item-before-useOn activation bridge. */
    public void onToolUsed(ItemStack stack,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand){wear(player.level(),player,stack,hand);}
    private void wear(UseOnContext context){wear(context.getLevel(),context.getPlayer(),context.getItemInHand(),context.getHand());}
    @Override public boolean isHandDrill(ItemStack stack,net.minecraft.world.entity.player.Player player){return type==ToolType.HAND_DRILL;}
    @Override public void onPartRemoved(ItemStack stack,net.minecraft.world.entity.player.Player player,net.minecraft.world.InteractionHand hand){wear(player.level(),player,stack,hand);}
    private void wear(net.minecraft.world.level.Level world,net.minecraft.world.entity.player.Player player,ItemStack stack,net.minecraft.world.InteractionHand hand){if(player==null||player.getAbilities().instabuild||sourceDurability<=0||!stack.isDamageableItem())return;
        var unbreaking=world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);int level=EnchantmentHelper.getItemEnchantmentLevel(unbreaking,stack);if(level>0&&player.getRandom().nextInt(level+1)>0)return;
        var before=stack.copy();stack.setDamageValue(stack.getDamageValue()+1);
        if(stack.getDamageValue()>sourceDurability){player.onEquippedItemBroken(this,LivingEntity.getSlotForHand(hand));stack.shrink(1);EventHooks.onPlayerDestroyItem(player,before,hand);stack.setDamageValue(0);}
    }
}
