package com.tradeassistant1c77;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.dbconnection.DbProgrammObject;
import com.tradeassistant1c77.dbconnection.IndexDbObject;
import com.tradeassistant1c77.dbconnection.SPDbObject;
import com.tradeassistant1c77.dbconnection.UDFDbObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DbSystemFragment extends Fragment {
    LinearLayout layoutContainer;
    RelativeLayout pbContainer;
    RecyclerView indexRecyclerView;
    RecyclerView udfRecyclerView;
    RecyclerView spRecyclerView;
    ArrayMap<String, ArrayList<DbProgrammObject>> dbObjects;
    ScrollView sw;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_db_system, container, false);

        Button procFunc = (Button) v.findViewById(R.id.procFuncCreateBtn);
        procFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncCreateAllProc().execute();
            }
        });
        Button indexesBtn = (Button) v.findViewById(R.id.indexesBtn);
        indexesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbObjects!=null && dbObjects.indexOfKey("indexes")>=0){
                    ArrayList<DbProgrammObject> indexes = dbObjects.get("indexes");
                    new AsyncCreateGroup().execute(indexes);
                }
            }
        });
        Button udfsBtn = (Button) v.findViewById(R.id.udfsBtn);
        udfsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbObjects!=null && dbObjects.indexOfKey("udfs")>=0){
                    ArrayList<DbProgrammObject> udfs = dbObjects.get("udfs");
                    new AsyncCreateGroup().execute(udfs);
                }
            }
        });
        Button spsBtn = (Button) v.findViewById(R.id.spsBtn);
        spsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbObjects!=null && dbObjects.indexOfKey("sprocs")>=0){
                    ArrayList<DbProgrammObject> sprocs = dbObjects.get("sprocs");
                    new AsyncCreateGroup().execute(sprocs);
                }
            }
        });
        layoutContainer = (LinearLayout)v.findViewById(R.id.container);
        pbContainer = (RelativeLayout)v.findViewById(R.id.progressContainer);

        indexRecyclerView = (RecyclerView) v.findViewById(R.id.indexes_list);
        indexRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        udfRecyclerView = (RecyclerView) v.findViewById(R.id.udfs_list);
        udfRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        spRecyclerView = (RecyclerView) v.findViewById(R.id.sps_list);
        spRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sw = (ScrollView)v.findViewById(R.id.sw);

        swipeRefreshLayout =(SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncOnCreate().execute();
            }
        });

        new AsyncOnCreate().execute();

        return v;
    }
    protected int  rebindRV(String key)
    {
        int recyclerTopPosition = 0;
        if(key!=null && !key.isEmpty()) {
            ArrayList<DbProgrammObject> obj = dbObjects.get(key);
            if (obj != null && obj.size() > 0) {
                if (key.equals("indexes")){
                    indexRecyclerView.setAdapter(new DbSystemAdapter(obj));
                    recyclerTopPosition = indexRecyclerView.getTop();
                }
                else if (key.equals("udfs")){
                    udfRecyclerView.setAdapter(new DbSystemAdapter(obj));
                    recyclerTopPosition = udfRecyclerView.getTop();
                }
                else if (key.equals("sprocs")){
                    spRecyclerView.setAdapter(new DbSystemAdapter(obj));
                    recyclerTopPosition = spRecyclerView.getTop();
                }
            }
        }
        // 60 высота заголовка
        return recyclerTopPosition - 60;
    }

    public class AsyncOnCreate extends AsyncTask<Void,Void,Void>{
        ArrayList<DbProgrammObject> indexes = new ArrayList<>();
        ArrayList<DbProgrammObject> udfs = new ArrayList<>();
        ArrayList<DbProgrammObject> sprocs = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbContainer.setVisibility(View.VISIBLE);
            layoutContainer.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String scheme = prefs.getString(getContext().getString(R.string.s_key_scheme), getContext().getString(R.string.s_default_scheme));
            AssetManager manager = getContext().getAssets();
            try {
                String[] functionsDbList = manager.list("sql_sources");
                for (String fileName : functionsDbList)
                {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(manager.open("sql_sources/"+fileName)));
                    String line;
                    String query = "";
                    while ((line = reader.readLine()) != null) {
                        line= line.replace("[dbo]","["+scheme+"]");
                        query += " \n " + line;
                    }

                    String[] namePaths = fileName.split("_");

                    switch (namePaths[1]) {
                        case "sp": {
                            sprocs.add(new SPDbObject(fileName,scheme,query));
                        }
                        break;
                        case "udf": {
                            udfs.add(new UDFDbObject(fileName,scheme,query));
                        }
                        break;
                        case "i": {
                            indexes.add(new IndexDbObject(fileName,scheme,query));
                        }
                        break;
                    }

                }
            }catch (Exception ex){
                Crashlytics.logException(ex);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dbObjects = new ArrayMap<>();
            if(indexes.size()>0){
                indexRecyclerView.setAdapter(new DbSystemAdapter(indexes));
                dbObjects.put("indexes",indexes);
            }
            if(udfs.size()>0){
                udfRecyclerView.setAdapter(new DbSystemAdapter(udfs));
                dbObjects.put("udfs",udfs);
            }
            if(sprocs.size()>0){
                spRecyclerView.setAdapter(new DbSystemAdapter(sprocs));
                dbObjects.put("sprocs",sprocs);
            }
            pbContainer.setVisibility(View.GONE);
            layoutContainer.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            sw.scrollTo(0,0);
        }
    }

    public class AsyncCreateAllProc extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbContainer.setVisibility(View.VISIBLE);
            layoutContainer.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void message) {
            super.onPostExecute(message);

            rebindRV("indexes");
            rebindRV("udfs");
            rebindRV("sprocs");

            pbContainer.setVisibility(View.GONE);
            layoutContainer.setVisibility(View.VISIBLE);
            sw.fullScroll(ScrollView.FOCUS_UP);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(dbObjects!=null && dbObjects.size()>0) {
                for (ArrayMap.Entry<String, ArrayList<DbProgrammObject>> entry : dbObjects.entrySet()) {
                    ArrayList<DbProgrammObject> objList = entry.getValue();
                    for(DbProgrammObject obj:objList){
                        obj.recreateOnServer();
                        obj.checkExist();
                    }
                }
            }
            return null;
        }
    }

    public class AsyncCreateGroup extends AsyncTask<ArrayList<DbProgrammObject>,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbContainer.setVisibility(View.VISIBLE);
            layoutContainer.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String className) {
            super.onPostExecute(className);
            int scrollY = 0;
            switch (className){
                case "IndexDbObject": {
                    scrollY = rebindRV("indexes");
                }break;
                case "UDFDbObject": {
                    scrollY = rebindRV("udfs");
                }break;
                case "SPDbObject":{
                    scrollY = rebindRV("sprocs");
                } break;
            }
            pbContainer.setVisibility(View.GONE);
            layoutContainer.setVisibility(View.VISIBLE);
            sw.scrollTo(0,scrollY);
        }

        @Override
        protected String doInBackground(ArrayList<DbProgrammObject>... params) {
            ArrayList<DbProgrammObject> objList = params[0];
            if(objList!=null && objList.size()>0) {
                String className = objList.get(0).getClass().getSimpleName();
                for (DbProgrammObject obj : objList) {
                    obj.recreateOnServer();
                    obj.checkExist();
                    obj.getName();
                }
                return className;
            }
            return null;
        }
    }

}
