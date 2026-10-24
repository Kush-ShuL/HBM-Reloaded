package com.hbm.network.cargo;

import com.hbm.item.BasicBlockItem;
import com.hbm.item.TooltipHints;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

/** Five fixed source identities. Drone cargo, targets and runtime loading remain entity state. */
public final class DroneItem extends Item {
    public enum Kind {
        PATROL("patrol",false,false),
        PATROL_CHUNKLOADING("patrol_chunkloading",true,false),
        PATROL_EXPRESS("patrol_express",false,true),
        PATROL_EXPRESS_CHUNKLOADING("patrol_express_chunkloading",true,true),
        REQUEST("request",false,false);
        public final String suffix;
        public final boolean chunkLoading,express;
        Kind(String suffix,boolean chunkLoading,boolean express){this.suffix=suffix;this.chunkLoading=chunkLoading;this.express=express;}
        public String id(){return "drone_"+suffix;}
        public static Kind patrol(boolean express,boolean chunkLoading){return express?(chunkLoading?PATROL_EXPRESS_CHUNKLOADING:PATROL_EXPRESS):(chunkLoading?PATROL_CHUNKLOADING:PATROL);}
    }

    private final Kind kind;
    public DroneItem(Kind kind){super(new Properties().stacksTo(64));this.kind=java.util.Objects.requireNonNull(kind);}
    public Kind kind(){return kind;}

    @Override public InteractionResult useOn(UseOnContext context){
        if(context.getClickedFace()!=Direction.UP)return InteractionResult.PASS;
        var level=context.getLevel();
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(kind!=Kind.REQUEST){
            var drone=CargoDroneContent.PATROL_ENTITY.get().create(level);
            if(drone!=null){
                if(kind.chunkLoading)drone.setChunkLoading();
                if(kind.express)drone.setExpress(true);
                var pos=context.getClickedPos();
                drone.setPos(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5);
                level.addFreshEntity(drone);
            }
        }
        // The source REQUEST item is dock-consumed; ground use still consumes without spawning.
        context.getItemInHand().shrink(1);
        return InteractionResult.PASS;
    }

    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        if(BasicBlockItem.leftShiftHeld.getAsBoolean()){
            for(String line:Language.getInstance().getOrDefault("item.drone."+kind.suffix+".desc").split("\\$"))lines.add(Component.literal("\u00a7e"+line));
        }else lines.add(TooltipHints.moreInfo());
    }
}
