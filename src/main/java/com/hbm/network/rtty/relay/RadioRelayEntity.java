package com.hbm.network.rtty.relay;

import com.hbm.network.rtty.autocal.script.Calculator;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class RadioRelayEntity extends BlockEntity {
    public boolean forceUpdate;
    public boolean polling;
    public String channel = "";
    public String expression = "";
    public CompoundTag variables = new CompoundTag();
    public double lastState;

    public RadioRelayEntity(BlockPos pos, BlockState state) { super(RadioRelayContent.ENTITY.get(), pos, state); }

    public static void tick(Level level, BlockPos pos, BlockState state, RadioRelayEntity relay) {
        if (level instanceof ServerLevel server) relay.tickServer(server);
    }

    private void tickServer(ServerLevel server) {
        if (expression.isEmpty() || channel.isEmpty()) return;
        boolean update = forceUpdate || polling && variables.isEmpty();
        for (String frequency : variables.getAllKeys()) {
            var signal = RTTYSystem.listen(server, frequency);
            if (signal == null) continue;
            String received = String.valueOf(signal.signal);
            if (polling || forceUpdate || !received.equals(variables.getString(frequency))) {
                if (polling && signal.timeStamp < server.getGameTime() - 1) received = "0";
                variables.putString(frequency, received);
                update = true;
            }
        }
        if (!update) return;
        forceUpdate = false;
        try {
            lastState = Calculator.evaluateExpression(generateExpression());
            RTTYSystem.broadcast(server, channel, Double.toString(lastState));
        } catch (RuntimeException invalidExpression) {
            // Expressions are player input; an incomplete edit must not stop the server tick.
            lastState = Double.NaN;
        }
        setChanged();
        server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    public void setChannel(String value) {
        if (!channel.equals(value)) { channel = value; forceUpdate = true; }
    }

    public void setExpression(String value) {
        if (expression.equals(value)) return;
        expression = value;
        forceUpdate = true;
        variables = new CompoundTag();
        for (String name : getVariablesFromExpression()) variables.putString(name, "");
    }

    public List<String> getVariablesFromExpression() {
        var names = new ArrayList<String>();
        int from = expression.indexOf('$');
        while (from >= 0) {
            int to = expression.indexOf('$', from + 1);
            if (to < 0) break;
            names.add(expression.substring(from + 1, to));
            from = expression.indexOf('$', to + 1);
        }
        return names;
    }

    public String generateExpression() {
        var result = new StringBuilder();
        int start = 0;
        for (int from = expression.indexOf('$'); from >= 0; from = expression.indexOf('$', start)) {
            result.append(expression, start, from);
            int to = expression.indexOf('$', from + 1);
            if (to < 0) return result.toString();
            String value = variables.getString(expression.substring(from + 1, to));
            result.append(value.isEmpty() ? "0" : value);
            start = to + 1;
        }
        return result.append(expression, start, expression.length()).toString();
    }

    public void control(CompoundTag data) {
        if (data.contains("p")) { polling = data.getBoolean("p"); forceUpdate = true; }
        if (data.contains("e")) setExpression(data.getString("e"));
        if (data.contains("c")) setChannel(data.getString("c"));
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    private void save(CompoundTag tag) {
        tag.putBoolean("p", polling);
        tag.putString("c", channel);
        tag.putString("e", expression);
        tag.putDouble("l", lastState);
        tag.put("v", variables.copy());
    }

    private void load(CompoundTag tag) {
        polling = tag.getBoolean("p");
        channel = tag.getString("c");
        expression = tag.getString("e");
        lastState = tag.getDouble("l");
        variables = tag.getCompound("v").copy();
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.saveAdditional(tag, lookup); save(tag); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.loadAdditional(tag, lookup); load(tag); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag = new CompoundTag(); save(tag); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { load(tag); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
}
