package com.misteraventure.ericb.arduinobtandroid;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CommunicationThread extends Thread {
    private final BluetoothSocket mSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private Handler mHandler;
    private int mState;
    private static final String TAG = "CommunicationThread";
    private final static int STATUS = 1;
    public CommunicationThread(Handler handler ,BluetoothSocket socket, String socketType) {

        Log.d(TAG, "create CommunicationThread: " + socketType);
        mHandler = handler;
        mSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "sockets temporaires pas créées", e);
        }

        mInputStream = tmpIn;
        mOutputStream = tmpOut;
        mState = Constantes.STATE_CONNECTED;

    }
    public void run() {
        Log.i(TAG, "Debut communication thread");
        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (mState == Constantes.STATE_CONNECTED) {
            try {

                // Read from the InputStream
                bytes = mInputStream.read(buffer);

                // Send the obtained bytes to the UI Activity
                mHandler.obtainMessage(Constantes.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
                connectionLost();
                break;
            }
        }
    }
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constantes.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        mHandler.obtainMessage(Constantes.STATUS, -1, -1, "Deconnecté").sendToTarget();

        mState = Constantes.STATE_NONE;

    }
}
