package com.misteraventure.ericb.arduinobtandroid;

public interface Constantes {
    String DEVICE_ADRESS = "20:16:11:14:30:48";

    // pour etats de la connexion
    int STATE_NONE = 0;       // we're doing nothing
    int STATE_LISTEN = 1;     // now listening for incoming connections
    int STATE_CONNECTING = 2; // now initiating an outgoing connection
    int STATE_CONNECTED = 3;  // now connected to a remote device

    // types de message envoyes au handle de communication
    int MESSAGE_READ = 2;
    int MESSAGE_TOAST = 5;
    int STATUS = 1;

    String TOAST = "toast";
}
