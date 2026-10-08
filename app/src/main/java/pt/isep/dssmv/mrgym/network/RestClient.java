package pt.isep.dssmv.mrgym.network;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestClient {

    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public static void makeRequest(final String urlString, final String method, final String jsonBody, final String apiKey, final VolleyCallback callback) {
        new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(method);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    
                    if (apiKey != null && !apiKey.isEmpty()) {
                        conn.setRequestProperty("x-apikey", apiKey);
                    }

                    if (("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) && jsonBody != null) {
                        conn.setDoOutput(true);
                        OutputStream os = conn.getOutputStream();
                        os.write(jsonBody.getBytes("UTF-8"));
                        os.close();
                    }

                    int responseCode = conn.getResponseCode();
                    BufferedReader in;
                    if (responseCode >= 200 && responseCode < 300) {
                        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    if (responseCode >= 200 && responseCode < 300) {
                        return new String[]{"SUCCESS", response.toString()};
                    } else {
                        return new String[]{"ERROR", response.toString()};
                    }

                } catch (Exception e) {
                    return new String[]{"ERROR", e.getMessage()};
                }
            }

            @Override
            protected void onPostExecute(String[] result) {
                if ("SUCCESS".equals(result[0])) {
                    callback.onSuccess(result[1]);
                } else {
                    callback.onError(result[1]);
                }
            }
        }.execute();
    }
}
