package com.hbm.item.toolability;

import com.hbm.block.pipe.*;
import com.hbm.explosion.LegacyExplosionMath;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/** Original shuffled26-neighbor recursion and harvest scope, with real native block callbacks and permissions. */
public final class CobaltBreaks {
    private static final ThreadLocal<Context> ACTIVE=new ThreadLocal<>();
    private static final List<BlockPos> OFFSETS=new ArrayList<>();
    static{for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++)if(x!=0||y!=0||z!=0)OFFSETS.add(new BlockPos(x,y,z));}
    private record Context(ServerPlayer player,BlockPos root,boolean relocate){}
    public static void register(){NeoForge.EVENT_BUS.addListener(CobaltBreaks::drops);}
    public static boolean breakFromPlayer(ServerPlayer player,BlockPos root){
        if(ACTIVE.get()!=null)return false;var stack=player.getMainHandItem();if(!(stack.getItem() instanceof SourceAbilityTool tool)||!tool.canOperate(stack))return false;var world=player.serverLevel();var state=world.getBlockState(root);String name=BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();if(name.equals("stone_keyhole")||name.equals("stone_keyhole_meta")||!(tool.sourceCanHarvest(stack,state)||tool.sourceCanShear(player,stack,world,root)))return false;
        var preset=CobaltPresets.read(stack).active();var enchantments=world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);var enchantment=preset.harvest==1?enchantments.getOrThrow(Enchantments.SILK_TOUCH):preset.harvest==2?enchantments.getOrThrow(Enchantments.FORTUNE):null;
        int temporaryLevel=preset.harvest==2?AvailableAbilities.luckPower(preset.harvestLevel):1;
        boolean had=enchantment!=null&&EnchantmentHelper.getItemEnchantmentLevel(enchantment,stack)>0;
        if(enchantment!=null&&!had)EnchantmentHelper.updateEnchantments(stack,mutable->mutable.set(enchantment,temporaryLevel));
        ACTIVE.set(new Context(player,root.immutable(),preset.area()!=4));
        try{
            if(preset.area()==1&&!(state.is(Blocks.STONE)&&!CobaltToolConfig.STONE.get())&&!(state.is(Blocks.NETHERRACK)&&!CobaltToolConfig.NETHERRACK.get()))recurse(player,tool,root,root,0,AvailableAbilities.radius(preset.recursionLevel),new HashSet<>());
            boolean skip=false;
            if(preset.area()==2)hammer(player,tool,root,preset.recursionLevel,false);
            else if(preset.area()==3)skip=hammer(player,tool,root,preset.recursionLevel,true);
            else if(preset.area()==4){
                var explosion=new com.hbm.explosion.ExplosionNT(world,player,root.getX()+.5,root.getY()+.5,root.getZ()+.5,AvailableAbilities.explosionPower(preset.recursionLevel));
                explosion.addAttrib(com.hbm.explosion.ExplosionNT.ExAttrib.ALLDROP).addAttrib(com.hbm.explosion.ExplosionNT.ExAttrib.NOHURT).addAttrib(com.hbm.explosion.ExplosionNT.ExAttrib.NOPARTICLE);explosion.doExplosionA();explosion.doExplosionB(false);
                world.explode(player,root.getX()+.5,root.getY()+.5,root.getZ()+.5,.1F,net.minecraft.world.level.Level.ExplosionInteraction.NONE);skip=true;
            }
            if(!skip)breakExtra(player,tool,root,root);
        }finally{
            ACTIVE.remove();if(enchantment!=null){var held=player.getMainHandItem();if(!held.isEmpty())EnchantmentHelper.updateEnchantments(held,mutable->mutable.set(enchantment,held==stack&&had?temporaryLevel:0));}
        }
        return true;
    }
    private static boolean hammer(ServerPlayer player,SourceAbilityTool tool,BlockPos root,int level,boolean flat){
        int range=level+1,xRange=range,yRange=range,zRange=range;
        if(flat){
            // Original fixed1.62eye origin and original float trigonometry, with the player's actual native reach.
            float yaw=player.getYRot(),pitch=player.getXRot();float a=net.minecraft.util.Mth.cos(-yaw*.017453292F-(float)Math.PI),b=net.minecraft.util.Mth.sin(-yaw*.017453292F-(float)Math.PI),c=-net.minecraft.util.Mth.cos(-pitch*.017453292F),d=net.minecraft.util.Mth.sin(-pitch*.017453292F);
            var start=new net.minecraft.world.phys.Vec3(player.getX(),player.getY()+1.62D,player.getZ());var end=start.add(b*c*player.blockInteractionRange(),d*player.blockInteractionRange(),a*c*player.blockInteractionRange());
            var hit=player.serverLevel().clip(new net.minecraft.world.level.ClipContext(start,end,net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.NONE,player));if(hit.getType()!=net.minecraft.world.phys.HitResult.Type.BLOCK)return true;
            switch(hit.getDirection().getAxis()){case Y->yRange=0;case Z->zRange=0;case X->xRange=0;}
        }
        for(int x=root.getX()-xRange;x<=root.getX()+xRange;x++)for(int y=root.getY()-yRange;y<=root.getY()+yRange;y++)for(int z=root.getZ()-zRange;z<=root.getZ()+zRange;z++){
            var target=new BlockPos(x,y,z);if(!target.equals(root))breakExtra(player,tool,target,root);
        }return false;
    }
    private static ItemStack crystallize(ServerLevel world,BlockState state){
        var input=new ItemStack(state.getBlock());if(input.isEmpty())return ItemStack.EMPTY;
        int meta=LegacyExplosionMath.metadata(state);if(meta!=0&&BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals("hbm"))input.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),meta);
        var recipe=com.hbm.machine.crystallizer.CrystallizerRecipeConfig.find(world.getRecipeManager(),input,com.hbm.fluid.HbmFluids.fromName("PEROXIDE"));
        return recipe==null?ItemStack.EMPTY:recipe.getResultItem(world.registryAccess()).copy();
    }
    /** null means the original ordinary-harvest fallback; an actual nonnull empty output list still consumes the block. */
    private static List<ItemStack> processHarvest(ServerPlayer player,BlockState state,BlockPos target,int mode){
        var world=player.serverLevel();
        if(mode==3){
            // Block.getDrops(..., fortune0), not the held tool's Silk/Luck state. A failed conversion retains source RNG consumption before ordinary harvest.
            var drops=new ArrayList<>(Block.getDrops(state,world,target,world.getBlockEntity(target)));boolean converted=false;
            for(int i=0;i<drops.size();i++){
                var drop=drops.get(i).copy();var recipe=world.getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.SMELTING,new net.minecraft.world.item.crafting.SingleRecipeInput(drop),world);
                if(recipe.isPresent()){var result=recipe.get().value().getResultItem(world.registryAccess()).copy();if(!result.isEmpty()){result.setCount(result.getCount()*drop.getCount());drops.set(i,result);converted=true;}}
            }return converted?drops:null;
        }
        if(mode==7){
            // Modern lit/unlit redstone is one actual block identity. Only the two exact source blocks convert.
            int amount=state.is(Blocks.REDSTONE_ORE)?player.getRandom().nextInt(5)+4:state.is(Blocks.REDSTONE_BLOCK)?player.getRandom().nextInt(7)+8:0;
            return amount==0?null:List.of(new ItemStack(com.hbm.registry.HbmContent.item("ingot_mercury"),amount));
        }
        if(mode==6){var result=crystallize(world,state);return result.isEmpty()?null:List.of(result);}
        var input=sourceBlockStack(state);if(input.isEmpty())return null;
        if(mode==4){var result=com.hbm.machine.shredder.ShredderRecipes.result(world,input);return result.isEmpty()||result.getItem()==com.hbm.registry.HbmContent.item("scrap")?null:List.of(result.copy());}
        if(mode==5){
            var available=world.getRecipeManager().getAllRecipesFor(com.hbm.machine.centrifuge.CentrifugeContent.TYPE.get());
            var single=new net.minecraft.world.item.crafting.SingleRecipeInput(input);
            // Sole installed native recipes, same exact-ComparableStack-first then general AStack search as the real machine.
            for(boolean exact:new boolean[]{true,false})for(var holder:available){var recipe=holder.value();if((!exact||recipe.exactInput())&&recipe.matches(single,world))return recipe.outputs().stream().map(ItemStack::copy).toList();}
        }return null;
    }
    private static ItemStack sourceBlockStack(BlockState state){
        var input=new ItemStack(state.getBlock());if(input.isEmpty())return input;
        int meta=LegacyExplosionMath.metadata(state);if(meta!=0&&BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals("hbm"))input.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),meta);
        return input;
    }
    private static void recurse(ServerPlayer player,SourceAbilityTool tool,BlockPos pos,BlockPos root,int depth,int radius,Set<BlockPos> seen){
        var offsets=new ArrayList<>(OFFSETS);Collections.shuffle(offsets);
        for(var offset:offsets){var target=pos.offset(offset);int next=depth+1;if(seen.contains(target)||next>CobaltToolConfig.DEPTH.get())continue;seen.add(target);if(target.equals(root)||target.distSqr(root)>radius*radius)continue;var world=player.serverLevel();if(!same(world,root,target)||player.getMainHandItem().isEmpty())continue;breakExtra(player,tool,target,root);recurse(player,tool,target,root,next,radius,seen);}
    }
    private static boolean same(ServerLevel world,BlockPos first,BlockPos second){var a=world.getBlockState(first);var b=world.getBlockState(second);if(a.getBlock()!=b.getBlock())return false;if(world.getBlockEntity(first) instanceof NativePipeBlockEntity pipeA&&world.getBlockEntity(second) instanceof NativePipeBlockEntity pipeB&&pipeA.getFluidType()!=pipeB.getFluidType())return false;int metaA=a.getBlock() instanceof SpecialPipeBlock pipe?pipe.metadata(a):LegacyExplosionMath.metadata(a),metaB=b.getBlock() instanceof SpecialPipeBlock pipe?pipe.metadata(b):LegacyExplosionMath.metadata(b);if(metaA!=metaB)return false;
        for(var property:a.getProperties()){String name=property.getName();if(name.equals("legacy_meta")||name.equals("metadata"))return true;}
        for(var property:a.getProperties()){String name=property.getName();if(name.equals("waterlogged")||a.getBlock() instanceof LeavesBlock&&name.equals("distance")||a.getBlock() instanceof StairBlock&&name.equals("shape")||a.getBlock() instanceof RedStoneOreBlock&&name.equals("lit"))continue;
            boolean connections=a.getBlock() instanceof FenceBlock||a.getBlock() instanceof WallBlock||a.getBlock() instanceof IronBarsBlock||a.getBlock() instanceof RedStoneWireBlock||a.getBlock() instanceof com.hbm.block.cable.NativeCableBlock||a.getBlock() instanceof NativePipeBlock;
            if(connections&&Set.of("north","east","south","west","up","down").contains(name))continue;if(!a.getValue(property).equals(b.getValue(property)))return false;
        }return true;}
    private static void breakExtra(ServerPlayer player,SourceAbilityTool tool,BlockPos target,BlockPos root){
        var world=player.serverLevel();var state=world.getBlockState(target);var stack=player.getMainHandItem();if(state.isAir()||stack.isEmpty()||!(tool.sourceCanHarvest(stack,state)||tool.sourceCanShear(player,stack,world,target)))return;
        if(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath().equals("stone_keyhole"))return;
        float strength=state.getDestroyProgress(player,world,target),reference=world.getBlockState(root).getDestroyProgress(player,world,root);
        if(state.getDestroySpeed(world,target)==-1&&strength==0||reference/strength>10||reference<0||!state.canHarvestBlock(world,target,player))return;
        var preset=CobaltPresets.read(stack).active();
        if(preset.harvest>=3&&preset.harvest<=7){var results=processHarvest(player,state,target,preset.harvest);if(results!=null){
            // Source harvestBlock(true): real protection event, setAir, one old-strict wear; no ordinary drops, stats or XP.
            var event=new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(world,target,state,player);if(NeoForge.EVENT_BUS.post(event).isCanceled())return;
            world.removeBlock(target,false);com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,1,player);
            for(var result:results)if(!result.isEmpty())world.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(world,root.getX()+.5,root.getY()+.5,root.getZ()+.5,result.copy()));return;
        }}
        boolean removed=player.gameMode.destroyBlock(target);if(removed)world.levelEvent(player,2001,target,Block.getId(state));
        if(player.isCreative())player.connection.send(new ClientboundBlockUpdatePacket(world,target));
        else if(removed&&!world.getBlockState(target).is(state.getBlock()))player.causeFoodExhaustion(.02F); // Native harvest adds.005; original block.harvestBlock adds.025.
    }
    private static void drops(BlockDropsEvent event){var context=ACTIVE.get();if(context==null||!context.relocate||event.getBreaker()!=context.player)return;event.setDroppedExperience(0);var from=event.getPos();for(var drop:event.getDrops())drop.setPos(drop.getX()-from.getX()+context.root.getX(),drop.getY()-from.getY()+context.root.getY(),drop.getZ()-from.getZ()+context.root.getZ());}
    private CobaltBreaks(){}
}
