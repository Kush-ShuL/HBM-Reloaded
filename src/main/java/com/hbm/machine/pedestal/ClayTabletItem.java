package com.hbm.machine.pedestal;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
/** Source once-generated persistent tabletSeed; the actual tablet opens its original concealed recipe screen. */
public final class ClayTabletItem extends Item {
    public interface Client{void open(InteractionHand hand);}
    public static Client CLIENT;
    public ClayTabletItem(){super(new Properties().stacksTo(1));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);boolean sourceTag=stack.has(DataComponents.CUSTOM_DATA)||stack.getComponentsPatch().entrySet().stream().anyMatch(entry->entry.getKey()!=com.hbm.hazard.HbmHazards.LEGACY_METADATA.get());if(!level.isClientSide&&!sourceTag)CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putLong("tabletSeed",player.getRandom().nextLong()));if(level.isClientSide&&CLIENT!=null)CLIENT.open(hand);return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
}
