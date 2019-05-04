package com.sub48.climaactual;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;

class Ciudad implements Parcelable {
    private String mNombre;
    private Date mUltimaActualizacion;
    private String mClima;
    private String mNombreIcono;
    private String mJsonText;
    private String mDescripcion;
    private transient Bitmap mIcono;
    private int mTemperatura;
    private int mHumedad;
    private Configuracion.Unidad mUnidad;

    Ciudad(@NonNull String nombre) {
        mNombre = nombre;
        mNombreIcono = "";
        mIcono = null;
        mUltimaActualizacion = null;
        mClima = "";
        mDescripcion = "";
        mJsonText = "";
        mTemperatura = 0;
        mHumedad = 0;
        mUnidad = Configuracion.getUnidadPorDefecto();
    }

    /**
     * Parsea la informacion del JSON y setea las variables internas
     * @param jsonText El JSON obtenido de OpenWeather
     */
    void setClima(@NonNull String jsonText, @NonNull Configuracion.Unidad unidad) {
        mJsonText = jsonText;
        mUnidad = unidad;

        if (MainActivity.esCadenaValida(mJsonText)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonText);
                if (jsonObject.getInt("cod") == 200) {
                    // "coord":{"lon":-0.13,"lat":51.51}
                    //JSONObject coordinates = jsonObject.getJSONObject("coord");
                    // mLongitud = coordinates.getDouble("lon");
                    // mLatitud = coordinates.getDouble("lat");

                    // "sys":{"type":1,"id":7230,"message":0.0065,"country":"CU","sunrise":1556275510,"sunset":1556321761}
                    //JSONObject sys = jsonObject.getJSONObject("sys");
                    //mPais = sys.getString("country");

                    // "main":{"temp":7,"pressure":1012,"humidity":81,"temp_min":5,"temp_max":8}
                    JSONObject main = jsonObject.getJSONObject("main");
                    mTemperatura = main.getInt("temp");
                    mHumedad = main.getInt("humidity");

                    // "weather":[{"id":300,"main":"Drizzle","description":"light intensity drizzle","icon":"09d"}]
                    JSONArray weathers = jsonObject.getJSONArray("weather");
                    for (int index = 0; index < weathers.length(); index++) {
                        JSONObject weather = weathers.getJSONObject(index);

                        mClima = weather.getString("main");
                        mDescripcion = weather.getString("description");
                        mNombreIcono = weather.getString("icon");
                    }

                    mUltimaActualizacion = new Date();
                }
            } catch (Exception e) {
                // NADA
            }
        }
    }

    void setIcono(Bitmap icono) {
        mIcono = icono;
    }

    String getNombre() {
        return mNombre;
    }

    String getIconoUrl() {
        String resultado = "";

        if (MainActivity.esCadenaValida(mNombreIcono)) {
            resultado = String.format("http://openweathermap.org/img/w/%s.png", mNombreIcono);
        }

        return resultado;
    }

    Bitmap getIcono() {
        return mIcono;
    }

    Date getUltimaActualizacion() {
        return mUltimaActualizacion;
    }

    String getClima() {
        return mClima;
    }

    String getDescripcion() {
        return mDescripcion;
    }

    int getTemperatura() {
        return mTemperatura;
    }

    String getAbreviaturaUnidad() {
        return mUnidad == Configuracion.Unidad.Celsius? "C" :
               mUnidad == Configuracion.Unidad.Fahrenheit? "F" : "K";
    }

    int getHumedad() {
        return mHumedad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNombre);                          // 1. Nombre

        if (MainActivity.esCadenaValida(mJsonText)) {
            dest.writeInt(mJsonText.length());              // 2. Longitud del Json
            dest.writeString(mJsonText);                    // 2.1. Json
            dest.writeString(mUnidad.toString());           // 2.2. Unidad
            dest.writeLong(mUltimaActualizacion.getTime()); // 2.3. Ultima actualizacion
        }
        else {
            dest.writeInt(0);                           // 2. Longitud del Json
        }

        byte bitmapBytes[] = {};
        if (mIcono != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            mIcono.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
            bitmapBytes = byteStream.toByteArray();
        }

        dest.writeInt(bitmapBytes.length);                  // 3. Longitud del bitmap
        if (bitmapBytes.length > 0) {
            dest.writeByteArray(bitmapBytes);               // 4. Bitmap
        }
    }

    /**
     * Constructor para el creador de parcel, usado para mantener los datos al girar
     * la pantalla. Use Serializable pero tenia que marcar el bitmap como transient
     * para no
     * @param in El parcel del cual sacar la informacion
     */
    private Ciudad(Parcel in) {
        this("");

        String value;
        int length;

        mNombre = in.readString();                          // 1. Nombre
        length = in.readInt();                              // 2. Longitud del Json

        if (length > 0) {
            mJsonText = in.readString();                    // 2.1. Json
            value = in.readString();                        // 2.2. Unidad

            if (MainActivity.esCadenaValida(value)) {
                mUnidad = Configuracion.Unidad.valueOf(in.readString());
            }

            setClima(mJsonText, mUnidad);

            long time = in.readLong();                      // 2.3. Ultima actualizacion
            mUltimaActualizacion = new Date(time);
        }

        length = in.readInt();                              // 3. Longitud bitmap
        if (length > 0) {
            byte bitmapBytes[] = new byte[length];
            in.readByteArray(bitmapBytes);                  // 4. Bitmap
            mIcono = BitmapFactory.decodeByteArray(bitmapBytes, 0, length);
        }
    }

    public static final Creator<Ciudad> CREATOR = new Creator<Ciudad>() {
        @Override
        public Ciudad createFromParcel(Parcel source) {
            return new Ciudad(source);
        }

        @Override
        public Ciudad[] newArray(int size) {
            return new Ciudad[size];
        }
    };
}
