package com.tradeassistant1c77;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.dbconnection.DbProgrammObject;

import java.util.ArrayList;


public class DbSystemAdapter extends RecyclerView.Adapter<DbSystemAdapter.ViewHolder> {

    ArrayList<DbProgrammObject> dbObjects;

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView status;
        ImageView action;
        TextView objectName;
        ProgressBar pb;

        public ViewHolder(View itemView) {
            super(itemView);

            status = (ImageView)itemView.findViewById(R.id.statusIW);
            action = (ImageView)itemView.findViewById(R.id.actionIW);
            objectName = (TextView) itemView.findViewById(R.id.objectName);
            pb = (ProgressBar)itemView.findViewById(R.id.createPB);
        }
    }

    public DbSystemAdapter(ArrayList<DbProgrammObject> objects) {
        dbObjects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View dbObjectView = inflater.inflate(R.layout.recycler_db_objects,parent,false);
        return new ViewHolder(dbObjectView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DbProgrammObject dbObject = dbObjects.get(position);
        if(dbObject.isExist()){
            holder.status.setImageResource(R.drawable.ic_check_ok);
            holder.action.setImageResource(R.drawable.ic_recreate_db_connection);
        }
        else{
            holder.status.setImageResource(R.drawable.ic_check_notok);
            holder.action.setImageResource(R.drawable.ic_add_db_component);
        }
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncCreateOnServer(holder).execute(dbObject);
            }
        });
        holder.objectName.setText(dbObject.getName());

    }

    @Override
    public int getItemCount() {
        return dbObjects.size();
    }

    public class AsyncCreateOnServer extends AsyncTask<DbProgrammObject,Void,DbProgrammObject>{
        ViewHolder holder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.holder.action.setVisibility(View.GONE);
            this.holder.pb.setVisibility(View.VISIBLE);
        }

        public AsyncCreateOnServer(ViewHolder iHolder) {
            this.holder = iHolder;
        }

        @Override
        protected void onPostExecute(DbProgrammObject dbObject) {
            super.onPostExecute(dbObject);
            this.holder.action.setVisibility(View.VISIBLE);
            this.holder.pb.setVisibility(View.GONE);

            try {
                if (dbObject.isExist()) {
                    this.holder.status.setImageResource(R.drawable.ic_check_ok);
                    this.holder.action.setImageResource(R.drawable.ic_recreate_db_connection);
                } else {
                    this.holder.status.setImageResource(R.drawable.ic_check_notok);
                    this.holder.action.setImageResource(R.drawable.ic_add_db_component);
                }
            }catch (Exception ex){
                Crashlytics.logException(ex);
            }
        }

        @Override
        protected DbProgrammObject doInBackground(DbProgrammObject... params) {
            DbProgrammObject dbObject = params[0];
            if(dbObject.isExist()) dbObject.recreateOnServer();
             else dbObject.createOnServer();
            dbObject.checkExist();

            return dbObject;
        }
    }
}
