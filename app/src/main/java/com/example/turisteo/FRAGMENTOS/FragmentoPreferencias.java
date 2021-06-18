package com.example.turisteo.FRAGMENTOS;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.turisteo.BD.BaseDatos;
import com.example.turisteo.R;

public class FragmentoPreferencias extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pantalla_preferencias, rootKey);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem miSearch = menu.findItem(R.id.app_bar_search);
        miSearch.setVisible(false);
    }
}
