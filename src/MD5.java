import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Advent of Code puzzles love to use MD5 hashes. They essentially are a
 * controlled randomization sequence, which is cross-platform and implementation
 * independent.
 * 
 * Simplest way to use is via its static methods.
 * Most puzzles grow an input string:
 *   byte[] hash = MD5.hash("your_input_string");
 * A few puzzles modify a binary structure (another byte[])
 *   byte[] hash = MD5.hash(your_binary_structure);
 */
public class MD5
{
    /**
     * I have not had reason to worry about reusing a digest instance,
     * so share one instance across all callers.
     */
    private static MessageDigest md5;

    /**
     * Wrap the initialization step in a try/catch block
     */
    private static MessageDigest init()
    {
        try
        {
            return MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException ex)
        {
            // convert the explicit exception to the kind that callers don't have to try/catch
            throw new RuntimeException("MD5 is unsupported", ex);
        }
    }

    /**
     * Initialize the shared MD5 object on demand
     */
    private static MessageDigest shared()
    {
        if (md5 == null)
            md5 = init();
        return md5;
    }

    /**
     * Get the hash digest for a binary input
     * @param input an array of bytes
     * @return An MD5 hash, as a 32-byte array
     */
    public static byte[] hash(byte[] input)
    {
        MessageDigest md = shared();
        md.update(input);
        return md.digest();
    }

    /**
     * Get the hash digest for a string input
     * @param input an array of bytes
     * @return An MD5 hash, as a 32-byte array
     */
    public static byte[] hash(String input)
    {
        MessageDigest md = shared();
        md.update(input.getBytes());
        return md.digest();
    }
}