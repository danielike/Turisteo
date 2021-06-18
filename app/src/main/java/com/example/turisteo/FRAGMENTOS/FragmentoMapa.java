package com.example.turisteo.FRAGMENTOS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.turisteo.BD.BaseDatos;
import com.example.turisteo.BD.Lugar;
import com.example.turisteo.R;
import com.example.turisteo.UnicaLocalizacion;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Objects;


public class FragmentoMapa extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
        GoogleMap.OnMapLongClickListener, SearchView.OnQueryTextListener, UnicaLocalizacion.LocationCallback
 {

    private MapView mapView;
    private GoogleMap gMap;
    private AlertDialog.Builder builder;
    private EditText etNombreLugar;
    private EditText etDescripcionLugar;
    private TextView tvLatitudDialog;
    private TextView tvLongitudDialog;
    private View dialogView;
    private AlertDialog dialogCreado;
    private LatLng posicionMap;
    private BaseDatos baseDatos;
    private SharedPreferences sharedPreferences;
    private UnicaLocalizacion.CoordenadasGPS miLocalizacion;
    private UnicaLocalizacion unicaLocalizacion;
    private ArrayList<Lugar> lugares;
    private SearchView searchView;


    public FragmentoMapa(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }


    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.fragment_map_mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        unicaLocalizacion = new UnicaLocalizacion(this);
        pedirPermisos();
        return view;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        getMyLocation();
        super.onResume();
    }

     @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem miSearch = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) miSearch.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.hint_busqueda));
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //método que gestionar lo que pasa en el mapa cuando está creado
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //colocamos el adapter de info window sobreescrito
        gMap.setInfoWindowAdapter(this);

        //obtengo el array de string tipo_mapa
        String[] mapas = getContext().getResources().getStringArray(R.array.array_tipo_mapa);
        //obtengo las preferencias de la lista de preferencias
        String tipo_mapa = sharedPreferences.getString("tipo_mapa", mapas[0]);
        //normal
        if (tipo_mapa.equalsIgnoreCase(mapas[0])){
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }//satélite
        if(tipo_mapa.equalsIgnoreCase(mapas[1])){
            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }//terrestre
        if(tipo_mapa.equalsIgnoreCase(mapas[2])){
            gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }//híbrido
        if(tipo_mapa.equalsIgnoreCase(mapas[3])){
            gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        //limpia el mapa por si hubiese alguna marca anterior
        gMap.clear();
        //llama al método y llena el gMap
        llenarMapa();
        gMap.setOnMapLongClickListener(this);
    }

    /**
     * obtiene la localizacion del usuario,y en caso de que no tenga permisos, los solicita
     */
    @SuppressLint("MissingPermission")
    public void getMyLocation(){
        try{
            unicaLocalizacion.requestSingleUpdate(getContext());
        }catch(SecurityException e){
            e.printStackTrace();
            Toast.makeText(getContext(), getString(R.string.error_mi_localizacion),Toast.LENGTH_SHORT).show();
        }
    }

    public void pedirPermisos(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        }
    }

    //método que recoge el resultado de la peticion de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //permiso concedido, podemos poner la ubicación del usuario
            Log.d("Request Permission", "Permisos concedidos");
        } else {
            Toast.makeText(getContext(), getString(R.string.error_mi_localizacion), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * llena el mapa con lugares de BD
     */
    private void llenarMapa() {
        ArrayList marcas = baseDatos.getLugares();
        for (int i = 0; i < marcas.size(); i++) {
            Lugar lugar = (Lugar) marcas.get(i);
            addMarca(lugar);
        }
    }

    /**
     * añade una marca al gMap
     * @param lugar
     */
    public void addMarca(Lugar lugar) {
        //obtengo las preferencias de la pantalla de preferencias
        String[] coloresMarca = getContext().getResources().getStringArray(R.array.array_color_marca);
        //guardo en un string el color de marca seleccionado en preferencias
        String colorMarca = sharedPreferences.getString("color_marca", coloresMarca[0]);
        //compruebo con qué color coincide la preferencia seleccionada
        //rojo
        if(colorMarca.equalsIgnoreCase(coloresMarca[0])){
            LatLng latLng = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            gMap.addMarker(new MarkerOptions().
                    title(lugar.getNombre())
                    .snippet(lugar.getDescripcion() + "\n"
                            + getString(R.string.latitud) + " " + lugar.getLatitud() + "\n"
                            + getString(R.string.longitud) + " " + lugar.getLongitud())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            return;
        }//cian
        if(colorMarca.equalsIgnoreCase(coloresMarca[1])){
            LatLng latLng = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            gMap.addMarker(new MarkerOptions().
                    title(lugar.getNombre())
                    .snippet(lugar.getDescripcion() + "\n"
                            + getString(R.string.latitud) + " " + lugar.getLatitud() + "\n"
                            + getString(R.string.longitud) + " " + lugar.getLongitud())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            return;
        }//azul
        if(colorMarca.equalsIgnoreCase(coloresMarca[2])){
            LatLng latLng = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            gMap.addMarker(new MarkerOptions().
                    title(lugar.getNombre())
                    .snippet(lugar.getDescripcion() + "\n"
                            + getString(R.string.latitud) + " " + lugar.getLatitud() + "\n"
                            + getString(R.string.longitud) + " " + lugar.getLongitud())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            return;
        }//verde
        if(colorMarca.equalsIgnoreCase(coloresMarca[3])){
            LatLng latLng = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            gMap.addMarker(new MarkerOptions().
                    title(lugar.getNombre())
                    .snippet(lugar.getDescripcion() + "\n"
                            + getString(R.string.latitud) + " " + lugar.getLatitud() + "\n"
                            + getString(R.string.longitud) + " " + lugar.getLongitud())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            return;
        }//magenta
        if(colorMarca.equalsIgnoreCase(coloresMarca[4])){
            LatLng latLng = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            gMap.addMarker(new MarkerOptions().
                    title(lugar.getNombre())
                    .snippet(lugar.getDescripcion() + "\n"
                            + getString(R.string.latitud) + " " + lugar.getLatitud() + "\n"
                            + getString(R.string.longitud) + " " + lugar.getLongitud())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            return;
        }//naranja
        if(colorMarca.equalsIgnoreCase(coloresMarca[5])){
            LatLng latLng = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            gMap.addMarker(new MarkerOptions().
                    title(lugar.getNombre())
                    .snippet(lugar.getDescripcion() + "\n"
                            + getString(R.string.latitud) + " " + lugar.getLatitud() + "\n"
                            + getString(R.string.longitud) + " " + lugar.getLongitud())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            return;
        }
    }


    /**
     * añade una marca con tu localización
     * @param myLocation
     */
    public void addMiLocalizacion(UnicaLocalizacion.CoordenadasGPS myLocation) {
        if(myLocation != null) {
            //obtengo las preferencias de la pantalla de preferencias
            String[] coloresMarca = getContext().getResources().getStringArray(R.array.array_color_marca);
            String colorMarca = sharedPreferences.getString("color_marca", coloresMarca[0]);
            //rojo
            if(colorMarca.equalsIgnoreCase(coloresMarca[0])){
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                gMap.addMarker(new MarkerOptions().title(getString(R.string.mi_localizacion))
                        .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                return;
            }//cian
            if(colorMarca.equalsIgnoreCase(coloresMarca[1])){
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                gMap.addMarker(new MarkerOptions().title(getString(R.string.mi_localizacion))
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                return;
            }//azul
            if(colorMarca.equalsIgnoreCase(coloresMarca[2])){
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                gMap.addMarker(new MarkerOptions().title(getString(R.string.mi_localizacion))
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                return;
            }//verde
            if(colorMarca.equalsIgnoreCase(coloresMarca[3])){
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                gMap.addMarker(new MarkerOptions().title(getString(R.string.mi_localizacion))
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                return;
            }//magenta
            if(colorMarca.equalsIgnoreCase(coloresMarca[4])){
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                gMap.addMarker(new MarkerOptions().title(getString(R.string.mi_localizacion))
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                return;
            }//naranja
            if(colorMarca.equalsIgnoreCase(coloresMarca[5])){
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                gMap.addMarker(new MarkerOptions().title(getString(R.string.mi_localizacion))
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                return;
            }
        }
    }


    /**
     * crea un alertDialog para agregar marcador a mapa
     * @return view del alertDialog
     */
    private View crearAlertDialog() {
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.agregar_marcador_lugar)
                .setCancelable(false)
                .setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cierra el alertDialog
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getText(R.string.aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //obtenemos el edit text de nombre lugar y su descripcion del alertDialog
                        etNombreLugar = dialogView.findViewById(R.id.layout_alertDialog_etNombreLugar);
                        etDescripcionLugar = dialogView.findViewById(R.id.layout_alertDialog_etDescripcion);

                        //validamos que ambos editText no están vacíos
                        if (etNombreLugar.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getContext(), R.string.nombre_vacio, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (etDescripcionLugar.getText().toString().trim().isEmpty()) {
                            Toast.makeText(getContext(), R.string.descripcion_vacio, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //creamos un lugar con los datos
                        Lugar lugar = new Lugar(etNombreLugar.getText().toString().trim(),
                                etDescripcionLugar.getText().toString().trim(), posicionMap.latitude,
                                posicionMap.longitude);
                        //añadimos la marca con el nombre del lugar y su descripcion al mapa
                        addMarca(lugar);
                        //añadimos la marca a la bd
                        baseDatos.insertarLugar(lugar);

                    }
                });
        //con el contexto de la aplicacion, obtenemos el servicio del layout inflater y lo almacenamos
        //en un layoutInflater
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflamos el xml deseado
        View inflador = layoutInflater.inflate(R.layout.layout_alertdialog, null);
        //coloca la view en el alertDialog
        builder.setView(inflador);
        //crea el alertDialog
        builder.create();
        //muestra alertDialog
        builder.show();
        return inflador;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(getContext());
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());
        title.setBackgroundColor(Color.CYAN);

        TextView snippet = new TextView(getContext());
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());
        snippet.setBackgroundColor(Color.CYAN);
        //añadimos las views creadas al linearLayout
        info.addView(title);
        info.addView(snippet);
        return info;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(!searchView.isIconified()){
            searchView.onActionViewCollapsed();
        }
        //se guarda el view del alertDialog
        dialogView = crearAlertDialog();
        posicionMap = latLng;
        //colocamos en el textView del alertDialog las coordenadas marcadas
        tvLatitudDialog = dialogView.findViewById(R.id.layout_alertDialog_tvLatitud);
        tvLongitudDialog = dialogView.findViewById(R.id.layout_alertDialog_tvLongitud);
        tvLatitudDialog.setText(getString(R.string.latitud) + " " + String.valueOf(posicionMap.latitude));
        tvLongitudDialog.setText(getString(R.string.longitud) + " " + String.valueOf(posicionMap.longitude));
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
            if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED && lugares.size() != 0){
                addMiLocalizacion(miLocalizacion);
                return false;
            }if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED &&
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_DENIED){
                pedirPermisos();
                return false;
            }else{
                Toast.makeText(getContext(), R.string.ningun_lugar, Toast.LENGTH_SHORT).show();
            }

        return false;
    }



    @Override
    public boolean onQueryTextChange(String newText) {
        gMap.clear();
        //se buscan los lugares a medida que el usuario introduce texto en el searchView
        lugares = baseDatos.buscarLugares(newText);
        for (int i = 0; i < lugares.size(); i++) {
            addMarca(lugares.get(i));
        }

        //si la lista de lugares está vacía, devuelve false
       if(lugares.size() != 0){
            return false;
        }
        llenarMapa();
        return true;
    }

    @Override
    public void onNewLocationAvailable(UnicaLocalizacion.CoordenadasGPS location) {
        miLocalizacion = location;
    }


}
