import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection; // created this to connect to AWS Metadata service ..
import java.net.URI;
import java.net.URL;
import org.json.JSONObject;

public class AWSMetadataIMDSv2 {

	// this url is to fetch EC2 instance metadata
    private static final String METADATA_URL = "http://169.254.169.254/latest/meta-data/";
    // below url endpoint is to request an IMDSv2 authentication token
    private static final String TOKEN_URL = "http://169.254.169.254/latest/api/token";
    
    // using this method to retrieve IMDSv2 token
    public static String getIMDSv2Token() {
        try {
            URL url = new URI(TOKEN_URL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            // let me set token expiry as 6hrs
            connection.setRequestProperty("X-aws-ec2-metadata-token-ttl-seconds", "21600");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String token = reader.readLine();
            reader.close();
            
            return token;
        } catch (Exception e) {
            return "Error retrieving IMDSv2 token: " + e.getMessage();
        }
    }
    
    // Method to retrieve a specific metadata key using IMDSv2 token
    public static String getMetadataKey(String key, String token) {
        try {
            URL url = new URI(METADATA_URL + key).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-aws-ec2-metadata-token", token);  // Include token in request
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String value = reader.readLine();
            reader.close();
            
            return value;
        } catch (Exception e) {
            return "Error retrieving metadata for key: " + key;
        }
    }

    // Method to retrieve full metadata as JSON
    public static JSONObject getInstanceMetadata(String token) {
        String[] metadataKeys = {
            "ami-id", "instance-id", "instance-type", "placement/availability-zone",
            "local-ipv4", "public-ipv4", "hostname"
        };

        JSONObject metadata = new JSONObject();
        for (String key : metadataKeys) {
            metadata.put(key, getMetadataKey(key, token));
        }
        return metadata;
    }

    public static void main(String[] args) {
        String token = getIMDSv2Token();
        if (token.startsWith("Error")) {
            System.out.println(token);
            return;
        }

        System.out.println("Full Metadata JSON:");
        System.out.println(getInstanceMetadata(token).toString(4));
        
        // below is for bonus point question to retrieve a specific metadata key dynamically
        if (args.length > 0) {
            String key = args[0]; // Pass key via command-line arguments
            System.out.println("\nSpecific Metadata Key:");
            System.out.println(key + ": " + getMetadataKey(key, token));
        }
    }
}
