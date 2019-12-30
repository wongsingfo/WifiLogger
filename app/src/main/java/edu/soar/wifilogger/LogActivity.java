package edu.soar.wifilogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.net.wifi.WifiManager;

import org.w3c.dom.Text;

import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

public class LogActivity extends AppCompatActivity {
    private TextView textView;
    final Handler handler = new Handler();

    Runnable logger = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(logger, 1000);
            textView.setText(getWifiStatus());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        textView = findViewById(R.id.textView);
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
        String ifname = info.getSSID();

        return ifname + "\n" + info.toString() + "\n";
    }
}
