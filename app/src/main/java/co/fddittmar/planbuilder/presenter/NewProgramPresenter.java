package co.fddittmar.planbuilder.presenter;

import java.util.List;

import co.fddittmar.planbuilder.contracts.NewProgramContract;
import co.fddittmar.planbuilder.data.ProgramRepository;
import co.fddittmar.planbuilder.data.model.Exercise;

/**
 * Presenter for the MainActivity. It takes a reference to a View and a Repository.
 */

public class NewProgramPresenter implements NewProgramContract.Actions {

    NewProgramContract.View view;
    ProgramRepository repository;

    public NewProgramPresenter(NewProgramContract.View view, ProgramRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void saveProgram(String title, String startDate, String endDate, List<Exercise> exercises) {
        repository.saveProgram(title, startDate, endDate, exercises);
        view.returnToMainActivity();
    }

    @Override
    public void onAddNewExercise() {
        view.setupNewExerciseDialog();
    }
}

