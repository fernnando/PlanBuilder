package co.fddittmar.planbuilder.presenter;

import co.fddittmar.planbuilder.contracts.ProgramDetailContract;
import co.fddittmar.planbuilder.data.ProgramRepository;

/**
 * Created by Fernnando on 05/09/2017.
 */

public class ProgramDetailPresenter {
    ProgramDetailContract.View view;
    ProgramRepository repository;

    public ProgramDetailPresenter(ProgramDetailContract.View view) {
        this.view = view;
    }
}
