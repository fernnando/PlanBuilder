package co.fddittmar.planbuilder.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.fddittmar.planbuilder.R;
import co.fddittmar.planbuilder.data.model.Exercise;

/**
 * Adapter to represent a list of Exercises items
 */

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ViewHolder> {
    private List<Exercise> exercises;


    public ExercisesAdapter(List<Exercise> exercises ){
        this.exercises = exercises;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from( parent.getContext() )
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData( exercises.get( position ) );
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvReps;
        private TextView tvWeight;

        private ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_exercise_name);
            tvReps = (TextView) itemView.findViewById(R.id.tv_exercise_reps);
            tvWeight = (TextView) itemView.findViewById(R.id.tv_exercise_weight);
        }

        private void setData( Exercise exercise ){

            tvName.setText( exercise.getName() );
            tvReps.setText( String.valueOf(exercise.getReps()) );
            tvWeight.setText( String.valueOf(exercise.getWeight()) );
        }
    }
}
