package co.fddittmar.planbuilder.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import co.fddittmar.planbuilder.R
import co.fddittmar.planbuilder.data.model.Exercise

/**
 * Adapter to represent a list of Exercises items
 */

class ExercisesAdapter(private val exercises: List<Exercise>) : RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(exercises[position])
    }

    override fun getItemCount(): Int {
        return exercises.size
    }


    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_exercise_name) as TextView
        private val tvReps: TextView = itemView.findViewById(R.id.tv_exercise_reps) as TextView
        private val tvWeight: TextView = itemView.findViewById(R.id.tv_exercise_weight) as TextView

        fun setData(exercise: Exercise) {

            tvName.text = exercise.name
            tvReps.text = exercise.reps.toString()
            tvWeight.text = exercise.weight.toString()
        }
    }
}
