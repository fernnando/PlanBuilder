package co.fddittmar.planbuilder.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.fddittmar.planbuilder.R;
import co.fddittmar.planbuilder.contracts.MainContract;
import co.fddittmar.planbuilder.data.SQLiteDatabaseHelper;
import co.fddittmar.planbuilder.data.model.Program;
import co.fddittmar.planbuilder.presenter.MainPresenter;
import co.fddittmar.planbuilder.utils.AlertDialogHelper;
import co.fddittmar.planbuilder.utils.RecyclerItemClickListener;
import co.fddittmar.planbuilder.view.adapter.ProgramsAdapter;

public class MainActivity extends BaseActivity implements MainContract.View, AlertDialogHelper.AlertDialogListener {

    MainPresenter presenter;
    List<Program> programList = new ArrayList<>();

    @BindView(R.id.rv_programs_list)
    RecyclerView rvProgramsList;

    @BindView(R.id.tv_new_entry)
    TextView tvNewEntry;

    private ProgramsAdapter adapter;

    AlertDialogHelper alertDialogHelper;
    ActionMode mActionMode;
    List<Program> multiselect_list = new ArrayList<>();
    boolean isMultiSelect = false;
    Menu context_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //TEMPORARY
        alertDialogHelper =new AlertDialogHelper(this);

        //Check if presenter is already created
        if(presenter == null){
            presenter = new MainPresenter(this, SQLiteDatabaseHelper.getInstance(this));
        }

        startAdapter();
    }

    private void startAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvProgramsList.setLayoutManager(layoutManager);
        rvProgramsList.setNestedScrollingEnabled(false); //Smooth scrool

        //Add divider between the items in the list
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                rvProgramsList.getContext(),
                layoutManager.getOrientation()
        );
        rvProgramsList.addItemDecoration(mDividerItemDecoration);

        programList = presenter.getAllPrograms();
        presenter.checkNewProgramText(programList);
        adapter = new ProgramsAdapter(this, programList, multiselect_list);
        rvProgramsList.setAdapter(adapter);

        rvProgramsList.addOnItemTouchListener(new RecyclerItemClickListener(
                this, rvProgramsList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);
            }
        }));
    }


    @OnClick(R.id.fab_add_program)
    public void addBtnClicked(){
        presenter.onAddBtnClicked();
    }

    public void openNewProgramActivity() {
        Intent intent = new Intent(this, NewProgramActivity.class);
        startActivityForResult(intent, 0);
    }

    public void itemProgramClicked(Program program) {
        presenter.onItemProgramClicked(program);
    }

    @Override
    public void openProgramDetailActivity(Program program) {
        Intent intent = new Intent(this, ProgramDetailActivity.class);
        intent.putExtra("program", program);
        startActivity(intent);
    }

    @Override
    public void hideNewProgramText() {
        tvNewEntry.setVisibility(View.GONE);
    }
    @Override
    public void showNewProgramText() {
        tvNewEntry.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.checkActivityResult(resultCode);
    }

    @Override
    public void newProgramAdded(){
        programList = presenter.getAllPrograms();
        refreshAdapter();
    }

    @Override
    public void refreshAdapter() {

        adapter.selected_usersList=multiselect_list;
        adapter.programs=programList;
        adapter.notifyDataSetChanged();
    }


    //TEMPORARY
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(programList.get(position)))
                multiselect_list.remove(programList.get(position));
            else
                multiselect_list.add(programList.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.finish();

            refreshAdapter();

        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.toolbar_cab, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog(
                            "",
                            "Deseja deletar o(s) programa(s) selecionado(s)?",
                            "DELETAR",
                            "CANCELAR",
                            1,false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<>();
            refreshAdapter();
        }
    };


    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if(multiselect_list.size()>0)
        {
            for(int i=0;i<multiselect_list.size();i++){
                programList.remove(multiselect_list.get(i));
                presenter.deleteProgram(multiselect_list.get(i).getId());
            }

            adapter.notifyDataSetChanged();
            presenter.checkNewProgramText(programList);

            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
}
