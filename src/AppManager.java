import java.util.LinkedList;

/**
 * The main controller class for the "Armut" Application.
 * This class acts as the center for managing users, processing job requests
 * by heap implemented priority queue, handling payments,
 * and simulating monthly events such as service change and skill update.
 */
public class AppManager {

    //Stores all registered customers using a Hash Table.
    private UserTable<Customer> customerHashTable = new UserTable<>();

    //Stores all registered freelancers using a Hash Table.
    private UserTable<Freelancer> freelancerHashTable = new UserTable<>();

    //Requirements for each service type.
    private int[][] serviceTypeArray = {
            {70, 60, 50, 85, 90}, // paint
            {95, 75, 85, 80, 90}, // web_dev
            {75, 85, 95, 70, 85}, // graphic_design
            {50, 50, 30, 95, 95}, // data_entry
            {80, 95, 70, 90, 75}, // tutoring
            {40, 60, 40, 90, 85}, // cleaning
            {70, 85, 90, 80, 95}, // writing
            {85, 80, 90, 75, 90}, // photography
            {85, 65, 60, 90, 85}, // plumbing
            {90, 65, 70, 95, 95}  // electrical
    };


    // Array of max heaps, one for each service category.
    // Index corresponds to the service ID (for example 0 for paint).
    private MaximumHeap[] servicePriorityQueue = new MaximumHeap[10];

    //Queue to store freelancers pending a service type change until the end of the month.
    LinkedList<Freelancer> changedService = new LinkedList<>();

    /**
     * Constructor initializes the priority queues for all 10 service types.
     */
    AppManager(){
        for (int i = 0; i < servicePriorityQueue.length; i++) {
            servicePriorityQueue[i] = new MaximumHeap();
        }
    }

    /**
     * Registers a new customer into the system.
     * @param customerID The unique identifier for the customer.
     * @return Success or error message.
     */
    public String registerCustomer(String customerID){
        // Check if both Customer and Freelancer is unique
        if(customerHashTable.containsID(customerID)||freelancerHashTable.containsID(customerID)){
            return "Some error occurred in register_customer.";
        }
        else{
            Customer newCustomer = new Customer(customerID);
            customerHashTable.put(newCustomer);
            return "registered customer " + customerID;
        }
    }

    /**
     * Registers a new freelancer, calculates their initial score, and adds them to the heap.
     * @param freelancerID Unique ID
     * @param serviceType  The service provided
     * @param servicePrice Cost per job
     * @param T            Technical Proficiency
     * @param C            Communication
     * @param R            Reliability
     * @param E            Efficiency
     * @param A            Attention to Detail
     * @return Success or error message.
     */
    public String registerFreelancer(String freelancerID, String serviceType, int servicePrice,
                                     int T , int C, int R ,int E ,int A){
        // Check if both customer and freelancer is unique
        if(customerHashTable.containsID(freelancerID)||freelancerHashTable.containsID(freelancerID)){
            return "Some error occurred in register_freelancer.";
        }
        // Check positive price
        if (servicePrice <= 0) {
            return "Some error occurred in register_freelancer.";
        }
        // Check skill ranges
        if (T < 0 || T > 100 || C < 0 || C > 100 || R < 0 || R > 100 || E < 0 || E > 100 || A < 0 || A > 100) {
            return "Some error occurred in register_freelancer.";
        }

        // Check if service type valid.
        int serviceIndex = getServiceIndex(serviceType);
        if (serviceIndex == -1) {
            return "Some error occurred in register_freelancer.";
        }
        else{
            Freelancer newFreelancer = new Freelancer(freelancerID,serviceType,servicePrice,T,C,R,E,A);
            int[] serviceSkillArray = null;
            serviceIndex = 0;

            // Determine required skill set based on service type
            switch (serviceType) {
                case "paint":
                    serviceSkillArray = serviceTypeArray[0];
                    serviceIndex = 0;
                    break;
                case "web_dev":
                    serviceSkillArray = serviceTypeArray[1];
                    serviceIndex = 1;
                    break;
                case "graphic_design":
                    serviceSkillArray = serviceTypeArray[2];
                    serviceIndex = 2;
                    break;
                case "data_entry":
                    serviceSkillArray = serviceTypeArray[3];
                    serviceIndex = 3;
                    break;
                case "tutoring":
                    serviceSkillArray = serviceTypeArray[4];
                    serviceIndex = 4;
                    break;
                case "cleaning":
                    serviceSkillArray = serviceTypeArray[5];
                    serviceIndex = 5;
                    break;
                case "writing":
                    serviceSkillArray = serviceTypeArray[6];
                    serviceIndex = 6;
                    break;
                case "photography":
                    serviceSkillArray = serviceTypeArray[7];
                    serviceIndex = 7;
                    break;
                case "plumbing":
                    serviceSkillArray = serviceTypeArray[8];
                    serviceIndex = 8;
                    break;
                case "electrical":
                    serviceSkillArray =serviceTypeArray[9];
                    serviceIndex = 9;
                    break;
            }

            // Calculate score and insert into hash table and binary heap to easily access
            int compositeScore = newFreelancer.findCompositeScore(serviceSkillArray);
            newFreelancer.setCompositeScore(compositeScore);

            freelancerHashTable.put(newFreelancer);
            servicePriorityQueue[serviceIndex].insert(newFreelancer);

            return "registered freelancer " + freelancerID;
        }
    }

