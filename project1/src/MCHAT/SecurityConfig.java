package MCHAT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SecurityConfig {

    private static final String SECURITY_CONFIG_FILE = "security.conf";
    public static String SYMMETRIC_ALGORITHM, SYMMETRIC_KEY, HASH_ALGORITHM, MAC_ALGORITHM, MAC_KEY;

    public static void readSecurityConfigFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(SECURITY_CONFIG_FILE));
        SYMMETRIC_ALGORITHM = br.readLine().split(" ")[1];
        SYMMETRIC_KEY = br.readLine().split(" ")[1];
        HASH_ALGORITHM = br.readLine().split(" ")[1];
        MAC_KEY = br.readLine().split(" ")[1];
        MAC_ALGORITHM = br.readLine().split(" ")[1];
    }

}
