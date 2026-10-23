package com.hbm.item.constructionwand;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Actual ItemWand / ModItems.wand, registered under its real source wand_k identity. */
public final class ConstructionWandItem extends Item {
    public ConstructionWandItem(){super(new Properties().stacksTo(1));}
    private static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    private static boolean unset(CompoundTag tag){return tag.getInt("x")==0&&tag.getInt("y")==0&&tag.getInt("z")==0;}
    private static void save(ItemStack stack,CompoundTag tag){stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    private static BlockState selected(CompoundTag tag){
        if(tag.contains("hbm_native_state",Tag.TAG_COMPOUND))return com.hbm.world.structure.templates.TemplateStateMappings.parse(tag.getCompound("hbm_native_state"));
        if(!tag.contains("block"))return Blocks.AIR.defaultBlockState();
        if(!tag.contains("block",Tag.TAG_STRING))throw new IllegalArgumentException("Legacy numeric block selection requires its actual original registry map");
        var id=ResourceLocation.tryParse(tag.getString("block"));if(id==null||!BuiltInRegistries.BLOCK.containsKey(id))throw new IllegalArgumentException("Unknown saved block "+tag.getString("block"));
        return com.hbm.world.structure.LegacyStructureStates.resolve(id.toString(),tag.getInt("meta"),0);
    }
    private static void block(CompoundTag tag,BlockState state){
        tag.putString("block",BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
        var property=state.getBlock().getStateDefinition().getProperty("legacy_meta");
        tag.putInt("meta",property!=null&&state.getValue(property) instanceof Integer value?value:0);
        // The 1.21 registry/state encoding is stable across numeric registry reordering and preserves flattened colors/facings.
        tag.put("hbm_native_state",NbtUtils.writeBlockState(state));
    }
    @Override public InteractionResult useOn(UseOnContext context){
        var player=context.getPlayer();if(player==null)return InteractionResult.PASS;
        var stack=context.getItemInHand();var level=context.getLevel();var pos=context.getClickedPos();var tag=data(stack);
        if(player.isShiftKeyDown()){
            var state=level.getBlockState(pos);block(tag,state);save(stack,tag);
            if(level.isClientSide)message(player,"block",state.getBlock().getDescriptionId());
        }else if(unset(tag)){
            tag.putInt("x",pos.getX());tag.putInt("y",pos.getY());tag.putInt("z",pos.getZ());save(stack,tag);
            if(level.isClientSide)message(player,"position");
        }else{
            int x=tag.getInt("x"),y=tag.getInt("y"),z=tag.getInt("z");tag.putInt("x",0);tag.putInt("y",0);tag.putInt("z",0);save(stack,tag);
            if(!level.isClientSide){
                final BlockState state;
                try{state=selected(tag);}catch(IllegalArgumentException invalid){player.displayClientMessage(Component.translatable("message.hbm.construction_wand.invalid",invalid.getMessage()),false);return InteractionResult.CONSUME;}
                ConstructionWandFills.submit((net.minecraft.server.level.ServerLevel)level,new BlockPos(x,y,z),pos,state,player.getUUID());
            }
            // A cold operation is not complete yet. The original completion message is sent by the real server job only after its final cell.
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);var tag=data(stack);
        if(player.isShiftKeyDown()){block(tag,Blocks.AIR.defaultBlockState());if(level.isClientSide)message(player,"block",Blocks.AIR.getDescriptionId());}
        save(stack,tag);return InteractionResultHolder.consume(stack);
    }
    private static void message(Player player,String key,Object...arguments){player.displayClientMessage(Component.translatable("message.hbm.construction_wand."+key,arguments),false);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags){
        for(int i=0;i<4;i++)tooltip.add(Component.translatable("tooltip.hbm.construction_wand."+i));
        var tag=data(stack);
        if(stack.has(DataComponents.CUSTOM_DATA)&&!unset(tag))tooltip.add(Component.translatable("tooltip.hbm.construction_wand.position",tag.getInt("x"),tag.getInt("y"),tag.getInt("z")));
        else tooltip.add(Component.translatable("tooltip.hbm.construction_wand.unset"));
        if(stack.has(DataComponents.CUSTOM_DATA))try{tooltip.add(Component.translatable("tooltip.hbm.construction_wand.saved",selected(tag).getBlock().getDescriptionId()));}
        catch(IllegalArgumentException invalid){tooltip.add(Component.translatable("message.hbm.construction_wand.invalid",invalid.getMessage()));}
    }
}
