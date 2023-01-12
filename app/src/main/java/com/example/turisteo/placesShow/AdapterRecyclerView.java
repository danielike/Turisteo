package com.example.turisteo.RECYCLERVIEW;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turisteo.BD.BaseDatos;
import com.example.turisteo.BD.Lugar;
import com.example.turisteo.R;

import java.util.ArrayList;

public class AdapterRecyclerView extends RecyclerView.Adapter {

    private ArrayList<Lugar> lugares;
    private Lugar lugarSeleccionadoAdapter;
    private ElementosCardView elementosCardView;
    private BaseDatos baseDatos;

    public AdapterRecyclerView(ArrayList<Lugar> lugares, BaseDatos baseDatos) {
        this.lugares = lugares;
        this.baseDatos = baseDatos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //creamos el layout inflater segun el contexto del padre
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //inflamos el layout del recycler view
        View view = inflater.inflate(R.layout.cardview_recyclerview, parent, false);
        //creamos el CardView y le pasamos el view del recyclerView, y la base de datos
        ElementosCardView viewHolder = new ElementosCardView(view, baseDatos, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //se obtiene el lugar pinchado por el usuario en el RecyclerView
        Lugar lugarCargar = lugares.get(holder.getAdapterPosition());
        ElementosCardView miViewHolder = (ElementosCardView) holder;

        elementosCardView = miViewHolder;

        View view = holder.itemView;
        //se coloca como tag en el View el lugar segun su posicion en el adapter
        view.setTag(lugarCargar);
        lugarSeleccionadoAdapter = lugarCargar;
        miViewHolder.getTvNombre().setText(lugarCargar.getNombre());
    }

    @Override
    public int getItemCount() {
        return lugares.size();
    }

    /**
     * elimina un Lugar del adapterRecyclerView
     * @param position
     */
    public void removeItem(int position){
        lugares.remove(position);
        notifyItemRemoved(position);
    }
}
