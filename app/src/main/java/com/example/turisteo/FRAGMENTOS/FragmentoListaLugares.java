package com.example.turisteo.FRAGMENTOS;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turisteo.BD.BaseDatos;
import com.example.turisteo.BD.Lugar;
import com.example.turisteo.R;
import com.example.turisteo.RECYCLERVIEW.AdapterRecyclerView;

import java.util.ArrayList;

public class FragmentoListaLugares extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Lugar> lugares;
    private Lugar lugarSeleccionado;
    private View view;
    private BaseDatos baseDatos;

    public FragmentoListaLugares(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }


    @Nullable
    //@Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflamos el layout para el fragmento
        view = inflater.inflate(R.layout.fragment_lista_lugares, container, false);
        //creamos el recyclerview
        recyclerView = view.findViewById(R.id.fragment_lista_lugares_recyclerView);

        /*baseDatos = new BaseDatos(getContext());
        baseDatos.abrirBDModoEscritura();*/

        inicializarDatos();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //baseDatos.cerrarBD();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem miSearch = menu.findItem(R.id.app_bar_search);
        miSearch.setVisible(false);
    }

    /**
     * inicializa el recycler View y le mete datos
     */
    private void inicializarDatos(){
        lugares = baseDatos.getLugares();
        AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(lugares, baseDatos);
        //se coloca el adaptador con los datos al recyclerView
        recyclerView.setAdapter(adapterRecyclerView);
        //se crea un layout manager con el sentido de orientacion escogido
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

    }
}
