package com.wjaronski.bricklistapp.model.Zestaw
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.view.LayoutInflater
import com.wjaronski.bricklistapp.R


class ZestawAdapter(
        context: Context,
        val list: ArrayList<Zestaw>
) : ArrayAdapter<Zestaw>(context, R.layout.project_row, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        return super.getView(position, convertView, parent)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // 2. Get rowView from inflater
        val rowView = inflater.inflate(R.layout.project_row, parent, false)

        // 3. Get the two text view from the rowView
        val labelView = rowView.findViewById<View>(R.id.descriptionTextView) as TextView
        val valueView = rowView.findViewById<View>(R.id.value) as TextView

        // 4. Set the text for textView
        labelView.text = list.get(position).getOpis()
        valueView.text = list.get(position).getDescription()

        if(list.get(position).Active==0){
            labelView.setTextColor(Color.DKGRAY)
            valueView.setTextColor(Color.DKGRAY)
        }

        // 5. return rowView
        return rowView

    }
}