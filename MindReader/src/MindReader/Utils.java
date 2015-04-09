package MindReader;

/**
 * Utility class
 * @author jordanreedie
 *
 */
public class Utils {

    /**
     * As named.
     * @param ms
     * @return
     */
    public static double msToSeconds(long ms) {
        return (double) ms / 1000.0;
    }
    
    /**
     * As named.
     * @param seconds
     * @return
     */
    public static long secondsToMs(double seconds) {
        return (long) seconds * 1000;
    }
}
