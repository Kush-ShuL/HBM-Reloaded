package com.hbm.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** The original ItemCustomLore scrambling algorithm, including its timing. */
public final class UndefinedNameState {
    public static final String[] NAMES = {"THE DEFAULT", "NEXT ONE", "ANOTHER ONE", "NON-STANDARD NAME", "AMBIGUOUS TITLE", "SHORT"};
    private static final Random RANDOM = new Random();
    public static final ScramblingName NAME = new ScramblingName(NAMES[0]);

    private UndefinedNameState() {}

    public static final class ScramblingName {
        private String previous, next;
        private String[] previousFragments, nextFragments, fragments;
        private int[] mask;
        private int age;

        public ScramblingName(String initial) {
            previous = next = initial;
            fragments = initial.split("");
            mask = new int[fragments.length];
            previousFragments = chop(previous, fragments.length);
            nextFragments = chop(next, fragments.length);
        }

        public String getResult() { return String.join("", fragments); }

        public void updateTick(String[] names) {
            age++;
            if (age % 200 == 0) nextName(names);
            if (age % 5 == 0) scramble();
        }

        private void nextName(String[] names) {
            if (names.length < 2) return;
            previous = next;
            String initial = next;
            while (initial.equals(next)) next = names[RANDOM.nextInt(names.length)];
            int length = Math.min(previous.length(), next.length());
            previousFragments = chop(previous, length);
            fragments = chop(previous, length);
            nextFragments = chop(next, length);
            mask = new int[length];
        }

        private void scramble() {
            List<Integer> indices = new ArrayList<>();
            for (int index = 0; index < mask.length; index++) {
                int value = mask[index];
                if (value == 0) indices.add(index);
                if (value > 0 && value <= 5) mask[index]++;
                if (value > 5) fragments[index] = nextFragments[index];
            }
            if (!indices.isEmpty()) {
                int selected = indices.get(RANDOM.nextInt(indices.size()));
                mask[selected] = 1;
                fragments[selected] = "\u00a7k" + previousFragments[selected] + "\u00a7r";
            }
        }

        private static String[] chop(String name, int parts) {
            if (parts == name.length()) return name.split("");
            double index = 0, step = (double) name.length() / parts;
            List<String> slices = new ArrayList<>();
            for (int part = 0; part < parts; part++) {
                int end = part == parts - 1 ? name.length() : (int) (index + step);
                slices.add(name.substring((int) index, end));
                index += step;
            }
            return slices.toArray(String[]::new);
        }
    }
}
