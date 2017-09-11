package co.fddittmar.planbuilder.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import co.fddittmar.planbuilder.R
import co.fddittmar.planbuilder.contracts.NewProgramContract
import co.fddittmar.planbuilder.data.SQLiteDatabaseHelper
import co.fddittmar.planbuilder.data.model.Exercise
import co.fddittmar.planbuilder.presenter.NewProgramPresenter
import co.fddittmar.planbuilder.view.adapter.ExercisesAdapter
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import java.text.DateFormat
import java.util.*

/**
 * Class responsible to create a new Program
 */

class NewProgramActivity : BaseActivity(), NewProgramContract.View {


    private lateinit var programTitle: EditText
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var rvExercisesList: RecyclerView
    private lateinit var btnSave: Button
    private lateinit var ctAddExercise: FrameLayout
    private lateinit var vgRoot: ConstraintLayout


    internal var presenter: NewProgramPresenter? = null

    internal var dateFormat = DateFormat.getDateInstance()
    internal var startDateCalendar = Calendar.getInstance()
    internal var endDateCalendar = Calendar.getInstance()
    internal var exercises = ArrayList<Exercise>()
    internal var adapter: ExercisesAdapter? = null

    // Defining AwesomeValidation object
    private var awesomeValidation: AwesomeValidation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_program)

        programTitle = bind(R.id.et_program_title)
        startDate = bind(R.id.et_start_date)
        endDate = bind(R.id.et_end_date)
        rvExercisesList = bind(R.id.rv_list_exercises)
        btnSave = bind(R.id.btn_save_program)
        ctAddExercise = bind(R.id.container_add_exercise)
        vgRoot = bind(R.id.root_viewgroup)

        startDate.setOnClickListener { getStartDateFromUser() }
        endDate.setOnClickListener { getEndDateFromUser() }
        btnSave.setOnClickListener { saveProgram() }
        ctAddExercise.setOnClickListener { addExercise() }

        vgRoot.setOnTouchListener { v, event ->
            onTouch()
        }

        if (savedInstanceState != null) {
            exercises = savedInstanceState.getSerializable(STATE_ITEMS) as ArrayList<Exercise>
        }

        if (presenter == null) {
            presenter = NewProgramPresenter(this, SQLiteDatabaseHelper.getInstance(this)!!)
        }

        awesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        title = getString(R.string.new_entry)

        // Add a validation for the "Program Title", using the regex below to validate the user input
        awesomeValidation!!.addValidation(this, R.id.et_program_title,
                "^[A-Za-z0-9]{1,}[A-Za-z0-9][A-Za-z0-9 _-]*$", R.string.title_error)


        startDate.setText(dateFormat.format(startDateCalendar.time))
        setupAdapter()
    }

    /**
     * Setup the exercises list adapter
     */
    private fun setupAdapter() {
        rvExercisesList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvExercisesList.layoutManager = layoutManager
        adapter = ExercisesAdapter(exercises)
        rvExercisesList.adapter = adapter

    }

    /**
     * Method to save the exercises list when configuration changes,
     * and later restore it in the onCreate() method.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState)
        // Save our own state now
        outState.putSerializable(STATE_ITEMS, exercises)
    }

    /**
     * Method that creates a DatePickerDialog for the 'EndDate' input. It also disable the dates
     * before the 'StartDate'.
     */
    fun getStartDateFromUser() {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            startDateCalendar.set(Calendar.MONTH, month)
            startDateCalendar.set(Calendar.YEAR, year)
            startDate.setText(dateFormat.format(startDateCalendar.time))
        },
                startDateCalendar.get(Calendar.YEAR),
                startDateCalendar.get(Calendar.MONTH),
                startDateCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    /**
     * Method that creates a DatePickerDialog for the 'EndDate' input. It also disable the dates
     * before the 'StartDate'.
     */
    fun getEndDateFromUser() {
        val dp = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            endDateCalendar.set(Calendar.MONTH, month)
            endDateCalendar.set(Calendar.YEAR, year)
            endDate.setText(dateFormat.format(endDateCalendar.time))
        }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH),
                endDateCalendar.get(Calendar.DAY_OF_MONTH))

        //Disable dates prior to the 'StartDate'
        dp.datePicker.minDate = startDateCalendar.timeInMillis
        dp.show()

    }

    /**
     * This method will send to the presenter the user will to add a new exercise
     */
    override fun addExercise() {
        presenter!!.onAddNewExercise()
    }

    /**
     * When the user hits the 'Save' button, it will validates all the data that the user provided.
     * If there's any error, it will show up on the screen, otherwise it will send all the data to
     * the presenter, that will handle the operation.
     */
    override fun saveProgram() {
        if (awesomeValidation!!.validate()) {
            if (datesValidation()) {
                presenter!!.saveProgram(
                        programTitle.text.toString(),
                        startDate.text.toString(),
                        endDate.text.toString(),
                        exercises)
            } else {
                startDate.setTextColor(Color.parseColor("#ff0000"))
                showDateErrorDialog()
            }
        }
    }

    /**
     * Create an error dialog informing the user that the 'EndDate' is before the 'StartDate'.
     */
    private fun showDateErrorDialog() {
        // Setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage("Insira uma data de ínicio anterior à data de término")

        // add a button
        builder.setPositiveButton("OK", null)

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Check if the 'StartDate' comes before the 'EndDate'
     */
    private fun datesValidation(): Boolean {
        return startDateCalendar.timeInMillis < endDateCalendar.timeInMillis
    }

    /**
     * Send the 'RESULT_OK' response to the MainActivity when sucessfully adds a new program
     * and close this activity.
     */
    override fun returnToMainActivity() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * Create a dialog with exercise's NAME, NUMBER OF REPS and WEIGHT. If the user
     * clicks the 'OK' button, it will add the data to the 'exercises' vector and update the adapter
     * that handles the exercises list on the screen. Otherwise, if the user clicks on the 'CANCEL'
     * option, it will just dismiss the dialog.
     */
    override fun setupNewExerciseDialog() {
        val mBuilder = AlertDialog.Builder(this)
        val mView = layoutInflater.inflate(R.layout.dialog_new_exercise, null)

        val etName = mView.findViewById(R.id.et_exercise_name) as EditText
        val etReps = mView.findViewById(R.id.et_exercise_reps) as EditText
        val etWeight = mView.findViewById(R.id.et_exercise_weight) as EditText

        mBuilder.setPositiveButton("Adicionar") { _, which ->
            val reps = if(etReps.text.toString() == "") 0 else Integer.parseInt(etReps.text.toString())
            val weight = if(etWeight.text.toString() == "") 0 else Integer.parseInt(etWeight.text.toString())

            exercises.add(Exercise(
                    etName.text.toString(),
                    reps,
                    weight
            ))
            adapter?.notifyDataSetChanged()
        }

        mBuilder.setNegativeButton("Cancelar") { dialog, which -> dialog.dismiss() }

        mBuilder.setView(mView)
        val dialog = mBuilder.create()
        dialog.show()
    }

    /**
     * Hide the keyboard when the user touches anywhere on the screen.
     */
    fun onTouch(): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        return true
    }

    companion object {

        private val STATE_ITEMS = "items"
    }

}
