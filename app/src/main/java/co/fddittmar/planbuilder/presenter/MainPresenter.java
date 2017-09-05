package co.fddittmar.planbuilder.presenter;

import android.util.Log;

import java.util.List;

import co.fddittmar.planbuilder.contracts.MainContract;
import co.fddittmar.planbuilder.data.ProgramRepository;
import co.fddittmar.planbuilder.data.model.Program;

import static android.app.Activity.RESULT_OK;

/**
 * Presenter for the MainActivity. It takes a reference to a View and a Repository.
 */

public class MainPresenter implements MainContract.Actions {
    MainContract.View view;
    ProgramRepository repository;

    public MainPresenter(MainContract.View view, ProgramRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public List<Program> getAllPrograms() {
        if(repository == null)
            Log.d("DEBUG", "Null repository");
        return repository.fetchAllPrograms();
    }

    @Override
    public void checkNewProgramText(List<Program> programList) {
        if(programList.size() > 0){
            view.hideNewProgramText();
        }else {
            view.showNewProgramText();
        }
    }

    @Override
    public void onAddBtnClicked() {
        view.openNewProgramActivity();
    }

    @Override
    public void onItemProgramClicked(Program program) {
        view.openProgramDetailActivity(program);
    }

    @Override
    public void checkActivityResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            view.newProgramAdded();
        }
    }

    @Override
    public void deleteProgram(long program_id) {
        repository.deleteProgram(program_id);
    }

}
