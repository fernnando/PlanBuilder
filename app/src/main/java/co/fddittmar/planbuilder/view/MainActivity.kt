package co.fddittmar.planbuilder.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import co.fddittmar.planbuilder.R
import co.fddittmar.planbuilder.contracts.MainContract
import co.fddittmar.planbuilder.data.SQLiteDatabaseHelper
import co.fddittmar.planbuilder.data.model.Program
import co.fddittmar.planbuilder.presenter.MainPresenter
import co.fddittmar.planbuilder.utils.AlertDialogHelper
import co.fddittmar.planbuilder.utils.RecyclerItemClickListener
import co.fddittmar.planbuilder.view.adapter.ProgramsAdapter
import java.util.*

class MainActivity : BaseActivity(), MainContract.View, AlertDialogHelper.AlertDialogListener {

    internal var presenter: MainPresenter? = null
    internal var programList: MutableList<Program> = ArrayList()

    private lateinit var rvProgramsList: RecyclerView
    private lateinit var tvNewEntry: TextView
    private lateinit var btnAddProgram: FloatingActionButton

    private var adapter: ProgramsAdapter? = null

    internal var alertDialogHelper: AlertDialogHelper? = null
    internal var mActionMode: ActionMode? = null
    internal var multiselect_list: MutableList<Program> = ArrayList()
    internal var isMultiSelect = false
    internal var context_menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNewEntry = bind(R.id.tv_new_entry)
        btnAddProgram = bind(R.id.fab_add_program)

        alertDialogHelper = AlertDialogHelper(this)

        //Check if presenter is already created
        if (presenter == null) {
            presenter = MainPresenter(this, SQLiteDatabaseHelper.getInstance(this))
        }

        btnAddProgram.setOnClickListener {
            presenter!!.onAddBtnClicked()
        }

        startAdapter()
    }

    private fun startAdapter() {
        rvProgramsList = bind(R.id.rv_programs_list)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvProgramsList.layoutManager = layoutManager
        rvProgramsList.isNestedScrollingEnabled = false //Smooth scrool

        //Add divider between the items in the list
        val mDividerItemDecoration = DividerItemDecoration(
                rvProgramsList.context,
                layoutManager.orientation
        )
        rvProgramsList.addItemDecoration(mDividerItemDecoration)

        programList = presenter!!.allPrograms
        presenter!!.checkNewProgramText(programList)
        adapter = ProgramsAdapter(this, programList, multiselect_list)
        rvProgramsList.adapter = adapter

        rvProgramsList.addOnItemTouchListener(RecyclerItemClickListener(
                this, rvProgramsList, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (isMultiSelect)
                    multi_select(position)
            }

            override fun onItemLongClick(view: View, position: Int) {
                if (!isMultiSelect) {
                    multiselect_list = ArrayList<Program>()
                    isMultiSelect = true

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback)
                    }
                }

                multi_select(position)
            }
        }))
    }

    override fun openNewProgramActivity() {
        val intent = Intent(this, NewProgramActivity::class.java)
        startActivityForResult(intent, 0)
    }

    fun itemProgramClicked(program: Program) {
        presenter!!.onItemProgramClicked(program)
    }

    override fun openProgramDetailActivity(program: Program) {
        val intent = Intent(this, ProgramDetailActivity::class.java)
        intent.putExtra("program", program)
        startActivity(intent)
    }

    override fun hideNewProgramText() {
        tvNewEntry.visibility = View.GONE
    }

    override fun showNewProgramText() {
        tvNewEntry.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        presenter!!.checkActivityResult(resultCode)
    }

    override fun newProgramAdded() {
        programList = presenter!!.allPrograms
        refreshAdapter()
    }

    override fun refreshAdapter() {

        adapter!!.selected_usersList = multiselect_list
        adapter!!.programs = programList
        adapter!!.notifyDataSetChanged()
    }


    //TEMPORARY
    fun multi_select(position: Int) {
        if (mActionMode != null) {
            if (multiselect_list.contains(programList[position]))
                multiselect_list.remove(programList[position])
            else
                multiselect_list.add(programList[position])

            if (multiselect_list.size > 0)
                mActionMode!!.title = "" + multiselect_list.size
            else
                mActionMode!!.finish()

            refreshAdapter()

        }
    }

    private val mActionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.toolbar_cab, menu)
            context_menu = menu
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_delete -> {
                    alertDialogHelper?.showAlertDialog(
                            "",
                            "Deseja deletar o(s) programa(s) selecionado(s)?",
                            "DELETAR",
                            "CANCELAR",
                            1, false)
                    return true
                }
                else -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mActionMode = null
            isMultiSelect = false
            multiselect_list = ArrayList<Program>()
            refreshAdapter()
        }
    }


    // AlertDialog Callback Functions

    override fun onPositiveClick(from: Int) {
        if (multiselect_list.size > 0) {
            for (i in multiselect_list) {
                programList.remove(i)
                presenter!!.deleteProgram(i.id.toLong())
            }

            adapter!!.notifyDataSetChanged()
            presenter!!.checkNewProgramText(programList)

            if (mActionMode != null) {
                mActionMode!!.finish()
            }
        }
    }

    override fun onNegativeClick(from: Int) {

    }

    override fun onNeutralClick(from: Int) {

    }

    fun <T : View> Activity.bind(@IdRes res : Int) : T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res) as T
    }
}

