import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
//import org.json.JSONObject;

import org.json.JSONObject;

public class AWSMetadata {

    private static final String METADATA_URL = "http://169.254.169.254/latest/meta-data/";

    public static JSONObject getInstanceMetadata() {
        String[] metadataKeys = {
            "ami-id", "instance-id", "instance-type", "placement/availability-zone",
            "local-ipv4", "public-ipv4", "hostname"
        };

        JSONObject metadata = new JSONObject();
        for (String key : metadataKeys) {
            metadata.put(key, getMetadataKey(key));
        }
        return metadata;
    }

    public static String getMetadataKey(String key) {
        try {
        	URI uri = URI.create(METADATA_URL + key);
        	URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String value = reader.readLine();
            reader.close();

            return value;
        } catch (Exception e) {
            return "Error retrieving metadata for key: " + key;
        }
    }

    public static void main(String[] args) {
        // Get full metadata
        System.out.println(getInstanceMetadata().toString(4));

        // Bonus: Retrieve a specific metadata key
        String key = "instance-id"; // Change this as needed
       // System.out.println(key + ": " + getMetadataKey(key));
    }
}
