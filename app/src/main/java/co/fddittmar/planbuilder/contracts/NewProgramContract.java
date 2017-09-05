package co.fddittmar.planbuilder.contracts;

import java.util.List;

import co.fddittmar.planbuilder.data.model.Exercise;

/**
 * Contract for the New Program activity. It has an interface for the View and Presenter.
 */

public class NewProgramContract {
    public interface View {
        void addExercise();
        void saveProgram();
        void returnToMainActivity();
        void setupNewExerciseDialog();
    }

    public interface Actions {

        void saveProgram(String title, String startDate, String endDate, List<Exercise> exercises);
        void onAddNewExercise();
    }
}
