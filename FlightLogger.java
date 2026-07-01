import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightLogger implements AutoCloseable {

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final BufferedWriter writer;

    public FlightLogger(String logFileName) throws IOException {
        writer = new BufferedWriter(new FileWriter(logFileName, true));
        log("=== Application run started ===");
    }

    public void log(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        String line = timestamp + " | " + message;
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Logger write error: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        log("=== Application run ended ===");
        writer.close();
    }
}