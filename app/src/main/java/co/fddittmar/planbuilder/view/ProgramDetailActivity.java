package co.fddittmar.planbuilder.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.fddittmar.planbuilder.R;
import co.fddittmar.planbuilder.contracts.ProgramDetailContract;
import co.fddittmar.planbuilder.data.model.Program;
import co.fddittmar.planbuilder.presenter.ProgramDetailPresenter;
import co.fddittmar.planbuilder.view.adapter.ExercisesAdapter;

public class ProgramDetailActivity extends AppCompatActivity implements ProgramDetailContract.View {

    @BindView(R.id.tv_program_title)
    TextView tvTitle;

    @BindView(R.id.tv_start_date)
    TextView tvStartDate;

    @BindView(R.id.tv_end_date)
    TextView tvEndDate;

    @BindView(R.id.rv_list_exercises)
    RecyclerView rvExercisesList;

    ProgramDetailPresenter presenter;
    ExercisesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);
        ButterKnife.bind(this);

        if (presenter == null) {
            presenter = new ProgramDetailPresenter(this);
        }

        populateActivity();
    }
    /**
     * Method that populates the activity with the data received from the MainActivity, such as its
     * title, starting date, ending date and the list of exercises.
     */
    private void populateActivity() {
        Program program = (Program) getIntent().getSerializableExtra("program");

        setTitle(program.getTitle());

        tvTitle.setText(program.getTitle());
        tvStartDate.setText(program.getStart_date());
        tvEndDate.setText(program.getEnd_date());

        rvExercisesList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvExercisesList.setLayoutManager(layoutManager);

        adapter = new ExercisesAdapter(program.getExercises());
        rvExercisesList.setAdapter(adapter);
    }
}