package edu.soar.wifilogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.net.wifi.WifiManager;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.OutputStreamWriter;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

public class LogActivity extends AppCompatActivity {
    final static private String TAG = "LogActivity";

    private TextView textView;
    final Handler handler = new Handler();
    OutputStreamWriter outputStreamWriter;

    Runnable logger = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(logger, 1000);
            long seconds = System.currentTimeMillis() / 1000;
            String rv = String.format(Locale.getDefault(), "%d %s\n", seconds, getWifiStatus());

            try {
                outputStreamWriter.write(rv);
                outputStreamWriter.flush();
            } catch (Exception e) {
                rv = e.toString();
            }

            textView.setText(rv);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        textView = findViewById(R.id.textView);
        try {
            // under /data/data/edu.soar.wifilogger/files/speed.txt
            outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(message, Context.MODE_PRIVATE));
        } catch (Exception e) {
            Snackbar.make(getWindow().getDecorView(), e.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        handler.postDelayed(logger, 1000);
    }

    private String getNetworkInterfaces() {
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (Exception e) {
            return e.toString();
        }

        if (interfaces == null) {
            return "do not have permission";
        }

        StringBuilder sb = new StringBuilder();
        for (NetworkInterface itf : Collections.list(interfaces)) {
            sb.append(itf.getDisplayName());
            sb.append('\n');
        }

        return sb.toString();
    }

    private String getWifiStatus() {
        WifiManager wifiManager;

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            return "wifi is not enabled";
        }

        WifiInfo info = wifiManager.getConnectionInfo();

        return info.getRssi() + " " + info.getLinkSpeed();
    }
}
