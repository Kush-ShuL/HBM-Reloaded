package com.hbm.world.structure.dependencies.materials45;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class StructuralMaterials45Content {
 public static final SoundType PLATE_SOUND=new net.neoforged.neoforge.common.util.DeferredSoundType(1,1,()->HbmSounds.get("block.platemetalPlace"),()->HbmSounds.get("step.platemetal"),()->HbmSounds.get("block.platemetalPlace"),()->HbmSounds.get("step.platemetal"),()->HbmSounds.get("step.platemetal"));
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<PlatemetalBlock> PLATEMETAL=BLOCKS.register("platemetal",PlatemetalBlock::new);
 public static final DeferredBlock<UberConcreteBlock> CONCRETE=BLOCKS.register("concrete_super",UberConcreteBlock::new);
 public static final DeferredBlock<AshGlassBlock> ASH_GLASS=BLOCKS.register("glass_ash",AshGlassBlock::new);
 public static final DeferredItem<StructuralMaterialItem> PLATEMETAL_ITEM=ITEMS.register("platemetal",()->new StructuralMaterialItem(PLATEMETAL.get()));
 public static final DeferredItem<StructuralMaterialItem> CONCRETE_ITEM=ITEMS.register("concrete_super",()->new StructuralMaterialItem(CONCRETE.get()));
 public static final DeferredItem<BlockItem> ASH_GLASS_ITEM=ITEMS.registerSimpleBlockItem(ASH_GLASS);
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static List<ItemStack> plateStacks(){var stacks=new ArrayList<ItemStack>();for(int i=0;i<PlatemetalBlock.NAMES.size();i++)stacks.add(PLATEMETAL.get().stack(i,1));return List.copyOf(stacks);}
 public static List<ItemStack> creativeStacks(String tab){if(tab.equals("machine"))return List.of(new ItemStack(ASH_GLASS_ITEM.get()));if(!tab.equals("block"))return List.of();var stacks=new ArrayList<>(plateStacks());stacks.add(new ItemStack(CONCRETE_ITEM.get()));return List.copyOf(stacks);}
 private StructuralMaterials45Content(){}
}
