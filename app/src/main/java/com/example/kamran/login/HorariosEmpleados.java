package com.example.kamran.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class HorariosEmpleados extends AppCompatActivity {

    String IMAGE[];
    String NOMBRES[];
    String DESCRIPCION[];
    String REFERENCIA[];

    String TAG = "Response";
    String ip, resultString, numestacion, turno;

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
            //To finish your current acivity
            setResult(R.id.logout);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios_empleados);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");
        Intent intent = getIntent();
        turno = intent.getStringExtra("turno");

        Button btnsalir = (Button) findViewById(R.id.btnSalir);

        HorariosEmpleados.AsyncCallWS task = new HorariosEmpleados.AsyncCallWS();
        task.execute();

        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String obtendatosempleados(String EstacionID) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtendatosEmpleados";
        String METHOD_NAME = "obtendatosEmpleados";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("estacionid", EstacionID);
            Request.addProperty("turno", turno);

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

            String datosempleados = obtendatosempleados(numestacion);

            if (!datosempleados.trim().equals("")) {

                try {

                    String substr = datosempleados.substring(0, datosempleados.indexOf("<"));
                    datosempleados = datosempleados.substring(datosempleados.indexOf("<"));
                    int i = Integer.parseInt(substr);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(datosempleados));

                    xpp.next();
                    int eventType = xpp.getEventType();

                    int c = 0;

                    IMAGE = new String[i];
                    NOMBRES = new String[i];
                    DESCRIPCION = new String[i];
                    REFERENCIA = new String[i];

                    String text = "";
                    String empleados = "";
                    String descripcion = "";
                    String foto = "";
                    String referencia = "";

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
                                    NOMBRES[c] = empleados;
                                    DESCRIPCION[c] = descripcion;
                                    IMAGE[c] = foto;
                                    REFERENCIA[c] = referencia;
                                    c++;

                                } else if (tagname.equalsIgnoreCase("IDEMPLEADO")) {
                                    empleados = "Empleado..[" + text + "]" + ": ";
                                } else if (tagname.equalsIgnoreCase("HORAINICIO")) {
                                    descripcion = "HORA DE ENTRADA: [" + text + "]" + "  HORA DE SALIDA: [";
                                } else if (tagname.equalsIgnoreCase("HORAFINAL")) {
                                    descripcion = descripcion + text + "]";
                                } else if (tagname.equalsIgnoreCase("NOMBREEMPLEADO")) {
                                    empleados = empleados + text;
                                } else if (tagname.equalsIgnoreCase("REFERENCIA")) {
                                    referencia = "Referencia: "+ text;
                                } else if (tagname.equalsIgnoreCase("FOTOBASE64")) {
                                    foto = text;
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
                    finish();
                } finally {
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView lvEmpleados= (ListView) findViewById(R.id.lvEmpleados);

            customadapter ca = new customadapter();
            lvEmpleados.setAdapter(ca);

            Log.i(TAG, "onPostExecute");
            //Toast.makeText(parametrosinventarios.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    class customadapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (NOMBRES!=null && NOMBRES.length > 0 ) {
                return NOMBRES.length;
            }
            else
                return 0;
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
            convertView = getLayoutInflater().inflate(R.layout.customlayout,null);

            if (NOMBRES.length > 0)
            {
            try {
                ImageView imageview = (ImageView) convertView.findViewById(R.id.ivcustomlayout);
                TextView textview1 = (TextView) convertView.findViewById(R.id.txtcustomlayout1);
                TextView textview2 = (TextView) convertView.findViewById(R.id.txtcustomlayout2);

                //TextView textview3 = (TextView) convertView.findViewById(R.id.textView7);

                textview1.setText(NOMBRES[position].toString());
                textview2.setText(DESCRIPCION[position].toString());
                //textview3.setText(REFERENCIA[position].toString());
                byte[] decodeString = Base64.decode(IMAGE[position], Base64.DEFAULT);
                Bitmap decode = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                imageview.setImageBitmap(decode);
            }catch(Exception e){}}

            return convertView;
        }
    }
}
