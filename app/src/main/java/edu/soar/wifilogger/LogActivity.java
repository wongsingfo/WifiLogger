package edu.soar.wifilogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.net.wifi.WifiManager;

import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView textView = findViewById(R.id.textView);
        textView.setText(getWifiStatus());
    }

    private String getWifiStatus() {

        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (Exception e) {
            return e.toString();
        }

        if (interfaces == null) {
            return "null";
        }

        String rv = "";
        for (NetworkInterface itf : Collections.list(interfaces)) {
            rv = rv + itf.getDisplayName() + "\n";
        }


        WifiManager wifiManager;

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            return "wifi is not enabled";
        }

        WifiInfo info = wifiManager.getConnectionInfo();
        String ifname = info.getSSID();

//        return ifname + "\n" + info.toString();
        return rv;
    }
}
