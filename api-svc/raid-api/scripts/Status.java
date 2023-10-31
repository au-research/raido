import java.net.HttpURLConnection;
import java.net.URL;

public class Status {
    public static void main(String[] args) throws Exception {
        assert args[0] != null;

        URL url = new URL(args[0]);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        assert responseCode == 200;
    }
}
