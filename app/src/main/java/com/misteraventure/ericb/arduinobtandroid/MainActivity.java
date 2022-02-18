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
    private final static int STATUS = 1;

    private ConnexionThread mConnexion;
    private BluetoothSocket mbt_socket = null;
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

                        tv_valeur.setText((String)(msg.obj));
                        break;
                }
            }
        };
        mConnexion = new ConnexionThread(connexion_handler);

    }
}
