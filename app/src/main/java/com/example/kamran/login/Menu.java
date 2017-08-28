package com.example.kamran.login;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
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
import java.util.Timer;
import java.util.TimerTask;

public class Menu extends AppCompatActivity {
    //pequeño cambioa
    public static final int SIGNATURE_ACTIVITY = 1;
    String usuarioid, nombreusuario;

    String IDTIPOMANTE[];
    String FECHA[];
    String CODIGO[];
    String DESCRIPCION[];
    String TIPOMANTE[];
    String STATUS[];

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
        setContentView(R.layout.activity_menu);

        Timer timer;
        MyTimerTask mytimertask;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");

        Intent intent = getIntent();
        nombreusuario = intent.getStringExtra("nombreusuario");
        usuarioid= intent.getStringExtra("usuarioid");

        Button btnhorariospersonal = (Button) findViewById(R.id.btnhorariospersonal);
        Button btnbitacora = (Button) findViewById(R.id.btnbitacora);
        Button btntareasprogramadas = (Button) findViewById(R.id.btntareaspersonal);

        btnhorariospersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HorarioEmpleadosTurnos.class);
                intent.putExtra("usuarioid", usuarioid);
                intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        btnbitacora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BitacoraServicio.class);
                intent.putExtra("usuarioid", usuarioid);
                intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        btntareasprogramadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TareasProgramadasMenu.class);
                intent.putExtra("usuarioid", usuarioid);
                intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        timer = new Timer();
        mytimertask = new MyTimerTask();

        timer.schedule(mytimertask, 1000, 885000);
    }

    public void showNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Notificaiones Utilerias y Servicios");
        builder.setContentText("Existen tareas pendientes a realizar el dia de hoy");
        Intent intent = new Intent(this, desglosemantenimientos.class);
        intent.putExtra("mntoestacion", numestacion);
        intent.putExtra("mntotipo", "3");
        intent.putExtra("noregistra", "SI");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(desglosemantenimientos.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0,builder.build());
    }

    class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Menu.AsyncCallWS task = new Menu.AsyncCallWS();
                    task.execute();
                }
            });
        }
    }

    public String obtendatosactividadesprogramadas(String EstacionID) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtenNotificacionesDiarias";
        String METHOD_NAME = "obtenNotificacionesDiarias";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("estacionid", EstacionID);

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

            String datosempleados = obtendatosactividadesprogramadas(numestacion);

            if (!datosempleados.trim().equals(""))
            {
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
                                } else if (tagname.equalsIgnoreCase("IDPROGRAMAMANTENIMIENTO")) {
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

                    showNotification();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            ListView lvnotificaciones= (ListView) findViewById(R.id.lvnotificaciones);

            if (CODIGO != null)
            {
                Menu.customadapter ca = new Menu.customadapter();
                lvnotificaciones.setAdapter(ca);
            }

            //Toast.makeText(parametrosinventarios.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    class customadapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customnotificationlayout ,null);

            TextView txtcodigo= (TextView) convertView.findViewById(R.id.txtCodigo);
            TextView txttipomante = (TextView) convertView.findViewById(R.id.txtTipoMante);
            TextView txtdesc = (TextView) convertView.findViewById(R.id.txtDescripcion);

            txtcodigo.setText(CODIGO[position].toString());
            txttipomante.setText(TIPOMANTE[position].toString());
            txtdesc.setText(DESCRIPCION[position].toString());

            return convertView;
        }
    }
}
