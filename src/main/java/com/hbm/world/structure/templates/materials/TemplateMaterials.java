package com.hbm.world.structure.templates.materials;

import com.hbm.block.LegacyStairsBlock;
import com.hbm.block.ore.*;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Necessary real source blocks from the active natural-template palettes. No functional machine placeholders. */
public final class TemplateMaterials {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> CONCRETE_TYPES=List.of("machine","machine_stripe","indigo","purple","pink","hazard","sand","bronze"),LIGHTSTONE_TYPES=List.of("unrefined","tile","bricks","bricks_chiseled","chiseled");
    public static final DeferredBlock<TemplateEnumBlock> CONCRETE=BLOCKS.register("concrete_colored_ext",()->new TemplateEnumBlock("concrete_colored_ext","BlockConcreteColoredExt",15,84,false,CONCRETE_TYPES));
    public static final DeferredBlock<TemplateEnumBlock> LIGHTSTONE=BLOCKS.register("lightstone",()->new TemplateEnumBlock("lightstone","BlockLightstone",2,9,true,LIGHTSTONE_TYPES));
    public static final DeferredBlock<DeadPlantBlock> DEAD_PLANT=BLOCKS.register("plant_dead",DeadPlantBlock::new);
    public static final DeferredBlock<LegacyOreBlock> TITANIUM=BLOCKS.register("deco_titanium",()->new LegacyOreBlock(new OreSpec("deco_titanium","BlockOre","iron","stone",5,6,0,true,true,null,null,0,1,0,"NONE",0,List.of(),"COMMON",false,0,false,false)));
    public static final DeferredBlock<LegacyStairsBlock> LIGHTSTONE_STAIRS=BLOCKS.register("lightstone_bricks_stairs",()->new LegacyStairsBlock(LIGHTSTONE.get().defaultBlockState().setValue(LegacyOreBlock.LEGACY_META,2),BlockBehaviour.Properties.ofFullCopy(LIGHTSTONE.get()).noOcclusion()));
    public static final DeferredBlock<TemplateSpikesBlock> SPIKES=BLOCKS.register("spikes",TemplateSpikesBlock::new);
    public static final DeferredBlock<TemplateAsphaltStairs> ASPHALT_STAIRS=BLOCKS.register("asphalt_stairs",TemplateAsphaltStairs::new);
    public static final DeferredBlock<TemplateReinforcedGlass> GLASS=BLOCKS.register("reinforced_glass",TemplateReinforcedGlass::new);
    public static final DeferredBlock<LegacyOreBlock> BERYLLIUM=BLOCKS.register("deco_beryllium",()->new LegacyOreBlock(new OreSpec("deco_beryllium","BlockDecoCT","iron","stone",5,6,0,true,true,null,null,0,1,0,"NONE",0,List.of(),"COMMON",false,0,false,false)));
    public static final DeferredItem<TemplateMaterialItem> CONCRETE_ITEM=ITEMS.register("concrete_colored_ext",()->new TemplateMaterialItem(CONCRETE.get(),true,true));
    public static final DeferredItem<TemplateMaterialItem> LIGHTSTONE_ITEM=ITEMS.register("lightstone",()->new TemplateMaterialItem(LIGHTSTONE.get(),false,false));
    public static final DeferredItem<TemplateMaterialItem> DEAD_PLANT_ITEM=ITEMS.register("plant_dead",()->new TemplateMaterialItem(DEAD_PLANT.get(),false,false));
    public static final DeferredItem<TemplateMaterialItem> TITANIUM_ITEM=ITEMS.register("deco_titanium",()->new TemplateMaterialItem(TITANIUM.get(),false,false));
    public static final DeferredItem<BlockItem> STAIRS_ITEM=ITEMS.registerSimpleBlockItem("lightstone_bricks_stairs",LIGHTSTONE_STAIRS);
    public static final DeferredItem<BlockItem> SPIKES_ITEM=ITEMS.registerSimpleBlockItem("spikes",SPIKES);
    public static final DeferredItem<BlockItem> ASPHALT_STAIRS_ITEM=ITEMS.register("asphalt_stairs",()->new BlockItem(ASPHALT_STAIRS.get(),new Item.Properties()){@Override public void appendHoverText(ItemStack stack,TooltipContext context,List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){lines.add(net.minecraft.network.chat.Component.translatable("tooltip.hbm.asphalt.speed").withStyle(net.minecraft.ChatFormatting.BLUE));}});
    public static final DeferredItem<BlockItem> GLASS_ITEM=ITEMS.register("reinforced_glass",()->new BlockItem(GLASS.get(),new Item.Properties()){@Override public void appendHoverText(ItemStack stack,TooltipContext context,List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){lines.add(net.minecraft.network.chat.Component.translatable("tooltip.hbm.meteor_materials.blast_resistance",15F).withStyle(net.minecraft.ChatFormatting.GOLD));}});
    public static final DeferredItem<TemplateMaterialItem> BERYLLIUM_ITEM=ITEMS.register("deco_beryllium",()->new TemplateMaterialItem(BERYLLIUM.get(),false,false));
    public static List<DeferredItem<TemplateMaterialItem>> enums(){return List.of(CONCRETE_ITEM,LIGHTSTONE_ITEM,DEAD_PLANT_ITEM);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("block"))return List.of();var result=new ArrayList<ItemStack>();for(var item:enums())result.addAll(item.get().creativeStacks());result.add(new ItemStack(TITANIUM_ITEM.get()));result.add(new ItemStack(STAIRS_ITEM.get()));result.add(new ItemStack(SPIKES_ITEM.get()));result.add(new ItemStack(ASPHALT_STAIRS_ITEM.get()));result.add(new ItemStack(GLASS_ITEM.get()));result.add(new ItemStack(BERYLLIUM_ITEM.get()));return result;}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    private TemplateMaterials(){}
}
