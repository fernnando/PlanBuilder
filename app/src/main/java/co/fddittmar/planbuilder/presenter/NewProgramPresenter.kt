package co.fddittmar.planbuilder.presenter

import co.fddittmar.planbuilder.contracts.NewProgramContract
import co.fddittmar.planbuilder.data.ProgramRepository
import co.fddittmar.planbuilder.data.model.Exercise

/**
 * Presenter for the MainActivity. It takes a reference to a View and a Repository.
 */

class NewProgramPresenter(internal var view: NewProgramContract.View, internal var repository: ProgramRepository) : NewProgramContract.Actions {

    override fun saveProgram(title: String, startDate: String, endDate: String, exercises: List<Exercise>) {
        repository.saveProgram(title, startDate, endDate, exercises)
        view.returnToMainActivity()
    }

    override fun onAddNewExercise() {
        view.setupNewExerciseDialog()
    }
}

