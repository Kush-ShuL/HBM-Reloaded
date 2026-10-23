package com.hbm.network.cargo;
import com.hbm.item.EnumMultiItem;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
/** All five original identities; REQUEST is dock-consumed, and source ground use consumes without spawning. */
public final class DroneItem extends EnumMultiItem {
 public DroneItem(){super("drone",64,true,List.of("patrol","patrol_chunkloading","patrol_express","patrol_express_chunkloading","request"),List.of(0,1,2,3,4));}
 public ItemStack stack(int meta){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),meta);return stack;}
 @Override public InteractionResult useOn(UseOnContext context){if(context.getClickedFace()!=Direction.UP)return InteractionResult.PASS;var level=context.getLevel();if(level.isClientSide)return InteractionResult.SUCCESS;var stack=context.getItemInHand();int meta=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);if(meta<4){var drone=CargoDroneContent.PATROL_ENTITY.get().create(level);if(drone!=null){if(meta%2==1)drone.setChunkLoading();if(meta>1)drone.setExpress(true);var pos=context.getClickedPos();drone.setPos(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5);level.addFreshEntity(drone);}}stack.shrink(1);return InteractionResult.PASS;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){if(com.hbm.item.BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:net.minecraft.locale.Language.getInstance().getOrDefault("item.drone."+List.of("patrol","patrol_chunkloading","patrol_express","patrol_express_chunkloading","request").get(ordinal(stack))+".desc").split("\\$"))lines.add(net.minecraft.network.chat.Component.literal("\u00a7e"+line));}else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
