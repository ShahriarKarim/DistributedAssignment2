
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

//nested hashmaps for each client
public class Server implements HealthAppInterface {
    private static Map<String, String> users = new HashMap<>();
    private static Map<String, Map<String, Integer>> clientHealthData = new HashMap<>();

    public Server() {}

    // Implementations of the remote methods
    //since this is a simple RMI project
    //I put implementattion with server

    @Override //no two users can have the same username
    public String register(String username, String password) {
        if (users.containsKey(username)) {
            return "Registration failed: Username already exists.";
        } else {
            users.put(username, password);
            return "Registration successful.";
        }
    }

    @Override
    public String login(String username, String password) {
        if (users.containsKey(username) && users.get(username).equals(password)) {
            return "Login successful.";
        } else {
            return "Login failed.";
        }
    }

    @Override //store the clients initial health data in a hashmap
    public String provideInitialHealthInfo(String username, String healthInfo) {
        String[] healthInfoParts = healthInfo.split(" ");
        if (healthInfoParts.length == 5) {
            try {
                int age = Integer.parseInt(healthInfoParts[0]);
                int weight = Integer.parseInt(healthInfoParts[1]);
                int height = Integer.parseInt(healthInfoParts[2]);
                int activityLevel = Integer.parseInt(healthInfoParts[3]);
                int weightGoal = Integer.parseInt(healthInfoParts[4]);

                Map<String, Integer> healthData = new HashMap<>();
                healthData.put("AGE", age);
                healthData.put("WEIGHT", weight);
                healthData.put("HEIGHT", height);
                healthData.put("ACTIVITY_LEVEL", activityLevel);
                healthData.put("WEIGHT_GOAL", weightGoal);
                clientHealthData.put(username, healthData);

                return "Initial health information saved.";
            } catch (NumberFormatException e) {
                return "Invalid input. Please enter valid numeric values.";
            }
        } else {
            return "Invalid input. Please enter age, weight, height, activity level, and weight goal.";
        }
    }

    @Override //calculates a clients TDEE
    public String getTDEE(String username) {
        if (clientHealthData.containsKey(username)) {
            Map<String, Integer> healthData = clientHealthData.get(username);
            int age = healthData.get("AGE");
            int weight = healthData.get("WEIGHT");
            int height = healthData.get("HEIGHT");
            int activityLevel = healthData.get("ACTIVITY_LEVEL");

            int tdee = calculateTDEE(age, weight, height, activityLevel);
            return "Your TDEE is: " + tdee;
        } else {
            return "Health information is missing. Please provide your initial health information.";
        }
    }

    @Override //calculates a clients BMI
    public String getBMI(String username) {
        if (clientHealthData.containsKey(username)) {
            Map<String, Integer> healthData = clientHealthData.get(username);
            int weight = healthData.get("WEIGHT");
            int height = healthData.get("HEIGHT");

            double bmi = calculateBMI(weight, height);
            return "Your BMI is " + bmi;
        } else {
            return "Health information is missing. Please provide your initial health information.";
        }
    }

    @Override //function that computes the clients weight estimation
    public String timeToReachWeightGoal(String username) {
        if (clientHealthData.containsKey(username)) {
            Map<String, Integer> healthData = clientHealthData.get(username);
            int weight = healthData.get("WEIGHT");
            int weightGoal = healthData.get("WEIGHT_GOAL");

            int weeks = weeksUntilGoal(weight, weightGoal);
            return "The number of weeks to reach your goal weight is " + weeks;
        } else {
            return "Health information is missing. Please provide your initial health information.";
        }
    }

    private int calculateTDEE(int age, int weight, int height, int activityLevel) {
        // TDEE calculation logic
        return (int) ((10 * weight) + (6.25 * height) - (5 * age) + (5 * activityLevel));
    }

    private double calculateBMI(int weight, int height) {
        double heightMeters = height / 100.0;
        double weightKg = weight * 0.4535;
        return weightKg / (heightMeters * heightMeters);
    }

    private int weeksUntilGoal(int currentWeight, int goalWeight) {
        // Assuming a loss of 1 pound per week
        int weightDifference = currentWeight - goalWeight;
        return Math.abs(weightDifference / 1); // 1 pound per week
    }

    //RMI registry start
    public static void main(String args[]) {
        try {

            // Creating the server object
            Server obj = new Server();
            HealthAppInterface stub = (HealthAppInterface) UnicastRemoteObject.exportObject(obj, 0);

            // using Registry on defult port of 1099
            LocateRegistry.createRegistry(1099);

            // gettting the registry
            Registry registry = LocateRegistry.getRegistry();

            // Bind the stubs
            registry.bind("HealthApp", stub);

            System.err.println("RMI registry active, ready for clients");
        } catch (Exception e) {
            System.err.println("Server problem: " + e.toString());
            e.printStackTrace();
        }
    }
}
