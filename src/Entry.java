/**
 * Represents a specific key-value pair used within the Hash Table buckets.
 * @param <T> The type of the value stored in this entry.
 */
public class Entry<T> {

    String key; // The unique key for this entry which is used for hashing.
    T value;    // The actual value associated with the key.

    /**
     * Constructs a new Entry with the specified key and value.
     * @param key   The key string.
     * @param value The value associated with the key.
     */
    public Entry(String key, T value) {
        this.key = key;
        this.value = value;
    }
}


