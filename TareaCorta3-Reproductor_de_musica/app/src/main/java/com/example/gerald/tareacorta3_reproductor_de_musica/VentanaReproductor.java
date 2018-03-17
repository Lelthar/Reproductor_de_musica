package com.example.gerald.tareacorta3_reproductor_de_musica;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class VentanaReproductor extends AppCompatActivity {
    private String path;
    private String nombre_cancion;
    private boolean playing_music;
    private MediaPlayer media;
    private Button button_play;
    private Button button_back;
    private Button button_next;
    private SeekBar progreso;
    private SeekBar volumen;
    private AudioManager audioManager;
    private Handler duracionHandler = new Handler();
    private int posicion_actual;

    private TextView mostrar_nombre;

    private int posicion_cancion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ventana_reproductor);

        mostrar_nombre =findViewById(R.id.textViewCancion);

        posicion_actual = 0;
        Intent intent = getIntent();

        path = intent.getStringExtra(MainActivity.PATH);
        nombre_cancion = intent.getStringExtra(MainActivity.CANCION);
        posicion_cancion = Integer.parseInt(intent.getStringExtra(MainActivity.POSICION));

        playing_music = false;

        button_play = findViewById(R.id.button);

        button_play.setBackgroundResource(R.drawable.play_icon);

        button_back = findViewById(R.id.button_back);

        button_back.setBackgroundResource(R.drawable.anterior_icon);

        button_next = findViewById(R.id.button_next);

        button_next.setBackgroundResource(R.drawable.siguiente_icon);

        progreso = findViewById(R.id.seekBarProgreso);
        volumen = findViewById(R.id.seekBarVolumen);

        inicializar_cancion();

        cambioVolumen();

        cambiarProgreso();


    }

    public void onDestroy() {
        super.onDestroy();
        media.pause();

    }

    public void onClickedPlay(View view){
        if(!playing_music){

            media.setAudioStreamType(AudioManager.STREAM_MUSIC);
            media.start();

            playing_music = true;
            button_play.setBackgroundResource(R.drawable.pause_icon);
            duracionHandler.postDelayed(actualizarSeekBarProgreso, 100);
        }else{
            media.pause();
            playing_music = false;
            button_play.setBackgroundResource(R.drawable.play_icon);
        }

    }

    private void cambioVolumen()
    {
        try
        {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumen.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumen.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));

            volumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void cambiarProgreso(){
        progreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0)
            {
                progreso.setPressed(false);
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                if(arg2){
                    media.seekTo(progress);
                }
            }
        });
    }

    private Runnable actualizarSeekBarProgreso = new Runnable() {
        public void run() {

            posicion_actual = media.getCurrentPosition();

            progreso.setPressed(false);
            progreso.setProgress(posicion_actual);


            duracionHandler.postDelayed(this, 50);
        }
    };

    private void inicializar_cancion(){
        media = new MediaPlayer();
        Uri myUri = Uri.parse(path);

        try {
            media.setDataSource(getApplicationContext(), myUri);
            media.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        media.setAudioStreamType(AudioManager.STREAM_MUSIC);
        media.start();

        playing_music = true;
        button_play.setBackgroundResource(R.drawable.pause_icon);
        duracionHandler.postDelayed(actualizarSeekBarProgreso, 100);

        progreso.setMax(media.getDuration());

        mostrar_nombre.setText(nombre_cancion);

    }

    public void siguiente_cancion(View view){
        if(posicion_cancion < MainActivity.lista_nombres.size()){
            media.pause();
            posicion_cancion++;
            path = MainActivity.lista_paths.get(posicion_cancion);
            nombre_cancion = MainActivity.lista_nombres.get(posicion_cancion);
            inicializar_cancion();
        }
    }

    public void anterior_cancion(View view){
        if(posicion_cancion > 0){
            media.pause();
            posicion_cancion--;
            path = MainActivity.lista_paths.get(posicion_cancion);
            nombre_cancion = MainActivity.lista_nombres.get(posicion_cancion);
            inicializar_cancion();
        }
    }
}
