package com.example.netapp.embedded;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity class";

    Switch concent_1;
    Switch concent_2;
    Switch concent_3;
    Switch concent_All;
    public String[] data = new String[2];

    public String LED_No_1 = "No_1";
    public String LED_No_2 = "No_2";
    public String LED_No_3 = "No_3";

    boolean isAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "refreshed Token : " + refreshedToken);
        Log.e(TAG, "MainActivity Started");

        concent_1 = (Switch) findViewById(R.id.switch1);
        concent_2 = (Switch) findViewById(R.id.switch2);
        concent_3 = (Switch) findViewById(R.id.switch3);
        concent_All = (Switch) findViewById(R.id.switch4);

        CompoundButton.OnCheckedChangeListener multiSwitch = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                switch (compoundButton.getId()) {
                    case R.id.switch1:
                        if (isCheck) {
                            //TODO: Turn off Plug No.1
                            sendLEDToServer(LED_No_1, "ON");
                        } else {
                            sendLEDToServer(LED_No_1, "OFF");
                        }
                        break;
                    case R.id.switch2:
                        if (isCheck) {
                            //TODO: Turn off Plug No.2
                            sendLEDToServer(LED_No_2, "ON");
                        } else {
                            sendLEDToServer(LED_No_2, "OFF");
                        }
                        break;
                    case R.id.switch3:
                        if (isCheck) {
                            //TODO: Turn off Plug 3
                            sendLEDToServer(LED_No_3, "ON");
                        } else {
                            sendLEDToServer(LED_No_3, "OFF");
                        }
                        break;
                    case R.id.switch4:
                        if (isCheck) {
                            //TODO: Turn off Plug ALl
                            isAll = true;
                            concent_1.setChecked(true);
                            concent_2.setChecked(true);
                            concent_3.setChecked(true);
                            isAll = false;
                        } else {
                            isAll = true;
                            concent_1.setChecked(false);
                            concent_2.setChecked(false);
                            concent_3.setChecked(false);
                            isAll = false;
                        }
                        break;
                }
            }
        };
        concent_1.setOnCheckedChangeListener(multiSwitch);
        concent_2.setOnCheckedChangeListener(multiSwitch);
        concent_3.setOnCheckedChangeListener(multiSwitch);
        concent_All.setOnCheckedChangeListener(multiSwitch);

    }
    public void sendLEDToServer(String LED_No, String state) {
        try {
            URL url = new URL("http://203.246.112.144:20001/led.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");

            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            StringBuffer buffer = new StringBuffer();
            buffer.append("LED_NO").append("=").append(LED_No).append("&");
            buffer.append("STATE").append("=").append(state);
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
            returnString(Result);
        }
        catch (MalformedURLException e) {}
        catch (IOException e) {}
    }
    private void returnString(String result) {
        String[] tempString = result.split("&");

        if(!isAll) {
            if (tempString[0].equals("No_1")) {
                data[0] = "컴퓨터 전원이 ";
                if (tempString[1].equals("OFF\n")) {
                    data[1] = "꺼졌습니다.";
                } else {
                    data[1] = "켜졌습니다.";
                }
                Toast.makeText(MainActivity.this, data[0] + data[1], Toast.LENGTH_SHORT).show();
            } else if (tempString[0].equals("No_2")) {
                data[0] = "TV 전원이 ";
                if (tempString[1].equals("OFF\n")) {
                    data[1] = "꺼졌습니다.";
                } else {
                    data[1] = "켜졌습니다.";
                }
                Toast.makeText(MainActivity.this, data[0] + data[1], Toast.LENGTH_SHORT).show();
            } else if (tempString[0].equals("No_3")) {
                data[0] = "에어컨 전원이 ";
                if (tempString[1].equals("OFF\n")) {
                    data[1] = "꺼졌습니다.";
                } else {
                    data[1] = "켜졌습니다.";
                }
                Toast.makeText(MainActivity.this, data[0] + data[1], Toast.LENGTH_SHORT).show();
            }
        }
        else {
            data[0] = "모든 전원이 ";
            if(tempString[0].equals("No_3")) {
                Log.e(TAG, "In if");
                if (tempString[1].equals("OFF\n")) {
                    data[1] = "꺼졌습니다.";
                } else {
                    data[1] = "켜졌습니다.";
                }
                Toast.makeText(MainActivity.this, data[0] + data[1], Toast.LENGTH_SHORT).show();
            }
        }
    }
}
