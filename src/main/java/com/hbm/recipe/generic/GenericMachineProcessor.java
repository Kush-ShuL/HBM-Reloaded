package com.hbm.recipe.generic;

import com.hbm.energy.EnergyHandler;
import com.hbm.fluid.FluidTank;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/** X5846 ModuleMachineBase, shared by the original generic-recipe machines. */
public class GenericMachineProcessor {
    public int index;
    public final EnergyHandler battery;
    public final Container inventory;
    public int[] inputSlots, outputSlots;
    public FluidTank[] inputTanks, outputTanks;
    public String recipeName = "null";
    public double progress;
    public boolean didProcess, markDirty, restrictedMode;
    private final Supplier<? extends List<? extends GenericMachineRecipe>> recipes;
    private final RandomSource random = RandomSource.create();
    private boolean oreProcessing;

    /** Extraction machines opt in; assembly and ordinary crafting keep exact ingredients. */
    public GenericMachineProcessor withOreProcessing() { oreProcessing = true; return this; }
    private boolean inputMatches(GenericIngredient ingredient, ItemStack stack, boolean ignoreCount) {
        return ingredient.matches(oreProcessing ? com.hbm.ore.RawOreContent.processingStack(stack) : stack, ignoreCount);
    }
    private double oreRate(GenericMachineRecipe recipe) {
        if (!oreProcessing) return 1D;
        int count = Math.min(recipe.inputItems().size(), inputSlots.length);
        var items = new ItemStack[count];
        var consumed = new int[count];
        for (int i = 0; i < count; i++) { items[i] = inventory.getItem(inputSlots[i]); consumed[i] = recipe.inputItems().get(i).count(); }
        return com.hbm.ore.OreProcessing.rate(items, consumed);
    }

    public GenericMachineProcessor(Container inventory, int inputStart, int inputCount,
            int outputStart, int outputCount, FluidTank[] inputs, FluidTank[] outputs,
            EnergyHandler battery, Supplier<? extends List<? extends GenericMachineRecipe>> recipes) {
        this.inventory = inventory;
        this.battery = battery;
        this.recipes = recipes;
        inputSlots = IntStream.range(inputStart, inputStart + inputCount).toArray();
        outputSlots = IntStream.range(outputStart, outputStart + outputCount).toArray();
        inputTanks = inputs;
        outputTanks = outputs;
    }

    /** An absent recipe leaves the last configured tank types intact, as in X5846. */
    public void setupTanks(GenericMachineRecipe recipe) {
        if (recipe == null) return;
        for (int i = 0; i < inputTanks.length; i++) {
            if (i < recipe.inputFluids().size()) inputTanks[i].conform(recipe.inputFluids().get(i));
            else inputTanks[i].resetTank();
        }
        for (int i = 0; i < outputTanks.length; i++) {
            if (i < recipe.outputFluids().size()) outputTanks[i].conform(recipe.outputFluids().get(i));
            else outputTanks[i].resetTank();
        }
    }

    public GenericMachineRecipe getRecipe() {
        for (var recipe : recipes.get()) if (recipe.name().equals(recipeName)) return recipe;
        return null;
    }
    public String getRecipeName() { return recipeName; }
    public void setRecipe(String name, boolean ror) { recipeName = name; restrictedMode = ror; }

    public void update(double speed, double power, boolean extraCondition, ItemStack blueprint) {
        var recipe = getRecipe();
        if (recipe != null && recipe.isPooled() && !recipe.isPartOfPool(Blueprints.pool(blueprint))) {
            didProcess = false;
            progress = 0D;
            recipeName = "null";
            return;
        }
        setupTanks(recipe);
        didProcess = false;
        markDirty = false;
        if (extraCondition && canProcess(recipe, speed, power)) {
            process(recipe, speed, power);
            didProcess = true;
        } else progress = 0D;
    }

    public boolean canProcess(GenericMachineRecipe recipe, double speed, double power) {
        if (recipe == null) return false;
        // The switch cancels this tick; new fluids are configured by the next update.
        if (!recipe.group().isEmpty() && inputSlots.length > 0 && !inventory.getItem(inputSlots[0]).isEmpty()) {
            var first = inventory.getItem(inputSlots[0]);
            for (var next : recipes.get()) {
                if (!next.group().equals(recipe.group()) || next.name().equals(recipeName) || next.inputItems().isEmpty()) continue;
                if (inputMatches(next.inputItems().getFirst(), first, true)) {
                    recipeName = next.name();
                    return false;
                }
            }
        }
        if (power != 1 && new java.math.BigDecimal(battery.getPowerExact()).compareTo(java.math.BigDecimal.valueOf(recipe.power() * power)) < 0) return false;
        if (power == 1 && battery.getPowerExact().compareTo(java.math.BigInteger.valueOf(recipe.power())) < 0) return false;
        return hasInput(recipe) && canFitOutput(recipe);
    }

    protected boolean hasInput(GenericMachineRecipe recipe) {
        for (int i = 0; i < Math.min(recipe.inputItems().size(), inputSlots.length); i++)
            if (!inputMatches(recipe.inputItems().get(i), inventory.getItem(inputSlots[i]), false)) return false;
        for (int i = 0; i < Math.min(recipe.inputFluids().size(), inputTanks.length); i++)
            if (inputTanks[i].getFill() < recipe.inputFluids().get(i).fill) return false;
        return true;
    }

