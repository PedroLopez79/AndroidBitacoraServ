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

public class HorarioEmpleadosTurnos extends AppCompatActivity {

    public static final int SIGNATURE_ACTIVITY = 1;
    String usuarioid, nombreusuario;
    String TAG = "Response";
    String ip, resultString, numestacion;

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
        setContentView(R.layout.activity_horario_empleados_turnos);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");

        Button turnouno = (Button) findViewById(R.id.btnturnouno);
        Button turnodos = (Button) findViewById(R.id.btnturnodos);
        Button turnotres= (Button) findViewById(R.id.btnturnotres);

        turnouno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HorariosEmpleados.class);
                intent.putExtra("usuarioid", usuarioid);
                intent.putExtra("nombreusuario", nombreusuario);
                intent.putExtra("turno", "1");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        turnodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HorariosEmpleados.class);
                intent.putExtra("usuarioid", usuarioid);
                intent.putExtra("nombreusuario", nombreusuario);
                intent.putExtra("turno", "2");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        turnotres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HorariosEmpleados.class);
                intent.putExtra("usuarioid", usuarioid);
                intent.putExtra("nombreusuario", nombreusuario);
                intent.putExtra("turno", "3");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });
    }
}
