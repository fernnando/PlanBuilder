package co.fddittmar.planbuilder.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import co.fddittmar.planbuilder.R
import co.fddittmar.planbuilder.contracts.ProgramDetailContract
import co.fddittmar.planbuilder.data.model.Program
import co.fddittmar.planbuilder.presenter.ProgramDetailPresenter
import co.fddittmar.planbuilder.view.adapter.ExercisesAdapter

class ProgramDetailActivity : BaseActivity(), ProgramDetailContract.View {

    private lateinit var tvTitle: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var rvExercisesList: RecyclerView

    internal var presenter: ProgramDetailPresenter? = null
    internal var adapter: ExercisesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_detail)

        tvTitle = bind(R.id.tv_program_title)
        tvStartDate = bind(R.id.tv_start_date)
        tvEndDate = bind(R.id.tv_end_date)
        rvExercisesList = bind(R.id.rv_list_exercises)

        if (presenter == null) {
            presenter = ProgramDetailPresenter(this)
        }

        populateActivity()
    }

    /**
     * Method that populates the activity with the data received from the MainActivity, such as its
     * title, starting date, ending date and the list of exercises.
     */
    private fun populateActivity() {
        val program = intent.getSerializableExtra("program") as Program

        title = program.title

        tvTitle.text = program.title
        tvStartDate.text = program.start_date
        tvEndDate.text = program.end_date

        rvExercisesList.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvExercisesList.layoutManager = layoutManager

        adapter = ExercisesAdapter(program.exercises)
        rvExercisesList.adapter = adapter
    }
}