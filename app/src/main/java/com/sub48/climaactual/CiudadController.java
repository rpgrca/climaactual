package com.sub48.climaactual;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CiudadController {
    private String CONFIG_LISTA_CIUDADES = "CONFIG_LISTA_CIUDADES";
    private ArrayList<Ciudad> mCiudades;
    private ArrayAdapter<Ciudad> mArrayAdapter;
    private Context mContext;

    CiudadController(@NonNull Context context) {
        mContext = context;
        mCiudades = new ArrayList<>();
    }

    /**
     * Carga la lista de ciudades de SharedPreferences.
     */
    void cargar() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> values = prefs.getStringSet(CONFIG_LISTA_CIUDADES, new HashSet<String>() {});

        mCiudades = new ArrayList<>();
        for (String ciudad: values) {
            if (MainActivity.esCadenaValida (ciudad)) {
                mCiudades.add(new Ciudad(ciudad));
            }
        }
    }

    /**
     * Carga la lista de ciudades de un Bundle. Usado al rotar la pantalla.
     * @param bundle El bundle del cual obtener la información.
     */
    void cargar(@NonNull Bundle bundle) {
        mCiudades = bundle.getParcelableArrayList(CONFIG_LISTA_CIUDADES);
    }

    /**
     * Agrega la ciudad a la lista interna solo si no existe.
     * @param ciudad La ciudad a agregar
     */
    boolean agregar(@NonNull String ciudad) {
        boolean result = false;

        if (this.encontrar(ciudad) == null) {
            mCiudades.add(new Ciudad(ciudad));
            result = true;
        }

        return result;
    }

    /**
     * Retorna la ciudad especificada.
     * @param indice El indice a obtener.
     * @return La ciudad especificada.
     */
    Ciudad obtener(int indice) {
        Ciudad resultado = null;

        if (indice >= 0 && indice < mCiudades.size()) {
            resultado = mCiudades.get(indice);
        }

        return resultado;
    }

    /**
     * Busca una ciudad de la lista por nombre
     * @param nombreCiudad El nombre a buscar
     * @return La ciudad encontrada
     */
    private Ciudad encontrar(@NonNull String nombreCiudad) {
        Ciudad resultado = null;

        if (MainActivity.esCadenaValida(nombreCiudad)) {
            for (Ciudad ciudad : mCiudades) {
                if (nombreCiudad.equals(ciudad.getNombre())) {
                    resultado = ciudad;
                    break;
                }
            }
        }

        return resultado;
    }

    /**
     * Borra la ciudad especificada de la lista.
     * @param nombreCiudad El nombre a borrar
     */
    void borrar(@NonNull String nombreCiudad) {
        Ciudad ciudad = encontrar(nombreCiudad);
        if (ciudad != null) {
            mCiudades.remove(ciudad);
        }
    }

    /**
     * Retorna la lista de nombres de ciudades actual.
     * @return La lista de nombres.
     */
    private List<String> getListadoCiudades() {
        List<String> resultado = new ArrayList<>();

        for (Ciudad ciudad: mCiudades) {
            resultado.add(ciudad.getNombre());
        }

        return resultado;
    }

    /**
     * Graba la lista de ciudades en SharedPreferences.
     */
    void grabar() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> values = new HashSet<>(getListadoCiudades());
        prefs.edit().putStringSet(CONFIG_LISTA_CIUDADES, values).apply();
    }

    /**
     * Graba la lista de ciudades en un Bundle. Usado al rotar la pantalla.
     * @param bundle El bundle del cual obtener la información.
     */
    void grabar(Bundle bundle) {
        bundle.putParcelableArrayList(CONFIG_LISTA_CIUDADES, mCiudades);
    }

    /**
     * Refresca el adaptador y con el la ListView principal.
     */
    void refrescar() {
        mArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Retorna el adaptador para el Array.
     * @return El adaptador
     */
    ArrayAdapter<Ciudad> getAdapter() {
        if (mArrayAdapter == null) {
            mArrayAdapter = new CiudadAdapter(mContext, mCiudades);

        }

        return mArrayAdapter;
    }

    /**
     * Retorna la lista de ciudades.
     * @return La lista de ciudades.
     */
    ArrayList<Ciudad> getCiudades() {
        return mCiudades;
    }
}
