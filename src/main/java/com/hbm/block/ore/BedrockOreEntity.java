package com.hbm.block.ore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import com.hbm.fluid.FluidStack;
import com.hbm.fluid.HbmFluids;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmOres;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Non-ticking source resource/tier/acid/color/shape data, preserved independently of drill machines. */
public final class BedrockOreEntity extends BlockEntity {
    public static ToIntFunction<ItemStack> CLIENT_STACK_COLOR = ignored -> 0;
    public ItemStack resource = ItemStack.EMPTY;
    public FluidStack acidRequirement;
    public int tier, color, shape;

    public BedrockOreEntity(BlockPos pos, BlockState state) { super(HbmOres.BEDROCK_ENTITY.get(), pos, state); }
    public BedrockOreEntity setStyle(int color, int shape) { this.color = color; this.shape = shape; return this; }
    public void changedAndSync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    public List<Component> overlayLines() {
        List<Component> result = new ArrayList<>();
        if (!resource.isEmpty()) result.add(resource.getHoverName());
        result.add(Component.translatable("gui.hbm.bedrock_ore.tier", tier));
        if (acidRequirement != null) result.add(Component.translatable("gui.hbm.bedrock_ore.requires", acidRequirement.fill, acidRequirement.type.displayName()));
        return result;
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        if (!resource.isEmpty()) tag.put("resource", resource.save(lookup));
        if (acidRequirement != null) { tag.putInt("fluid", acidRequirement.type.getID()); tag.putInt("amount", acidRequirement.fill); }
        tag.putInt("tier", tier); tag.putInt("color", color); tag.putInt("shape", shape);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        resource = tag.contains("resource") ? ItemStack.parseOptional(lookup, tag.getCompound("resource")) : new ItemStack(HbmContent.item("powder_iron"));
        if (resource.isEmpty()) resource = new ItemStack(HbmContent.item("powder_iron"));
        var acid = HbmFluids.fromID(tag.getInt("fluid"));
        acidRequirement = acid == HbmFluids.NONE ? null : new FluidStack(acid, tag.getInt("amount"));
        tier = tag.getInt("tier"); color = tag.getInt("color"); shape = tag.getInt("shape");
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { return saveWithoutMetadata(lookup); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) {
        loadWithComponents(tag, lookup);
        if (color == 0) color = CLIENT_STACK_COLOR.applyAsInt(resource);
        requestModelDataUpdate();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    @Override public ModelData getModelData() { return ModelData.builder().with(BedrockOreModelData.SHAPE, shape).build(); }
}
