package co.fddittmar.planbuilder.presenter

import android.app.Activity.RESULT_OK
import co.fddittmar.planbuilder.contracts.MainContract
import co.fddittmar.planbuilder.data.ProgramRepository
import co.fddittmar.planbuilder.data.model.Program

/**
 * Presenter for the MainActivity. It takes a reference to a View and a Repository.
 */

class MainPresenter(internal var view: MainContract.View, internal var repository: ProgramRepository?) : MainContract.Actions {

    override val allPrograms: ArrayList<Program>
        get() {
            return repository!!.fetchAllPrograms()
        }

    override fun checkNewProgramText(programList: List<Program>) {
        if (programList.isNotEmpty()) {
            view.hideNewProgramText()
        } else {
            view.showNewProgramText()
        }
    }

    override fun onAddBtnClicked() {
        view.openNewProgramActivity()
    }

    override fun onItemProgramClicked(program: Program) {
        view.openProgramDetailActivity(program)
    }

    override fun checkActivityResult(resultCode: Int) {
        if (resultCode == RESULT_OK) {
            view.newProgramAdded()
        }
    }

    override fun deleteProgram(program_id: Long) {
        repository!!.deleteProgram(program_id)
    }

}