    /**
     * Adds a freelancer to a customer's personal blacklist.
     * @param customerID The ID of the customer performing the action.
     * @param freelancerID The ID of the freelancer to be blocked.
     * @return A status string indicating the result of the blacklist operation.
     */
    public String blacklist(String customerID , String freelancerID){
        // Check if both users exist in the system
        if(!freelancerHashTable.containsID(freelancerID)|| !customerHashTable.containsID(customerID)){
            return "Some error occurred in blacklist.";
        }
        else{
            Customer customer = (Customer) customerHashTable.get(customerID);
            UserTable<Freelancer> blacklistedHashTable = customer.getBlacklistedHashTable();

            // Prevent duplicate blacklist entries
            if (blacklistedHashTable.containsID(freelancerID)){
                return "Some error occurred in blacklist.";
            }
            else{
                // Put freelancer to blacklist hash table
                customer.blacklistFreelancer(freelancerID,freelancerHashTable);
                return customerID + " blacklisted " +freelancerID;
            }
        }
    }

    /**
     * Removes a freelancer from a customer's blacklist.
     * @param customerID The ID of the customer.
     * @param freelancerID The ID of the freelancer to unblock.
     * @return A status string indicating the result.
     */
    public String unblacklist(String customerID , String freelancerID){
        // Check if both users exist in the system
        if(!freelancerHashTable.containsID(freelancerID)|| !customerHashTable.containsID(customerID)){
            return "Some error occurred in unblacklist.";
        }
        else{
            Customer customer = (Customer) customerHashTable.get(customerID);
            UserTable<Freelancer> blacklistedHashTable = customer.getBlacklistedHashTable();

            // Check if they are actually blacklisted before attempting to remove
            if (!blacklistedHashTable.containsID(freelancerID)){
                return "Some error occurred in unblacklist.";
            }
            else{
                // Remove freelancer from unblacklist Hash Table
                customer.unblacklistFreelancer(freelancerID);
                return customerID + " unblacklisted " +freelancerID;
            }
        }
    }

