package cryptoexplorer;

import org.junit.Test;

/**
 * @author Marcelo Morales
 *         Date: 8/24/12
 */
public class SecureRandomRestTest {

    @Test
    public void testSecureRandom() throws Exception {
        String s = new SecureRandomRest().secureRandom("NativePRNG", "SUN");

        System.out.println(s);
    }
}
