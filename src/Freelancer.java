/**
 * This class implements the User interface and takes care of
 * the freelancer's skills, performance history, and current availability status.
 * It is responsible for calculating a composite score to order freelancers.
 */
public class Freelancer implements User {

    private final String freelancerID;
    private String serviceType;
    private int servicePrice;

    // These fields represent specific skill dimensions
    private int T;
    private int C;
    private int R;
    private int E;
    private int A;

    private double averageRating;   // Current average rating from completed jobs
    private int completedJobs;      // Total count of successfully finished tasks
    private int cancelledJobs;      // Total count of jobs cancelled
    private int numberCurrentJobs;
    private boolean isAvailable;    // False if fully booked or manually set to unavailable
    private boolean isBurnout;      // True if the freelancer is overworked
    private Customer currentJob;    // Reference to the active client
    private int compositeScore;     // Calculated score for ranking purposes
    private int jobsCompletedMonthly;
    private int jobsCancelledMonthly;
    private String newServiceType;
    private int newServicePrice;

    /**
     * Initializes a new Freelancer given skill features.
     * @param freelancerID Unique identifier for the freelancer.
     * @param serviceType  The service provided by the freelancer.
     * @param servicePrice The price for the service.
     * @param T            Technical Proficiency
     * @param C            Communication
     * @param R            Reliability
     * @param E            Efficiency
     * @param A            Attention to Detail
     */
    Freelancer(String freelancerID, String serviceType, int servicePrice,
               int T, int C, int R, int E, int A){
        this.freelancerID = freelancerID;
        this.serviceType = serviceType;
        this.servicePrice = servicePrice;
        this.T = T;
        this.C = C;
        this.R = R;
        this.E = E;
        this.A = A;

        // Setting default performance metrics
        this.averageRating = 5.0;
        this.completedJobs = 0;
        this.cancelledJobs = 0;
        // Initializing status flags
        this.isAvailable = true;
        this.isBurnout = false;
        this.currentJob = null;
        this.numberCurrentJobs = 0;
        this.compositeScore = 0;
        // Resetting monthly counters and future updates
        this.jobsCompletedMonthly = 0;
        this.newServicePrice = 0;
        this.newServiceType = "";
    }
    /**
     * @return The unique ID string.
     */
    public String getID() {
        return freelancerID;
    }

    /**
     * @return The skill level.
     */
    public int getA() {
        return A;
    }

    /**
     * @return The skill level.
     */
    public int getC() {
        return C;
    }

    /**
     * @return The skill level.
     */
    public int getE() {
        return E;
    }

    /**
     * @return The skill level.
     */
    public int getR() {
        return R;
    }

    /**
     * @return The price amount.
     */
    public int getServicePrice() {
        return servicePrice;
    }

    /**
     * @return The skill level.
     */
    public int getT() {
        return T;
    }

    /**
     * @return The service type string.
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param a The new skill level.
     */
    public void setA(int a) {
        A = a;
    }

    /**
     * @param c The new skill level.
     */
    public void setC(int c) {
        C = c;
    }

    /**
     * @param e The new skill level.
     */
    public void setE(int e) {
        E = e;
    }

    /**
     * @param r The new skill level.
     */
    public void setR(int r) {
        R = r;
    }

    /**
     * @param servicePrice The new price.
     */
    public void setServicePrice(int servicePrice) {
        this.servicePrice = servicePrice;
    }

    /**
     * @param serviceType The service category.
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param t The new skill level.
     */
    public void setT(int t) {
        T = t;
    }

    /**
     * @return The average rating.
     */
    public double getAverageRating() {
        return averageRating;
    }

    /**
     * @param averageRating The new calculated average.
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Sets the freelancer's availability status.
     * @param available true if available, false otherwise.
     */
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    /**
     * Checks if the freelancer is currently available.
     * @return true if available, false otherwise.
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * @param currentJobs The count of active jobs.
     */
    public void setNumberCurrentJobs(int currentJobs) {
        this.numberCurrentJobs = currentJobs;
    }

    /**
     * Sets the customer object representing the current job.
     * @param customer The customer object.
     */
    public void setCurrentJob(Customer customer){
        currentJob = customer;
    }

    /**
     * @return The customer object, or null if no active job.
     */
    public Customer getCurrentJob() {
        return currentJob;
    }

    /**
     * Sets the burnout state of the freelancer.
     * @param burnout true if burned out, false otherwise.
     */
    public void setBurnout(boolean burnout) {
        isBurnout = burnout;
    }

