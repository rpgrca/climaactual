package com.sub48.climaactual;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CiudadAdapter extends ArrayAdapter<Ciudad> {
    private Context mContext;
    private List<Ciudad> mCiudades;

    private static class CiudadViewHolder {
        private ImageView mIcono;
        private TextView mNombre;
        private TextView mDescripcion;
        private TextView mActualizacion;

        CiudadViewHolder(@NonNull View view) {
            mIcono = view.findViewById(R.id.climaImageView);
            mNombre = view.findViewById(R.id.ciudadTextView);
            mDescripcion = view.findViewById(R.id.descripcionTextView);
            mActualizacion = view.findViewById(R.id.actualizacionTextView);
        }

        ImageView getIcono() {
            return mIcono;
        }

        TextView getNombre() {
            return mNombre;
        }

        TextView getDescripcion() {
            return mDescripcion;
        }

        TextView getActualizacion() {
            return mActualizacion;
        }
    }

    CiudadAdapter(@NonNull Context context, @NonNull ArrayList<Ciudad> ciudades) {
        super(context, 0, ciudades);
        mContext = context;
        mCiudades = ciudades;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        CiudadViewHolder holder;

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_ciudad, parent, false);
            holder = new CiudadViewHolder(listItem);
            listItem.setTag(holder);
        }
        else {
            holder = (CiudadViewHolder)listItem.getTag();
        }

        Ciudad ciudad = mCiudades.get(position);

        // 1. Seteamos el icono
        if (ciudad.getIcono() == null) {
            holder.getIcono().setImageResource(R.drawable.ic_clima_default);
        }
        else {
            holder.getIcono().setImageBitmap(ciudad.getIcono());
        }

        // 2. Seteamos el nombre de la ciudad
        holder.getNombre().setText(ciudad.getNombre());

        // 3. Seteamos el clima
        String clima = mContext.getString(R.string.texto_tocar_para_refrescar);
        if (MainActivity.esCadenaValida(ciudad.getClima())) {
            if (MainActivity.esCadenaValida(ciudad.getDescripcion())) {
                clima = String.format("%s: %s", ciudad.getClima(), ciudad.getDescripcion());
            } else {
                clima = ciudad.getClima();
            }

            clima = String.format(Locale.getDefault(), "%s (%d %s, Hum. %d%%)", clima, ciudad.getTemperatura(), ciudad.getAbreviaturaUnidad(), ciudad.getHumedad());
        }

        holder.getDescripcion().setText(clima);

        // 4. Seteamos la fecha de ultima actualizacion
        String actualizacion = "";
        if (ciudad.getUltimaActualizacion() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM/dd/yyyy hh:mm:ss aa"),
                    Locale.getDefault());
            actualizacion = mContext.getString(R.string.texto_ultima_actualizacion, dateFormat.format(ciudad.getUltimaActualizacion()));
        }

        holder.getActualizacion().setText(actualizacion);

        return listItem;
    }
}
