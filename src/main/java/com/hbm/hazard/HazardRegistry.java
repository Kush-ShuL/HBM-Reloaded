package com.hbm.hazard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.item.ItemStack;

/** Ordered ore-tag -> item -> metadata evaluation with the source mutex rules. */
public final class HazardRegistry {
    private final Map<String, HazardData> tags = new HashMap<>();
    private final Map<String, HazardData> items = new HashMap<>();
    private final Map<StackKey, HazardData> stacks = new HashMap<>();
    private final Set<String> tagBlacklist = new HashSet<>();
    private final Set<StackKey> stackBlacklist = new HashSet<>();
    private final List<HazardTransformer> transformers = new ArrayList<>();

    public HazardRegistry() {
        transformers.add(new HazardTransformer() {
            @Override
            public void transformPost(HazardStack stack, List<HazardEntry> entries) {
                if (stack.extraRadiation().isPresent()) {
                    entries.add(new HazardEntry(HazardType.RADIATION, (float) stack.extraRadiation().getAsDouble()));
                }
            }
        });
    }

    public void registerTag(String tag, HazardData data) { tags.put(tag, data); }
    public void registerItem(String item, HazardData data) { items.put(item, data); }
    public void registerStack(String item, int metadata, HazardData data) { stacks.put(new StackKey(item, metadata), data); }
    public void blacklistTag(String tag) { tagBlacklist.add(tag); }
    public void blacklistStack(String item, int metadata) { stackBlacklist.add(new StackKey(item, metadata)); }
    public void addTransformer(HazardTransformer transformer) { transformers.add(transformer); }
    public int tagDefinitions() { return tags.size(); }
    public int itemDefinitions() { return items.size(); }
    public int stackDefinitions() { return stacks.size(); }

    public HazardEvaluation evaluate(ItemStack stack) {
        if (stack.isEmpty()) return new HazardEvaluation(List.of(), false);
        return evaluate(HazardStack.from(stack));
    }

    public HazardEvaluation evaluate(HazardStack stack) {
        boolean blacklisted = isBlacklisted(stack);
        return new HazardEvaluation(entries(stack).stream().map(entry -> entry.evaluate(stack)).toList(), blacklisted);
    }

    public boolean isBlacklisted(HazardStack stack) {
        var key = new StackKey(stack.item(), stack.metadata());
        return stackBlacklist.contains(key) || stack.orderedTags().stream().anyMatch(tagBlacklist::contains);
    }

    /** Raw source entries allow behavior to evaluate modifiers immediately before each effect. */
    public List<HazardEntry> entries(HazardStack stack) {
        if (isBlacklisted(stack)) return List.of();
        var key = new StackKey(stack.item(), stack.metadata());
        List<HazardData> chronological = new ArrayList<>();
        for (var tag : stack.orderedTags()) {
            var data = tags.get(tag);
            if (data != null) chronological.add(data);
        }
        HazardData item = items.get(stack.item());
        if (item != null) chronological.add(item);
        HazardData specific = stacks.get(key);
        if (specific != null) chronological.add(specific);
        List<HazardEntry> entries = new ArrayList<>();
        for (var transformer : transformers) transformer.transformPre(stack, entries);
        int mutex = 0;
        for (var data : chronological) {
            // Source does not reset accumulated mutex bits when clearing entries.
            if (data.doesOverride()) entries.clear();
            if ((data.mutex() & mutex) == 0) {
                entries.addAll(data.entries());
                mutex |= data.mutex();
            }
        }
        for (var transformer : transformers) transformer.transformPost(stack, entries);
        return List.copyOf(entries);
    }

    private record StackKey(String item, int metadata) {}
}
