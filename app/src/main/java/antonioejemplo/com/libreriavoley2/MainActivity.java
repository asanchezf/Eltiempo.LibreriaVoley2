package antonioejemplo.com.libreriavoley2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;//Cola de peticiones de Volley. se encarga de gestionar automáticamente el envió de las peticiones, la administración de los hilos, la creación de la caché y la publicación de resultados en la UI.

    //JsonObjectRequest jsArrayRequest;//Tipo de petición Volley utilizada...
    //private static String URL_BASE="http://api.openweathermap.org/data/2.5/weather?q=Madrid,ES&appid=b1b15e88fa797225412429c1c50c122a";
    private TextView txtrespuesta, txtcoordenadas,txtbase,txtwind,txtclima,txtinformacion,txtnubes,txtestation;
    private EditText txtciudad;
    //private EditText txtpais;
    private Button btnResultado;
    private FloatingActionButton btnfloat;
    private ImageView icono;
    private ImageView imgToolbar;

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
    private String nombre;

    private String icon;
    private String png;

    private String uriprueba;

    private static final String LOGTAG = "LibreriaVoley2";//Constante para gestionar la escritura en el Log
    private CollapsingToolbarLayout ctlLayout;

    private static long back_pressed;//Contador para cerrar la app al pulsar dos veces seguidas el btón de cerrar. Se gestiona en el evento onBackPressed

    private static int index = -1;
    private static int top = -1;
    private LinearLayoutManager llmanager;

    //private Request.Priority priorityinmediato;
    private String Uri;

   private JsonObjectRequest myjsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.back);

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
        icono=(ImageView)findViewById(R.id.icono);
        txtestation=(TextView)findViewById(R.id.txtstation);

        imgToolbar=(ImageView)findViewById(R.id.imgToolbar);

        /*String parametro = String.format(txtciudad.getText().toString(), txtpais.getText().toString());
        String URL_BASE = "http://api.openweathermap.org/data/2.5/weather?q=";
        String URL_BASE2 = "http://api.openweathermap.org/data/2.5/weather?q=" + txtciudad.getText().toString() + txtpais.getText().toString() + "&appid=b1b15e88fa797225412429c1c50c122a";
        String URL_BASE3 = "http://api.openweathermap.org/data/2.5/weather?q=" + txtciudad.getText().toString() + "," + txtpais.getText().toString() + "&appid=b1b15e88fa797225412429c1c50c122a";
        String URL_BASE4 = "http://api.openweathermap.org/data/2.5/weather?q=" + txtciudad.getText().toString() + "," + txtpais.getText().toString();
*/
        //Se ha solicitado una nueva apikey a la página del WS.Correo yahoo petyl@

        //&units=metric..Sistema métrico
        //&lang=ES..Lenguaje

        //String patron = "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&units=metric&appid=ffff21faa9754c531c28bad3ddc19605";
        //String patron = "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=ffff21faa9754c531c28bad3ddc19605";

        //Log.v(LOGTAG,"Uri antes de llamada: "+Uri);

        requestQueue = Volley.newRequestQueue(this);
        cargarImagenToolbar();

        btnResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borraDatos();
            }
        });



        if (btnfloat != null) {
            btnfloat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Si no informan la ciudad no accedemos a la API...
                    if (txtciudad.getText().toString().equals("")) {

                        Snackbar snack = Snackbar.make(v, R.string.ciudad, Snackbar.LENGTH_LONG);
                        ViewGroup group = (ViewGroup) snack.getView();
                        group.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        snack.show();
                    } else {

                        //REALIZAMOS LA PRIMERA PETICIÓN. DEBE SER DE PRIORITY INMEDIATE,DENTRO DE ELLA SE REALIZA LA SEGUNDA PARA OBTENR EL ICONO.
                        immediateRequest2();

                        //Lineas para ocultar el teclado virtual (Hide keyboard)
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
        }


    }


    private void immediateRequest2() {
        //PETICIÓN DATOS DEL TIEMPO:

        // Etiqueta utilizada para cancelar la petición
        String tag_json_obj = "json_obj_req";
        //priorityinmediato = Request.Priority.IMMEDIATE;
        String ciudad = txtciudad.getText().toString();
        //String pais = txtpais.getText().toString();
        String pais = "";
        String patronUrl = "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&units=metric&lang=ES&appid=ffff21faa9754c531c28bad3ddc19605";
        Uri = String.format(patronUrl, ciudad, pais);


        Log.v(LOGTAG, "Ha llegado a immediateRequest. Uri: " + Uri);

         myjsonObjectRequest = new MyJsonRequest(
                Request.Method.GET,
                Uri,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response2) {

                        try {

                            for (int i = 0; i < response2.length(); i++) {

                                //RECOJEMOS DATOS EN VARIABLES:

                                //"weather"--EN ESTE CASO ES UN ARRAY DE OBJETOS:
                                JSONArray json_array = response2.getJSONArray("weather");
                                //String description2="";
                                //Log.v(LOGTAG, "Respuesta en JSON- valor descripción antes" + description);
                                for (int z = 0; z < json_array.length(); z++) {
                                    icon = json_array.getJSONObject(z).getString("icon");
                                    id = json_array.getJSONObject(z).getString("id");
                                    main2 = json_array.getJSONObject(z).getString("main");
                                    description = json_array.getJSONObject(z).getString("description");

                                    //Pruebas:
                                    //description2=json_array.getJSONObject(1).getString("description");
                                    //Log.v(LOGTAG, "Respuesta en JSON- valor descripción después"+description);
                                    //Log.v(LOGTAG, "Respuesta en JSON- valor descripción2 después"+description2);

                                }



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
                                speed = wind.getDouble("speed");
                                //deg=wind.getDouble("deg");

                                //Nubes clouds
                                JSONObject nubes = response2.getJSONObject("clouds");
                                clouds = nubes.getInt("all");


//                                JSONObject nombrefinal = response2.getJSONObject("name");
//                                nombre = nombre.ge;
                                 nombre = response2.getString("name");


                            }


                            int velocidad = (int) (speed * 3.6);
                            //PINTAMOS LOS DATOS EN EL LAYOUT:
                            txtcoordenadas.setText(String.format("Coordenadas: Longitud: %s -Latitud: %s", longitud.toString(), latitud.toString()));
                            txtclima.setText(String.format("Temperatura : %sºC  Mín: %sºC  Máx: %sºC", temperatura.toString(), temp_min.toString(), temp_max.toString()));
                            txtnubes.setText(String.format("Nubosidad: %d%%", clouds));
                            txtbase.setText(String.format("Presión: %dmbar Humedad: %d%%", presion, humedad));
                            txtwind.setText(String.format("Datos del viento: Velocidad: %s km/h ", velocidad));

                            txtinformacion.setText(String.format("Resultado obtenido sobre la meteorología de %s", txtciudad.getText()));

                            txtestation.setText(String.format("Estación meteorológica de %s", nombre));

                            //txtrespuesta.setText("Descripción del tiempo: "+" "+fin.toString());
                            txtrespuesta.setText("Descripción del tiempo: " + " " + description);
                            //txtrespuesta.setText(String.format("Descripción del tiempo:  %s", description));
                            //btnResultado.setVisibility(View.VISIBLE);
                            btnResultado.setEnabled(true);
                            // uriprueba="http://openweathermap.org/img/w/"+icon+""+png+"&appid=ffff21faa9754c531c28bad3ddc19605";
                            png = ".png";

                            //REALIZAMOS LA SEGUNDA PETICIÓN. PRIORITY NORMAL.
                            lowRequest();

                            icono.setVisibility(View.VISIBLE);
                            uriprueba = String.format("http://openweathermap.org/img/w/%s%s", icon, png);

                            Log.v(LOGTAG, "JSON uriprueba: " + uriprueba);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(LOGTAG, "Error Respuesta en JSON: " + description);
                        }

                        //priority = Request.Priority.IMMEDIATE;

                    }//fin onresponse

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOGTAG, "Error Respuesta en JSON: " + error.getMessage());
                        //txtrespuesta.setText(error.toString());
                        txtinformacion.setText(R.string.conexionerror);

                        Snackbar snack = Snackbar.make(txtinformacion, R.string.conexionerror, Snackbar.LENGTH_LONG);
                        ViewGroup group = (ViewGroup) snack.getView();
                        group.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        snack.show();

                    }
                }


        ) ;


        // Añadir petición a la cola
        //requestQueue.add(myjsonObjectRequest);
        //requestQueue.add(request);
        // Añadimos la petición a la cola de peticiones
        AppController.getInstance().addToRequestQueue(myjsonObjectRequest, tag_json_obj);
        //AppController.getInstance().getRequestQueue().getCache().invalidate(Uri, true);
    }





    private void immediateRequest() {
        //PETICIÓN DATOS DEL TIEMPO:
 /*SE HA DEJADO PARA PODER REUTILIZAR EL CÓDIGO*/
 /*UTILIZANDO ESTA PETICIÓN NO ES NECESARIO REALZIAR UNA SEGUNDA PARA OBTENER EL ICONO, PERO A CAMBIO LOS ICONOS DEBEN ESTAR EN LA PROPIA APP COMO ARCHIVOS .PNG
 * GUARDADOS EN DRAWABLE. POSTERIORMENTE SE ACABAN LLAMANDO CON UN IF*/
        //priorityinmediato = Request.Priority.IMMEDIATE;
        String ciudad = txtciudad.getText().toString();
        //String pais = txtpais.getText().toString();
        String pais = "";
        String patronUrl = "http://api.openweathermap.org/data/2.5/weather?q=%s,%s&units=metric&lang=ES&appid=ffff21faa9754c531c28bad3ddc19605";
        Uri = String.format(patronUrl, ciudad, pais);
        Log.v(LOGTAG, "Ha llegado a immediateRequest. Uri: " + Uri);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
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

                               /* JSONStringer name = response2.JSONStringer();
                                nombre = coord.getString("name");*/

                                //main
                                JSONObject main = response2.getJSONObject("main");
                                temperatura = main.getDouble("temp");
                                presion = main.getInt("pressure");
                                humedad = main.getInt("humidity");
                                temp_min = main.getDouble("temp_min");
                                temp_max = main.getDouble("temp_max");

                                //wind
                                JSONObject wind = response2.getJSONObject("wind");
                                speed = wind.getDouble("speed");
                                //deg=wind.getDouble("deg");

                                //Nubes clouds
                                JSONObject nubes = response2.getJSONObject("clouds");
                                clouds = nubes.getInt("all");

                                //"weather"--EN ESTE CASO ES UN ARRAY DE OBJETOS:
                                JSONArray json_array = response2.getJSONArray("weather");
                                //String description2="";
                                //Log.v(LOGTAG, "Respuesta en JSON- valor descripción antes" + description);
                                for (int z = 0; z < json_array.length(); z++) {
                                    id = json_array.getJSONObject(z).getString("id");
                                    main2 = json_array.getJSONObject(z).getString("main");
                                    description = json_array.getJSONObject(z).getString("description");
                                    icon = json_array.getJSONObject(z).getString("icon");
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

                            int velocidad = (int) (speed * 3.6);
                            //PINTAMOS LOS DATOS EN EL LAYOUT:
                            txtcoordenadas.setText(String.format("Coordenadas: Longitud: %s -Latitud: %s", longitud.toString(), latitud.toString()));
                            txtclima.setText(String.format("Temperatura : %sºC  Mín: %sºC  Máx: %sºC", temperatura.toString(), temp_min.toString(), temp_max.toString()));
                            txtnubes.setText(String.format("Nubosidad: %d%%", clouds));
                            txtbase.setText(String.format("Presión: %dmbar Humedad: %d%%", presion, humedad));
                            txtwind.setText(String.format("Datos del viento: Velocidad: %s km/h ", velocidad));

                            //txtinformacion.setText(String.format("Resultado obtenido sobre la meteorología de %s", txtciudad.getText()));
                            txtinformacion.setText(String.format("Resultado obtenido sobre la meteorología de %s", nombre));

                            //txtrespuesta.setText("Descripción del tiempo: "+" "+fin.toString());
                            txtrespuesta.setText("Descripción del tiempo: " + " " + description);
                            //txtrespuesta.setText(String.format("Descripción del tiempo:  %s", description));
                            //btnResultado.setVisibility(View.VISIBLE);
                            btnResultado.setEnabled(true);
                            // uriprueba="http://openweathermap.org/img/w/"+icon+""+png+"&appid=ffff21faa9754c531c28bad3ddc19605";
                            png = ".png";

                            uriprueba = String.format("http://openweathermap.org/img/w/%s%s", icon, png);

                            Log.v(LOGTAG, "JSON uriprueba: " + uriprueba);

                            //icono.setImageResource(Integer.parseInt(componerimagen));
                            //String componerimagen="R.drawable."+icon;
                            //icono.setImageResource(Integer.parseInt(componerimagen));



                            if(icon.equals("01d")){
                                icono.setImageResource(R.drawable.sol);
                            }else if (icon.equals("02d")){
                                icono.setImageResource(R.drawable.claros);
                            }else if (icon.equals("03d")){
                                icono.setImageResource(R.drawable.nubes);
                            }else if (icon.equals("04d")){
                                icono.setImageResource(R.drawable.masnubes);
                            }else if (icon.equals("09d")){
                                icono.setImageResource(R.drawable.nubeslluvia);
                            }else if (icon.equals("10d")){
                                icono.setImageResource(R.drawable.lluviasol);
                            }else if (icon.equals("11d")){
                                icono.setImageResource(R.drawable.maslluvia);
                            }else if (icon.equals("13d")){
                                icono.setImageResource(R.drawable.nieve);
                            }else if (icon.equals("50d")){
                                icono.setImageResource(R.drawable.niebla);
                            }else if (icon.equals("01n")){
                                icono.setImageResource(R.drawable.nocheclara);
                            }else if (icon.equals("02n")){
                                icono.setImageResource(R.drawable.nochenubes);
                            }else if (icon.equals("03n")){
                                icono.setImageResource(R.drawable.nubes);
                            }else if (icon.equals("04n")){
                                icono.setImageResource(R.drawable.masnubes);
                            }else if (icon.equals("10n")){
                                icono.setImageResource(R.drawable.nubeslluvia);
                            }else if (icon.equals("11n")){
                                icono.setImageResource(R.drawable.maslluvia);
                            }else if (icon.equals("13n")){
                                icono.setImageResource(R.drawable.nieve);
                            }else if (icon.equals("50n")){
                                icono.setImageResource(R.drawable.niebla);
                            }
                            else{
                                icono.setImageResource(R.drawable.sol);
                            }

                            icono.setVisibility(View.VISIBLE);



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(LOGTAG, "Error Respuesta en JSON: " + description);
                        }

                        //priority = Request.Priority.IMMEDIATE;

                    }//fin onresponse

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOGTAG, "Error Respuesta en JSON: " + error.getMessage());
                        //txtrespuesta.setText(error.toString());
                        txtinformacion.setText(R.string.conexionerror);

                        Snackbar snack = Snackbar.make(txtinformacion, R.string.conexionerror, Snackbar.LENGTH_LONG);
                        ViewGroup group = (ViewGroup) snack.getView();
                        group.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        snack.show();

                    }
                }


        );


        // Añadir petición a la cola
        requestQueue.add(jsonObjectRequest);
    }




        // PETICIÓN PARA OBTENER LA IMAGEN.
        private void lowRequest() {
 /*PARA QUE FUNCIONE CORRECTAMENTE HEMOS TENIDO QUE MODIFICAR LAS CLASES ORIGINALES DE VOLLEY QUE OBTIENEN LAS IMÁGENES Y LOS JSONOBJECT PARA PODER ESTABLECER UNA PRIORITY
 * DISTINTA A LA QUE VIENE POR DEFECTO. ADEMÁS ESTA SEGUNDA PETICIÓN SE REALIZA EN LA RESPUESTA DE LA PRIMERA PORQUE NECESITA DATOS DE ÉSTA.*/
 /*UTILIZANDO ESTA PETICIÓN SE OBTIENE EL ICONO CUYO CÓDIGO HEMOS OBTENIDO PREVIAMENTE EN LA RESPUESTA DE LA PRIMERA PETICIÓN*/
        /*En este situación usaremos el tipo ImageRequest para obtener cada imagen. Solo necesitamos concatenar
        la url absoluta que fue declarada como atributo, más la dirección relativa que cada imagen trae consigo
        en el objeto JSON*/

            // Etiqueta utilizada para cancelar la petición
            String tag_json_obj_img = "json_obj_req_img";
            //http://openweathermap.org/img/w/10d.png&appid=ffff21faa9754c531c28bad3ddc19605
            String patronIcono = "http://openweathermap.org/img/w/%s%s";
            String uriIcono = String.format(patronIcono, icon, png);

            //String uriprueba="http://openweathermap.org/img/w/"+icon+""+png+"&appid=ffff21faa9754c531c28bad3ddc19605";

            //String uriprueba="http://openweathermap.org/img/w/04d.png&appid=ffff21faa9754c531c28bad3ddc19605";
            //String uriprueba="http://openweathermap.org/img/w/01n.png";
            Log.v(LOGTAG, "Respuesta en JSON uriprueba: " + uriprueba);
            Log.v(LOGTAG, "Respuesta en JSON icono: " + icon);
            Log.v(LOGTAG, "Respuesta en JSON png: " + png);
            Log.v(LOGTAG, "Respuesta en JSON uriIcono: " + uriIcono);
            //urlIcono="http://openweathermap.org/img/w/10d.png";


           // requestQueue = Volley.newRequestQueue(this);

            //priority = Request.Priority.LOW;
            ImageRequest request = new MyImageRequest(

                    uriIcono,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            icono.setImageBitmap(bitmap);
                            Log.v(LOGTAG, "Error Respuesta en JSON icono: " + icon);
                        }
                    },
                    0, 0, null, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            icono.setImageResource(R.drawable.sol);//Si de error ponemos un sol...
                            Log.d(LOGTAG, "Error en respuesta Bitmap: " + error.getMessage());
                        }
                    }
            );

            // Añadir petición a la cola
            //requestQueue.add(jsArrayRequest);
            //requestQueue.add(myjsonObjectRequest);

            //requestQueue.add(request);

            AppController.getInstance().addToRequestQueue(request, tag_json_obj_img);

            //AppController.getInstance().getRequestQueue().getCache().remove(uriIcono);//Eliminar cache

            //AppController.getInstance().getRequestQueue().getCache().invalidate(uriIcono, true);//Desactivar cache



        }

    private void cargarImagenToolbar() {
 /*PARA QUE FUNCIONE CORRECTAMENTE HEMOS TENIDO QUE MODIFICAR LAS CLASES ORIGINALES DE VOLLEY QUE OBTIENEN LAS IMÁGENES Y LOS JSONOBJECT PARA PODER ESTABLECER UNA PRIORITY
 * DISTINTA A LA QUE VIENE POR DEFECTO. ADEMÁS ESTA SEGUNDA PETICIÓN SE REALIZA EN LA RESPUESTA DE LA PRIMERA PORQUE NECESITA DATOS DE ÉSTA.*/
 /*UTILIZANDO ESTA PETICIÓN SE OBTIENE EL ICONO CUYO CÓDIGO HEMOS OBTENIDO PREVIAMENTE EN LA RESPUESTA DE LA PRIMERA PETICIÓN*/
        /*En este situación usaremos el tipo ImageRequest para obtener cada imagen. Solo necesitamos concatenar
        la url absoluta que fue declarada como atributo, más la dirección relativa que cada imagen trae consigo
        en el objeto JSON*/

        // Etiqueta utilizada para cancelar la petición
        String tag_json_obj_img2 = "json_obj_req_img2";
        //http://openweathermap.org/img/w/10d.png&appid=ffff21faa9754c531c28bad3ddc19605
        //String patronIcono = "http://openweathermap.org/img/w/%s%s";
        //String uriIcono = String.format(patronIcono, icon, png);

        String url="http://wiki.openstreetmap.org/w/images/5/50/Leaflet-OpenWeatherMap.png";

        //String uriprueba="http://openweathermap.org/img/w/"+icon+""+png+"&appid=ffff21faa9754c531c28bad3ddc19605";

        //String uriprueba="http://openweathermap.org/img/w/04d.png&appid=ffff21faa9754c531c28bad3ddc19605";
        //String uriprueba="http://openweathermap.org/img/w/01n.png";
      /*  Log.v(LOGTAG, "Respuesta en JSON uriprueba: " + uriprueba);
        Log.v(LOGTAG, "Respuesta en JSON icono: " + icon);
        Log.v(LOGTAG, "Respuesta en JSON png: " + png);
        Log.v(LOGTAG, "Respuesta en JSON uriIcono: " + uriIcono);*/
        //urlIcono="http://openweathermap.org/img/w/10d.png";


        // requestQueue = Volley.newRequestQueue(this);

        //priority = Request.Priority.LOW;
        ImageRequest request = new MyImageRequest(

                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        //icono.setImageBitmap(bitmap);
                        imgToolbar.setImageBitmap(bitmap);
                        Log.v(LOGTAG, "Respuesta en JSON imgToolbar: " + icon);
                    }
                },
                0, 0, null, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        icono.setImageResource(R.drawable.sol);//Si de error ponemos un sol...
                        Log.d(LOGTAG, "Error en respuesta Bitmap imgToolbar: " + error.getMessage());
                    }
                }
        );

        // Añadir petición a la cola
        //requestQueue.add(jsArrayRequest);
        //requestQueue.add(myjsonObjectRequest);

        //requestQueue.add(request);

        AppController.getInstance().addToRequestQueue(request, tag_json_obj_img2);

        //AppController.getInstance().getRequestQueue().getCache().remove(uriIcono);//Eliminar cache

        //AppController.getInstance().getRequestQueue().getCache().invalidate(uriIcono, true);//Desactivar cache



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
        icono.setVisibility(View.INVISIBLE);
        txtestation.setText("");
        imgToolbar.setImageResource(R.drawable.alberta);

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



}