    protected boolean canFitOutput(GenericMachineRecipe recipe) {
        double rate = oreRate(recipe);
        for (int i = 0; i < Math.min(recipe.outputItems().size(), outputSlots.length); i++) {
            var stack = inventory.getItem(outputSlots[i]);
            var output = recipe.outputItems().get(i);
            if (stack.isEmpty()) {
                for (var possibility : output.possibilities())
                    if (com.hbm.ore.OreProcessing.maximumCount(possibility.stack().getCount(), rate) > possibility.stack().getMaxStackSize()) return false;
                continue;
            }
            if (output.possibleMultiOutput()) return false;
            var single = output.single();
            if (single.isEmpty() || stack.getItem() != single.getItem()) return false;
            // Old getItemDamage includes enum variants; source did not compare NBT.
            if (metadata(stack) != metadata(single)) return false;
            if (stack.getCount() + com.hbm.ore.OreProcessing.maximumCount(single.getCount(), rate) > stack.getMaxStackSize()) return false;
        }
        for (int i = 0; i < Math.min(recipe.outputFluids().size(), outputTanks.length); i++)
            if ((long)com.hbm.ore.OreProcessing.maximumCount(recipe.outputFluids().get(i).fill, rate) + outputTanks[i].getFill() > outputTanks[i].getMaxFill()) return false;
        return true;
    }
    private static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue()); }

    public void process(GenericMachineRecipe recipe, double speed, double power) {
        if (restrictedMode) speed *= .25D;
        battery.setPowerExact(battery.getPowerExact().subtract(java.math.BigInteger.valueOf(power == 1 ? recipe.power() : (long) (recipe.power() * power))));
        progress += Math.min(speed / recipe.duration(), 1D);
        if (progress >= 1D) {
            double rate = oreRate(recipe);
            consumeInput(recipe);
            produceItem(recipe, rate);
            if (canProcess(recipe, speed, power)) progress -= 1D;
            else progress = 0D;
        }
    }
    protected void consumeInput(GenericMachineRecipe recipe) {
        for (int i = 0; i < Math.min(recipe.inputItems().size(), inputSlots.length); i++) {
            var stack = inventory.getItem(inputSlots[i]);
            stack.shrink(recipe.inputItems().get(i).count());
            if (stack.isEmpty()) inventory.setItem(inputSlots[i], ItemStack.EMPTY);
        }
        for (int i = 0; i < Math.min(recipe.inputFluids().size(), inputTanks.length); i++)
            inputTanks[i].setFill(inputTanks[i].getFill() - recipe.inputFluids().get(i).fill);
    }
    protected void produceItem(GenericMachineRecipe recipe) {
        produceItem(recipe, 1D);
    }
    protected void produceItem(GenericMachineRecipe recipe, double rate) {
        for (int i = 0; i < Math.min(recipe.outputItems().size(), outputSlots.length); i++) {
            var collapsed = com.hbm.ore.OreProcessing.output(recipe.outputItems().get(i).collapse(random), rate, random);
            var stack = inventory.getItem(outputSlots[i]);
            if (stack.isEmpty()) inventory.setItem(outputSlots[i], collapsed);
            else if (!collapsed.isEmpty()) stack.grow(collapsed.getCount());
        }
        for (int i = 0; i < Math.min(recipe.outputFluids().size(), outputTanks.length); i++)
            outputTanks[i].setFill(outputTanks[i].getFill() + com.hbm.ore.OreProcessing.count(recipe.outputFluids().get(i).fill, rate, random));
        markDirty = true;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        var recipe = getRecipe();
        if (recipe == null || recipe.inputItems().isEmpty()) return false;
        for (int i = 0; i < Math.min(inputSlots.length, recipe.inputItems().size()); i++)
            if (inputSlots[i] == slot && inputMatches(recipe.inputItems().get(i), stack, true)) return true;
        if (!recipe.group().isEmpty()) for (var next : recipes.get()) {
            if (next.group().equals(recipe.group()) && !next.inputItems().isEmpty()
                    && inputSlots.length > 0 && inputSlots[0] == slot && inputMatches(next.inputItems().getFirst(), stack, true)) return true;
        }
        return false;
    }
    public boolean isSlotClogged(int slot) {
        boolean input = false;
        for (int candidate : inputSlots) if (candidate == slot) input = true;
        return input && !inventory.getItem(slot).isEmpty() && !isItemValid(slot, inventory.getItem(slot));
    }
    public void serialize(FriendlyByteBuf buffer) { buffer.writeDouble(progress); buffer.writeBoolean(restrictedMode); buffer.writeUtf(recipeName); }
    public void deserialize(FriendlyByteBuf buffer) { progress = buffer.readDouble(); restrictedMode = buffer.readBoolean(); recipeName = buffer.readUtf(); }
    public void readFromNBT(CompoundTag tag) { progress = tag.getDouble("progress" + index); recipeName = tag.getString("recipe" + index); restrictedMode = tag.getBoolean("restrictedMode"); }
    public void writeToNBT(CompoundTag tag) { tag.putDouble("progress" + index, progress); tag.putString("recipe" + index, recipeName); tag.putBoolean("restrictedMode", restrictedMode); }
}
