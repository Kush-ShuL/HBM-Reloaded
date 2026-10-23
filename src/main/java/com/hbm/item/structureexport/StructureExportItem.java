package com.hbm.item.structureexport;

import com.hbm.config.GeneralModes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;

/** The five actual client-side source-code exporters, not construction or worldgen tools. */
public final class StructureExportItem extends Item {
    public enum Mode { SINGLE,SOLID,PATTERN,RANDOMIZED,RANDOMLY }
    public final Mode mode;
    public StructureExportItem(Mode mode){super(new Properties().stacksTo(1));this.mode=mode;}
    public boolean dualUse(){return mode!=Mode.SINGLE;}
    public static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    public static BlockPos anchor(ItemStack stack){if(!stack.has(DataComponents.CUSTOM_DATA))return null;var tag=data(stack);return new BlockPos(tag.getInt("anchorX"),tag.getInt("anchorY"),tag.getInt("anchorZ"));}
    @Override public InteractionResult useOn(UseOnContext context){
        var stack=context.getItemInHand();var level=context.getLevel();var pos=context.getClickedPos();var tag=data(stack);
        if(BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).equals(ResourceLocation.parse("hbm:structure_anchor"))){tag.putInt("anchorX",pos.getX());tag.putInt("anchorY",pos.getY());tag.putInt("anchorZ",pos.getZ());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return InteractionResult.sidedSuccess(level.isClientSide);}
        if(anchor(stack)==null)return InteractionResult.PASS;
        if(!dualUse()&&level.isClientSide)export(context,tag);
        else if(!tag.contains("x")){tag.putInt("x",pos.getX());tag.putInt("y",pos.getY());tag.putInt("z",pos.getZ());}
        else{if(level.isClientSide)export(context,tag);tag.remove("x");tag.remove("y");tag.remove("z");}
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return InteractionResult.sidedSuccess(level.isClientSide);
    }
    private void export(UseOnContext context,CompoundTag tag){
        try{String message=source(context.getLevel(),context.getClickedPos(),anchor(context.getItemInHand()),tag);System.out.print(message);writeToFile(message);}
        catch(IllegalArgumentException error){if(context.getPlayer()!=null)context.getPlayer().displayClientMessage(Component.translatable("message.hbm.structure_export.unrepresentable",error.getMessage()).withStyle(ChatFormatting.RED),false);}
    }
    private String source(Level level,BlockPos clicked,BlockPos anchor,CompoundTag tag){
        int ix=clicked.getX()-anchor.getX(),iy=clicked.getY()-anchor.getY(),iz=clicked.getZ()-anchor.getZ();
        if(mode==Mode.SINGLE){var block=StructureSourceState.capture(level.getBlockState(clicked));return place(block,ix,iy,iz);}
        int minX=Math.min(tag.getInt("x"),clicked.getX())-anchor.getX(),minY=Math.min(tag.getInt("y"),clicked.getY())-anchor.getY(),minZ=Math.min(tag.getInt("z"),clicked.getZ())-anchor.getZ();
        int maxX=Math.max(tag.getInt("x"),clicked.getX())-anchor.getX(),maxY=Math.max(tag.getInt("y"),clicked.getY())-anchor.getY(),maxZ=Math.max(tag.getInt("z"),clicked.getZ())-anchor.getZ();
        String bounds=minX+", "+minY+", "+minZ+", "+maxX+", "+maxY+", "+maxZ;
        if(mode==Mode.RANDOMIZED)return "fillWithRandomizedBlocks(world, box, "+bounds+", rand, <block-selector>);\n";
        if(mode==Mode.RANDOMLY)return "randomlyFillWithBlocks(world, box, rand, <limit>, "+bounds+", "+StructureSourceState.capture(level.getBlockState(clicked)).name()+");\n";
        if(mode==Mode.SOLID){var block=StructureSourceState.capture(level.getBlockState(clicked));return block.metadata()>0?"fillWithMetadataBlocks(world, box, "+bounds+", "+block.name()+", "+block.metadata()+");\n":"fillWithBlocks(world, box, "+bounds+", "+block.name()+");\n";}
        StringBuilder message=new StringBuilder();
        for(int x=minX;x<=maxX;x++)for(int y=minY;y<=maxY;y++)for(int z=minZ;z<=maxZ;z++){
            var state=level.getBlockState(anchor.offset(x,y,z));if(state.isAir())continue;
            message.append(place(StructureSourceState.capture(state),x,y,z));
        }
        return message.toString();
    }
    private static String place(StructureSourceState.Sample block,int x,int y,int z){return "placeBlockAtCurrentPosition(world, "+block.name()+", "+block.metadata()+", "+x+", "+y+", "+z+", box);\n";}
    private static void writeToFile(String message){
        if(!GeneralModes.localFlag("debugModeEnabled"))return;
        var path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig").resolve("structureOutput.txt");
        try{Files.createDirectories(path.getParent());Files.writeString(path,message,StandardOpenOption.CREATE,StandardOpenOption.APPEND);}
        catch(IOException error){System.out.print("ItemStructureWand encountered an IOException!");}
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        if(anchor(stack)==null)tooltip.add(Component.translatable("tooltip.hbm.structure_export.no_anchor").withStyle(ChatFormatting.RED));
        if(GeneralModes.localFlag("debugModeEnabled"))tooltip.add(Component.translatable("tooltip.hbm.structure_export.file").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.hbm.structure_export."+mode.name().toLowerCase(java.util.Locale.ROOT)+".0").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.hbm.structure_export."+mode.name().toLowerCase(java.util.Locale.ROOT)+".1").withStyle(ChatFormatting.YELLOW));
    }
}