    /**
     * Assigns a freelancer to a customer directly if conditions are met.
     * @param customerID The ID of the hiring customer.
     * @param freelancerID The ID of the freelancer to be hired.
     * @return A success message or error if the freelancer is unavailable or blacklisted.
     */
    public String employ(String customerID , String freelancerID){
        // Check if both users exist in the system
        if(!freelancerHashTable.containsID(freelancerID)|| !customerHashTable.containsID(customerID)){
            return "Some error occurred in employ.";
        }
        else {
            Customer customer = (Customer) customerHashTable.get(customerID);
            Freelancer freelancer = (Freelancer) freelancerHashTable.get(freelancerID);
            UserTable<Freelancer> blacklistedHashTable = customer.getBlacklistedHashTable();

            // Freelancer must not be blacklisted and must be available.
            if (blacklistedHashTable.containsID(freelancerID) || !freelancer.isAvailable()){
                return "Some error occurred in employ.";
            }
            else {
                // Set freelancer state to busy
                freelancer.setNumberCurrentJobs(1);
                customer.setTotalEmployments(customer.getTotalEmployments() + 1);
                freelancer.setAvailable(false);
                freelancer.setCurrentJob(customer);
                return customerID + " employed " + freelancerID + " for " + freelancer.getServiceType();
            }
        }
    }

    /**
     * Handles job cancellation by the customer, freelancer becomes free.
     * @param customerID The ID of the customer cancelling the job.
     * @param freelancerID The ID of the freelancer.
     * @return A confirmation message.
     */
    public String cancelByCustomer(String customerID, String freelancerID){
        // Check if both users exist in the system
        if(!freelancerHashTable.containsID(freelancerID)|| !customerHashTable.containsID(customerID)){
            return "Some error occurred in cancel_by_customer.";
        }
        else {
            Customer customer = (Customer) customerHashTable.get(customerID);
            Freelancer freelancer = (Freelancer) freelancerHashTable.get(freelancerID);
            Customer currentCustomer = freelancer.getCurrentJob();

            // Validate that these two are matched
            if (currentCustomer == null || !currentCustomer.getID().equals(customerID)){
                return "Some error occurred in cancel_by_customer.";
            }
            else{
                // Reset freelancer availability
                freelancer.setNumberCurrentJobs(0);
                freelancer.setCurrentJob(null);
                freelancer.setAvailable(true);

                // Track cancellation stats for customer to facilitate future loyalty tier calculation.
                customer.setCancelledRequests(customer.getCancelledRequests()+1);

                // Readd to heap so they can be hired by others
                int serviceIndex = getServiceIndex(freelancer.getServiceType());
                if (serviceIndex != -1) {
                    servicePriorityQueue[serviceIndex].insert(freelancer);
                }
                return "cancelled by customer: " +customerID+ " cancelled " +freelancerID;
            }
        }
    }

    /**
     * Handles job cancellation by the freelancer.
     * Applies penalties or bans the freelancer if limit is attained.
     * @param freelancerID The ID of the freelancer cancelling the job.
     * @return A status message indicating if the freelancer was penalized or banned.
     */
    public String cancelByFreelancer(String freelancerID){
        // Check if freelancer exist in the system
        if(!freelancerHashTable.containsID(freelancerID)){
            return "Some error occurred in cancel_by_freelancer.";
        }
        else {
            Freelancer freelancer = (Freelancer) freelancerHashTable.get(freelancerID);
            Customer currentCustomer = freelancer.getCurrentJob();
            // Validate that these two are actually matched
            if (currentCustomer == null){
                return "Some error occurred in cancel_by_freelancer.";
            }
            else{
                String customerID = currentCustomer.getID();
                freelancer.setCancelledJobs(freelancer.getCancelledJobs() + 1);
                freelancer.setJobsCancelledMonthly(freelancer.getJobsCancelledMonthly() + 1);

                // Freelancer are banned if they cancel 5 or more jobs in a single month
                if (freelancer.getJobsCancelledMonthly() >= 5){
                    freelancerHashTable.remove(freelancerID);

                    String serviceType = freelancer.getServiceType();
                    int serviceIndex = getServiceIndex(serviceType);
                    if(serviceIndex != -1) {
                        servicePriorityQueue[serviceIndex].remove(freelancerID);
                    }

                    return "cancelled by freelancer: " +freelancerID+ " cancelled " + customerID
                            +"\n" + "platform banned freelancer: " +freelancerID;
                }
                else{
                    // Reduce average rating
                    int completedJobs = freelancer.getCompletedJobs();
                    int cancelledJobs = freelancer.getCancelledJobs();
                    double averageRating = (freelancer.getAverageRating() *
                            (completedJobs + cancelledJobs) + 0)/((completedJobs + cancelledJobs) + 1.0);
                    freelancer.setAverageRating(averageRating);

                    // Reset job status
                    freelancer.setNumberCurrentJobs(0);
                    freelancer.setCurrentJob(null);
                    freelancer.setAvailable(true);

                    // Decrease all skill stats by 3 points (cannot go below 0)
                    freelancer.setT(Math.max(0, freelancer.getT() - 3));
                    freelancer.setC(Math.max(0, freelancer.getC() - 3));
                    freelancer.setR(Math.max(0, freelancer.getR() - 3));
                    freelancer.setE(Math.max(0, freelancer.getE() - 3));
                    freelancer.setA(Math.max(0, freelancer.getA() - 3));

                    // Recalculate heap position with new composite score since score has dropped
                    updateCompositeScore(freelancerID);

                    return "cancelled by freelancer: " +freelancerID+ " cancelled " + customerID;
                }
            }
        }
    }

