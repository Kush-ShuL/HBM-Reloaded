package com.hbm.machine.custommachine;
import com.google.gson.*;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import java.nio.file.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.fml.loading.FMLPaths;
/** Original anchor ->pos1 ->pos2 ->write/clearpositions loop; fixed path and real actual blockstate export. */
public final class CMStructureItem extends Item {
    public CMStructureItem(){super(new Properties().stacksTo(1));}
    public static CompoundTag data(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);return data==null?null:data.copyTag();}
    public static BlockPos anchor(ItemStack stack){var data=data(stack);return data==null?null:new BlockPos(data.getInt("anchorX"),data.getInt("anchorY"),data.getInt("anchorZ"));}
    @Override public InteractionResult useOn(UseOnContext context){var stack=context.getItemInHand();var level=context.getLevel();var pos=context.getClickedPos();var data=data(stack);if(level.getBlockState(pos).is(CustomMachineContent.ANCHOR.get())){if(data==null)data=new CompoundTag();data.putInt("anchorX",pos.getX());data.putInt("anchorY",pos.getY());data.putInt("anchorZ",pos.getZ());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));return InteractionResult.SUCCESS;}if(data==null)return InteractionResult.PASS;if(!data.contains("x1")){data.putInt("x1",pos.getX());data.putInt("y1",pos.getY());data.putInt("z1",pos.getZ());}else if(!data.contains("x2")){data.putInt("x2",pos.getX());data.putInt("y2",pos.getY());data.putInt("z2",pos.getZ());}else{if(!level.isClientSide)export(level,data);for(String key:List.of("x1","y1","z1","x2","y2","z2"))data.remove(key);}stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));return InteractionResult.SUCCESS;}
    private static void export(net.minecraft.world.level.Level level,CompoundTag data){int ax=data.getInt("anchorX"),ay=data.getInt("anchorY"),az=data.getInt("anchorZ");var dir=ForgeDirection.getOrientation(level.getBlockState(new BlockPos(ax,ay,az)).hasProperty(CMComponentBlock.META)?level.getBlockState(new BlockPos(ax,ay,az)).getValue(CMComponentBlock.META):CustomMachineStates.metadata(level.getBlockState(new BlockPos(ax,ay,az))));var components=new JsonArray();for(int x=Math.min(data.getInt("x1"),data.getInt("x2"));x<=Math.max(data.getInt("x1"),data.getInt("x2"));x++)for(int y=Math.min(data.getInt("y1"),data.getInt("y2"));y<=Math.max(data.getInt("y1"),data.getInt("y2"));y++)for(int z=Math.min(data.getInt("z1"),data.getInt("z2"));z<=Math.max(data.getInt("z1"),data.getInt("z2"));z++){if(x==ax&&y==ay&&z==az)continue;var state=level.getBlockState(new BlockPos(x,y,z));if(state.isAir())continue;int cx=0,cz=0;if(dir==ForgeDirection.SOUTH){cx=ax-x;cz=az-z;}if(dir==ForgeDirection.NORTH){cx=x-ax;cz=z-az;}if(dir==ForgeDirection.WEST){cz=x-ax;cx=az-z;}if(dir==ForgeDirection.EAST){cz=ax-x;cx=z-az;}var row=new JsonObject();row.addProperty("block",BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());row.addProperty("x",cx);row.addProperty("y",y-ay);row.addProperty("z",cz);var metas=new JsonArray();metas.add(CustomMachineStates.metadata(state));row.add("metas",metas);/* Native explicit properties preserve post1.7 states as well as the original metadata field. */row.add("properties",CustomMachineStates.exportProperties(state));components.add(row);}var root=new JsonObject();root.add("components",components);Path path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/CMstructureOutput.txt");try{Files.createDirectories(path.getParent());Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}catch(java.io.IOException error){com.mojang.logging.LogUtils.getLogger().error("Unable to export original custom-machine structure",error);}}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(int i=0;i<4;i++)lines.add(Component.translatable("item.hbm.custom_machine.structure."+i).withStyle(ChatFormatting.YELLOW));}
}
