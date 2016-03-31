package antonioejemplo.com.libreriavoley2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;//Cola de peticiones de Volley. se encarga de gestionar automáticamente el envió de las peticiones, la administración de los hilos, la creación de la caché y la publicación de resultados en la UI.
    //JsonObjectRequest jsArrayRequest;//Tipo de petición Volley utilizada...
    //private static String URL_BASE="http://api.openweathermap.org/data/2.5/weather?q=Madrid,ES&appid=b1b15e88fa797225412429c1c50c122a";
    private TextView txtrespuesta, txtcoordenadas,txtbase,txtwind,txtclima,txtinformacion,txtnubes;
    private EditText txtciudad;
    //private EditText txtpais;
    private Button btnResultado;
    private FloatingActionButton btnfloat;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private  Double longitud;
    private Double latitud;
    private Double temperatura;
    private int presion;
    private int humedad;
    private Double temp_min;
    private Double temp_max;
    private Double speed;
   // private Double deg;No incluimos la desviación del tiempo
    private int clouds;
   private String id;
    private String main2;
    private String description;
    private String icon;



    private static final String LOGTAG = "LibreriaVoley2";//Constante para gestionar la escritura en el Log
    private CollapsingToolbarLayout ctlLayout;

    private static long back_pressed;//Contador para cerrar la app al pulsar dos veces seguidas el btón de cerrar. Se gestiona en el evento onBackPressed

    private static int index = -1;
    private static int top = -1;
    private LinearLayoutManager llmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //App bar style NoActionBar es obligatorio...
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
       setSupportActionBar(toolbar);
        llmanager = new LinearLayoutManager(this);
        llmanager.setOrientation(LinearLayoutManager.VERTICAL);

        ctlLayout = (CollapsingToolbarLayout) findViewById(R.id.ctlLayout);
        ctlLayout.setTitle("El tiempo");

         FloatingActionButton btnfloat=(FloatingActionButton)findViewById(R.id.btnFab);

        txtciudad = (EditText) findViewById(R.id.txtciudad);
        //txtpais = (EditText) findViewById(R.id.txtpais);
        txtrespuesta = (TextView) findViewById(R.id.txtrespuesta);
        txtclima= (TextView) findViewById(R.id.txtclima);
        txtcoordenadas = (TextView) findViewById(R.id.txtcoordenadas);
        txtbase= (TextView) findViewById(R.id.textbase);
        txtwind= (TextView) findViewById(R.id.txtwind);
        btnResultado = (Button) findViewById(R.id.btnresultado);
        txtinformacion=(TextView)findViewById(R.id.txtinformacion);
        txtnubes=(TextView)findViewById(R.id.txtnubes);

        btnResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verclima();
                borraDatos();
            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



        /*Log.i(LOGTAG,main_weather+descripcion+icono);
        Log.e(LOGTAG, main_weather + descripcion + icono);
        Log.d(LOGTAG, main_weather + descripcion + icono);
        Log.v(LOGTAG,main_weather+descripcion+icono);*/


        btnfloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Si no informan la ciudad no accedemos a la API...
                if(txtciudad.getText().toString().equals("")) {

                    Snackbar snack = Snackbar.make(v, R.string.ciudad, Snackbar.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) snack.getView();
                    group.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    snack.show();
                }
                else {
                    verclima(v);
                    //Lineas para ocultar el teclado virtual (Hide keyboard)
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


    }

    public void verclima(View v) {

        String ciudad = txtciudad.getText().toString();
        //String pais = txtpais.getText().toString();
        String pais = "";

        /*String parametro = String.format(txtciudad.getText().toString(), txtpais.getText().toString());
        String URL_BASE = "http://api.openweathermap.org/data/2.5/weather?q=";
        String URL_BASE2 = "http://api.openweathermap.org/data/2.5/weather?q=" + txtciudad.getText().toString() + txtpais.getText().toString() + "&appid=b1b15e88fa797225412429c1c50c122a";
        String URL_BASE3 = "http://api.openweathermap.org/data/2.5/weather?q=" + txtciudad.getText().toString() + "," + txtpais.getText().toString() + "&appid=b1b15e88fa797225412429c1c50c122a";
        String URL_BASE4 = "http://api.openweathermap.org/data/2.5/weather?q=" + txtciudad.getText().toString() + "," + txtpais.getText().toString();
*/
        //Se ha solicitado una nueva apikey a la página del WS.Correo yahoo petyl@

        //&units=metric..Sistema métrico
        //&lang=ES..Lenguaje
        String patron = "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&units=metric&lang=ES&appid=ffff21faa9754c531c28bad3ddc19605";
        //String patron = "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&units=metric&appid=ffff21faa9754c531c28bad3ddc19605";
        //String patron = "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=ffff21faa9754c531c28bad3ddc19605";

        String Uri = String.format(patron, ciudad, pais);

        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                Uri,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response2) {

                        try {

                            for (int i = 0; i < response2.length(); i++) {

                                //RECOJEMOS DATOS EN VARIABLES:
                                //coord
                                JSONObject coord = response2.getJSONObject("coord");
                                 longitud = coord.getDouble("lon");
                                 latitud = coord.getDouble("lat");

                                //main
                                JSONObject main = response2.getJSONObject("main");
                                temperatura = main.getDouble("temp");
                                presion = main.getInt("pressure");
                                humedad = main.getInt("humidity");
                                temp_min = main.getDouble("temp_min");
                                temp_max = main.getDouble("temp_max");

                                //wind
                                JSONObject wind = response2.getJSONObject("wind");
                                speed=wind.getDouble("speed");
                                //deg=wind.getDouble("deg");

                                //Nubes clouds
                                JSONObject nubes = response2.getJSONObject("clouds");
                                clouds=nubes.getInt("all");


                                //"weather"--EN ESTE CASO ES UN ARRAY DE OBJETOS:
                                JSONArray json_array =response2.getJSONArray("weather");
                                //String description2="";
                                //Log.v(LOGTAG, "Respuesta en JSON- valor descripción antes" + description);
                                for (int z = 0; z < json_array.length(); z++) {
                                    id=json_array.getJSONObject(z).getString("id");
                                    main2=json_array.getJSONObject(z).getString("main");
                                    description=json_array.getJSONObject(z).getString("description");
                                    icon=json_array.getJSONObject(z).getString("icon");
                                    //Pruebas:
                                    //description2=json_array.getJSONObject(1).getString("description");
                                    //Log.v(LOGTAG, "Respuesta en JSON- valor descripción después"+description);
                                    //Log.v(LOGTAG, "Respuesta en JSON- valor descripción2 después"+description2);

                                }



                            }


///////////////////////////////////////////////////////////////////////7///////////////////////////////
/*                            //Para que nos dé la descripción del tiempo. se trata de un array.
                            int posicioninicio=response2.getString("weather").indexOf("description")+13;//Posición que ocupa el carácter o la cadena dentro de una cadena...POsición 20 inicio
                            int posicionfin=0;
                            int coma=0;
                            String sCadena="";

                            //Recorremos la cadena definida arriba y contamos el número de comas que hay en ella y su número de caracteres. Cuando sean tres dejamos de contar.
                        for(int i=0;i<response2.getString("weather").length();i++) {
                                sCadena = sCadena + response2.getString("weather").charAt(i);
                                //Según se va construyendo el string contamos las comas...
                                if(sCadena.endsWith(","))	{
                                    coma++;
                                    //System.out.println("soy coma: "+coma);
                                }
                                //Mientras haya menos de 3 comas seguimos contando el número de caracteres a tener en cuenta en el substring final
                                if (coma<3){
                                    posicionfin++;
                                    //System.out.println("soy posicionfin:"+posicionfin);
                                }
                            }
                            String fin=sCadena.substring(posicioninicio, posicionfin);*/
////////////////////////////////////////////////////////////////////////////////////////

                            Double velocidad=  (speed*3.6);
                            //PINTAMOS LOS DATOS EN EL LAYOUT:
                            txtcoordenadas.setText(String.format("Coordenadas: Longitud: %s -Latitud: %s", longitud.toString(), latitud.toString()));
                            txtclima.setText(String.format("Temperatura : %sºC  Mín: %sºC  Máx: %sºC", temperatura.toString(), temp_min.toString(), temp_max.toString()));
                            txtnubes.setText(String.format("Nubosidad: %d%%", clouds));
                            txtbase.setText(String.format("Presión: %dmbar Humedad: %d%%", presion, humedad));
                            txtwind.setText(String.format("Datos del viento: Velocidad: %s km/h ", velocidad));
                            txtinformacion.setText(String.format("Resultado obtenido sobre la meteorología de %s", txtciudad.getText()));
                            //txtrespuesta.setText("Descripción del tiempo: "+" "+fin.toString());
                            //txtrespuesta.setText("Descripción del tiempo: "+" "+id+main2+description+icon);
                            txtrespuesta.setText(String.format("Descripción del tiempo:  %s", description));
                            //btnResultado.setVisibility(View.VISIBLE);
                            btnResultado.setEnabled(true);



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(LOGTAG, "Error Respuesta en JSON: " +description);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOGTAG, "Error Respuesta en JSON: " + error.getMessage());
                        //txtrespuesta.setText(error.toString());
                        txtinformacion.setText(R.string.conexionerror);

                        Snackbar snack = Snackbar.make(txtinformacion,R.string.conexionerror, Snackbar.LENGTH_LONG);
                        ViewGroup group = (ViewGroup) snack.getView();
                        group.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        snack.show();

                    }
                }
        );


        // Añadir petición a la cola
        requestQueue.add(jsArrayRequest);

    }

    public void borraDatos(){

        txtcoordenadas.setText("");
        txtciudad.setText("");
        txtrespuesta.setText("");
        txtinformacion.setText("");
        txtwind.setText("");
        txtclima.setText("");
        txtbase.setText("");
        txtnubes.setText("");
    }

    @Override
    public void onBackPressed() {
/**
 * Cierra la app cuando se ha pulsado dos veces seguidas en un intervalo inferior a dos segundos.
 */

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), R.string.eltiempo_salir, Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
        // super.onBackPressed();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://antonioejemplo.com.libreriavoley2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://antonioejemplo.com.libreriavoley2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