    /**
     * Displays customer details including total spending and calculated loyalty tier.
     * @param customerID The ID of the customer to query.
     * @return A formatted string with customer stats.
     */
    public String queryCustomer(String customerID){
        // Check if customer exist in the system
        if(!customerHashTable.containsID(customerID)){
            return "Some error occurred in query_customer.";
        }
        else {
            Customer customer = (Customer) customerHashTable.get(customerID);
            int totalSpent =  customer.getTotalSpending();
            int loyaltyPoints = customer.getLoyaltyPoints();
            int blacklistedFreelancerCounter = customer.getBlacklistedCounter();
            int totalEmployment = customer.getTotalEmployments();

            // Determine tier based on loyalty points thresholds
            String tier;
            if (loyaltyPoints >= 5000) {
                tier = "PLATINUM";
            } else if (loyaltyPoints >= 2000) {
                tier = "GOLD";
            } else if (loyaltyPoints >= 500) {
                tier = "SILVER";
            } else {
                tier = "BRONZE";
            }
            // Display qualities of customer
            return customerID +": total spent: $" + totalSpent +", loyalty tier: " + tier +", blacklisted freelancer count: "+ blacklistedFreelancerCounter +", total employment count: "+ totalEmployment;
        }
    }

    /**
     * Displays freelancer details including skills, rating, and status.
     * @param freelancerID The ID of the freelancer to query.
     * @return A formatted string with freelancer stats.
     */
    public String queryFreelancer(String freelancerID){
        // Check if freelancer exist in the system
        if(!freelancerHashTable.containsID(freelancerID)){
            return "Some error occurred in query_freelancer.";
        }
        else{
            // Get freelancer from the hash table
            Freelancer freelancer = (Freelancer) freelancerHashTable.get(freelancerID);
            String service = freelancer.getServiceType();
            int price = freelancer.getServicePrice();
            String rating =String.format("%.1f", freelancer.getAverageRating());
            String available = "";
            String burnout = "";

            // Getting skills for display
            int T = freelancer.getT();
            int C = freelancer.getC();
            int R = freelancer.getR();
            int E = freelancer.getE();
            int A = freelancer.getA();
            int completed = freelancer.getCompletedJobs();
            int cancelled = freelancer.getCancelledJobs();

            // Check and assign availability and burnout status.
            if(freelancer.isAvailable()){
                available = "yes";
            }
            else{
                available = "no";
            }
            if(freelancer.isBurnout()){
                burnout = "yes";
            }
            else{
                burnout = "no";
            }
            // Display qualities of freelancer
            return freelancerID + ": " + service + ", price: " + price + ", rating: "
                    + rating + ", completed: " + completed + ", cancelled: " + cancelled +
                    ", skills: (" + T + "," + C + "," + R + "," + E + "," + A + "), " +
                    "available: " + available + ", burnout: " + burnout;
        }
    }

