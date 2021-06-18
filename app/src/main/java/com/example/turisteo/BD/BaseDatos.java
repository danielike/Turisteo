package com.example.turisteo.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.turisteo.MainActivity;

import java.util.ArrayList;


public class BaseDatos extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "marcas.db";
    private static final int VERSION_BD = 1;
    private SQLiteDatabase bd;

    public BaseDatos(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }


    /**
     * abre BD en modo escritura
     */
    public void abrirBDModoEscritura(){
        if(bd == null){
            bd = getWritableDatabase();
        }else{
            bd.close();
            bd = getWritableDatabase();
        }
    }

    /**
     * busca lugares en la BD
     * @param query
     * @return
     */
    public ArrayList<Lugar> buscarLugares(String query){
        ArrayList<Lugar> lugares = new ArrayList<>();
        String consulta ="SELECT nombre_lugar, descripcion_lugar, latitud, longitud FROM LUGARES" +
                " WHERE nombre_lugar LIKE '"+query+"'";
        Cursor cursor = bd.rawQuery(consulta, null);
        while(cursor.moveToNext()){
            Lugar lugar = new Lugar(cursor.getString(0), cursor.getString(1),
                    cursor.getDouble(2), cursor.getDouble(3));
            lugares.add(lugar);
        }
        return lugares;
    }
    /**
     * abre BD en modo lectura
     */
    public void abrirBDModoLectura(){
        if(bd == null){
            bd = getReadableDatabase();
        }else{
            bd.close();
            bd = getReadableDatabase();
        }
    }

    /**
     * cierra la BD
     */
    public void cerrarBD(){
        if(bd != null){
            bd.close();
            bd = null;
        }
    }

    /**
     * obtiene los lugares almacenados en BD
     * @return lugares de BD
     */
    public ArrayList<Lugar> getLugares(){
        String consulta = "SELECT id, nombre_lugar, descripcion_lugar, latitud, longitud " +
                "FROM LUGARES ORDER BY nombre_lugar;";
        ArrayList<Lugar> lugares = new ArrayList<>();
        Cursor cursor = bd.rawQuery(consulta, null);
        while(cursor.moveToNext()){
            Lugar lugar = new Lugar(cursor.getInt(0), cursor.getString(1),
                     cursor.getString(2), cursor.getDouble(3),
                    cursor.getDouble(4));
            lugares.add(lugar);
        }
        return lugares;
    }

    /**
     * Añade un lugar a la BD
     * @param lugar
     * @return numReg
     */
    public int insertarLugar(Lugar lugar){
        ContentValues datos= new ContentValues();
        datos.put("nombre_lugar", lugar.getNombre());
        datos.put("descripcion_lugar", lugar.getDescripcion());
        datos.put("latitud", lugar.getLatitud());
        datos.put("longitud", lugar.getLongitud());
        int numReg = (int) bd.insert("LUGARES", null, datos);
        return numReg;
    }

    /**
     * elimina lugar de BD
     * @param lugar
     * @return numReg afectado
     */
    public int removeLugar(Lugar lugar){
        String whereClause = "id= ?";
        String[] whereArgs = {String.valueOf(lugar.getId())};
        int numReg = bd.delete("LUGARES", whereClause, whereArgs);
        return numReg;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE LUGARES (\n" +
                "    id                INTEGER       PRIMARY KEY AUTOINCREMENT\n" +
                "                                    NOT NULL,\n" +
                "    nombre_lugar      VARCHAR (30)  NOT NULL,\n" +
                "    descripcion_lugar VARCHAR (100) NOT NULL,\n" +
                "    latitud           DOUBLE  NOT NULL,\n" +
                "    longitud          DOUBLE  NOT NULL\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
