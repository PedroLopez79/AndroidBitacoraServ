package com.example.kamran.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class TareasProgramadasMenu extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(final android.view.Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /*RUTINA LOGOUT********************************************************************************/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode) {
            case R.id.logout:
                setResult(R.id.logout);
                finish();            // to close this activity
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout)
        {
            setResult(R.id.logout);
            finish();                     //To finish your current acivity
        }
        return super.onOptionsItemSelected(item);
    }
    /**********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_programadas_menu);

        final String numestacion;
        Button btnmntotodos = (Button) findViewById(R.id.btnmntotodos);
        Button btnmntocinco = (Button) findViewById(R.id.btnmntocinco);
        Button btnmntodia   = (Button) findViewById(R.id.btnmntodia);
        Button btnmntoreg   = (Button) findViewById(R.id.btnmntoreg);

        final int SIGNATURE_ACTIVITY = 1;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        numestacion = prefs.getString("numeroestacion","2601");

        btnmntotodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), desglosemantenimientos.class);
                intent.putExtra("mntoestacion", numestacion);
                intent.putExtra("mntotipo", "1");
                intent.putExtra("noregistra", "SI");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        btnmntocinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), desglosemantenimientos.class);
                intent.putExtra("mntoestacion", numestacion);
                intent.putExtra("mntotipo", "2");
                intent.putExtra("noregistra", "SI");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        btnmntodia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), desglosemantenimientos.class);
                intent.putExtra("mntoestacion", numestacion);
                intent.putExtra("mntotipo", "3");
                intent.putExtra("noregistra", "SI");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        btnmntoreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), desglosemantenimientos.class);
                intent.putExtra("mntoestacion", numestacion);
                intent.putExtra("mntotipo", "4");
                intent.putExtra("noregistra", "NO");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });
    }
}
