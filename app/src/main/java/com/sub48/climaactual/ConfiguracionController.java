package com.sub48.climaactual;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

class ConfiguracionController {
    private Configuracion mConfiguracion;
    private String CONFIG_UNIDAD = "CONFIG_UNIDAD";
    private String CONFIG_LENGUAJE = "CONFIG_LENGUAJE";

    ConfiguracionController() {
        mConfiguracion = new Configuracion();
    }

    /**
     * Graba la información de configuración dentro de SharedPreferences.
     * @param context Contexto con SharedPreferences a usar.
     */
    void grabar(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(CONFIG_UNIDAD, mConfiguracion.getUnidad().toString())
                .putString(CONFIG_LENGUAJE, mConfiguracion.getLenguaje().toString())
                .apply();
    }

    /**
     * Graba la información de configuración dentro de un Bundle. Usado para mantener
     * la configuración al rotar la pantalla.
     * @param bundle Bundle al cual grabar la información
     */
    void grabar(Bundle bundle) {
        bundle.putString(CONFIG_UNIDAD, mConfiguracion.getUnidad().toString());
        bundle.putString(CONFIG_LENGUAJE, mConfiguracion.getLenguaje().toString());
    }

    /**
     * Carga los valores de la configuracion de SharedPreferences. Usado al iniciar y
     * salir de la aplicacion.
     * @param context Contexto con SharedPreferences a usar.
     */
    void cargar(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value;

        value = prefs.getString(CONFIG_UNIDAD, Configuracion.getUnidadPorDefecto().toString());
        mConfiguracion.setUnidad(Configuracion.Unidad.valueOf(value));

        value = prefs.getString(CONFIG_LENGUAJE, Configuracion.getLenguajePorDefecto().toString());
        mConfiguracion.setLenguaje(Configuracion.Lenguaje.valueOf(value));
    }

    /**
     * Carga los valores de la configuracion de un Bundle. Usado para mantener la
     * configuración al rotar la pantalla.
     * @param bundle Bundle del cual sacar la información
     */
    void cargar(Bundle bundle) {
        try {
            mConfiguracion.setUnidad(Configuracion.Unidad.valueOf(bundle.getString(CONFIG_UNIDAD, Configuracion.getUnidadPorDefecto().toString())));
        }
        catch (Exception e) {
            mConfiguracion.setUnidad(Configuracion.Unidad.Celsius);
        }

        try {
            mConfiguracion.setLenguaje(Configuracion.Lenguaje.valueOf(bundle.getString(CONFIG_LENGUAJE, Configuracion.getLenguajePorDefecto().toString())));
        }
        catch (Exception e) {
            mConfiguracion.setLenguaje(Configuracion.Lenguaje.Ingles);
        }
    }

    /**
     * Retorna la configuración actual.
     * @return Configuración actual.
     */
    Configuracion getConfiguration() {
        return mConfiguracion;
    }
}