    /**
     * Finds the best available freelancers for a job using the heap.
     * Skips freelancers that are on the customer's blacklist.
     * @param customerID The customer requesting the job.
     * @param serviceType The type of service requested.
     * @param candidateNumber The number of candidates to list.
     * @return A string listing the available candidates and the one automatically employed.
     */
    public String requestJob(String customerID, String serviceType,int candidateNumber){
        // Check if service type is in given services
        int serviceIndex = getServiceIndex(serviceType);
        if(!customerHashTable.containsID(customerID)|| serviceIndex== -1){
            return "Some error occurred in request_job.";
        }
        // Initializing heap and control if there are any freelancer in it.
        MaximumHeap maxHeap = servicePriorityQueue[serviceIndex];
        if(servicePriorityQueue[serviceIndex].getSize() == 0){
            return "no freelancers available";
        }
        Customer customer = customerHashTable.get(customerID);
        UserTable<Freelancer> blacklistedHashTable = customerHashTable.get(customerID).getBlacklistedHashTable();

        // Temporary list to hold candidates that we remove from the heap except the best freelancer.
        // We need to put them back later to restore the heap state.
        LinkedList<Freelancer> candidatesToReInsert = new LinkedList<>();
        String freelancerString = "";
        int candidatesFound = 0;
        Freelancer bestFreelancer = null;

        // Iterate until we find the requested number of valid candidates
        while(candidatesFound < candidateNumber && maxHeap.getSize() != 0){

            // Get the best available from heap
            Freelancer candidate = maxHeap.deleteMax();
            candidatesToReInsert.add(candidate);

            // Check if this freelancer is blocked by the customer
            if (blacklistedHashTable.containsID(candidate.getID())) {
                continue; // Skip if blacklisted
            }
            candidatesFound++;

            // Display primary and secondary best freelancers of a given number
            freelancerString += "\n" + candidate.getID() + " - composite: " + candidate.getCompositeScore() +
                    ", price: " + candidate.getServicePrice() +
                    ", rating: " + String.format("%.1f", candidate.getAverageRating());
            // Assign first coming freelancer as the best since we are using heap structure
            if (bestFreelancer == null) {
                bestFreelancer = candidate;
            }
        }

        // If we couldn't find any valid freelancer.
        if (bestFreelancer == null) {
            // Restore the heap before returning
            for (Freelancer freelancerToReInsert : candidatesToReInsert) {
                maxHeap.insert(freelancerToReInsert);
            }
            return "no freelancers available";
        }

        // Prepare output and actually hire the best candidate
        String outputString = "available freelancers for " + serviceType + " (top " + candidatesFound + "):" + freelancerString;
        bestFreelancer.setAvailable(false);
        bestFreelancer.setNumberCurrentJobs(1);
        bestFreelancer.setCurrentJob(customer);
        customer.setTotalEmployments(customer.getTotalEmployments() + 1);

        outputString += "\nauto-employed best freelancer: " + bestFreelancer.getID() + " for customer " + customerID;

        // The hired freelancer is not put back into the heap.
        // Everyone else in the temporary list must go back.
        candidatesToReInsert.remove(bestFreelancer);

        for (Freelancer freelancerToReInsert : candidatesToReInsert) {
            maxHeap.insert(freelancerToReInsert);
        }
        return outputString;
    }

    /**
     * Queues a service change for a freelancer. Actual change is not made until simulateMonth.
     * @param freelancerID The ID of the freelancer.
     * @param serviceType The new service type.
     * @param price The new service price.
     * @return A confirmation message.
     */
    public String changeService(String freelancerID, String serviceType, int price){
        // Check if service type is in given services or freelancer is registered.
        int serviceIndex = getServiceIndex(serviceType);
        if(!freelancerHashTable.containsID(freelancerID)|| serviceIndex == -1){
            return "Some error occurred in change_service.";
        }
        // Check if the price is in the given interval
        if(price <= 0){
            return "Some error occurred in change_service.";
        }
        Freelancer freelancer = freelancerHashTable.get(freelancerID);
        String oldService = freelancer.getServiceType();
        String newService = serviceType;
        freelancer.setNewServicePrice(price);
        freelancer.setNewServiceType(serviceType);

        // Add to queue to be processed at the end of the month
        changedService.add(freelancer);
        return "service change for "+ freelancerID +" queued from "+oldService+" to "+newService;
    }

