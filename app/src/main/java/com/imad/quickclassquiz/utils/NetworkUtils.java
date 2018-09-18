package com.imad.quickclassquiz.utils;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class NetworkUtils extends AsyncTask<Void, Void, Boolean> {

    private Consumer mConsumer;

    public NetworkUtils(Consumer consumer) {
        mConsumer = consumer;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean internet) {
        mConsumer.accept(internet);
    }

    public interface Consumer {
        void accept(Boolean internet);
    }
}

