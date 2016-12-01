package com.example.netapp.embedded;

/**
 * Created by Netapp on 2016-11-25.
 */

import com.google.firebase.iid.FirebaseInstanceIdService;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
        Log.e(TAG, "Send Registeration Started");
    }
    public void sendRegistrationToServer(String token) {
        try {
            URL url = new URL("http://203.246.112.144:20001/register.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            StringBuffer buffer = new StringBuffer();
            buffer.append("Token").append("=").append(token);
            Log.e(TAG, buffer.toString());

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
            outStream.write(buffer.toString());
            outStream.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String Result = builder.toString();
            Log.e(TAG, Result);
        }
        catch (MalformedURLException e) {}
        catch (IOException e) {}
    }
}