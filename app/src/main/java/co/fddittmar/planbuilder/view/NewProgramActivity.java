package co.fddittmar.planbuilder.view;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import co.fddittmar.planbuilder.R;
import co.fddittmar.planbuilder.contracts.NewProgramContract;
import co.fddittmar.planbuilder.data.SQLiteDatabaseHelper;
import co.fddittmar.planbuilder.data.model.Exercise;
import co.fddittmar.planbuilder.presenter.NewProgramPresenter;
import co.fddittmar.planbuilder.view.adapter.ExercisesAdapter;

/**
 * Class responsible to create a new Program
 */

public class NewProgramActivity extends BaseActivity implements NewProgramContract.View{

    @BindView(R.id.et_program_title)
    EditText programTitle;

    @BindView(R.id.et_start_date)
    EditText startDate;

    @BindView(R.id.et_end_date)
    EditText endDate;

    @BindView(R.id.rv_list_exercises)
    RecyclerView rvExercisesList;

    private static final String STATE_ITEMS = "items";

    NewProgramPresenter presenter;

    DateFormat dateFormat = DateFormat.getDateInstance();
    Calendar startDateCalendar = Calendar.getInstance();
    Calendar endDateCalendar = Calendar.getInstance();
    ArrayList<Exercise> exercises = new ArrayList<>();
    ExercisesAdapter adapter;

    // Defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_program);
        ButterKnife.bind(this);

        if(savedInstanceState != null){
            exercises = (ArrayList<Exercise>) savedInstanceState.getSerializable(STATE_ITEMS);
        }

        if(presenter == null){
            presenter = new NewProgramPresenter(this, SQLiteDatabaseHelper.Companion.getInstance(this));
        }

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        setTitle(getString(R.string.new_entry));

        // Add a validation for the "Program Title", using the regex below to validate the user input
        awesomeValidation.addValidation(this, R.id.et_program_title,
                "^[A-Za-z0-9]{1,}[A-Za-z0-9][A-Za-z0-9 _-]*$", R.string.title_error);


        startDate.setText(dateFormat.format(startDateCalendar.getTime()));
        setupAdapter();
    }

    /**
     * Method to setup the exercises list adapter
     */
    private void setupAdapter() {
        rvExercisesList.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvExercisesList.setLayoutManager(layoutManager);
        adapter = new ExercisesAdapter(exercises);
        rvExercisesList.setAdapter(adapter);

    }

    /**
     * Method to save the exercises list when configuration changes,
     * and later restore it in the onCreate() method.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putSerializable(STATE_ITEMS, exercises);
    }

    /**
     * Method that creates a DatePickerDialog for the 'EndDate' input. It also disable the dates
     * before the 'StartDate'.
     */
    @OnClick(R.id.et_start_date)
    public void getStartDateFromUser(){
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.YEAR, year);
                startDate.setText(dateFormat.format(startDateCalendar.getTime()));
            }
        },
                startDateCalendar.get(Calendar.YEAR),
                startDateCalendar.get(Calendar.MONTH),
                startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Method that creates a DatePickerDialog for the 'EndDate' input. It also disable the dates
     * before the 'StartDate'.
     */
    @OnClick(R.id.et_end_date)
    public void getEndDateFromUser(){
        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.YEAR, year);
                endDate.setText(dateFormat.format(endDateCalendar.getTime()));
            }
        }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH),
                endDateCalendar.get(Calendar.DAY_OF_MONTH));

        //Disable dates prior to the 'StartDate'
        dp.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
        dp.show();

    }

    /**
     * This method will send to the presenter the user will to add a new exercise
     */
    @OnClick(R.id.container_add_exercise)
    public void addExercise(){
        presenter.onAddNewExercise();
    }

    /**
     * When the user hits the 'Save' button, it will validates all the data that the user provided.
     * If there's any error, it will show up on the screen, otherwise it will send all the data to
     * the presenter, that will handle the operation.
     */
    @OnClick(R.id.btn_save_program)
    public void saveProgram(){
        if(awesomeValidation.validate()) {
            if (datesValidation()) {
                presenter.saveProgram(
                        programTitle.getText().toString(),
                        startDate.getText().toString(),
                        endDate.getText().toString(),
                        exercises);
            } else {
                startDate.setTextColor(Color.parseColor("#ff0000"));
                showDateErrorDialog();
            }
        }
    }
    /**
     * Method that will create an error dialog informing the user that the 'EndDate' is before the
     * 'StartDate'.
     */
    private void showDateErrorDialog() {
        // Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Erro");
        builder.setMessage("Insira uma data de ínicio anterior à data de término");

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Check if the 'StartDate' comes before the 'EndDate'
     */
    private boolean datesValidation() {
        return startDateCalendar.getTimeInMillis() < endDateCalendar.getTimeInMillis();
    }

    /**
     * This method will send the 'RESULT_OK' response to the MainActivity when sucessfully adds a
     * new program and close this activity.
     */
    @Override
    public void returnToMainActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * This method will create a dialog with exercise's NAME, NUMBER OF REPS and WEIGHT. If the user
     * clicks the 'OK' button, it will add the data to the 'exercises' vector and update the adapter
     * that handles the exercises list on the screen. Otherwise, if the user clicks on the 'CANCEL'
     * option, it will just dismiss the dialog.
     */
    @Override
    public void setupNewExerciseDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_new_exercise, null);

        final EditText etName = (EditText) mView.findViewById(R.id.et_exercise_name);
        final EditText etReps = (EditText) mView.findViewById(R.id.et_exercise_reps);
        final EditText etWeight = (EditText) mView.findViewById(R.id.et_exercise_weight);

        mBuilder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                exercises.add(new Exercise(
                        etName.getText().toString(),
                        Integer.parseInt(etReps.getText().toString()),
                        Integer.parseInt(etWeight.getText().toString())
                ));
                adapter.notifyDataSetChanged();

                Log.d("List:", ""+adapter.getItemCount());
            }
        });

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    /**
     * Method to hide the keyboard when the user touches anywhere on the screen.
     */
    @OnTouch(R.id.root_viewgroup)
    public boolean onTouch() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

}
