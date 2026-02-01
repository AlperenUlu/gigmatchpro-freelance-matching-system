/**
 * Represents a client in the system who hires freelancers.
 * Implements the User interface. This class manages personal information
 * such as spending, loyalty points, and a personalized blacklist to filter out
 * blacklisted service providers using a custom Hash Table structure.
 */
public class Customer implements User {

    private final String customerID;

    //A personal hash table to store blocked freelancers.
    private UserTable<Freelancer> blacklistedHashTable;

    private int blacklistedCounter; // Tracks the number of blocked users
    private int totalEmployments;   // Total number of jobs given to freelancers
    private int loyaltyPoints;      // Award system based on previous spending
    private int totalSpending;      // Total financial spending
    private int cancelledRequests;  // Number of cancelled jobs

    /**
     * Constructs a new Customer with a unique ID.
     * @param customerID The unique string identifier.
     */
    Customer(String customerID){
        this.customerID = customerID;
        this.blacklistedHashTable = new UserTable<>(97);
        this.blacklistedCounter = 0;
        this.totalEmployments = 0;
        this.loyaltyPoints = 0;
        this.totalSpending = 0;
        this.cancelledRequests = 0;

    }

    /**
     * @return The customer ID string.
     */
    public String getID() {
        return customerID;
    }

    /**
     * Adds a specific freelancer to this customer's personal blacklist.
     * This prevents the freelancer from being matched with this customer in future queries.
     * The method pulls the freelancer object from the main table and stores it in the object itself.
     * @param freelancerID        The ID of the freelancer to block.
     * @param freelancerHashTable The main data source containing all system freelancers.
     */
    public void blacklistFreelancer(String freelancerID , UserTable<Freelancer> freelancerHashTable){
        // Retrieve the Freelancer object reference from the hash table
        Freelancer blacklistedFreelancer = (Freelancer) freelancerHashTable.get(freelancerID);
        // Insert into the local blacklist table
        blacklistedHashTable.put(blacklistedFreelancer);
        blacklistedCounter++;
    }

    /**
     * Removes a freelancer from the blacklist, allowing them to be hired again.
     * @param freelancerID The ID of the freelancer to unblock.
     */
    public void unblacklistFreelancer(String freelancerID){
        // Remove from the custom hash table
        blacklistedHashTable.remove(freelancerID);
        blacklistedCounter--;
    }

    /**
     * Getter for the blacklist list.
     * @return The UserTable containing blocked freelancers.
     */
    public UserTable<Freelancer> getBlacklistedHashTable() {
        return blacklistedHashTable;
    }

    /**
     * Sets the total number of freelancers this customer has employed.
     * @param totalEmployments The total count of employments.
     */
    public void setTotalEmployments(int totalEmployments) {
        this.totalEmployments = totalEmployments;
    }

    /**
     * Returns the total number of employments of this customer.
     * @return The total count of employments.
     */
    public int getTotalEmployments() {
        return totalEmployments;
    }

    /**
     * Sets the customer's current loyalty points.
     * @param loyaltyPoints The new loyalty points value.
     */
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    /**
     * Sets the total amount of money spent by the customer.
     * @param totalSpending The total spending amount.
     */
    public void setTotalSpending(int totalSpending) {
        this.totalSpending = totalSpending;
    }

    /**
     * Returns the count of freelancers currently on the blacklist.
     * @return The number of blacklisted freelancers.
     */
    public int getBlacklistedCounter() {
        return blacklistedCounter;
    }

    /**
     * Returns the customer's current loyalty points.
     * @return The loyalty points.
     */
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     * Returns the total amount of money spent by the customer.
     * @return The total spending amount.
     */
    public int getTotalSpending() {
        return totalSpending;
    }

    /**
     * Sets the total number of job requests cancelled by the customer.
     * @param cancelledRequests The new cancellation count.
     */
    public void setCancelledRequests(int cancelledRequests) {
        this.cancelledRequests = cancelledRequests;
    }

    /**
     * Returns the total number of job requests cancelled by the customer.
     * @return The cancellation count.
     */
    public int getCancelledRequests() {
        return cancelledRequests;
    }
}