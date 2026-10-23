package com.hbm.advancement;

import com.hbm.block.fluid.WorldFluidHooks;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.anvil.AnvilCraftingHooks;
import com.hbm.recipe.generic.GenericOutputSlot;
import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

/** Native replacements of AchievementHandler.fire and direct triggerAchievement calls. Parent gating is source-strict. */
public final class HbmAdvancements {
    private static final DeferredRegister<net.minecraft.advancements.CriterionTrigger<?>> TRIGGERS=DeferredRegister.create(Registries.TRIGGER_TYPE,"hbm");
    public static final DeferredHolder<net.minecraft.advancements.CriterionTrigger<?>,SourceAchievementTrigger> SOURCE=TRIGGERS.register("source_achievement",SourceAchievementTrigger::new);
    private static boolean registered;
    public static void register(IEventBus bus){
        if(registered)return;registered=true;TRIGGERS.register(bus);
        GenericOutputSlot.registerAchievementHook(HbmAdvancements::crafted);
        AnvilCraftingHooks.register(HbmAdvancements::crafted);
        var previous=WorldFluidHooks.sulfuricAchievement;
        WorldFluidHooks.sulfuricAchievement=player->{previous.accept(player);if(player instanceof ServerPlayer server)trigger(server,"sulfuric");};
        NeoForge.EVENT_BUS.register(new SourceAchievementEvents());
    }
    /** Source symbols, source local names and modern hbm IDs resolve to the same unique definition. */
    public static boolean trigger(ServerPlayer player,String sourceName){
        var node=SourceAchievementData.node(sourceName);if(node==null||!node.available())return false;
        var id=SourceAchievementData.id(node);var holder=player.server.getAdvancements().get(id);if(holder==null||!mayAward(player.getAdvancements(),player,holder))return false;
        boolean before=player.getAdvancements().getOrStartProgress(holder).isDone();
        SOURCE.get().fire(player,id);
        return !before&&player.getAdvancements().getOrStartProgress(holder).isDone();
    }
    /** Applies to existing owner calls to PlayerAdvancements.award as well as this new real criterion. */
    public static boolean mayAward(PlayerAdvancements progress,ServerPlayer player,AdvancementHolder holder){
        var node=SourceAchievementData.node(holder.id());if(node==null||node.parent()==null)return true;
        var parent=player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath("hbm",node.parent()));
        return parent!=null&&progress.getOrStartProgress(parent).isDone();
    }
    public static void crafted(Player player,ItemStack stack){
        if(!(player instanceof ServerPlayer server)||stack.isEmpty())return;
        var id=BuiltInRegistries.ITEM.getKey(stack.getItem());
        for(var binding:SourceAchievementData.crafting(id)){
            // ComparableStack.makeSingular ignores count/NBT but keeps exact source itemDamage.
            int meta=binding.damage()?stack.getDamageValue():stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());
            if(meta==binding.metadata())trigger(server,binding.achievement());
        }
    }
    /** Called at the actual setter, after its real immunity/health logic; not a new independent disease/timer table. */
    public static void digammaChanged(net.minecraft.world.entity.LivingEntity entity,float dose){
        if(!(entity instanceof ServerPlayer player)||player.isSpectator())return;
        if(dose>0)trigger(player,"digammaSee");
        if(dose>=2)trigger(player,"digammaFeel");
        if(dose>=10)trigger(player,"digammaKnow");
    }
    public static void radiationPoison(net.minecraft.world.entity.LivingEntity entity){if(entity instanceof ServerPlayer player)trigger(player,"radPoison");}
    public static void radiationDeath(net.minecraft.world.entity.LivingEntity entity){if(entity instanceof ServerPlayer player&&entity.getHealth()<=0)trigger(player,"radDeath");}
    /** True source AABB recipient rules, not a sphere or killer-only award. */
    public static void around(Level level,AABB sourceBox,String achievement){if(level instanceof ServerLevel server)for(var player:server.getPlayers(player->player.getBoundingBox().intersects(sourceBox)))trigger(player,achievement);}
    public static void allInDimension(Level level,String achievement){if(level instanceof ServerLevel server)for(var player:server.players())trigger(player,achievement);}
    public static void meltdown(Level level,net.minecraft.core.BlockPos pos,String sourceName,double radius){around(level,new AABB(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5).inflate(radius),sourceName);}
    /** Only actual source EntityBombletZeta, named entity_zeta; no generic explosive/fire substitute. */
    public static void zetaIgnited(Level level,net.minecraft.core.BlockPos sourcePos,net.minecraft.world.level.Explosion explosion){
        var source=explosion.getDirectSourceEntity();if(source!=null&&BuiltInRegistries.ENTITY_TYPE.getKey(source.getType()).equals(ResourceLocation.parse("hbm:entity_zeta")))meltdown(level,sourcePos,"inferno",100);
    }
    private HbmAdvancements(){}
}
