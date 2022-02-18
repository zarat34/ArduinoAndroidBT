package com.misteraventure.ericb.arduinobtandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ConnexionThread extends Thread {
    private Handler mhandler;
    private final static int STATUS = 1;
    private BluetoothAdapter my_bt_adapter;
    private BluetoothSocket mbt_socket = null;
    private OutputStream mbt_output_stream = null;
    private InputStream mbt_input_stream = null;
    private String device_address;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConnexionThread(Handler  handler){
        Log.d("connexion thread", "constructor "  );
        mhandler = handler;
    // ===========  affecter un identificateur au module bluetooth ============================
    my_bt_adapter = BluetoothAdapter.getDefaultAdapter();
        if (my_bt_adapter == null) {
            Log.d("connexion thread", "BTAbsent "  );
            mhandler.obtainMessage(STATUS, -1, -1, "BTAbsent").sendToTarget();
    }
        if (!my_bt_adapter.isEnabled()) {
            mhandler.obtainMessage(STATUS, -1, -1, "BTisDisabled").sendToTarget();
            Log.d("connexion thread", "BTisDisabled"  );

        } else{ mhandler.obtainMessage(STATUS, -1, -1, "BTisEnabled").sendToTarget();};
        device_address = Constantes.DEVICE_ADRESS;
    }

    public void launchBt(){
        Log.d("connexion thread", "launch bt "  );
        // ============== attendre que le BT soit actif =============================

        while (!my_bt_adapter.isEnabled()) ; // attendre que le démarrage soit effectif
        mhandler.obtainMessage(STATUS, -1, -1, "BTActif").sendToTarget();
    }

    public BluetoothSocket getMbt_socket() {
        return mbt_socket;
    }

    public OutputStream getMbt_output_stream() {
        return mbt_output_stream;
    }

    public InputStream getMbt_input_stream() {
        return mbt_input_stream;
    }

    public void run() {
        Log.d("connexion thread", "debut run "  );
        boolean SOCKET_OK, CONX_OK, OUTS_OK, INPS_OK;
        my_bt_adapter.cancelDiscovery();
        // créer un objet bluetooth pour notre HC05

        BluetoothDevice HC05 = my_bt_adapter.getRemoteDevice(device_address);
        Log.d("mainActivity", "BluetoothDevice HC05 = " + HC05.getAddress());

        //Créer un soket (pipeline) pour communiquer avec notre HC05
        SOCKET_OK = true;
        try {
            mbt_socket = HC05.createInsecureRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            Log.d("mainActivity", "ioException 1 "  );
            SOCKET_OK = false;
        }
        if (SOCKET_OK) {
            // connecter le soket
            CONX_OK = true;
            try {
                mbt_socket.connect();
                Log.d("mainActivity", "mbt_socket =  " + mbt_socket.isConnected());

            } catch (IOException e) {

                Log.e("mainActivity", "ioException lors de la connexion: "  , e);
                CONX_OK = false;
            }
            if (CONX_OK) {
                OUTS_OK = true;

                try {
                    mbt_output_stream = mbt_socket.getOutputStream();
                } catch (IOException e) {
                    mhandler.obtainMessage(STATUS, -1, -1, "Echec création OUTPUT stream").sendToTarget();
                    OUTS_OK = false;
                }
                INPS_OK = true;
                try {
                    mbt_input_stream = mbt_socket.getInputStream();
                } catch (IOException e) {
                    mhandler.obtainMessage(STATUS, -1, -1, "Echec création INPUT STREAM").sendToTarget();
                    INPS_OK = false;
                }
                if (OUTS_OK && INPS_OK)
                    mhandler.obtainMessage(STATUS, -1, -1, "Connecté").sendToTarget();
            } else {
                mhandler.obtainMessage(STATUS, -1, -1, "Echec Connexion").sendToTarget();
            }
        } else {
            mhandler.obtainMessage(STATUS, -1, -1, "Echec création Soket COMM").sendToTarget();
        }
    }
}

