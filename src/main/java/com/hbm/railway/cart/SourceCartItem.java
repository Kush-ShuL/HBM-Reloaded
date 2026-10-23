package com.hbm.railway.cart;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.storage.CrateStackData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
/** The actual five-kind/four-base ItemModMinecart and source click/dispenser coordinates. */
public final class SourceCartItem extends Item {
    public SourceCartItem(){super(new Properties().stacksTo(4));}
    public static SourceCartKind kind(ItemStack stack){return SourceCartKind.safe(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
    public static SourceCartKind.Base base(ItemStack stack){return SourceCartKind.Base.safe(CrateStackData.data(stack).getInt("cartBase"));}
    public static ItemStack stack(SourceCartKind.Base base,SourceCartKind kind){var stack=new ItemStack(SourceCartContent.CART.get());stack.set(HbmHazards.LEGACY_METADATA.get(),kind.ordinal());var tag=new net.minecraft.nbt.CompoundTag();tag.putInt("cartBase",base.ordinal());CrateStackData.data(stack,tag);return stack;}
    @Override public String getDescriptionId(ItemStack stack){return "item.cart."+kind(stack).key()+".name";}
    public static SourceCartEntity create(Level level,double x,double y,double z,ItemStack stack){
        SourceCartEntity entity=switch(kind(stack)){
            case EMPTY->new SourceEmptyCart(SourceCartContent.EMPTY.get(),level);
            case CRATE->new SourceCrateCart(SourceCartContent.CRATE.get(),level);
            case DESTROYER->new SourceDestroyerCart(SourceCartContent.DESTROYER.get(),level);
            case POWDER->new SourcePowderCart(SourceCartContent.POWDER.get(),level);
            case SEMTEX->new SourceSemtexCart(SourceCartContent.SEMTEX.get(),level);
        };
        entity.setPos(x,y,z);entity.setBase(base(stack));
        if(entity instanceof SourceCrateCart crate)crate.loadSourcePayload(stack);
        var name=stack.get(DataComponents.CUSTOM_NAME);if(name!=null)entity.setCustomName(name);
        return entity;
    }
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var pos=context.getClickedPos();if(!BaseRailBlock.isRail(level.getBlockState(pos)))return InteractionResult.PASS;
        var hit=context.getClickLocation();if(!SourceCartEntity.ready(level,new AABB(hit.x-2,hit.y-1,hit.z-2,hit.x+2,hit.y+1,hit.z+2)))return InteractionResult.FAIL;
        if(!level.isClientSide)level.addFreshEntity(create(level,hit.x,hit.y,hit.z,context.getItemInHand()));
        context.getItemInHand().shrink(1);return InteractionResult.sidedSuccess(level.isClientSide);
    }
    public static final class Dispense extends DefaultDispenseItemBehavior {
        private final DefaultDispenseItemBehavior fallback=new DefaultDispenseItemBehavior();
        @Override protected ItemStack execute(BlockSource source,ItemStack stack){
            var dir=source.state().getValue(DispenserBlock.FACING);var target=source.pos().relative(dir);var center=source.center();
            double x=center.x+dir.getStepX()*1.125,y=center.y+dir.getStepY()*1.125,z=center.z+dir.getStepZ()*1.125;
            if(!SourceCartEntity.ready(source.level(),new AABB(x-2,y-2,z-2,x+2,y+1,z+2)))return stack;
            double offset;
            if(BaseRailBlock.isRail(source.level().getBlockState(target)))offset=0;
            else {if(!source.level().getBlockState(target).isAir()||!BaseRailBlock.isRail(source.level().getBlockState(target.below())))return fallback.dispense(source,stack);offset=-1;}
            source.level().addFreshEntity(create(source.level(),x,y+offset,z,stack));stack.shrink(1);return stack;
        }
    }
}
