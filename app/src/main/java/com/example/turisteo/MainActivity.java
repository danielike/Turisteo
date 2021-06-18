package com.example.turisteo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.turisteo.BD.BaseDatos;
import com.example.turisteo.FRAGMENTOS.FragmentoListaLugares;
import com.example.turisteo.FRAGMENTOS.FragmentoMapa;
import com.example.turisteo.FRAGMENTOS.FragmentoPreferencias;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private BaseDatos baseDatos;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navview);
        drawerLayout = findViewById(R.id.drawerLayout);
        //se crea el boton de desplegado del navigation view, al que se le pasa el contexto, la toolbar
        //y el drawerLayout en el que irá
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.abrir_drawer,R.string.cerrar_drawer);
        drawerLayout.addDrawerListener(toggle);
        //hace el efecto de animación al abrir y cerrar el navigation View
        toggle.syncState();


        baseDatos = new BaseDatos(this);
        baseDatos.abrirBDModoEscritura();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentoMapa(baseDatos))
                    .commit();
        }


        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if(!searchView.isIconified()){
            searchView.onActionViewCollapsed();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) search.getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miSalir) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_lugares:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentoListaLugares(baseDatos)).commit();
                getSupportActionBar().setTitle(R.string.lista_lugares);
                break;
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentoMapa(baseDatos))
                        .commit();
                getSupportActionBar().setTitle(R.string.title_activity_maps);
                break;
            case R.id.nav_preferencias:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentoPreferencias()).commit();
                getSupportActionBar().setTitle(R.string.preferencias);
        }
        drawerLayout.closeDrawers();
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseDatos.cerrarBD();
    }


}