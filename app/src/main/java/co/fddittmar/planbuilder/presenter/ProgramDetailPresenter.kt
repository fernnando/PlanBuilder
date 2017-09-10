package co.fddittmar.planbuilder.presenter

import co.fddittmar.planbuilder.contracts.ProgramDetailContract
import co.fddittmar.planbuilder.data.ProgramRepository

/**
 * Created by Fernnando on 05/09/2017.
 */

class ProgramDetailPresenter(internal var view: ProgramDetailContract.View) {
    internal var repository: ProgramRepository? = null
}
