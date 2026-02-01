/**
 * A Max-Heap implementation for Freelancer objects.
 * This class enables the selection of the highest-priority freelancer
 * based on a composite score. It contains a secondary Hash Table to track
 * the array indices of elements, enabling removal of arbitrary nodes.
 */
public class MaximumHeap {

    private int capacity; // The maximum capacity of the heap.
    private int size; // The current number of active users stored in the heap.
    private Freelancer[] maxHeap; //The array for the heap.

    //Maps Freelancer IDs to their current index in the maxHeap array.
    private HashTable<Integer> positionTable;

    /**
     * Initializes the heap with a default capacity.
     */
    MaximumHeap() {
        this.capacity = 10000;
        this.size = 0;
        // Size + 1 because index 0 is left unused.
        this.maxHeap = new Freelancer[capacity + 1];
        this.positionTable = new HashTable<>();
    }

    /**
     * Inserts a new freelancer into the heap and maintains the max-heap property.
     * @param freelancer The freelancer entity to be added.
     */
    public void insert(Freelancer freelancer) {
        // Check for overflow and resize if necessary
        if (size == maxHeap.length - 1) {
            enlargeArray(maxHeap.length * 2 + 1);
        }
        // Place the new item at the end and percolate it up.
        int hole = ++size;
        maxHeap[hole] = freelancer;
        this.positionTable.put(freelancer.getID(), hole);
        percolateUp(hole);
    }

    /**
     * Finds the freelancer with the highest priority without removing it.
     * @return The top freelancer, or null if the heap is empty.
     */
    public Freelancer findMax() {
        if (size == 0) {
            return null;
        }
        return maxHeap[1];
    }

    /**
     * Removes and returns the freelancer with the highest priority.
     * @return The removed freelancer, or null if empty.
     */
    public Freelancer deleteMax() {
        if (size == 0) {
            return null;
        }

        Freelancer maxFreelancer = findMax();
        // Note the positions before removal
        this.positionTable.remove(maxFreelancer.getID());

        // Move the last element to the root and percolate down
        maxHeap[1] = maxHeap[size--];

        if (size > 0) {
            this.positionTable.put(maxHeap[1].getID(), 1);
            percolateDown(1);
        }
        return maxFreelancer;
    }

    /**
     * Removes a specific freelancer identified by their ID.
     * This operation utilizes the positionTable to
     * find the element's index, then restores heap properties.
     * @param freelancerID The unique ID of the freelancer to remove.
     * @return The removed object, or null if not found.
     */
    public Freelancer remove(String freelancerID) {
        Integer hole = this.positionTable.get(freelancerID);

        // Fail if the ID doesn't exist in our heap
        if (hole == null) {
            return null;
        }

        Freelancer removedFreelancer = maxHeap[hole];
        this.positionTable.remove(freelancerID);

        // Replace the hole with the last element in the heap
        Freelancer lastFreelancer = maxHeap[size--];

        // If we removed the last element, no reordering needed.
        if (hole == size + 1 || size == 0) {
            return removedFreelancer;
        }

        maxHeap[hole] = lastFreelancer;
        this.positionTable.put(lastFreelancer.getID(), hole);

        // Decide whether to percolate up or percolate down based on the new value's priority
        if (hole > 1 && compare(lastFreelancer, maxHeap[hole / 2]) > 0) {
            percolateUp(hole);
        } else {
            percolateDown(hole);
        }

        return removedFreelancer;
    }

    /**
     * Rearranges the heap by moving the element at hole down until heap is functional .
     * @param hole The index to start percolating down from.
     */
    private void percolateDown(int hole) {
        int child;
        Freelancer tmpFreelancer = maxHeap[hole];

        // Percolate down loop
        while (hole * 2 <= size) {
            child = hole * 2;
            // Pick the larger child
            if (child != size && compare(maxHeap[child + 1], maxHeap[child]) > 0) {
                child++;
            }
            // If child is larger than tmpFreelancer, swap and continue
            if (compare(maxHeap[child], tmpFreelancer) > 0) {
                Freelancer movedFreelancer = maxHeap[child];
                // Update the position map during the swap
                this.positionTable.put(movedFreelancer.getID(), hole);
                maxHeap[hole] = movedFreelancer;
                hole = child;
            } else {
                break;
            }
        }
        maxHeap[hole] = tmpFreelancer;
        // Final position table update
        this.positionTable.put(tmpFreelancer.getID(), hole);
    }

    /**
     * Rearranges the heap by moving the element at hole up until heap is functional again.
     * @param hole The index to start percolating up from.
     */
    private void percolateUp(int hole) {
        Freelancer tmpFreelancer = maxHeap[hole];
        // Compare with parent and swap if necessary
        while (hole > 1 && compare(tmpFreelancer, maxHeap[hole / 2]) > 0) {
            Freelancer parentFreelancer = maxHeap[hole / 2];
            // Update map for the parent being moved down
            this.positionTable.put(parentFreelancer.getID(), hole);
            maxHeap[hole] = parentFreelancer;
            hole = hole / 2;
        }

        maxHeap[hole] = tmpFreelancer;
        // Final position table update
        this.positionTable.put(tmpFreelancer.getID(), hole);
    }

    /**
     * Resizes the internal array when capacity is reached.
     * @param newSize The new capacity for the array.
     */
    private void enlargeArray(int newSize) {
        Freelancer[] oldHeap = maxHeap;
        maxHeap = new Freelancer[newSize];
        for (int i = 0; i < oldHeap.length; i++) {
            maxHeap[i] = oldHeap[i];
        }
    }

    /**
     * Comparison method for freelancers
     * Higher composite score is better. If scores are equal, lexicographically smaller ID wins.
     * @param firstFreelancer  first element
     * @param secondFreelancer second element
     * @return 1 if first > second, -1 if first < second.
     */
    private int compare(Freelancer firstFreelancer, Freelancer secondFreelancer) {
        if (firstFreelancer.getCompositeScore() < secondFreelancer.getCompositeScore()) {
            return -1;
        } else if (firstFreelancer.getCompositeScore() > secondFreelancer.getCompositeScore()) {
            return 1;
        } else {
            // Tie-breaker: Lower string ID is considered higher priority here
            if (compareString(firstFreelancer.getID(), secondFreelancer.getID()) < 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * Lexicographical string comparison helper.
     * @return -1 if first < second, 1 if first > second, 0 if equal.
     */
    private int compareString(String firstID, String secondID) {
        // Implementation of string comparison logic
        int firstLength = firstID.length();
        int secondLength = secondID.length();

        int minLength;

        if (firstLength < secondLength) {
            minLength = firstLength;
        } else {
            minLength = secondLength;
        }

        for (int i = 0; i < minLength; i++) {
            char firstCharacter = firstID.charAt(i);
            char secondCharacter = secondID.charAt(i);

            if (firstCharacter < secondCharacter) {
                return -1;
            }
            if (firstCharacter > secondCharacter) {
                return 1;
            }
        }

        if (firstLength < secondLength) {
            return -1;
        } else if (firstLength > secondLength) {
            return 1;
        }

        return 0;
    }
    /**
     * Returns the number of elements in heap.
     * @return The total number of entries.
     */
    public int getSize() {
        return size;
    }
}