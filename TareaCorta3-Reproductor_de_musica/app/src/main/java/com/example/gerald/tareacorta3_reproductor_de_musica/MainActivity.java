package com.example.gerald.tareacorta3_reproductor_de_musica;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static ArrayList<String> lista_nombres;
    public static ArrayList<String> lista_paths;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final String PATH  = "path";
    public static final String CANCION  = "nombreCancion";
    public static final String POSICION  = "posicion_cancion";
    public static final String MAXIMO  = "maximo_canciones";

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            (Activity) context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialog("External storage", context,Manifest.permission.READ_EXTERNAL_STORAGE);

                    } else {
                        ActivityCompat
                                .requestPermissions(
                                        (Activity) context,
                                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }

            } else {
                return true;
            }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            lista_nombres = obtenerListaMusicaNombre();

            lista_paths = obtenerListaMusicaURL();

            adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtitem,lista_nombres);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    reproducirCancion(position);

                }

            });
        }
    }

    public ArrayList<String> obtenerListaMusicaNombre() {
        ArrayList<String> resultado = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    //String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    //String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    resultado.add(name);

                } while (cursor.moveToNext());

            }

            cursor.close();
        }
        return resultado;

    }

    public ArrayList<String> obtenerListaMusicaURL() {
        ArrayList<String> resultado = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    //String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    resultado.add(url);

                } while (cursor.moveToNext());

            }

            cursor.close();
        }
        return resultado;

    }

    public void reproducirCancion(int posicion){
        String texto1 = lista_paths.get(posicion);
        String texto2 = lista_nombres.get(posicion);
        Intent intent = new Intent(this,VentanaReproductor.class);
        intent.putExtra(PATH,texto1);
        intent.putExtra(CANCION,texto2);
        intent.putExtra(POSICION,Integer.toString(posicion));
        startActivity(intent);

    }




}
