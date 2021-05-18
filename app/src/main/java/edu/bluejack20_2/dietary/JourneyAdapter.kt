package edu.bluejack20_2.dietary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class JourneyAdapter (
    var journeyItem: MutableList<JourneyData>
): RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder>() {
    inner class JourneyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.journey, parent, false)

        return JourneyViewHolder(view)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<MaterialTextView>(R.id.journey_date).text = journeyItem[position].date
            findViewById<MaterialTextView>(R.id.total_cal).text = resources.getString(
                R.string.calories,
                journeyItem[position].totalCalories.toString()
            )
            findViewById<MaterialTextView>(R.id.breakfast_meal).text = journeyItem[position].breakfastMeal
            findViewById<MaterialTextView>(R.id.lunch_meal).text = journeyItem[position].lunchMeal
            findViewById<MaterialTextView>(R.id.dinner_meal).text = journeyItem[position].dinnerMeal
            findViewById<MaterialTextView>(R.id.snack).text = journeyItem[position].snack
        }
    }

    override fun getItemCount(): Int {
        return journeyItem.size
    }
}