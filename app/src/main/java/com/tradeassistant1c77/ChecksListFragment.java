package com.tradeassistant1c77;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tradeassistant1c77.Entites.CheckListItem;
import com.tradeassistant1c77.dbconnection.ConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ChecksListFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar initPB;
    // пометка о том что декоратор уже был назначен RecyclerView
    private boolean flagDecoration = false;


    private TextView startDate;
    private TextView endDate;

    // тег для фрагмента измненеия кол-ва
    private static final String DATEPICKER_DIALOG_TAG = "DatePickerDialog";

    private static final int START_DATE_REQUEST_CODE = 0;
    private static final int END_DATE_REQUEST_CODE = 1;

    private String startDateKey ;
    private String endDateKey;
    String startDatesp;
    String endDatesp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_check_list, container, false);

        MobileAds.initialize(getContext(), getString(R.string.ADMOB_APP_ID));
        AdView mAdView = (AdView)v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        startDateKey = getString(R.string.dh_key_start_date);
        endDateKey = getString(R.string.dh_key_end_date);

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.ea_date_format), Locale.ENGLISH);
        startDatesp = dateFormat.format((Date)DataHolder.getData(startDateKey));
        endDatesp = dateFormat.format((Date)DataHolder.getData(endDateKey));

        ImageView startImage = (ImageView)v.findViewById(R.id.startDateIcon);
        ImageView endImage = (ImageView)v.findViewById(R.id.endDateIcon);
        startDate = (TextView)v.findViewById(R.id.startDate);
        endDate = (TextView)v.findViewById(R.id.endDate);
        startDate.setText(startDatesp);
        endDate.setText(endDatesp);

        View.OnClickListener startDateListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                Date startDate = (Date)DataHolder.getData(startDateKey);
                DatePickerDialogFragment dialog = DatePickerDialogFragment.newInstance(startDate);
                dialog.setTargetFragment(ChecksListFragment.this, START_DATE_REQUEST_CODE);
                dialog.show(manager, DATEPICKER_DIALOG_TAG);
            }
        };
        View.OnClickListener endDateListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                Date endDate = (Date)DataHolder.getData(endDateKey);
                DatePickerDialogFragment dialog = DatePickerDialogFragment.newInstance(endDate);
                dialog.setTargetFragment(ChecksListFragment.this,END_DATE_REQUEST_CODE);
                dialog.show(manager, DATEPICKER_DIALOG_TAG);
            }
        };
        startImage.setOnClickListener(startDateListener);
        endImage.setOnClickListener(endDateListener);
        startDate.setOnClickListener(startDateListener);
        endDate.setOnClickListener(endDateListener);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CheckDetailActivity.class );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        mRecyclerView = (RecyclerView) v.findViewById(R.id.check_list);
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

        swipeRefreshLayout =(SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        initPB = (ProgressBar) v.findViewById(R.id.initPB);
        refreshData();
        return v;
    }

    // метод, вызываемый из фрагментов для передачи результатов в целевую активность
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.ea_date_format), Locale.ENGLISH);
        Date newDate = (Date)data.getSerializableExtra(DatePickerDialogFragment.EXTRA_DATE);
        try {

            Date startDateOld  = dateFormat.parse(startDatesp);
            Date endDateOld  = dateFormat.parse(endDatesp);
            Exception rangeEx = new Exception(getString(R.string.err_range_date));
            if(requestCode==START_DATE_REQUEST_CODE)
            {
                if(newDate.compareTo(endDateOld)>0) throw rangeEx;
                DataHolder.setData(startDateKey,newDate);
                startDatesp = dateFormat.format(newDate);
                startDate.setText(startDatesp);
            }
            else if (requestCode==END_DATE_REQUEST_CODE)
            {
                if(startDateOld.compareTo(newDate)>0) throw rangeEx;
                DataHolder.setData(endDateKey,newDate);
                endDatesp = dateFormat.format(newDate);
                endDate.setText(endDatesp);
            }
            swipeRefreshLayout.setVisibility(View.GONE);
            initPB.setVisibility(View.VISIBLE);
            refreshData();
        }catch (Exception ex){
            Toast.makeText(getContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            Crashlytics.logException(ex);
        }
    }

    public ArrayList<CheckListItem> getCheckList()
    {
        Connection connection = ConnectionClass.getConnection();
        ArrayList<CheckListItem> items = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("set DATEFORMAT dmy;SELECT [IDDOC], [DATE], [DOCTYPE], [DOCNUMBER], [SUMM], [USERNAME], [CLOSED] FROM [sa_udf_GetCheckList] (?  ,?) ORDER BY [ROW_ID] DESC");
            if(ps!=null) {
                ps.setString(1, startDatesp);
                ps.setString(2, endDatesp);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    items.add(new CheckListItem(rs.getString(1), rs.getDate(2), rs.getInt(3), rs.getString(4), rs.getDouble(5), rs.getString(6), rs.getInt(7)));
                }
                ps.close();
            }
        }
        catch (Exception ex){
            Crashlytics.logException(ex);
        }
        finally {
            try {
                if(connection!=null) connection.close();
            }catch (SQLException ex){
                Crashlytics.logException(ex);
            }
        }
        return items;
    }

    public void refreshData()
    {
        new GetCheckLstTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        // не обновлять список при переданном параметре resetRefreshList=true
        if(DataHolder.getData("resetRefreshList")==null || DataHolder.getData("resetRefreshList")!="true") refreshData();
        DataHolder.setData("resetRefreshList",null);
    }

    // таск на обновление списка чеков
    public class GetCheckLstTask extends AsyncTask<String, Integer, ArrayList<CheckListItem>> {

        @Override
        protected ArrayList<CheckListItem> doInBackground(String... arg) {
            return getCheckList();
        }

        @Override
        protected void onPostExecute(ArrayList<CheckListItem> items) {
            super.onPostExecute(items);
            RecyclerView.Adapter mAdapter = new CheckListAdapter(items,getActivity(),ChecksListFragment.this);
            mRecyclerView.setAdapter(mAdapter);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            initPB.setVisibility(View.GONE);
        }
    }
}
