package co.fddittmar.planbuilder.view.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.fddittmar.planbuilder.R;
import co.fddittmar.planbuilder.data.model.Program;
import co.fddittmar.planbuilder.view.MainActivity;

/**
 * Adapter to represent a list of Programs
 */

public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ViewHolder>{
    public MainActivity activity;
    public List<Program> programs = new ArrayList<>();
    public List<Program> selected_usersList = new ArrayList<>();


    public ProgramsAdapter( MainActivity activity, List<Program> programs, List<Program> selectedList){
        this.activity = activity;
        this.programs = programs;
        this.selected_usersList = selectedList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from( parent.getContext() )
                .inflate(R.layout.item_program, parent, false);

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        setData( holder, programs.get( position ) );

        if(selected_usersList.contains(programs.get(position)))
            holder.clProgramItem.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_selected_state));
        else
            holder.clProgramItem.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_normal_state));
    }

    private void setData(ViewHolder holder, Program program ){

        holder.tvTitle.setText(program.getTitle());
        holder.tvStartDate.setText( program.getStart_date() );
        holder.tvEndDate.setText( program.getEnd_date() );
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle;
        private TextView tvStartDate;
        private TextView tvEndDate;
        private ConstraintLayout clProgramItem;

        private ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_program_title);
            tvStartDate = (TextView) itemView.findViewById(R.id.tv_program_start_date);
            tvEndDate = (TextView) itemView.findViewById(R.id.tv_program_end_date);
            clProgramItem = (ConstraintLayout) itemView.findViewById(R.id.vg_program_item);

            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View view) {
            activity.itemProgramClicked( programs.get( getAdapterPosition() ) );
        }
    }
}
