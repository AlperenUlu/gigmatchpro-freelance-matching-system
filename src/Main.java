import java.io.*;
import java.util.Locale;

/**
 * Main entry point for GigMatch Pro platform.
 */
public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            AppManager appManager = new AppManager();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                processCommand(line, writer, appManager);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer, AppManager appManager)
            throws IOException {

        String[] parts = command.split("\\s+");
        String operation = parts[0];

        try {
            String result = "";

            switch (operation) {
                case "register_customer":
                    String customerID = parts[1];
                    result = appManager.registerCustomer(customerID);
                    break;

                case "register_freelancer":
                    String freelancerID = parts[1];
                    String type = parts[2];
                    int price = Integer.parseInt(parts[3]);
                    int T = Integer.parseInt(parts[4]);
                    int C = Integer.parseInt(parts[5]);
                    int R = Integer.parseInt(parts[6]);
                    int E = Integer.parseInt(parts[7]);
                    int A = Integer.parseInt(parts[8]);
                    result = appManager.registerFreelancer(freelancerID,type,price,T,C,R,E,A);
                    // Format: register_freelancer freelancerID serviceName basePrice T C R E A
                    break;

                case "request_job":
                    String customerToRequest = parts[1];
                    String serviceType = parts[2];
                    int candidateNumber = Integer.parseInt(parts[3]);
                    result = appManager.requestJob(customerToRequest,serviceType,candidateNumber);
                    break;

                case "employ_freelancer":
                    String customerToEmploy = parts[1];
                    String employedFreelancer= parts[2];
                    result = appManager.employ(customerToEmploy,employedFreelancer);
                    // Format: employ_freelancer customerID freelancerID
                    break;

                case "complete_and_rate":
                    String IDOfFreelancer = parts[1];
                    int rating= Integer.parseInt(parts[2]);
                    result =appManager.completeAndRate(IDOfFreelancer,rating);
                    // Format: complete_and_rate freelancerID rating
                    break;

                case "cancel_by_freelancer":
                    String freelancerToCancel = parts[1];
                    result = appManager.cancelByFreelancer(freelancerToCancel);
                    // Format: cancel_by_freelancer freelancerID
                    break;

                case "cancel_by_customer":
                    String customerToCancel = parts[1];
                    String cancelledFreelancer= parts[2];
                    result = appManager.cancelByCustomer(customerToCancel,cancelledFreelancer);
                    // Format: cancel_by_customer customerID freelancerID
                    break;

                case "blacklist":
                    String customerToBlacklist = parts[1];
                    String blacklistedFreelancer= parts[2];
                    result = appManager.blacklist(customerToBlacklist,blacklistedFreelancer);
                    // Format: blacklist customerID freelancerID
                    break;

                case "unblacklist":
                    String customerToUnblacklist = parts[1];
                    String unblacklistedFreelancer= parts[2];
                    result = appManager.unblacklist(customerToUnblacklist,unblacklistedFreelancer);
                    // Format: unblacklist customerID freelancerID
                    break;

                case "change_service":
                    String customerToChangeService = parts[1];
                    String newService = parts[2];
                    int newPrice = Integer.parseInt(parts[3]);
                    result = appManager.changeService(customerToChangeService,newService,newPrice);
                    // Format: change_service freelancerID newService newPrice
                    break;

                case "simulate_month":
                    result = appManager.simulateMonth();
                    // Format: simulate_month
                    break;

                case "query_freelancer":
                    String freelancerToQuery = parts[1];
                    result= appManager.queryFreelancer(freelancerToQuery);
                    // Format: query_freelancer freelancerID
                    break;

                case "query_customer":
                    String customerToQuery = parts[1];
                    result= appManager.queryCustomer(customerToQuery);
                    // Format: query_customer customerID
                    break;

                case "update_skill":
                    String freelancerToUpdate = parts[1];
                    int updatedT = Integer.parseInt(parts[2]);
                    int updatedC = Integer.parseInt(parts[3]);
                    int updatedR = Integer.parseInt(parts[4]);
                    int updatedE = Integer.parseInt(parts[5]);
                    int updatedA = Integer.parseInt(parts[6]);
                    result = appManager.updateSkill(freelancerToUpdate,
                            updatedT,updatedC,updatedR,updatedE,updatedA);
                    // Format: update_skill freelancerID T C R E A
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

            writer.write(result);
            writer.newLine();


        } catch (Exception e) {
            writer.write("Error processing command: " + command);
            writer.newLine();
        }

    }
}