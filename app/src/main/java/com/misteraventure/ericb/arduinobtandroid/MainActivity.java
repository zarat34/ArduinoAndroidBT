package com.misteraventure.ericb.arduinobtandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
/*
Cette activité affiche les informations envoyées par un module Arduino via un module blue tooth (HC-05)

Dans son create , elle instancie une classe "ConnectionBT" qui va
        se charger de connecter le module BT avec le device Android
        informer l'activité de l'avancement de la connexion via un handle
 quand la connexion est établie , l'activité instencie une classe communicationBT qui va:
        lire les informations émises par le module BT
        répercuter ces informations à l'activité via un handle
        informer l'activité en cas de déconnexion.

En cas de déconnexion , l'activité demande ferme la lecture en cours et redemande la reconnexion (ceci n fois)
Au bout de n fois , affichage d'un bouton de reconnection (si demandé en paramètre) 

 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