    /**
     * Simulates monthly updates: Check burnout and recovery, update loyalty, apply service changes.
     * @return Status message indicating month completion.
     */
    public String simulateMonth(){
        // Update burnout status for all freelancers
        LinkedList<Freelancer> allFreelancersList = freelancerHashTable.getAllUsers();
        for(Freelancer freelancer: allFreelancersList){
            if(freelancer.isBurnout()){
                // Recovery check: If they worked maximum two times in a month, they recover.
                if(freelancer.getJobsCompletedMonthly() <= 2){
                    freelancer.setBurnout(false);
                    updateCompositeScore(freelancer.getID());
                }
            }
            else{
                // Burnout check: If they worked minimum five times in a month, they burn out
                if(freelancer.getJobsCompletedMonthly() >= 5){
                    freelancer.setBurnout(true);
                    updateCompositeScore(freelancer.getID());
                }
            }
            // Reset monthly counters for the next month
            freelancer.setJobsCompletedMonthly(0);
            freelancer.setJobsCancelledMonthly(0);
        }

        // Update customer loyalty points
        LinkedList<Customer> allCustomersList = customerHashTable.getAllUsers();
        for(Customer customer: allCustomersList){

            int totalSpending = customer.getTotalSpending();
            int cancelledRequests = customer.getCancelledRequests();
            // Update points: Spending adds points, cancellations subtract points
            customer.setLoyaltyPoints(totalSpending - 250 * cancelledRequests);
        }

        // Perform queued service changes
        for (Freelancer freelancer : changedService){
            if (!freelancerHashTable.containsID(freelancer.getID())) {
                continue;
            }
            String newServiceType = freelancer.getNewServiceType();
            int newServicePrice = freelancer.getNewServicePrice();
            int oldServiceIndex = getServiceIndex(freelancer.getServiceType());
            int newServiceIndex = getServiceIndex(newServiceType);

            // Remove from old service heap
            servicePriorityQueue[oldServiceIndex].remove(freelancer.getID());

            // Apply new details
            freelancer.setServiceType(newServiceType);
            freelancer.setServicePrice(newServicePrice);

            // Recalculate score for new service and insert into new heap
            int[] newServiceSkillArray = serviceTypeArray[newServiceIndex];
            int newScore = freelancer.findCompositeScore(newServiceSkillArray);
            freelancer.setCompositeScore(newScore);

            servicePriorityQueue[newServiceIndex].insert(freelancer);
        }
        // Clear the queue since all changes are applied
        changedService.clear();
        return "month complete";
    }

    /**
     * Method to update a freelancer's skills.
     * @param freelancerID Unique ID
     * @param T            Technical Proficiency
     * @param C            Communication
     * @param R            Reliability
     * @param E            Efficiency
     * @param A            Attention to Detail
     * @return Status message regarding the update.
     */
    public String updateSkill(String freelancerID , int T,int C,int R,int E,int A){
        // Check if freelancer is registered
        if (!freelancerHashTable.containsID(freelancerID)){
            return "Some error occurred in update_skill.";
        }
        // Checking skill range
        if (T < 0 || T > 100 || C < 0 || C > 100 || R < 0 || R > 100 || E < 0 || E > 100 || A < 0 || A > 100) {
            return "Some error occurred in update_skill.";
        }
        Freelancer updatedFreelancer = freelancerHashTable.get(freelancerID);

        updatedFreelancer.setT(T);
        updatedFreelancer.setC(C);
        updatedFreelancer.setR(R);
        updatedFreelancer.setE(E);
        updatedFreelancer.setA(A);

        // Recalculate heap position after skill change as score might have changed
        updateCompositeScore(updatedFreelancer.getID());
        String serviceType = updatedFreelancer.getServiceType();

        return "updated skills of " + freelancerID + " for " + serviceType;
    }

