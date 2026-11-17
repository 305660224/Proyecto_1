package com.example.proyecto_1_pataaldia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/** Adaptador sencillo para mostrar las citas veterinarias en un ListView. */
class CitaAdapter(
    context: Context,
    citas: MutableList<Cita>
) : ArrayAdapter<Cita>(context, 0, citas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_cita, parent, false)
        val cita = getItem(position) ?: return vista

        vista.findViewById<TextView>(R.id.tvItemCitaMotivo).text = cita.motivo
        vista.findViewById<TextView>(R.id.tvItemCitaFechaHora).text =
            "${fechaCitaParaMostrar(cita.fechaHora)} a las ${horaCitaParaMostrar(cita.fechaHora)}"
        vista.findViewById<TextView>(R.id.tvItemCitaVeterinaria).text = cita.veterinaria

        val estado = estadoDeCita(cita.fechaHora)
        vista.findViewById<TextView>(R.id.tvItemCitaEstado).apply {
            text = estado.etiqueta
            setTextColor(estado.color)
        }
        return vista
    }
}
