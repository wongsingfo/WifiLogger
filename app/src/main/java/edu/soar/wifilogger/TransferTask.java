package edu.soar.wifilogger;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class TransferTaskParams {

}

class TransferTask extends AsyncTask<TransferTaskParams, String, Integer> {
    private final static String TAG = "TransferTask";

    final static private int BUFFER_SIZE = 4096;
    final static private String FILENAME = "/data/data/edu.soar.wifilogger/mtp";

    private Handler handler;

    TransferTask(Handler handler) {
        this.handler = handler;
    }

    private void sendMessage(String s) {
        Message msg = Message.obtain();
        msg.obj = s;
        msg.setTarget(handler);
        msg.sendToTarget();
    }

    @Override
    protected Integer doInBackground(TransferTaskParams... params) {
        try {
            publishProgress("start process\n");
            Process process = new ProcessBuilder()
                    .command(FILENAME)
                    .redirectErrorStream(true).start();

//            Process process = Runtime.getRuntime().exec(FILENAME);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            int read;
            char[] buffer = new char[BUFFER_SIZE];
            while ((read = reader.read(buffer)) > 0) {
                String s = String.valueOf(buffer, 0, read);
                Log.d(TAG, s);
                publishProgress(s);

                if (isCancelled()) {
                    throw new InterruptedException("Cancelled");
                }
            }
            reader.close();

            process.waitFor();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return -1;
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
            return 2;
        }

        return 0;
    }

    @Override
    protected void onProgressUpdate(String... outputs) {
        for (String s : outputs) {
            sendMessage(s);
        }
    }

    @Override
    protected void onPostExecute(Integer exitCode) {
        sendMessage("\nexit with: " + exitCode);
    }
}