    /**
     * Handles job completion, rating, skill upgrades, and payment processing.
     * @param freelancerID The ID of the freelancer completing the job.
     * @param rating The rating given by the customer (0-5).
     * @return A completion message containing the payment and rating details.
     */
    public String completeAndRate(String freelancerID ,int rating){
        // Check if freelancer is registered
        if(!freelancerHashTable.containsID(freelancerID)){
            return "Some error occurred in complete_and_rate.";
        }
        else {
            Freelancer freelancer = (Freelancer) freelancerHashTable.get(freelancerID);
            Customer currentCustomer = freelancer.getCurrentJob();
            if (currentCustomer == null){
                return "Some error occurred in complete_and_rate.";
            }
            // Checking rating
            if (rating < 0 || rating > 5) {
                return "Some error occurred in complete_and_rate.";
            }
            else {
                // Only upgrade skills if rating is higher than 4 (4 included)
                if(rating >= 4){
                    String serviceType = freelancer.getServiceType();
                    int[] serviceSkillArray = null;
                    int[] skillUpgrades = new int[5];
                    switch (serviceType) {
                        case "paint":
                            serviceSkillArray = serviceTypeArray[0];
                            break;
                        case "web_dev":
                            serviceSkillArray = serviceTypeArray[1];
                            break;
                        case "graphic_design":
                            serviceSkillArray = serviceTypeArray[2];
                            break;
                        case "data_entry":
                            serviceSkillArray = serviceTypeArray[3];
                            break;
                        case "tutoring":
                            serviceSkillArray = serviceTypeArray[4];
                            break;
                        case "cleaning":
                            serviceSkillArray = serviceTypeArray[5];
                            break;
                        case "writing":
                            serviceSkillArray = serviceTypeArray[6];
                            break;
                        case "photography":
                            serviceSkillArray = serviceTypeArray[7];
                            break;
                        case "plumbing":
                            serviceSkillArray = serviceTypeArray[8];
                            break;
                        case "electrical":
                            serviceSkillArray =serviceTypeArray[9];
                            break;
                    }

                    // Clone skill array to avoid modifying the original reference data
                    int[] skillArray = new int[5];
                    for (int i = 0; i < 5; i++) {
                        skillArray[i] = serviceSkillArray[i];
                    }

                    // Finding the top 3 most important skills for this service.
                    int max = -1;
                    int maxIndex = -1;
                    for (int i=0; i<skillArray.length; i++){
                        if (skillArray[i] > max) {
                            max = skillArray[i];
                            maxIndex = i;
                        }
                    }
                    if (maxIndex != -1) {
                        skillArray[maxIndex] = -1; // Put a placeholder as processed
                    }

                    // Find second max
                    int secondMax = -1;
                    int secondMaxIndex = -1;
                    for (int i=0; i<skillArray.length; i++){
                        if (skillArray[i] > secondMax) {
                            secondMax = skillArray[i];
                            secondMaxIndex = i;
                        }
                    }
                    if (secondMaxIndex != -1) {
                        skillArray[secondMaxIndex] = -1;
                    }

                    // Find third max
                    int thirdMax = -1;
                    int thirdMaxIndex = -1;
                    for (int i=0; i<skillArray.length; i++){
                        if (skillArray[i] > thirdMax) {
                            thirdMax = skillArray[i];
                            thirdMaxIndex = i;
                        }
                    }
                    if (thirdMaxIndex != -1) {
                        skillArray[thirdMaxIndex] = -1;
                    }

                    // Apply upgrades: Top skill gets +2, others get +1
                    skillUpgrades[maxIndex] = 2;
                    skillUpgrades[secondMaxIndex] = 1;
                    skillUpgrades[thirdMaxIndex] = 1;

                    // Update stats, clamping at 100
                    int newT = freelancer.getT() + skillUpgrades[0];
                    freelancer.setT(Math.min(newT, 100));

                    int newC = freelancer.getC() + skillUpgrades[1];
                    freelancer.setC(Math.min(newC, 100));

                    int newR = freelancer.getR() + skillUpgrades[2];
                    freelancer.setR(Math.min(newR, 100));

                    int newE = freelancer.getE() + skillUpgrades[3];
                    freelancer.setE(Math.min(newE, 100));

                    int newA = freelancer.getA() + skillUpgrades[4];
                    freelancer.setA(Math.min(newA, 100));
                }

                // Update performance metrics
                int completedJobs = freelancer.getCompletedJobs();
                int cancelledJobs = freelancer.getCancelledJobs();
                // Calculate new average rating
                double averageRating = (freelancer.getAverageRating() * (completedJobs + cancelledJobs + 1)
                        + rating)/((completedJobs + cancelledJobs +1) + 1.0);

                // Update freelancers qualities after finishing job
                freelancer.setCompletedJobs(freelancer.getCompletedJobs() + 1);
                freelancer.setJobsCompletedMonthly(freelancer.getJobsCompletedMonthly() + 1);
                freelancer.setAverageRating(averageRating);
                String customerID = currentCustomer.getID();

                // Free the freelancer
                freelancer.setAvailable(true);
                freelancer.setCurrentJob(null);
                freelancer.setNumberCurrentJobs(0);

                // Calculate payment with loyalty discount
                int freelancerPrice = freelancer.getServicePrice();
                int currentLoyaltyPoints = currentCustomer.getLoyaltyPoints();
                double subsidy = 0.0;

                // Discount tiers
                if (currentLoyaltyPoints >= 5000) {
                    subsidy = 0.15;
                } else if (currentLoyaltyPoints >= 2000) {
                    subsidy = 0.10;
                } else if (currentLoyaltyPoints >= 500) {
                    subsidy = 0.05;
                }

                int customerPayment = (int) (freelancerPrice * (1.0 - subsidy));
                currentCustomer.setTotalSpending(currentCustomer.getTotalSpending() + customerPayment);

                // Update heap position since composite score might be changed
                updateCompositeScore(freelancerID);
                return freelancerID + " completed job for " + customerID + " with rating " + rating;
            }
        }
    }

