package com.hbm.machine.oilextraction;
import java.util.List;import net.minecraft.network.chat.Component;import net.minecraft.world.*;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.*;import net.minecraft.world.level.Level;import net.minecraft.core.BlockPos;
/** Exact original13-column oil-rock detector. It does not detect depleted rock or bedrock-oil nodes. */
public final class OilDetectorItem extends Item {
 public OilDetectorItem(){super(new Properties().stacksTo(1));}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("item.oil_detector.desc1"));lines.add(Component.translatable("item.oil_detector.desc2"));}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){int x=(int)player.getX(),y=(int)player.getY(),z=(int)player.getZ();boolean direct=scan(level,x,y+15,z,5),oil=false;for(var offset:new int[][]{{5,0},{-5,0},{0,5},{0,-5},{5,5},{-5,5},{5,-5},{-5,-5}})oil|=scan(level,x+offset[0],y+15,z+offset[1],5);for(var offset:new int[][]{{10,0},{-10,0},{0,10},{0,-10}})oil|=scan(level,x+offset[0],y+15,z+offset[1],10);if(!level.isClientSide)OilDetectorNotice.send(player,direct?0:oil?1:2);level.playSound(player,player.getX(),player.getY(),player.getZ(),com.hbm.registry.HbmSounds.get("item.techBleep"),net.minecraft.sounds.SoundSource.PLAYERS,1,1);player.swing(hand);return InteractionResultHolder.success(player.getItemInHand(hand));}
 private static boolean scan(Level level,int x,int top,int z,int bottom){boolean found=false;for(int y=top;y>level.getMinBuildHeight()+bottom;y--)if(level.getBlockState(new BlockPos(x,y,z)).is(OilDrillEntity.block("ore_oil")))found=true;return found;}
}
