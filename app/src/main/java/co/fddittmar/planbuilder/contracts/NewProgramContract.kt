package co.fddittmar.planbuilder.contracts

import co.fddittmar.planbuilder.data.model.Exercise

/**
 * Contract for the New Program activity. It has an interface for the View and Presenter.
 */

class NewProgramContract {
    interface View {
        fun addExercise()
        fun saveProgram()
        fun returnToMainActivity()
        fun setupNewExerciseDialog()
    }

    interface Actions {

        fun saveProgram(title: String, startDate: String, endDate: String, exercises: List<Exercise>)
        fun onAddNewExercise()
    }
}
