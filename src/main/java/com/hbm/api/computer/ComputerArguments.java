package com.hbm.api.computer;

/** Detached Lua values, safe to read after a call is scheduled on the server thread. */
public final class ComputerArguments {
    private final Object[] values;

    public ComputerArguments(Object[] values) {
        this.values = values.clone();
    }

    public int count() {
        return values.length;
    }

    public Object get(int index) {
        return index < 0 || index >= values.length ? null : values[index];
    }

    public boolean checkBoolean(int index) {
        if (get(index) instanceof Boolean value) return value;
        throw argumentError(index, "boolean");
    }

    public String checkString(int index) {
        if (get(index) instanceof String value) return value;
        throw argumentError(index, "string");
    }

    public double checkDouble(int index) {
        if (get(index) instanceof Number value && Double.isFinite(value.doubleValue())) return value.doubleValue();
        throw argumentError(index, "finite number");
    }

    public int checkInteger(int index) {
        if (get(index) instanceof Number value) {
            // Original OC integers truncate fractions and saturate floating/long overflow.
            if (value instanceof Double || value instanceof Float) {
                double number = value.doubleValue();
                if (Double.isNaN(number)) throw argumentError(index, "integer");
                return (int) number;
            }
            if (value instanceof Long) {
                return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, value.longValue()));
            }
            return value.intValue();
        }
        throw argumentError(index, "integer");
    }

    public long checkLong(int index) {
        if (get(index) instanceof Byte || get(index) instanceof Short || get(index) instanceof Integer || get(index) instanceof Long) {
            return ((Number) get(index)).longValue();
        }
        double value = checkDouble(index);
        if (value != Math.rint(value) || value < -0x1.0p63 || value >= 0x1.0p63) throw argumentError(index, "long integer");
        return (long) value;
    }

    private ComputerException argumentError(int index, String expected) {
        return new ComputerException("bad argument #" + (index + 1) + " (" + expected + " expected)");
    }
}
