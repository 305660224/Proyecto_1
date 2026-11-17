package com.example.proyecto_1_pataaldia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/** Adaptador sencillo para mostrar las vacunas en un ListView. */
class VacunaAdapter(
    context: Context,
    vacunas: MutableList<Vacuna>
) : ArrayAdapter<Vacuna>(context, 0, vacunas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_vacuna, parent, false)
        val vacuna = getItem(position) ?: return vista

        vista.findViewById<TextView>(R.id.tvItemVacunaNombre).text = vacuna.nombre
        vista.findViewById<TextView>(R.id.tvItemVacunaFecha).text =
            "Aplicada el ${vacuna.fechaAplicacion}"
        return vista
    }
}
