package com.example.pondmatev1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SyncManager {

    public static void syncUsersToServer(Context context, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);


        if (cursor.getCount() == 0) {
            Toast.makeText(context, "No users to sync.", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            String id = String.valueOf(cursor.getInt(0));
            String username = cursor.getString(1);
            String password = cursor.getString(2);
            String fullname = cursor.getString(3);
            String address = cursor.getString(4);
            String usertype = cursor.getString(5);

            // Send user data in a background thread
            new Thread(() -> {
                try {
                    URL url = new URL("https://pondmate.alwaysdata.net/post_user.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String postData = "id=" + URLEncoder.encode(id, "UTF-8") +
                            "&username=" + URLEncoder.encode(username, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") +
                            "&fullname=" + URLEncoder.encode(fullname, "UTF-8") +
                            "&address=" + URLEncoder.encode(address, "UTF-8") +
                            "&usertype=" + URLEncoder.encode(usertype, "UTF-8");

                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();

                        String finalResponse = response.toString();
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(context, "✅ Synced: " + username + "\n" + finalResponse, Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        Log.e("SyncError", "Failed for user: " + username + ", Code: " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("SyncException", "Error syncing user: " + username + " - " + e.getMessage());
                }
            }).start();
        }

        cursor.close();
    }
}
