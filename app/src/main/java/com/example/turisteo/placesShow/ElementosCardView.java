package com.example.turisteo.RECYCLERVIEW;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turisteo.BD.BaseDatos;
import com.example.turisteo.BD.Lugar;
import com.example.turisteo.FRAGMENTOS.FragmentoMapa;
import com.example.turisteo.MainActivity;
import com.example.turisteo.R;

import java.io.Serializable;
import java.util.ArrayList;

public class ElementosCardView extends RecyclerView.ViewHolder {
    private TextView tvNombre;
    private BaseDatos baseDatos;
    private AdapterRecyclerView adapterRecyclerView;

    public ElementosCardView(@NonNull final View itemView, final BaseDatos baseDatos,
                             final AdapterRecyclerView adapterRecyclerView) {
        super(itemView);
        //se le pasan al ViewHolder la BD y el adapterRecyclerView para que pueda usarlos
        //para eliminar elementos
        this.baseDatos = baseDatos;
        this.adapterRecyclerView =adapterRecyclerView;
        final MainActivity activity = (MainActivity) itemView.getContext();
        //localizamos cada uno de los componentes del cardview
        tvNombre = itemView.findViewById(R.id.cardview_tvNombreLugar);
        //eventos de cardView
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lugar lugar = (Lugar) v.getTag();
                AlertDialog alertDialog = new AlertDialog.Builder(activity)
                        .setTitle(tvNombre.getText().toString())
                        .setMessage(lugar.getDescripcion()+"\n\n"+activity.getString(R.string.latitud)+" "+lugar.getLatitud()
                        +"\n"+activity.getString(R.string.longitud)+" " +lugar.getLongitud())
                        .setCancelable(true)
                        .setNegativeButton(R.string.cerrar_dialogo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Lugar lugar = (Lugar) v.getTag();
                AlertDialog alertDialog = new AlertDialog.Builder(activity)
                        .setTitle(R.string.eliminar_lugar)
                        .setMessage(R.string.mensaje_eliminar_lugar)
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //eliminamos el lugar seleccionado del cardView y de la base datos
                                baseDatos.removeLugar(lugar);
                                adapterRecyclerView.removeItem(getAdapterPosition());
                            }
                        })
                        .create();
                alertDialog.show();
                return false;
            }
        });
    }
    public TextView getTvNombre() {
        return tvNombre;
    }

    public void setTvNombre(TextView tvNombre) {
        this.tvNombre = tvNombre;
    }

}

