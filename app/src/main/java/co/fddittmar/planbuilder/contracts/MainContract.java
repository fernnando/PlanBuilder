package co.fddittmar.planbuilder.contracts;

import java.util.List;

import co.fddittmar.planbuilder.data.model.Program;

/**
 * Contract for the Main activity. It has an interface for the View and Presenter.
 */

public class MainContract {

    public interface View {
        void hideNewProgramText();
        void showNewProgramText();
        void openNewProgramActivity();
        void openProgramDetailActivity(Program program);
        void refreshAdapter();
        void newProgramAdded();
    }

    public interface Actions {

        List<Program> getAllPrograms();
        void checkNewProgramText(List<Program> programList);
        void onAddBtnClicked();
        void onItemProgramClicked(Program program);
        void checkActivityResult(int resultCode);
        void deleteProgram(long program_id);
    }
}

