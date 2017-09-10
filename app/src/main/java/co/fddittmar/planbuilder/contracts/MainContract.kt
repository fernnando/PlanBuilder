package co.fddittmar.planbuilder.contracts

import co.fddittmar.planbuilder.data.model.Program

/**
 * Contract for the Main activity. It has an interface for the View and Presenter.
 */

class MainContract {

    interface View {
        fun hideNewProgramText()
        fun showNewProgramText()
        fun openNewProgramActivity()
        fun openProgramDetailActivity(program: Program)
        fun refreshAdapter()
        fun newProgramAdded()
    }

    interface Actions {

        val allPrograms: ArrayList<Program>
        fun checkNewProgramText(programList: List<Program>)
        fun onAddBtnClicked()
        fun onItemProgramClicked(program: Program)
        fun checkActivityResult(resultCode: Int)
        fun deleteProgram(program_id: Long)
    }
}

