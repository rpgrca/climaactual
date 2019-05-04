package com.sub48.climaactual;

class Configuracion {
    enum Unidad {
        Celsius,
        Fahrenheit,
        Kelvin
    }

    enum Lenguaje {
        Castellano,
        Ingles
    }

    @SuppressWarnings("FieldCanBeLocal")
    private String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    @SuppressWarnings("FieldCanBeLocal")
    private String API_KEY = "39ba69269a097ba23770f2162b62df72";
    private Unidad mUnidad;
    private Lenguaje mLenguaje;

    Configuracion() {
        mUnidad = getUnidadPorDefecto();
        mLenguaje = getLenguajePorDefecto();
    }

    /**
     * Retorna la unidad por defecto del sistema, Celsius
     * @return Celsius
     */
    static Unidad getUnidadPorDefecto() {
        return Unidad.Celsius;
    }

    /**
     * Retorna el lenguaje por defecto del sistema, Castellano
     * @return Castellano
     */
    static Lenguaje getLenguajePorDefecto() {
        return Lenguaje.Castellano;
    }

    Unidad getUnidad() {
        return mUnidad;
    }

    void setUnidad(Unidad unidad) {
        mUnidad = unidad;
    }

    Lenguaje getLenguaje() {
        return mLenguaje;
    }

    void setLenguaje(Lenguaje lenguaje) {
        mLenguaje = lenguaje;
    }

    String getApiUrl() {
        return API_URL;
    }

    String getApiKey() {
        return API_KEY;
    }
}
