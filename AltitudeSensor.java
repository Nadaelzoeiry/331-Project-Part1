import java.util.Random;

public class AltitudeSensor {

    public static final int MIN_ALTITUDE = 0;
    public static final int MAX_ALTITUDE = 200;
    private static final int VALID_RANGE = MAX_ALTITUDE;
    private static final int CORRUPTED_OFFSET = 201;
    private static final int CORRUPTED_RANGE = 100;

    private final String sensorId;
    private final Random random;

    public AltitudeSensor(String sensorId) {
        this.sensorId = sensorId;
        this.random = new Random();
    }

    public int readSensor() throws SensorReadException {
        int chance = random.nextInt(100);

        if (chance < 15) {
            throw new SensorReadException(
                    "Sensor " + sensorId + " failed (hardware fault)");
        } else if (chance < 30) {
            return CORRUPTED_OFFSET + random.nextInt(CORRUPTED_RANGE);
        } else {
            return MIN_ALTITUDE + random.nextInt(VALID_RANGE);
        }
    }

    public String getSensorId() {
        return sensorId;
    }

    public static boolean isValid(int value) {
        return value >= MIN_ALTITUDE && value <= MAX_ALTITUDE;
    }
}