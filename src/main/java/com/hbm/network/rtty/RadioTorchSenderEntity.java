package com.hbm.network.rtty;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original support-side indirect power, comparator override, state-change/polling and custom outgoing mapping. */
public final class RadioTorchSenderEntity extends RadioTorchEntity {
 public RadioTorchSenderEntity(BlockPos pos,BlockState state){super(RadioTorchContent.SENDER_ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,RadioTorchSenderEntity sender){if(level instanceof ServerLevel server)sender.tickRadio(server);}
 @Override protected boolean signalTick(ServerLevel level){var outward=RadioTorchBlock.outward(getBlockState());var support=outward==null?worldPosition:worldPosition.relative(outward.getOpposite());var state=level.getBlockState(support);int input=outward==null?0:level.getSignal(support,outward);if(state.hasAnalogOutputSignal())input=state.getAnalogOutputSignal(level,support);boolean send=polling;if(input!=lastState){lastState=input;stateChanged(false);send=true;}if(send&&!channel.isEmpty()){String message=customMap?mapping[input]:Integer.toString(input);if(message!=null&&!message.isEmpty())RTTYSystem.broadcast(level,channel,message);}return true;}
}
