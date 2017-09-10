package co.fddittmar.planbuilder.view.adapter

import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.fddittmar.planbuilder.R
import co.fddittmar.planbuilder.data.model.Program
import co.fddittmar.planbuilder.view.MainActivity
import java.util.*

/**
 * Adapter to represent a list of Programs items
 */

class ProgramsAdapter(var activity: MainActivity, programs: List<Program>, selectedList: List<Program>) : RecyclerView.Adapter<ProgramsAdapter.ViewHolder>() {
    var programs: List<Program> = ArrayList()
    var selected_usersList: List<Program> = ArrayList()


    init {
        this.programs = programs
        this.selected_usersList = selectedList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_program, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setData(holder, programs[position])

        if (selected_usersList.contains(programs[position]))
            holder.clProgramItem.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_selected_state))
        else
            holder.clProgramItem.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_normal_state))
    }

    fun setData(holder: ViewHolder, program: Program) {

        holder.tvTitle.text = program.title
        holder.tvStartDate.text = program.start_date
        holder.tvEndDate.text = program.end_date
    }

    override fun getItemCount(): Int {
        return programs.size
    }


    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_program_title) as TextView
        val tvStartDate: TextView = itemView.findViewById(R.id.tv_program_start_date) as TextView
        val tvEndDate: TextView = itemView.findViewById(R.id.tv_program_end_date) as TextView
        val clProgramItem: ConstraintLayout = itemView.findViewById(R.id.vg_program_item) as ConstraintLayout

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            activity.itemProgramClicked(programs[adapterPosition])
        }
    }
}