    /**
     * @param completedJobs The total count of completed jobs.
     */
    public void setCompletedJobs(int completedJobs) {
        this.completedJobs = completedJobs;
    }

    /**
     * @param cancelledJobs The total count of cancelled jobs.
     */
    public void setCancelledJobs(int cancelledJobs) {
        this.cancelledJobs = cancelledJobs;
    }

    /**
     * @return The cancellation count.
     */
    public int getCancelledJobs() {
        return cancelledJobs;
    }

    /**
     * @return The completion count.
     */
    public int getCompletedJobs() {
        return completedJobs;
    }

    /**
     * @return true if burnt out, false otherwise.
     */
    public boolean isBurnout() {return isBurnout;}

    /**
     * @return The composite score.
     */
    public int getCompositeScore() {
        return compositeScore;
    }

    /**
     * @param compositeScore The new score.
     */
    public void setCompositeScore(int compositeScore) {
        this.compositeScore = compositeScore;
    }

    /**
     * @return The monthly completion count.
     */
    public int getJobsCompletedMonthly() {
        return jobsCompletedMonthly;
    }

    /**
     * @param jobsCompletedMonthly The new count.
     */
    public void setJobsCompletedMonthly(int jobsCompletedMonthly) {
        this.jobsCompletedMonthly = jobsCompletedMonthly;
    }

    /**
     * @return The new price amount.
     */
    public int getNewServicePrice() {
        return newServicePrice;
    }

    /**
     * @return The new service type string.
     */
    public String getNewServiceType() {
        return newServiceType;
    }

    /**
     * @param newServicePrice The new price.
     */
    public void setNewServicePrice(int newServicePrice) {
        this.newServicePrice = newServicePrice;
    }

    /**
     * @param newServiceType The new service type string.
     */
    public void setNewServiceType(String newServiceType) {
        this.newServiceType = newServiceType;
    }

    /**
     * @return The monthly cancellation count.
     */
    public int getJobsCancelledMonthly() {
        return jobsCancelledMonthly;
    }

    /**
     * @param jobsCancelledMonthly The new count.
     */
    public void setJobsCancelledMonthly(int jobsCancelledMonthly) {
        this.jobsCancelledMonthly = jobsCancelledMonthly;
    }
    /**
     * Calculates a composite score to rank freelancer in the heap.
     * The method has three main factors which are skill matching,rating and reliability.
     * It also applies a penalty coefficient if the freelancer is burnout.
     * @param serviceSkillArray An array containing the required skill levels.
     * @return An integer score to rank freelancers.
     */
    public int findCompositeScore(int[] serviceSkillArray){
        // Defining weights for the algorithm.
        double skillMatchingCoefficient = 0.55;
        double ratingQualityCoefficient = 0.25;
        double reliabilityCoefficient = 0.20;
        double burnoutCoefficient;

        // Temporary variables for score components
        double skillScore = 0.0;
        double ratingScore = 0.0;
        double reliabilityScore = 0.0;

        // Burnout check: If burned out, apply a penalty to the final score.
        if(isBurnout()){
            burnoutCoefficient= 0.45;
        }
        else{
            burnoutCoefficient = 0;
        }

        // Unpacking the freelancer's skill vector
        int Tf = this.T;
        int Cf = this.C;
        int Rf = this.R;
        int Ef = this.E;
        int Af = this.A;

        // Unpacking the job's required skill vector
        int Ts = serviceSkillArray[0];
        int Cs = serviceSkillArray[1];
        int Rs = serviceSkillArray[2];
        int Es = serviceSkillArray[3];
        int As = serviceSkillArray[4];

        // Calculating Dot Product
        int dotProduct = Tf * Ts + Cf * Cs + Rf * Rs + Ef * Es + Af * As;
        int sumOfService = Ts + Cs + Rs + Es + As;

        skillScore = dotProduct / (100.0 * sumOfService);
        ratingScore = averageRating / 5.0;

        // Calculating reliability.
        if (completedJobs + cancelledJobs == 0){
            reliabilityScore = 1.0;
        }
        else {
            reliabilityScore = 1.0 -(cancelledJobs * 1.0 /(completedJobs +cancelledJobs));
        }

        // Final weighted calculation
        int compositeScore = (int) (10000 * (skillMatchingCoefficient * skillScore +
                ratingQualityCoefficient * ratingScore +
                reliabilityCoefficient * reliabilityScore -
                burnoutCoefficient));

        return compositeScore;
    }
}