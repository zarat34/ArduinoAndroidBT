package com.misteraventure.ericb.arduinobtandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
/*
Cette activité affiche les informations envoyées par un module Arduino via un module blue tooth (HC-05)

Dans son create , elle instancie une classe "ConnexionThread" qui va
        se charger de connecter le module BT avec le device Android
        informer l'activité de l'avancement de la connexion via un handle
 quand la connexion est établie , l'activité instencie une classe communicationBT qui va:
        lire les informations émises par le module BT
        répercuter ces informations à l'activité via un handle
        informer l'activité en cas de déconnexion.

En cas de déconnexion , l'activité demande ferme la lecture en cours et redemande la reconnexion (ceci n fois)
Au bout de n fois , affichage d'un bouton de reconnection (si demandé en paramètre)

 */
private TextView tv_valeur;
    private Button bt_connecter;
    private Handler connexion_handler;
    private Handler communication_handler;
    private final static int STATUS = 1;
    private int NbTentatives = 0;
    private ConnexionThread mConnexion;
    private BluetoothSocket mbt_socket = null;
    private CommunicationThread mCommunication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_valeur =  findViewById(R.id.TV_VALEUR);
        bt_connecter =  findViewById(R.id.BT_CONNECTER);

        /*
On definit un handler pour manipuler les messages BT , ce handler est utilisé par la classe interne « MyBlueToothClass » ,
 celle ci envoie au handler les différents status de la connexion
 */
        connexion_handler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case STATUS:
                        if(msg.obj=="BTAbsent"){
                            tv_valeur.setText("Pas d'interface Bluetooth");
                        }
                        if(msg.obj=="BTisDisabled"){
                            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(turnOn, 0);
                            Log.d("MainActivity", "avant launchBT "  );
                            mConnexion.launchBt();
                        }
                        if(msg.obj=="BTisEnabled"){

                            Log.d("MainActivity", "avant launchBT 2"  );
                            mConnexion.launchBt();
                        }
                        if(msg.obj=="BTActif"){
                            mConnexion.start();
                        }
                        if(msg.obj=="Connecté"){
                            bt_connecter.setVisibility(View.INVISIBLE);
                            mCommunication = new CommunicationThread(communication_handler ,mConnexion.getMbt_socket(), "Insecure");
                            mCommunication.start();
                        }
                        if(msg.obj=="Echec connexion socket"){
                             // on tente une autre reconnexion (prevoir un nombre limite et un delais entre chaque essai
                            NbTentatives ++;
                            Log.d("MainActivity", "nb ten,tatives reconnexion : " +  NbTentatives);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // yourMethod();
                                    reconnecter();
                                }
                            }, 5000);   //5 seconds

                        }
                        tv_valeur.setText((String)(msg.obj));
                        break;
                }
            }
        };
        /**
         * Le Handler qui recupere les informations en provenance de la thread communication
         */
        communication_handler = new Handler() {
            @Override

            public void handleMessage(Message msg) {

                switch (msg.what) {


                    case Constantes.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        Log.i("MainActivity", "message recu = : " + readMessage  );

                        tv_valeur.setText( "on a recu:" + readMessage);

                        break;

                    case Constantes.MESSAGE_TOAST:
                        tv_valeur.setText( msg.getData().getString(Constantes.TOAST));


                        break;

                    case Constantes.STATUS:
                        if(msg.obj=="Deconnecté") {
                            mConnexion.interrupt();
                            mCommunication.interrupt();
                            //bt_connecter.setVisibility(View.VISIBLE);
                            reconnecter();
                            tv_valeur.setText("state none");
                        }


                        break;
                }
            }
        };
        mConnexion = new ConnexionThread(connexion_handler);

    }

    public void onStop(){
        super.onStop();

            mConnexion.stopConnexion();

    }

    public void closeBT() {
        mConnexion.stopConnexion();
    }

    public void reconnecter() {
        closeBT();
        // mybluetooth = new MyBluetoothClass();
        Thread.State stateBT = mConnexion.getState();

        Log.d("mainActivity", "mybluetooth.getState = " + stateBT);
        if(stateBT==Thread.State.TERMINATED){
            tv_valeur.setText("LANCEMENT CONNEXION ");
            mConnexion.run();};


    }

}
