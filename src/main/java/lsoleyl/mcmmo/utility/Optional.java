package lsoleyl.mcmmo.utility;

/** Custom optional implementation for backward compatibility with the stone age java version required here
 */
public class Optional<T> {
    T value;
    boolean hasValue;

    private Optional(T value) {
        this.value = value;
        this.hasValue = true;
    }

    private Optional() {
        this.value = null;
        this.hasValue = false;
    }

    public boolean isPresent() {
        return hasValue;
    }

    public T get() {
        return value;
    }


    public static <T> Optional<T> of(T value) {
        return new Optional<T>(value);
    }

    public static <T> Optional<T> empty() {
        return new Optional<T>();
    }
}
