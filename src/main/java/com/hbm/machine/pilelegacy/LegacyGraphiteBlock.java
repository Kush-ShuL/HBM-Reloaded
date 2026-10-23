package com.hbm.machine.pilelegacy;

import api.hbm.block.IInsertable;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.material.crystallizer.CrystallizerMaterials;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

/** True legacy drilled channels: insertion/ejection, aluminum shrouds, control propagation and original drop quirks. */
public class LegacyGraphiteBlock extends Block implements ToolableBlock,IInsertable {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public final LegacyPileKind kind;
    public LegacyGraphiteBlock(LegacyPileKind kind){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.METAL).requiresCorrectToolForDrops());this.kind=kind;registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    public BlockState state(int metadata){return defaultBlockState().setValue(META,metadata&15);}
    static int metadata(BlockState state){return state.getOptionalValue(META).orElse(0);}
    static boolean axial(int meta,Direction side){int value=side.get3DDataValue(),cfg=meta&3;return value==cfg*2||value==cfg*2+1;}
    static void replace(Level level,BlockPos pos,LegacyPileKind kind,int metadata,int flags){level.setBlock(pos,LegacyPileContent.CHANNELS.get(kind).get().state(metadata),flags);}
    @Override public int getFlammability(BlockState state,BlockGetter level,BlockPos pos,Direction side){return 5;}
    @Override public int getFireSpreadSpeed(BlockState state,BlockGetter level,BlockPos pos,Direction side){return 30;}
    public boolean shouldIgnite(Level level,BlockPos pos){for(var side:Direction.values())if(level.getBlockState(pos.relative(side)).is(Blocks.FIRE))return true;return false;}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=metadata(state);if(rotation==Rotation.CLOCKWISE_90||rotation==Rotation.COUNTERCLOCKWISE_90){int axis=meta&3;if(axis==1||axis==2)meta=(meta&~3)|(3-axis);}return state(meta);}
    // Orientation stores the undirected axis pair, so mirrors do not alter source metadata.
    @Override protected BlockState mirror(BlockState state,Mirror mirror){return state;}
    Item inserted(int meta){return switch(kind){case DRILLED->null;case FUEL->LegacyPileContent.RODS.get((meta&8)!=0?"pu239":"uranium").get();case TRITIUM->LegacyPileItems.item("cell_tritium");default->LegacyPileContent.RODS.get(kind.rod).get();};}
    private Item insertedNoArgument(){return kind==LegacyPileKind.FUEL?null:inserted(0);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
        var tool=context.getOptionalParameter(LootContextParams.TOOL);if(tool==null)tool=ItemStack.EMPTY;
        var enchants=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if(!kind.hasEntity()&&EnchantmentHelper.getItemEnchantmentLevel(enchants.getOrThrow(Enchantments.SILK_TOUCH),tool)>0)return List.of(new ItemStack(this));
        int meta=metadata(state);var drops=new ArrayList<ItemStack>();drops.add(LegacyPileItems.stack("ingot_graphite",8));
        if((meta&4)!=0)drops.add(LegacyPileItems.shell());
        // Original checks the no-argument overload first. Fuel overrides only getInsertedItem(meta), so it drops no rod!
        if(insertedNoArgument()!=null)drops.add(new ItemStack(inserted(meta)));
        return drops;
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return kind==LegacyPileKind.FUEL;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){return level.getBlockEntity(pos) instanceof LegacyPileFuelEntity fuel?net.minecraft.util.Mth.clamp(fuel.progress*15/(fuel.maxProgress()-1000),0,15):0;}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return activate(level,pos,state,player,hand,hit.getDirection())?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,state,player,InteractionHand.MAIN_HAND,hit.getDirection())?InteractionResult.sidedSuccess(level.isClientSide):InteractionResult.PASS;}
    private boolean activate(Level level,BlockPos pos,BlockState state,Player player,InteractionHand hand,Direction side){
        int meta=metadata(state);
        if(kind==LegacyPileKind.ROD&&!player.isShiftKeyDown()&&axial(meta,side)){
            if(level.isClientSide)return true;
            int next=meta^8;level.setBlock(pos,state(next),3);
            sound(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,SoundEvents.UI_BUTTON_CLICK.value(),.3F,(meta&3)==(meta&11)?.75F:.65F);
            propagate(level,pos,side,meta,next,false);return true;
        }
        if(kind!=LegacyPileKind.DRILLED||!axial(meta,side))return false;
        var held=player.getItemInHand(hand);if(held.isEmpty())return false;
        Selection selected=selection(held);
        if(selected!=null){held.shrink(1);replace(level,pos,selected.kind,meta|selected.metadata,3);plug(level,pos,1F,1F);return true;}
        if(meta>>2!=1){
            if(LegacyPileItems.shell(held)){held.shrink(1);replace(level,pos,kind,meta|4,3);plug(level,pos,1F,1F);return true;}
            if(LegacyPileItems.is(held,"ingot_graphite")){held.shrink(1);level.setBlock(pos,CrystallizerMaterials.GRAPHITE.get().defaultBlockState(),3);plug(level,pos,1F,1F);return true;}
        }
        return false;
    }
    private record Selection(LegacyPileKind kind,int metadata){}
    private static Selection selection(ItemStack stack){
        for(var entry:LegacyPileContent.RODS.entrySet())if(stack.is(entry.getValue().get()))return switch(entry.getKey()){
            case "uranium"->new Selection(LegacyPileKind.FUEL,0);case "pu239"->new Selection(LegacyPileKind.FUEL,8);case "plutonium"->new Selection(LegacyPileKind.PLUTONIUM,0);
            case "source"->new Selection(LegacyPileKind.SOURCE,0);case "boron"->new Selection(LegacyPileKind.ROD,0);case "lithium"->new Selection(LegacyPileKind.LITHIUM,0);default->new Selection(LegacyPileKind.DETECTOR,0);};
        return LegacyPileItems.is(stack,"cell_tritium")?new Selection(LegacyPileKind.TRITIUM,0):null;
    }
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){
        int meta=metadata(level.getBlockState(pos));
        if(kind==LegacyPileKind.DRILLED){
            if(tool!=ToolType.SCREWDRIVER)return false;
            if(!level.isClientSide&&axial(meta,side)&&meta>>2==1){replace(level,pos,kind,meta&3,3);plug(level,pos,1F,.85F);eject(level,pos,side,LegacyPileItems.shell());}
            return true;
        }
        if(kind==LegacyPileKind.FUEL){
            if(!level.isClientSide){
                if(tool==ToolType.SCREWDRIVER&&axial(meta,side)){replace(level,pos,LegacyPileKind.DRILLED,meta&7,3);eject(level,pos,side,new ItemStack(inserted(meta)));}
                if(tool==ToolType.HAND_DRILL&&level.getBlockEntity(pos) instanceof LegacyPileFuelEntity fuel){heading(player,pos);message(player,"message.hbm.pile_legacy.heat",fuel.heat,1000);message(player,"message.hbm.pile_legacy.depletion",fuel.progress,fuel.maxProgress());message(player,"message.hbm.pile_legacy.flux",fuel.lastNeutrons);if((meta&8)!=0)player.displayClientMessage(Component.translatable("message.hbm.pile_legacy.pu239").withStyle(ChatFormatting.DARK_GREEN),false);}
            }
            return true;
        }
        if(kind==LegacyPileKind.LITHIUM){
            if(!level.isClientSide){
                if(tool==ToolType.SCREWDRIVER&&axial(meta,side)){replace(level,pos,LegacyPileKind.DRILLED,meta,3);eject(level,pos,side,new ItemStack(inserted(meta)));}
                if(tool==ToolType.HAND_DRILL&&level.getBlockEntity(pos) instanceof LegacyPileBreedingEntity fuel){heading(player,pos);message(player,"message.hbm.pile_legacy.depletion",fuel.progress,fuel.maxProgress());message(player,"message.hbm.pile_legacy.flux",fuel.lastNeutrons);}
            }
            return true;
        }
        if(kind==LegacyPileKind.DETECTOR){
            if(!level.isClientSide&&level.getBlockEntity(pos) instanceof LegacyPileDetectorEntity detector){
                if(tool==ToolType.SCREWDRIVER){if(!player.isShiftKeyDown()){if(axial(meta,side)){replace(level,pos,LegacyPileKind.DRILLED,meta&7,3);eject(level,pos,side,new ItemStack(inserted(meta)));}}else{heading(player,pos);message(player,"message.hbm.pile_legacy.flux_limit",detector.lastNeutrons,detector.maxNeutrons);}}
                if(tool==ToolType.DEFUSER){if(player.isShiftKeyDown()){if(detector.maxNeutrons>1)detector.maxNeutrons--;}else detector.maxNeutrons++;detector.setChanged();}
            }
            return true;
        }
        if(tool!=ToolType.SCREWDRIVER)return false;
        if(!level.isClientSide&&axial(meta,side)){replace(level,pos,LegacyPileKind.DRILLED,meta&7,3);eject(level,pos,side,new ItemStack(inserted(meta)));}
        return true;
    }
    void triggerRods(Level level,BlockPos pos){int old=metadata(level.getBlockState(pos)),next=old^8;level.setBlock(pos,state(next),3);sound(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("hbm:item.techbleep")),.02F,1F);int cfg=old&3;if(cfg<3)propagate(level,pos,Direction.from3DDataValue(cfg*2),old,next,true);}
    private void propagate(Level level,BlockPos pos,Direction direction,int old,int next,boolean detector){
        for(int i=-1;i<=1;i++){
            var at=pos.relative(direction,i);
            while(level.getBlockState(at).getBlock()==(detector?LegacyPileContent.CHANNELS.get(LegacyPileKind.ROD).get():this)&&metadata(level.getBlockState(at))==old){level.setBlock(at,LegacyPileContent.CHANNELS.get(LegacyPileKind.ROD).get().state(next),3);at=at.relative(direction,i);}
        }
    }
    private static void heading(Player player,BlockPos pos){player.displayClientMessage(Component.translatable("message.hbm.pile_legacy.heading",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.GOLD),false);}
    private static void message(Player player,String key,Object... values){player.displayClientMessage(Component.translatable(key,values).withStyle(ChatFormatting.YELLOW),false);}
    static void eject(Level level,BlockPos pos,Direction direction,ItemStack stack){var entity=new ItemEntity(level,pos.getX()+.5+direction.getStepX()*.75,pos.getY()+.5+direction.getStepY()*.75,pos.getZ()+.5+direction.getStepZ()*.75,stack);entity.setDeltaMovement(direction.getStepX()*.25,direction.getStepY()*.25,direction.getStepZ()*.25);level.addFreshEntity(entity);}
    private static void sound(Level level,double x,double y,double z,SoundEvent sound,float volume,float pitch){level.playSound(null,x,y,z,sound,SoundSource.BLOCKS,volume,pitch);}
    private static void plug(Level level,BlockPos pos,float volume,float pitch){sound(level,pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5,BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("hbm:item.upgradeplug")),volume,pitch);}
    @Override public boolean insertItem(Level level,BlockPos pos,Direction direction,ItemStack stack){
        if(stack==null||stack.isEmpty()||direction==null)return false;
        var selected=selection(stack);if(selected==null)return false;
        int pureMeta=metadata(level.getBlockState(pos))&3;if(!axial(pureMeta,direction))return false;
        for(int i=0;i<=3;i++){
            var at=pos.relative(direction,i);var state=level.getBlockState(at);
            if(state.getBlock() instanceof LegacyGraphiteBlock channel){int meta=metadata(state);if((meta&3)!=pureMeta)return false;if(channel.inserted(meta)==null)break;else if(i>=3)return false;}
            else{if(state.isSolidRender(level,at))return false;else break;}
        }
        int oldMeta=pureMeta|selected.metadata;LegacyGraphiteBlock oldBlock=LegacyPileContent.CHANNELS.get(selected.kind).get();
        var oldTag=new CompoundTag();oldTag.putInt("x",pos.getX());oldTag.putInt("y",pos.getY());oldTag.putInt("z",pos.getZ());
        for(int i=0;i<=3;i++){
            var at=pos.relative(direction,i);var newState=level.getBlockState(at);
            if(newState.getBlock() instanceof LegacyGraphiteBlock newBlock){
                int newMeta=metadata(newState);var newTag=new CompoundTag();
                if(newBlock.kind.hasEntity()&&level.getBlockEntity(at)!=null){var entity=level.getBlockEntity(at);newTag=entity.saveWithFullMetadata(level.registryAccess());newTag.putInt("x",at.getX()+direction.getStepX());newTag.putInt("y",at.getY()+direction.getStepY());newTag.putInt("z",at.getZ()+direction.getStepZ());}
                var installed=oldBlock.state((oldMeta&~4)|(newMeta&4));level.setBlock(at,installed,0);
                if(oldBlock.kind.hasEntity()&&!oldTag.isEmpty()&&level.getBlockEntity(at)!=null)level.getBlockEntity(at).loadWithComponents(oldTag,level.registryAccess());
                level.markAndNotifyBlock(at,level.getChunkAt(at),newState,installed,3,512);
                oldMeta=newMeta;oldBlock=newBlock;oldTag=newTag;if(oldBlock.kind==LegacyPileKind.DRILLED)break;
            }else{eject(level,at.relative(direction.getOpposite()),direction,new ItemStack(oldBlock.inserted(oldMeta)));plugAtExit(level,at);break;}
        }
        return true;
    }
    private static void plugAtExit(Level level,BlockPos pos){sound(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("hbm:item.upgradeplug")),1.25F,1F);}
}
