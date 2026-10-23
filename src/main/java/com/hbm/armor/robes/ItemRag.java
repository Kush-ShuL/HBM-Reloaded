package com.hbm.armor.robes;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Source rag's two real transformations, including loss on full inventory and creative consumption. */
public final class ItemRag extends Item {
 public ItemRag(){super(new Properties());}
 @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity entity){
  if(entity!=null&&!entity.level().isClientSide&&LegacyMaterials.of(entity.level().getBlockState(BlockPos.containing(entity.getX(),entity.getY(),entity.getZ()))).name().equals("water")){
   entity.setItem(new ItemStack(HbmContent.item("rag_damp"),entity.getItem().getCount()));return true;
  }
  return false;
 }
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);stack.shrink(1);player.getInventory().add(new ItemStack(HbmContent.item("rag_piss")));return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(var line:Component.translatable("item.rag.desc").getString().split("\\$"))lines.add(Component.literal(line));}
}
