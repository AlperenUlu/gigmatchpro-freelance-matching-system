import java.util.LinkedList;

/**
 * A generic implementation of a Hash Table using separate chaining for collision resolution.
 * This class maps String keys to values of a generic type V.
 * @param <V> The type of values stored in the map.
 */
public class HashTable<V> {

    private int capacity; // The fixed size of the hash table array.
    private int size; // The current number of entries in the table.
    private LinkedList<Entry<V>>[] table; // The array of LinkedLists used to store entries.

    /**
     * Initializes the hash table with a hardcoded capacity.
     */
    public HashTable() {
        this.capacity = 50077; // Using a prime number for capacity to improve hash distribution
        this.table = new LinkedList[this.capacity];
        // Initialize every bucket with an empty LinkedList
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        this.size = 0;
    }
    /**
     * Initializes the hash table with a specified capacity.
     * Useful for smaller tables like a customer's personal blacklist.
     * @param capacity The initial size of the hash table array.
     */
    public HashTable(int capacity) {
        this.capacity = capacity;
        this.table = new LinkedList[this.capacity];
        // Initialize every bucket with an empty LinkedList to avoid null checks
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        this.size = 0;
    }

    /**
     * Associates the specified value with the specified key in the map.
     * If the key already exists, the old value is replaced.
     * @param key   The key to associate with the value.
     * @param value The value to be stored.
     */
    public void put(String key, V value) {
        int position = hashcodeGenerator(key) % capacity; // Calculate the index
        LinkedList<Entry<V>> entryList = table[position];

        // Check for existing key to update value
        for (Entry<V> entry : entryList) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        // If key does not exist, create a new entry and add it to the bucket
        Entry<V> newEntry = new Entry<>(key, value);
        entryList.add(newEntry);
        size++;
    }

    /**
     * Retrieves the value associated with a specific key.
     * @param key The key whose associated value is to be returned.
     * @return The value mapped to the key, or null if the key is not found.
     */
    public V get(String key) {
        int position = hashcodeGenerator(key) % capacity;
        LinkedList<Entry<V>> entryList = table[position];

        // Searching the key
        for (Entry<V> entry : entryList) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        // Key not found
        return null;
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key The key whose mapping is to be removed.
     */
    public void remove(String key) {
        int position = hashcodeGenerator(key) % capacity;
        LinkedList<Entry<V>> entryList = table[position];
        Entry<V> removedEntry = null;

        // Find the entry object
        for (Entry<V> entry : entryList) {
            if (entry.key.equals(key)) {
                removedEntry = entry;
                break;
            }
        }

        // Remove the entry only if it was found
        if (removedEntry != null) {
            entryList.remove(removedEntry);
            size--;
        }
    }

    /**
     * Checks if the map contains a mapping for the specified key.
     * @param key The key is to be tested whether it is present.
     * @return true if the key exists, false otherwise.
     */
    public boolean containsID(String key) {
        int position = hashcodeGenerator(key) % capacity;
        LinkedList<Entry<V>> entryList = table[position];

        // Check if key is present in the corresponding bucket
        for (Entry<V> entry : entryList) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the map contains no key-value mappings.
     * @return true if the map is empty, false otherwise.
     */
    public boolean isEmpty(){
        if(size == 0){
            return true;
        }
        return false;
    }

    /**
     * Returns the number of key-value mappings in this map.
     * @return The total number of entries.
     */
    public int getSize() {
        return size;
    }

    /**
     * Generates a hash code for a given String key using Horner's Method.
     * @param ID The input string to be hashed.
     * @return A non-negative integer hash code.
     */
    public int hashcodeGenerator(String ID) {
        int hashcode = 0;
        // Computing hash using 31 as a multiplier
        for (int i = 0; i < ID.length() ; i++){
            hashcode = 31 * hashcode + ID.charAt(i);
        }
        // Handling potential integer overflow resulting in MIN_VALUE
        if (hashcode == Integer.MIN_VALUE) {
            return 0;
        }
        return Math.abs(hashcode);
    }
    /**
     * Retrieves all values from all buckets in the table.
     * @return A LinkedList containing all user objects.
     */
    public LinkedList<V> getAllValues() {
        LinkedList<V> allValues = new LinkedList<>();
        for (int i = 0; i < capacity; i++) {
            for (Entry<V> entry : table[i]) {
                allValues.add(entry.value);
            }
        }
        return allValues;
    }
}