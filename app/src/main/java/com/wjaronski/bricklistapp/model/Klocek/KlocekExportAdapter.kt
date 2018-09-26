package com.wjaronski.bricklistapp.model.Klocek

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import com.wjaronski.bricklistapp.R

class KlocekExportAdapter(
        context: Context,
        val list: ArrayList<Klocek>
) : ArrayAdapter<Klocek>(context, R.layout.klocek_export_row, list) {



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        return super.getView(position, convertView, parent)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // 2. Get rowView from inflater
        val rowView = inflater.inflate(R.layout.klocek_export_row, parent, false)

        // 3. Get the two text view from the rowView
        val descriptionView = rowView.findViewById<View>(R.id.descriptionTextView) as TextView
//        val qtyView = rowView.findViewById<View>(R.id.qtyTextView) as TextView

//        val incBtn = rowView.findViewById<View>(R.id.increaseButton) as Button
//        val decBtn = rowView.findViewById<View>(R.id.decreaseButton) as Button

        val r = rowView.findViewById<View>(R.id.radioButton) as RadioButton
        val rU = rowView.findViewById<View>(R.id.radioButtonU) as RadioButton
        val rN = rowView.findViewById<View>(R.id.radioButtonN) as RadioButton

//        r.isChecked = true
        when (list.get(position).Condition){
            ""->{r.isChecked=true}
            "U"->{rU.isChecked=true}
            "N"->{rN.isChecked=true}
            else->{r.isChecked=true}
        }

        r.setOnClickListener {uncheck(rU, rN); list.get(position).Condition = "";descriptionView.text = updateText(list.get(position))}
        rU.setOnClickListener {uncheck(r, rN); list.get(position).Condition = "U";descriptionView.text = updateText(list.get(position))}
        rN.setOnClickListener {uncheck(r, rU); list.get(position).Condition = "N";descriptionView.text = updateText(list.get(position))}

//        incBtn.setOnClickListener { updateQty(qtyView,list.get(position), true, rowView) }
//        decBtn.setOnClickListener { updateQty(qtyView,list.get(position), false, rowView) }

        // 4. Set the text for textView
        descriptionView.text = updateText(list.get(position))
//        qtyView.text = list.get(position).getQty()
//        labelView.text = list.get(position).getOpis()
//        valueView.text = list.get(position).getDescription()

        // 5. return rowView

//        val klocek = list.get(position)
//        rowView.setBackgroundColor( if (klocek.qty - klocek.qtyInStore >0) Color.rgb(255,230,230) else Color.rgb(230,255,230))
        return rowView

    }

    private fun uncheck(r1: RadioButton, r2: RadioButton) {
        r1.isChecked = false
        r2.isChecked = false
    }

    private fun updateText(klocek:Klocek): String{
        return "${klocek.getDesc()} [${klocek.Condition}]"
    }

    fun setConditionTo(s: String) {
        list.forEach { it.Condition=s }
        notifyDataSetChanged()
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


}