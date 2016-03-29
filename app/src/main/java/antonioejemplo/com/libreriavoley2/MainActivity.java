package antonioejemplo.com.libreriavoley2;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;//Cola de peticiones de Volley. se encarga de gestionar automáticamente el envió de las peticiones, la administración de los hilos, la creación de la caché y la publicación de resultados en la UI.
    //JsonObjectRequest jsArrayRequest;//Tipo de petición Volley utilizada...
    //private static String URL_BASE="http://api.openweathermap.org/data/2.5/weather?q=Madrid,ES&appid=b1b15e88fa797225412429c1c50c122a";
    private TextView txtrespuesta, txtcoordenadas,txtbase,txtwind,txtclima,txtinformacion;
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
    private Double deg;

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

        btnResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verclima();
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

                verclima();
            }
        });


    }

    public void verclima() {

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

                                //JSONObject c = cli.getJSONObject(i);

                                //RECOJEMOS DATOS EN VARIABLES
                                JSONObject coord = response2.getJSONObject("coord");
                                 longitud = coord.getDouble("lon");
                                 latitud = coord.getDouble("lat");

                                JSONObject main = response2.getJSONObject("main");
                                temperatura = main.getDouble("temp");
                                presion = main.getInt("pressure");
                                humedad = main.getInt("humidity");
                                temp_min = main.getDouble("temp_min");
                                temp_max = main.getDouble("temp_max");

                                JSONObject wind = response2.getJSONObject("wind");
                                speed=wind.getDouble("speed");
                                deg=wind.getDouble("deg");

//                                JSONObject weather = response2.getJSONObject("weather");
//                                descripcion=weather.getString("description");
                                //Log.v(LOGTAG,descripcion);

                                /*response2.getString("weather");
                                descripcion=response2.getString("weather");
                                Log.v(LOGTAG,descripcion);*/


                                /*El objeto que devuelve Volley no es un array y weather sí lo es. Esto no funciona bien.*/
                                //for (int j = 0; j < response2.getString("weather").length(); j++) {
                                  //JSONObject json=response2.getJSONObject("weather");
//
//                                    main_weather = json.getString("main");
//                                    descripcion = json.getString("description");
//                                    icono = json.getString("icon");

//                                    JSONObject json=response2.getJSONObject(String.valueOf(i));
//                                    descripcion = json.getString("description");

                                    /*JSONObject json=response2.getJSONObject("weather");
                                    descripcion=json.getString("description");

                                    Log.v(LOGTAG,descripcion);*/

                                    /*JSONArray array=response2.getJSONArray("weather");
                                    descripcion=array.getString(j);*/

                                    //FUNCIONA pero se desarrolla abajo sin necesidad de un for
                                    /*response2.getString("weather");
                                    descripcion=response2.getString("weather");*/

                                //}Fin del for


                                //SUBITEM CON LAS HABILIDADES
                                /*JSONObject habilidades = c.getJSONObject("Habilidades");
                                String fuerza = habilidades.getString("Fuerza");
                                String espiritu = habilidades.getString("Espiritu");
                                String fortaleza = habilidades.getString("Fortaleza");*/
                            }


                           // response2.getString("weather");
                           // Log.v(LOGTAG, response2.getString("weather"));
                            /*for (int z = 0; z < response2.length(); z++) {

                                //main_weather = json.getString("main");
                                main_weather =  response2.getString("main");
                                descripcion = response2.getString("description");
                                icono = response2.getString("icon");

                            }
*/
                            //Para que nos dé la descripción del tiempo. se trata de un array.Traido de Eclipse:
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

                            String fin=sCadena.substring(posicioninicio, posicionfin);

                            txtcoordenadas.setText("Coordenadas: "+"Longitud: "+longitud.toString()+" -Latitud: "+latitud.toString());
                            txtclima.setText("Temperatura : "+temperatura.toString()+"ºC  Mín: "+temp_min.toString()+"ºC  Máx: "+temp_max.toString()+"ºC");
                            txtbase.setText("Presión: "+presion+"mmHg"+" Humedad: "+humedad+"%");
                            txtwind.setText("Datos del viento: "+"Velocidad: "+speed.toString()+"km/h "+"Deg: "+deg.toString());
                            txtrespuesta.setText("Descripción del tiempo: "+" "+fin.toString());
                            txtinformacion.setText("Resultado de las condiciones meteorológicas de " +txtciudad.getText());


//                            response2.getString("lon");
//                            response2.getString("lat");
//                            String c= (String) response2.get(response2.getString("lon"));
//                            String latitud= (String) response2.get(response2.getString("lat"));

                           /* JSONObject jsonlongitud=response2.getJSONObject("lon");

                            double  longitud=jsonlongitud.getDouble(String.valueOf(jsonlongitud));
                            JSONObject jsonlatitud=response2.getJSONObject("lat");
                            String latitud=jsonlongitud.getString("lat");*/

                            //JSONObject jobject = new JSONObject(aux);
                            //int valor= jobject .getInt("suma");
                            //response2.getString("coord").indexOf(",");



                            /*int cadenabuscada=response2.getString("coord").indexOf(",");
                            String longitud = response2.getString("coord").substring(1, cadenabuscada);
                            String latitud=response2.getString("coord").substring(cadenabuscada + 1, (response2.getString("coord").length()-1));
*/


                            // txtcoordenadas.setText(jsonlongitud.toString());
                           /* txtcoordenadas.setText(response2.getString("coord").substring(1,).toString());*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOGTAG, "Error Respuesta en JSON: " + error.getMessage());
                        //txtrespuesta.setText(error.toString());

                    }
                }
        );
        // Añadir petición a la cola
        requestQueue.add(jsArrayRequest);






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
