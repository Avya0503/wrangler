package io.cdap.wrangler.api.parser;

public class TimeDuration extends Token {
    private final long nanoseconds;

    public TimeDuration(String value) {
        super(value);
        this.nanoseconds = parseDuration(value);
    }

    private long parseDuration(String value) {
        value = value.toLowerCase();
        if (value.endsWith("ms")) return (long) (Double.parseDouble(value.replace("ms", "")) * 1_000_000);
        if (value.endsWith("s")) return (long) (Double.parseDouble(value.replace("s", "")) * 1_000_000_000);
        return Long.parseLong(value.replace("ms", "")); // Default
    }

    public long getNanoseconds() {
        return nanoseconds;
    }
}

