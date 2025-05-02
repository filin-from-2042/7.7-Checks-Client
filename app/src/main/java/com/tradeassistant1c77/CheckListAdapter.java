package com.tradeassistant1c77;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradeassistant1c77.Entites.CheckListItem;
import com.tradeassistant1c77.Entites.DocumentTypes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CheckListAdapter extends  RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private ArrayList<CheckListItem> checksList;
    final private Activity activity;
    final private Fragment fragment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView typeIcon;
        public ImageView closedIcon;
        public TextView firstTxt;
        public TextView secondaryTxt;
        public TextView thirdTxt;
        public TextView summTxt;
        public TextView dateTxt;
        public RelativeLayout row_container;

        public ViewHolder(View v) {
            super(v);
            this.typeIcon = (ImageView)v.findViewById(R.id.imageView);
            this.closedIcon = (ImageView)v.findViewById(R.id.icon_mark);
            this.firstTxt = (TextView)v.findViewById(R.id.firstTxt);
            this.secondaryTxt = (TextView)v.findViewById(R.id.secondaryTxt);
            this.thirdTxt = (TextView)v.findViewById(R.id.thirdTxt);
            this.summTxt = (TextView)v.findViewById(R.id.summTxt);
            this.dateTxt = (TextView)v.findViewById(R.id.dateTxt);
            this.row_container = (RelativeLayout) v.findViewById(R.id.kkm_row_container);
        }
    }

    public CheckListAdapter(ArrayList<CheckListItem> checks, Activity activity, Fragment fragment) {
        checksList = checks;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater
                .inflate(R.layout.recycler_check_list_item_layout, parent, false);
        return new CheckListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CheckListItem checkItem = checksList.get(position);
        holder.row_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, KKMItemViewPagerActivity.class);
                intent.putExtra(KKMItemViewPagerActivity.EXT_ITEM_ID,checkItem.getId());
                fragment.startActivity(intent);
            }
        });
        String itemType="";
        if (checkItem.getDocType()== DocumentTypes.CHECK)
        {
            holder.typeIcon.setImageResource(R.drawable.ic_check_item_t);
            itemType = "Чек";
        }
        else if(checkItem.getDocType()==DocumentTypes.REPORT_KKM)
        {
            holder.typeIcon.setImageResource(R.drawable.ic_kkm_report_t);
            itemType="Отчет ККМ";
        }
        else if(checkItem.getDocType()==DocumentTypes.BUYER_RETURN)
        {
            holder.typeIcon.setImageResource(R.drawable.ic_return_type_t);
            itemType="Возврат от покупателя";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.FRANCE);
        String summ = Helpers.formatNumData(checkItem.getSumm()) + " Р";

        if(!checkItem.isClosed()) holder.closedIcon.setImageResource(R.drawable.ic_mark_opened);
        else  holder.closedIcon.setImageResource(R.drawable.ic_mark_closed_primary);
        holder.firstTxt.setText(itemType);
        holder.secondaryTxt.setText(checkItem.getDocNumber());
        holder.thirdTxt.setText(checkItem.getCreator());
        holder.summTxt.setText(summ);
        holder.dateTxt.setText(sdf.format(checkItem.getCheckDate()));
    }

    @Override
    public int getItemCount() {
        return checksList.size();
    }
}
