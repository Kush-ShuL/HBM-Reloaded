package com.hbm.railway.train;
import com.hbm.hazard.HbmHazards;
import com.hbm.railway.track.*;
import com.hbm.railway.track.IRailNTM.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
/** Real hidden two-meta ItemTrain placement, gauge and both-bogie validation. */
public final class SourceTrainItem extends Item {
    public SourceTrainItem(){super(new Properties().stacksTo(1));}
    public static int ordinal(ItemStack stack){int meta=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);return meta>=0&&meta<2?meta:0;}
    public static ItemStack stack(int type){var stack=new ItemStack(RailTrainContent.TRAIN.get());stack.set(HbmHazards.LEGACY_METADATA.get(),type);return stack;}
    @Override public String getDescriptionId(ItemStack stack){return "item.hbm.train."+(ordinal(stack)==0?"cargo_tram":"cargo_tram_trailer");}
    @Override public InteractionResult useOn(UseOnContext context){var level=context.getLevel();var pos=context.getClickedPos();if(!SourceRailBlock.full(level,pos)||!(level.getBlockState(pos).getBlock()instanceof IRailNTM rail))return InteractionResult.PASS;var player=context.getPlayer();if(player==null)return InteractionResult.PASS;RailCarEntity train=ordinal(context.getItemInHand())==0?new TrainCargoTram(level):new TrainCargoTramTrailer(level);if(train.getGauge()!=rail.getGauge())return InteractionResult.PASS;var hit=context.getClickLocation();train.setPos(hit.x,hit.y,hit.z);train.rotationYaw=player.getYRot();try{var anchor=train.getCurrentAnchorPos();var core=train.getRelPosAlongRail(anchor,0,new MoveContext(RailCheckType.CORE,0));if(core==null)return InteractionResult.PASS;train.setPos(core.xCoord,core.yCoord,core.zCoord);var front=train.getRelPosAlongRail(anchor,train.getLengthSpan(),new MoveContext(RailCheckType.FRONT,train.getCollisionSpan()-train.getLengthSpan()));var back=train.getRelPosAlongRail(anchor,-train.getLengthSpan(),new MoveContext(RailCheckType.BACK,train.getCollisionSpan()-train.getLengthSpan()));if(front==null||back==null)return InteractionResult.PASS;if(!RailTrainMotion.readyBox(level,train.getBoundingBox()))return InteractionResult.PASS;if(!level.isClientSide){train.rotationYaw=RailTrainMath.yaw(front,back);train.movementYaw=train.rotationYaw;train.publishAngles();level.addFreshEntity(train);}context.getItemInHand().shrink(1);return InteractionResult.SUCCESS;}catch(RailTrainMotion.Unavailable unavailable){return InteractionResult.PASS;}}
    private static Component line(String key,String value){Component actual=switch(value){case "Electric"->Component.translatable("tooltip.hbm.source_train.value.electric");case "Standard Gauge"->Component.translatable("tooltip.hbm.source_train.value.standard");case "Yes"->Component.translatable("tooltip.hbm.source_train.value.yes");case "No"->Component.translatable("tooltip.hbm.source_train.value.no");default->Component.literal(value);};return Component.translatable("tooltip.hbm.source_train."+key,actual.copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GREEN);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(ordinal(stack)==0)lines.add(line("engine","Electric"));lines.add(line("gauge","Standard Gauge"));lines.add(line("max_speed",ordinal(stack)==0?"10m/s":"Yes"));if(ordinal(stack)==0){lines.add(line("acceleration","0.2m/s²"));lines.add(line("brake_threshold","<1m/s"));}lines.add(line("parking_brake",ordinal(stack)==0?"Yes":"No"));}
}
