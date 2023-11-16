//assignment 2 RMI Client HealthAndFitness APP distributed systems

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    private Client() {}

    //stubs, connect to rmiregistry 1099 def port
    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            HealthAppInterface stub = (HealthAppInterface) registry.lookup("HealthApp");

            Scanner scanner = new Scanner(System.in);
            boolean loggedIn = false;
            String username = "";

            //user interface for the clients
            while (true) {
                if (!loggedIn) {
                    System.out.println("Please choose an option:");
                    System.out.println("1. To Register");
                    System.out.println("2. To Login");

                    int option = Integer.parseInt(scanner.nextLine());

                    if (option == 1) {
                        System.out.print("Enter a new username: ");
                        String newUsername = scanner.nextLine();
                        System.out.print("Enter a password: ");
                        String newPassword = scanner.nextLine();

                        String response = stub.register(newUsername, newPassword);
                        System.out.println("Server: " + response);

                    } else if (option == 2) {
                        System.out.print("Enter your username: ");
                        username = scanner.nextLine();
                        System.out.print("Enter your password: ");
                        String password = scanner.nextLine();

                        String response = stub.login(username, password);
                        System.out.println("Server: " + response);

                        loggedIn = response.equals("Login successful.");
                    }
                } else {
                    System.out.println("WELCOME TO FULL FITNESS AND HEALTH APP");
                    System.out.println("This program will help you on all aspects of your Fitness Journey");

                    System.out.println("Choose an option:");
                    System.out.println("3. Provide Initial Health Information");
                    System.out.println("4. Get TDEE");
                    System.out.println("5. Get BMI");
                    System.out.println("6. Time to reach Weight Goal");
                    System.out.println("7. Logout");

                    int option = Integer.parseInt(scanner.nextLine());

                    if (option == 3) {
                        System.out.print("Enter your age, weight, height, activity level, and weight goal: ");
                        String healthInfo = scanner.nextLine();
                        String response = stub.provideInitialHealthInfo(username, healthInfo);
                        System.out.println("Server: " + response);
                    } else if (option == 4) {
                        String response = stub.getTDEE(username);
                        System.out.println("Server: " + response);
                    } else if (option == 5) {
                        String response = stub.getBMI(username);
                        System.out.println("Server: " + response);
                    } else if (option == 6) {
                        String response = stub.timeToReachWeightGoal(username);
                        System.out.println("Server: " + response);
                    } else if (option == 7) {
                        loggedIn = false;
                        username = "";
                        System.out.println("Logged out successfully.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
