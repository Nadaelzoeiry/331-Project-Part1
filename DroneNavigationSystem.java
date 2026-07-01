import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DroneNavigationSystem {
    private static final int TOTAL_CYCLES = 10;
    private static final int INITIAL_ALTITUDE = 100;

    public static void main(String[] args) {
        // I created a dynamic log file name using timestamps
        String logFileName = "drone_log_" + System.currentTimeMillis() + ".txt";
        try {
            FileWriter writer = new FileWriter(logFileName);
            
            // I initialized the 3 sensors and the central voting controller
            List<AltitudeSensor> sensors = Arrays.asList(new AltitudeSensor("A"), new AltitudeSensor("B"), new AltitudeSensor("C"));
            List<String> sensorIds = Arrays.asList("A", "B", "C");
            VotingController controller = new VotingController(INITIAL_ALTITUDE, writer);

            System.out.println("Starting drone navigation simulation");
            System.out.println("Log file created: " + logFileName);
            System.out.println();
            writer.write("Drone navigation simulation started\n");

            try {
                // I started the 10-cycle simulation loop
                for (int cycle = 1; cycle <= TOTAL_CYCLES; cycle++) {
                    System.out.println("CYCLE " + cycle);
                    writer.write("CYCLE " + cycle + "\n");

                    // I gathered raw sensor results for this cycle
                    List<Integer> outcomes = new ArrayList<Integer>();
                    for (AltitudeSensor sensor : sensors) outcomes.add(readOneSensor(sensor, writer));

                    // I logged raw data and passed readings to the voter
                    printRawReadings(sensorIds, outcomes);
                    int resolvedAltitude = controller.vote(outcomes, sensorIds);

                    System.out.println("Resolved altitude this cycle: " + resolvedAltitude + " m");
                    System.out.println("Reliability status: " + controller.getConsecutiveFailures() + " consecutive failure(s)");
                    System.out.println();
                }

                System.out.println("Simulation complete. " + TOTAL_CYCLES + " cycles finished without a SAFE MODE event.");
                writer.write("Simulation complete. " + TOTAL_CYCLES + " cycles finished without a SAFE MODE event.\n");
            } catch (SystemReliabilityException sys) {
                // I handled system-level failures by triggering safe mode
                System.out.println();
                System.out.println("SAFE MODE ACTIVATED");
                System.out.println("Reason: " + sys.getMessage());
                System.out.println("Drone navigation system halted.");
                writer.write("SAFE MODE ACTIVATED: " + sys.getMessage() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not create log file: " + e.getMessage());
        }
    }

    private static Integer readOneSensor(AltitudeSensor sensor, FileWriter writer) {
        try {
            // I captured individual sensor readings and checked for bad data
            int value = sensor.readSensor();
            if (!AltitudeSensor.isValid(value)) {
                String msg = "Corrupted data: Sensor " + sensor.getSensorId() + " returned " + value + " m, outside valid range";
                System.out.println("  " + msg);
                writeLog(writer, msg);
            }
            return value;
        } catch (SensorReadException sre) {
            // I caught hardware crash exceptions here and returned null
            String msg = "Sensor failure: " + sre.getMessage();
            System.out.println("  " + msg);
            writeLog(writer, msg);
            return null;
        }
    }

    private static void writeLog(FileWriter writer, String msg) {
        // I added a quick helper to swallow IO exceptions during file logging
        try { writer.write(msg + "\n"); } catch (IOException e) { System.out.println("Could not write to log file: " + e.getMessage()); }
    }

    private static void printRawReadings(List<String> ids, List<Integer> outcomes) {
        // I formatted the console output to show FAILED vs numeric altitudes
        System.out.print("Raw readings:");
        for (int i = 0; i < ids.size(); i++) {
            String display = (outcomes.get(i) == null) ? "FAILED" : outcomes.get(i) + " m";
            System.out.print(" Sensor " + ids.get(i) + ": " + display + ",");
        }
        System.out.println();
    }
}
