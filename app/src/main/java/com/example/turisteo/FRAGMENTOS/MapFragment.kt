package com.example.turisteo.FRAGMENTOS

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.turisteo.BD.AppDatabase
import com.example.turisteo.BD.BaseDatos
import com.example.turisteo.BD.Entities.Place
import com.example.turisteo.BD.Lugar
import com.example.turisteo.R
import com.example.turisteo.UnicaLocalizacion
import com.example.turisteo.UnicaLocalizacion.CoordenadasGPS
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
                    GoogleMap.OnMapLongClickListener, SearchView.OnQueryTextListener,
                    UnicaLocalizacion.LocationCallback {

    private lateinit var mapView: MapView
    private lateinit var gMap: GoogleMap
    private lateinit var etNombreLugar: EditText
    private lateinit var etDescripcionLugar: EditText
    private lateinit var tvLatitudDialog: TextView
    private lateinit var tvLongitudDialog: TextView
    private lateinit var dialogView: View
    private lateinit var dialogCreado: AlertDialog
    private lateinit var posicionMap: LatLng
    private lateinit var baseDatos: BaseDatos
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var miLocalizacion: CoordenadasGPS
    private lateinit var unicaLocalizacion: UnicaLocalizacion
    private lateinit var lugares: ArrayList<Lugar>
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById<MapView>(R.id.fragment_map_mapView)
        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            context
        )
        unicaLocalizacion = UnicaLocalizacion(this)
        askPermissions()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        getMyLocation()
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val miSearch = menu.findItem(R.id.app_bar_search)
        searchView = miSearch.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = getString(R.string.hint_busqueda)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) gMap = p0
        gMap.setInfoWindowAdapter(this)

        val maps = context?.resources?.getStringArray(R.array.array_tipo_mapa)

        val mapType = sharedPreferences.getString("tipo_mapa", maps?.get(0))

        setMapType(maps!!, mapType)

        gMap.clear()

        fillMap()

        gMap.setOnMapLongClickListener(this)

    }

    private fun setMapType(maps: Array<String>, mapType: String?) {
        when(mapType) {
            maps[0] -> gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            maps[1] -> gMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            maps[2] -> gMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            maps[3] -> gMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
    }

    /**
     * obtiene la localizacion del usuario,y en caso de que no tenga permisos, los solicita
     */
    @SuppressLint("MissingPermission")
    fun getMyLocation() {
        try {
            unicaLocalizacion.requestSingleUpdate(context)
        } catch (e: SecurityException) {
            e.printStackTrace()
            showErrorLocationMessage()
        }
    }

    override fun getInfoWindow(p0: Marker?): View {
        TODO("Not yet implemented")
    }

    override fun getInfoContents(p0: Marker?): View {
        TODO("Not yet implemented")
    }

    override fun onMapLongClick(p0: LatLng?) {
        TODO("Not yet implemented")
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onNewLocationAvailable(location: UnicaLocalizacion.CoordenadasGPS?) {
        TODO("Not yet implemented")
    }

    private fun askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 100
                )
            }
        }
    }

    //pick up result of permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //permission granted, we put user's location
            Log.d("Request Permission", "Permisos concedidos")
            return
        }
        showErrorLocationMessage()
    }

    private fun fillMap() {
        val marks: List<Place>? = context?.let { AppDatabase.getInstance(it).PlaceDao().getPlaces() }
            for (i in marks?.indices!!) {
                val place = marks[i]
                addMark(place)
        }
    }

    fun addMark(place: Place?) {
        //obtengo las preferencias de la pantalla de preferencias
        val colorsMark = context!!.resources.getStringArray(R.array.array_color_marca)
        //guardo en un string el color de marca seleccionado en preferencias
        val colorMark = sharedPreferences.getString("color_marca", colorsMark[0])
        addMarkToMap(place!!, colorMark, colorsMark)
    }

    private fun addMarkToMap(place: Place, colorMark: String?,
                             colorsMark:Array<String>) {
        when(colorMark) {
            //red
            colorsMark[0] -> {
                createMarker(place, createCoordinates(place), BitmapDescriptorFactory.HUE_RED)
            }
            //cian
            colorsMark[1] -> {
                createMarker(place, createCoordinates(place), BitmapDescriptorFactory.HUE_CYAN)
            }
            //blue
            colorsMark[2] -> {
                createMarker(place, createCoordinates(place), BitmapDescriptorFactory.HUE_BLUE)
            }
            //green
            colorsMark[3] -> {
                createMarker(place, createCoordinates(place), BitmapDescriptorFactory.HUE_GREEN)
            }
            //magenta
            colorsMark[4] -> {
                createMarker(place, createCoordinates(place), BitmapDescriptorFactory.HUE_MAGENTA)
            }
            //orange
            colorsMark[5] -> {
                createMarker(place, createCoordinates(place), BitmapDescriptorFactory.HUE_ORANGE)
            }
        }
    }

    private fun addMyLocation() {

    }

    private fun createCoordinates(place: Place): LatLng {
        return LatLng(place.latitude, place.longitude)
    }

    private fun createMarker(place: Place, latLng: LatLng, colorIcon:Float) {
        gMap.addMarker(
            MarkerOptions().title(place.name)
                .snippet(
                    (place.description + "\n"
                            + getString(R.string.latitud) + " " + place.latitude + "\n"
                            + getString(R.string.longitud) + " " + place.longitude)
                )
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(colorIcon))
        )
    }

    private fun showErrorLocationMessage() {
        Toast.makeText(context, getString(R.string.error_mi_localizacion), Toast.LENGTH_SHORT)
            .show()
    }
}