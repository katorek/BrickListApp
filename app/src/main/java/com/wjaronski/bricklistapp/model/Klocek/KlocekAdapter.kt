package com.wjaronski.bricklistapp.model.Klocek
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.wjaronski.bricklistapp.R

class KlocekAdapter(
        context: Context,
        val list: ArrayList<Klocek>
) : ArrayAdapter<Klocek>(context, R.layout.klocek_row, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        return super.getView(position, convertView, parent)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // 2. Get rowView from inflater
        val rowView = inflater.inflate(R.layout.klocek_row, parent, false)

        // 3. Get the two text view from the rowView
        val descriptionView = rowView.findViewById<View>(R.id.descriptionTextView) as TextView
        val qtyView = rowView.findViewById<View>(R.id.qtyTextView) as TextView

        val incBtn = rowView.findViewById<View>(R.id.increaseButton) as Button
        val decBtn = rowView.findViewById<View>(R.id.decreaseButton) as Button


        incBtn.setOnClickListener { updateQty(qtyView,list.get(position), true, rowView) }
        decBtn.setOnClickListener { updateQty(qtyView,list.get(position), false, rowView) }

        // 4. Set the text for textView
        descriptionView.text = list.get(position).getDesc()
        qtyView.text = list.get(position).getQty()
//        labelView.text = list.get(position).getOpis()
//        valueView.text = list.get(position).getDescription()

        // 5. return rowView

        val klocek = list.get(position)
        rowView.setBackgroundColor( if (klocek.qty - klocek.qtyInStore >0) Color.rgb(255,230,230) else Color.rgb(230,255,230))
        return rowView

    }

    private fun updateQty(tv: TextView, k: Klocek, increase: Boolean, rowView: View){
        val before = k.qtyInStore

        if(increase){
            k.qtyInStore++
//            k.qtyInStore = if (k.qty > k.qtyInStore) k.qtyInStore + 1 else k.qtyInStore
        }else{
            k.qtyInStore = if (k.qtyInStore > 0) k.qtyInStore - 1 else k.qtyInStore
        }
        k.changed = before != k.qtyInStore

        tv.text = k.getQty()
        rowView.setBackgroundColor( if (k.qty - k.qtyInStore >0) Color.rgb(255,230,230) else Color.rgb(230,255,230))
    }

}