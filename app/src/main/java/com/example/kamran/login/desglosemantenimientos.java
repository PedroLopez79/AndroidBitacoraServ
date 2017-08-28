package com.example.kamran.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvView;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.IDN;
import java.security.KeyStore;

public class desglosemantenimientos extends AppCompatActivity {

    String IDTIPOMANTE[];
    String FECHA[];
    String CODIGO[];
    String DESCRIPCION[];
    String TIPOMANTE[];
    String STATUS[];

    String TAG = "Response";
    String ip, resultString, numestacion;

    String mntoestacion = "";
    String mntotipo= "";
    String noregistra = "";
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
            finish();                     //To finish your current acivit
        }
        return super.onOptionsItemSelected(item);
    }
    /**********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desglosemantenimientos);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");
        Intent intent = getIntent();
        mntoestacion = intent.getStringExtra("mntoestacion");
        mntotipo= intent.getStringExtra("mntotipo");
        noregistra = intent.getStringExtra("noregistra");

        Button btnsalir = (Button) findViewById(R.id.btnSalir);

        desglosemantenimientos.AsyncCallWS task = new desglosemantenimientos.AsyncCallWS();
        task.execute();

        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String obtendatosactividadesprogramadas(String EstacionID, String Tipo) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtendatosActividadesProgramadas";
        String METHOD_NAME = "obtendatosActividadesProgramadas";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("estacionid", EstacionID);
            Request.addProperty("TIPO",Tipo);

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

            String datosempleados = obtendatosactividadesprogramadas(mntoestacion, mntotipo);
            if (!datosempleados.equals("")) {
                String substr = datosempleados.substring(0, datosempleados.indexOf("<"));
                datosempleados = datosempleados.substring(datosempleados.indexOf("<"));
                int i = Integer.parseInt(substr);

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(datosempleados));

                    xpp.next();
                    int eventType = xpp.getEventType();

                    int c = 0;

                    FECHA = new String[i];
                    CODIGO = new String[i];
                    DESCRIPCION = new String[i];
                    TIPOMANTE = new String[i];
                    STATUS = new String[i];
                    IDTIPOMANTE = new String[i];

                    String text = "";
                    String empleados = "";
                    String descripcion = "";
                    String foto = "";

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String tagname = xpp.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (tagname.equalsIgnoreCase("SERVICES")) {
                                    // create a new instance of employee
                                    //employee = new Employee();
                                }
                                break;

                            case XmlPullParser.TEXT:
                                text = xpp.getText();
                                break;

                            case XmlPullParser.END_TAG:
                                if (tagname.equalsIgnoreCase("SERVICES")) {
                                    c++;

                                } else if (tagname.equalsIgnoreCase("FECHAAREALIZARMANTENIMIENTO")) {
                                    FECHA[c] = text;
                                } else if (tagname.equalsIgnoreCase("CODIGO")) {
                                    CODIGO[c] = text;
                                } else if (tagname.equalsIgnoreCase("CONCEPTO")) {
                                    DESCRIPCION[c] = text.trim();
                                } else if (tagname.equalsIgnoreCase("TIPOMANTO")) {
                                    TIPOMANTE[c] = text;
                                } else if (tagname.equalsIgnoreCase("STATUS")) {
                                    STATUS[c] = text;
                                } else if (tagname.equalsIgnoreCase("IDMANTENIMIENTOACTIVIDAD")) {
                                    IDTIPOMANTE[c] = text;
                                }
                                break;

                            default:
                                break;
                        }
                        eventType = xpp.next();
                    }

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView lvMantenimientos= (ListView) findViewById(R.id.lvMantenimientos);

            if (CODIGO != null)
            {
            desglosemantenimientos.customadapter ca = new desglosemantenimientos.customadapter();
            lvMantenimientos.setAdapter(ca);
            }

            Log.i(TAG, "onPostExecute");
            //Toast.makeText(parametrosinventarios.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    class customadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return CODIGO.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customactividadeslayout,null);

            final TextView txtfecha = (TextView) convertView.findViewById(R.id.txtFecha);
            final TextView txtcodigo= (TextView) convertView.findViewById(R.id.txtCodigo);
            final TextView txttipomante = (TextView) convertView.findViewById(R.id.txtTipoMante);
            final TextView txtstatus = (TextView) convertView.findViewById(R.id.txtStatus);
            final TextView txtdesc = (TextView) convertView.findViewById(R.id.txtDescripcion);
            Button btncaptura= (Button) convertView.findViewById(R.id.btncaptura);


            try {
                txtfecha.setText(FECHA[position].toString());
                txtcodigo.setText(CODIGO[position].toString());
                txttipomante.setText(TIPOMANTE[position].toString());
                txtstatus.setText(STATUS[position].toString());
                txtdesc.setText(DESCRIPCION[position].toString());
            }catch (Exception e){}

            btncaptura.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CapturaMantenimiento.class);
                    intent.putExtra("fecha", FECHA[position].toString());
                    intent.putExtra("codigo", CODIGO[position].toString());
                    intent.putExtra("tipomante", TIPOMANTE[position].toString());
                    intent.putExtra("descripcion", DESCRIPCION[position].toString());
                    intent.putExtra("IDTIPOMANTE", IDTIPOMANTE[position].toString());
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);
                }
            });

            if (noregistra.equals("SI"))
            {
                btncaptura.setVisibility(View.VISIBLE);
            }
            if (noregistra.equals("NO"))
            {
                btncaptura.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }


}
