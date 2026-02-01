import java.util.LinkedList;

/**
 * A generic Hash Table implementation using separate chaining to store User objects.
 * This class uses LinkedList to handle hash collisions.
 * @param <T> The type of User objects to be stored, extending the User class.
 */
public class UserTable<T extends User> {


    private int capacity; // The maximum capacity of the hash table.
    private int size; // The current number of active users stored in the table.
    private LinkedList<T>[] hashTable; // The hash table used for storage.


    /**
     * Initializes the table.
     */
    UserTable(){
        this.capacity = 200017;
        this.hashTable = new LinkedList[this.capacity];

        // Initializing every index with an empty list right now.
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = new LinkedList<>();
        }
        size = 0;
    }

    /**
     * Constructs a table with a specific capacity.
     * @param smallCapacity The desired size of the table.
     */
    UserTable(int smallCapacity){
        this.capacity = smallCapacity;
        this.hashTable = new LinkedList[this.capacity];
        // Filling it up to avoid null checks later on.
        for (int i = 0; i < capacity; i++) {
            hashTable[i] = new LinkedList<>();
        }
        size = 0;
    }

    /**
     * Inserts a user into the table.
     * Calculates the hash based on User ID and adds it to the corresponding bucket.
     * @param user The user object to be added.
     */
    public void put(T user){
        String userID = user.getID();
        // Converting ID to a hash code.
        int hashcode = hashcodeGenerator(userID);

        // Modulo operator to keep it within array bounds.
        int position = hashcode % capacity;

        // Take the list at that index and just add to the end.
        LinkedList<T> userList = hashTable[position];
        userList.add(user);
        size++;
    }

    /**
     * Pulls a user from the table based on their ID.
     * @param userID The unique identifier of the user to search for.
     * @return The User object if found, otherwise.
     */
    public T get(String userID){
        int hashcode = hashcodeGenerator(userID);
        int position = hashcode % capacity;
        LinkedList<T> userList = hashTable[position];

        // Search in linked list of a given index
        for (T user : userList) {
            if (user.getID().equals(userID)) {
                return user;
            }
        }
        // Returned null if we scanned the whole linked list and found nothing.
        return null;
    }

    /**
     * Removes a user from the table based on their ID.
     * @param userID The unique identifier of the user to be removed.
     */
    public void remove(String userID){
        T removedUser = null;
        int hashcode = hashcodeGenerator(userID);
        int position = hashcode % capacity;
        LinkedList<T> userList = hashTable[position];

        // Find the user reference.
        for (T user : userList) {
            if (user.getID().equals(userID)) {
                removedUser = user;
                break;
            }
        }

        // If we actually found someone, delete them now.
        if (removedUser != null){
            userList.remove(removedUser);
            size--;
        }
    }

    /**
     * Checks if a user with the specified ID exists in the table.
     * @param userID The ID to check.
     * @return true if the user exists, false otherwise.
     */
    public boolean containsID (String userID){
        int hashcode = hashcodeGenerator(userID);
        int position = hashcode % capacity;
        LinkedList<T> userList = hashTable[position];

        // Search in linked list of a given index
        for (T user : userList) {
            if (user.getID().equals(userID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the total number of users currently in the table.
     * @return The size of the table.
     */
    public int getSize(){
        return size;
    }

    /**
     * Generates a custom hash code for a given String ID.
     * Uses Horner's Method hash algorithm.
     * @param ID The string to hash.
     * @return A positive integer hash value.
     */
    public int hashcodeGenerator(String ID){
        int hashcode = 0;
        // Processing every char to get a unique number.
        for (int i = 0; i < ID.length() ; i++){
            hashcode = 31 * hashcode + ID.charAt(i);
        }

        // Integer overflow can result in MIN_VALUE.
        // so we are handling it manually to prevent negative array index errors.
        if (hashcode == Integer.MIN_VALUE) {
            return 0;
        }
        return Math.abs(hashcode);
    }

    /**
     * Retrieves all users from all buckets in the table.
     * @return A LinkedList containing all user objects.
     */
    public LinkedList<T> getAllUsers() {
        LinkedList<T> allUsersList = new LinkedList<>();
        // Iterating through the whole table.
        for (int i = 0; i < capacity; i++) {
            for (T user : hashTable[i]) {
                allUsersList.add(user);
            }
        }
        return allUsersList;
    }
}