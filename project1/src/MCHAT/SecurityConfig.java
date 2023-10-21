package MCHAT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HexFormat;

public class SecurityConfig {

    private static final String SECURITY_CONFIG_FILE = "project1/security.conf";
    public static String SYMMETRIC_ALGORITHM, HASH_ALGORITHM, MAC_ALGORITHM;
    public static byte[] SYMMETRIC_KEY, IV, MAC_KEY;

    public static void readSecurityConfigFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(SECURITY_CONFIG_FILE));
        SYMMETRIC_ALGORITHM = br.readLine().split(" ")[1];
        SYMMETRIC_KEY = HexFormat.of().parseHex(br.readLine().split(" ")[1]);
        IV = HexFormat.of().parseHex(br.readLine().split(" ")[1]);
        HASH_ALGORITHM = br.readLine().split(" ")[1];
        MAC_KEY = HexFormat.of().parseHex(br.readLine().split(" ")[1]);
        MAC_ALGORITHM = br.readLine().split(" ")[1];
    }

}
