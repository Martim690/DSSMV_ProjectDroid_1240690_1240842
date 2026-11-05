package mrgym.network;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;
import mrgym.models.Exercise;

public class RestClient {

    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public interface ExercisesCallback {
        void onSuccess(List<Exercise> exercises);
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
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (MrGym Android App)");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(20000);
                    
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

    public static void fetchExercisesFromWger(final ExercisesCallback callback) {
        String imgUrl = "https://wger.de/api/v2/exerciseimage/?limit=400";
        makeRequest(imgUrl, "GET", null, null, new VolleyCallback() {
            @Override
            public void onSuccess(String imgResult) {
                final Map<Integer, String> imageMap = new HashMap<>();
                try {
                    JSONObject imgRoot = new JSONObject(imgResult);
                    JSONArray imgArray = imgRoot.getJSONArray("results");
                    for (int i = 0; i < imgArray.length(); i++) {
                        JSONObject obj = imgArray.getJSONObject(i);
                        int exId = obj.getInt("exercise");
                        String img = obj.optString("image", "");
                        if (!img.isEmpty() && !imageMap.containsKey(exId)) {
                            imageMap.put(exId, img);
                        }
                    }
                } catch (Exception ignored) {
                }
                loadTranslations(imageMap, callback);
            }

            @Override
            public void onError(String error) {
                loadTranslations(new HashMap<Integer, String>(), callback);
            }
        });
    }

    private static void loadTranslations(final Map<Integer, String> imageMap, final ExercisesCallback callback) {
        String url = "https://wger.de/api/v2/exercise-translation/?language=2&limit=1000";
        makeRequest(url, "GET", null, null, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                List<Exercise> exercises = new ArrayList<>();
                try {
                    JSONObject root = new JSONObject(result);
                    JSONArray resultsArray = root.getJSONArray("results");
                    Set<Integer> seenIds = new HashSet<>();
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject item = resultsArray.getJSONObject(i);
                        int id = item.getInt("exercise");
                        String name = item.getString("name");
                        if (name == null || name.trim().isEmpty() || seenIds.contains(id)) {
                            continue;
                        }
                        seenIds.add(id);
                        String rawDesc = item.optString("description_source", item.optString("description", ""));
                        String description = android.text.Html.fromHtml(rawDesc).toString().trim();
                        String imageUrl = imageMap.get(id);
                        exercises.add(new Exercise(id, name, description, imageUrl));
                    }
                    Collections.sort(exercises, new Comparator<Exercise>() {
                        @Override
                        public int compare(Exercise e1, Exercise e2) {
                            if (e1.getName() == null) return -1;
                            if (e2.getName() == null) return 1;
                            return e1.getName().compareToIgnoreCase(e2.getName());
                        }
                    });
                    callback.onSuccess(exercises);
                } catch (Exception e) {
                    callback.onError("Failed to parse exercises: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
}
