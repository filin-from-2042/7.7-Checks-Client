package com.tradeassistant1c77;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.Entites.Check;
import com.tradeassistant1c77.Entites.CheckAddStatus;
import com.tradeassistant1c77.Entites.Product;

public class CheckDetailFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ConstraintLayout mlistContainer;
    // "изменить" в панели инструментов
    private MenuItem editAction;
    // "удалить" в панели инструментов
    private MenuItem removeAction;
    private ProgressBar initPB;

    FloatingActionButton fabSearch;
    FloatingActionButton fabSave;

    Toolbar toolbar;
    Toolbar toolbarBottom;

    // переменная для передачи данных их OnSaveInstanceState в OnStart
    LinkedHashMap<String,Product> transferNewItems;

    // список текущих выделенных элементов
    private ArrayList<String> selectedItems = new ArrayList<>();
    public void setSelectedItems(ArrayList<String> selectedItems) {
        this.selectedItems = selectedItems;
    }
    // тег для фрагмента измененеия кол-ва
    private static final String COUNT_DIALOG_TAG = "ItemDialog";
    // пометка о том что декоратор уже был назначен RecyclerView
    private boolean flagDecoration = false;

    private static final String STATE_ITEMS_KEY_INDEX = "newCheckItems";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_check_detail, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbarBottom = (Toolbar) v.findViewById(R.id.toolbar_bottom);
        toolbarBottom.inflateMenu(R.menu.menu_activity_check_detail);

        Menu menu = toolbarBottom.getMenu();
        editAction = menu.findItem(R.id.action_edit);
        editAction.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(DataHolder.getData("newCheck") != null) {
                    Check newCheck = (Check) DataHolder.getData("newCheck");
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    String productID = selectedItems.get(0);
                    CheckItemDialogFragment dialog = CheckItemDialogFragment.newInstance(CheckDetailFragment.this, productID, newCheck.getNewItems().get(productID).count);
                    dialog.show(manager, COUNT_DIALOG_TAG);
                }

                return true;
            }
        });
        removeAction = menu.findItem(R.id.action_remove);
        removeAction.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(DataHolder.getData("newCheck") != null) {
                    Check newCheck = (Check) DataHolder.getData("newCheck");
                    if(selectedItems.size()>0) {
                        for (String productID : selectedItems) {
                            newCheck.removeItemById(productID);
                        }
                        mAdapter.notifyDataSetChanged();
                        changeToolbar(Mode.NORMAL);
                        selectedItems.clear();
                        setActualTitle(newCheck);
                    }
                }
                return true;
            }
        });

        try {
            AppCompatActivity acvt = (AppCompatActivity)getActivity();
            acvt.setSupportActionBar(toolbar);
            // Enable the Up button
            acvt.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex ){
            Crashlytics.logException(ex);
        }

        fabSearch = (FloatingActionButton) v.findViewById(R.id.fab_search);
        fabSave = (FloatingActionButton) v.findViewById(R.id.fab_save);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onSearchRequested();
                fabSearch.setVisibility(View.GONE);
                fabSave.setVisibility(View.GONE);
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataHolder.getData("newCheck") != null)
                {
                    Check newCheck = (Check) DataHolder.getData("newCheck");
                    if(newCheck.getNewItems()==null || newCheck.getNewItems().size() == 0)
                    {
                        Toast.makeText(getActivity(), R.string.tm_check_items_empty ,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AsyncSaveCheck().execute(newCheck);
                }
            }
        });
        ImageView refreshNumber = (ImageView) v.findViewById(R.id.newNumberIV);
        refreshNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncReinitNumber().execute();
            }
        });
        // показываем кнопки по завершению поиска (в частности, отмена)
        SearchManager mng = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mng.setOnDismissListener(new SearchManager.OnDismissListener() {
            @Override
            public void onDismiss() {
                fabSearch.setVisibility(View.VISIBLE);
                fabSave.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState != null) {
            try {
                transferNewItems = (LinkedHashMap<String, Product>) savedInstanceState.getSerializable(STATE_ITEMS_KEY_INDEX);
            }catch (Exception ex){
                Crashlytics.logException(ex);
            }
        } else transferNewItems = null;

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        initPB = (ProgressBar) v.findViewById(R.id.initProgressBar);
        mlistContainer = (ConstraintLayout) v.findViewById(R.id.listContainer);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(DataHolder.getData("newCheck") != null){
            Check newCheck = (Check) DataHolder.getData("newCheck");
            if(newCheck.getNewItems()!=null) outState.putSerializable(STATE_ITEMS_KEY_INDEX,newCheck.getNewItems());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        new AsyncCheckStart().execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        unleashCheckNumber();
        initPB.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataHolder.setData("newCheck", null);
    }


    // разблокировка номера чека
    protected void unleashCheckNumber()
    {
        if(DataHolder.getData("newCheck") != null) {
            Check newCheck = (Check) DataHolder.getData("newCheck");
            if(newCheck.isNew()) new AsyncUnleashCheck(newCheck).execute();
        }
    }
    // обновляет заголовк в чеке
    protected  void setActualTitle(Check newCheck)
    {
        Double checkCost = newCheck.getItemsCosts();

        String toolbarTitle = "Чек - "+newCheck.getCheckNumber();
        String toolbarSubTitle = Helpers.formatNumData(checkCost)+" Р";
        ((TextView)toolbar.findViewById(R.id.title)).setText(toolbarTitle);
        ((TextView)toolbar.findViewById(R.id.subTitleText)).setText(toolbarSubTitle);
    }
    // обновляет количество позиции в чеке по ид
    public void setCheckItemCount(String productId, Double newCount)
    {
        if(DataHolder.getData("newCheck")!=null) {
            Check newCheck = (Check) DataHolder.getData("newCheck");
            if(newCheck.updateItemCount(productId,newCount)) {
                DataHolder.setData("newCheck", newCheck);
                mAdapter.notifyDataSetChanged();
                setActualTitle(newCheck);
                changeToolbar(Mode.NORMAL);
                selectedItems.clear();
            }
        }
    }
    // переключение видимости кнопок по переданному
    protected  void changeToolbar(Mode mode)
    {
        if(editAction==null && removeAction==null) return;
        editAction.setVisible(false);
        removeAction.setVisible(false);
        toolbarBottom.setVisibility(View.GONE);
        fabSave.setTranslationY(0);
        fabSearch.setTranslationY(0);
        switch (mode)
        {
            case NORMAL:break;
            case SINGLE:{
                toolbarBottom.setVisibility(View.VISIBLE);
                editAction.setVisible(true);
                removeAction.setVisible(true);
                // вместо высоты toolbarBottom берем высоту toolbar, т.к. высоты одинаковые
                fabSave.setTranslationY(-toolbar.getHeight());
                fabSearch.setTranslationY(-toolbar.getHeight());
            }break;
            case MULTIPLE:{
                toolbarBottom.setVisibility(View.VISIBLE);
                removeAction.setVisible(true);
                fabSave.setTranslationY(-toolbar.getHeight());
                fabSearch.setTranslationY(-toolbar.getHeight());
            }break;
        }
    }
    // таск принудительного обновления номера чека
    public class AsyncReinitNumber extends AsyncTask<Void, Void, Check>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected Check doInBackground(Void... params) {
            Check newCheck = null;
            if(DataHolder.getData("newCheck") != null){
                newCheck = (Check) DataHolder.getData("newCheck");
                newCheck.checkNumberUnlock(true);
                newCheck.initNewCheckNumber();
            }
            return newCheck;
        }

        @Override
        protected void onPostExecute(Check newCheck) {
            super.onPostExecute(newCheck);
            try{
                setActualTitle(newCheck);
            } catch (Exception ex ){
                Crashlytics.logException(ex);
            }
            initPB.setVisibility(View.GONE);
        }
    }

    // таск на начальные операции по инициализации чека
    public class AsyncCheckStart extends AsyncTask<Void, Void, Check> {
        @Override
        protected Check doInBackground(Void... params) {
            // данные по чеку
            Check newCheck;
            if (DataHolder.getData("newCheck") == null) {
                newCheck = new Check();
                if(transferNewItems!=null) newCheck.setNewItems(transferNewItems);
                DataHolder.setData("newCheck", newCheck);
            } else {
                newCheck = (Check) DataHolder.getData("newCheck");
                if (newCheck.getCheckNumber() == null) newCheck.initNewCheckNumber();
            }
            return newCheck;
        }

        @Override
        protected void onPostExecute(Check newCheck) {
            super.onPostExecute(newCheck);

            // чтение данных, переданных из активности с поиском
            if (DataHolder.getData("newCheckItem") != null){
                Product productItem = (Product)DataHolder.getData("newCheckItem");
                if (newCheck.getNewItems() == null) newCheck.setNewItems(new LinkedHashMap<String, Product>());
                newCheck.addNewItem(productItem);
                DataHolder.setData("newCheckItem",null);
            }

            if(newCheck.getNewItems()!=null) {
                mAdapter = new CheckDetailAdapter(newCheck.getNewItems(), CheckDetailFragment.this);
            }
            else {
                mAdapter = new CheckDetailAdapter(Collections.<String,Product>emptyMap(), CheckDetailFragment.this);
            }
            mRecyclerView.setAdapter(mAdapter);
            changeToolbar(Mode.NORMAL);
            setActualTitle(newCheck);
            mRecyclerView.setHasFixedSize(true);


            Context context = getContext();
            if(context!=null) {
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                mRecyclerView.setLayoutManager(mLayoutManager);

                if (!flagDecoration) {
                    RecyclerView.ItemDecoration itemDecoration = new
                            DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                    mRecyclerView.addItemDecoration(itemDecoration);
                    flagDecoration = true;
                }
            }
            initPB.setVisibility(View.GONE);
            mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
        }
    }

    public class AsyncUnleashCheck extends AsyncTask<Check,Void,Void>{
        Check newCheck;
        public  AsyncUnleashCheck(Check check){
            this.newCheck = check;
        }
        @Override
        protected Void doInBackground(Check... params) {
            newCheck.checkNumberUnlock(true);
            return null;
        }
    }

    public class AsyncSaveCheck extends AsyncTask<Check,Void,String>{
        private boolean isNew;
        @Override
        protected String doInBackground(Check... params) {
            Check newCheck = params[0];
            isNew = newCheck.isNew();
            return newCheck.save();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initPB.setVisibility(View.VISIBLE);
            mlistContainer.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String checkRes) {
            super.onPostExecute(checkRes);
            initPB.setVisibility(View.GONE);
            mlistContainer.setVisibility(View.VISIBLE);
            FragmentActivity activity = getActivity();
            if(activity!=null) {
                Toast.makeText(activity, checkRes, Toast.LENGTH_SHORT).show();
                // если потерялись данные о текущем пользователе, то на активность логина
                if (CheckAddStatus.ERROR_LOGGED_USER.equals(checkRes))
                    Helpers.redirectToLogin(activity);
                // чек закрывать только при обновлении
                if (!isNew) {
                    activity.finish();
                    DataHolder.setData("newCheck", null);
                }
            }
        }
    }
}