    /**
     * Helper method to map service string to array index.
     * @param serviceType The string representation of the service.
     * @return The integer index corresponding to the service, or -1 if invalid.
     */
    private int getServiceIndex(String serviceType) {
        switch (serviceType) {
            case "paint": return 0;
            case "web_dev": return 1;
            case "graphic_design": return 2;
            case "data_entry": return 3;
            case "tutoring": return 4;
            case "cleaning": return 5;
            case "writing": return 6;
            case "photography": return 7;
            case "plumbing": return 8;
            case "electrical": return 9;
            default: return -1;
        }
    }

    /**
     * Updates a freelancer's composite score and adjusts their position in the priority queue.
     * Uses heap remove and insert operations.
     * @param freelancerID The ID of the freelancer to update.
     */
    public void updateCompositeScore(String freelancerID) {
        // Check if freelancer exists
        Freelancer freelancer = (Freelancer) freelancerHashTable.get(freelancerID);
        if (freelancer == null) {
            return;
        }

        //Check if the service type is valid
        String serviceType = freelancer.getServiceType();
        int serviceIndex = getServiceIndex(serviceType);
        if (serviceIndex == -1) {
            return;
        }

        MaximumHeap heap = servicePriorityQueue[serviceIndex];
        int[] serviceSkillArray = serviceTypeArray[serviceIndex];

        // Remove first to update the score
        heap.remove(freelancerID);

        int newScore = freelancer.findCompositeScore(serviceSkillArray);
        freelancer.setCompositeScore(newScore);

        // Only reinsert if they are available to work
        if (freelancer.isAvailable()) {
            heap.insert(freelancer);
        }
    }
}