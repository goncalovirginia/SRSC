package MCHAT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SecurityConfig {

    private static final String SECURITY_CONFIG_FILE = "project1/security.conf";
    private static final String PUBLICKEYS_CONFIG_FILE = "project1/publickeys.conf";
    
    public static String SYMMETRIC_ALGORITHM, HASH_ALGORITHM, MAC_ALGORITHM, SIGNATURE_ALGORITHM;
    public static byte[] SYMMETRIC_KEY, IV, MAC_KEY;
    
    public static final String ELLIPTIC_CURVE_ALGORITHM = "secp256r1";
    
    public static final Map<String, String[]> publicKeys = new HashMap<>();

    public static void init() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(SECURITY_CONFIG_FILE));
        SYMMETRIC_ALGORITHM = br.readLine().split(" ")[1];
        SYMMETRIC_KEY = HexFormat.of().parseHex(br.readLine().split(" ")[1]);
        IV = HexFormat.of().parseHex(br.readLine().split(" ")[1]);
        HASH_ALGORITHM = br.readLine().split(" ")[1];
        MAC_KEY = HexFormat.of().parseHex(br.readLine().split(" ")[1]);
        MAC_ALGORITHM = br.readLine().split(" ")[1];
        SIGNATURE_ALGORITHM = br.readLine().split(" ")[1];
        
        br = new BufferedReader(new FileReader(PUBLICKEYS_CONFIG_FILE));
        for (String line : br.lines().toList()) {
            String[] lineParts = line.split(":");
            publicKeys.put(lineParts[0], new String[]{lineParts[1], lineParts[2], lineParts[3]});
        }
    }

}
