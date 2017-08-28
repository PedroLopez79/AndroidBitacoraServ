package com.example.kamran.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class CapturaMantenimiento extends AppCompatActivity {

    String status, idtipomante, nota;

    String TAG = "Response";
    String ip, resultString, numestacion;

    public static final int SIGNATURE_ACTIVITY = 1;

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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbRealizado:
                if (checked)
                    status = "Realizada";
                    break;
            case R.id.rbNoRealizado:
                if (checked)
                    status = "No Realizada";
                    break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_mantenimiento);

        Intent intent = getIntent();
        String fecha = intent.getStringExtra("fecha");
        String codigo= intent.getStringExtra("codigo");
        String tipomante= intent.getStringExtra("tipomante");
        String descripcion= intent.getStringExtra("descripcion");
        idtipomante= intent.getStringExtra("IDTIPOMANTE");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");
        status = "Realizada";

        TextView txtfecha = (TextView) findViewById(R.id.txtFecha);
        TextView txtcodigo = (TextView) findViewById(R.id.txtCodigo);
        TextView txttipomante = (TextView) findViewById(R.id.txtTipoMante);
        TextView txtdescripcion = (TextView) findViewById(R.id.txtDescripcion);
        final EditText edtnota = (EditText) findViewById(R.id.edtnota);
        Button btnguarda = (Button) findViewById(R.id.btnguardaactividad);

        txtfecha.setText(fecha);
        txtcodigo.setText(codigo);
        txttipomante.setText(tipomante);
        txtdescripcion.setText(descripcion);

        btnguarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nota = edtnota.getText().toString();
                CapturaMantenimiento.AsyncCallWS task = new CapturaMantenimiento.AsyncCallWS();
                task.execute();
            }
        });
    }

    public String registraactivadadrealizada(String idmantenimiento, String status, String nota) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#registraactividadrealizada";
        String METHOD_NAME = "registraactividadrealizada";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("status", status);
            Request.addProperty("nota",nota);
            Request.addProperty("idmantenimiento", idmantenimiento);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            Object response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return resultString;
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            String datosempleados = registraactivadadrealizada(idtipomante, status, nota);
            if (!datosempleados.equals("")) {
                resultString = datosempleados;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            Toast.makeText(CapturaMantenimiento.this,  resultString.toString(), Toast.LENGTH_LONG).show();

            finish();
        }
    }
}
