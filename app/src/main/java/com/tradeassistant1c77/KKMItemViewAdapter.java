package com.tradeassistant1c77;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradeassistant1c77.Entites.Product;

import java.util.Map;


public class KKMItemViewAdapter extends RecyclerView.Adapter<KKMItemViewAdapter.ViewHolder> {

    private Map<String,Product> kkmItemsList;

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView codeTV;
        public TextView nameTV;

        public LinearLayout item_costLL;
        public TextView item_costTV;
        public TextView item_countTV;
        public TextView items_costTV;

        public RelativeLayout rowContainer;
        public LinearLayout messageContainer;

        public ViewHolder(View v) {
            super(v);
            this.codeTV = (TextView) v.findViewById(R.id.code);
            this.nameTV = (TextView) v.findViewById(R.id.name);

            this.item_costLL = (LinearLayout) v.findViewById(R.id.cost_container);
            this.item_costTV = (TextView) v.findViewById(R.id.item_cost);
            this.item_countTV = (TextView) v.findViewById(R.id.item_count);
            this.items_costTV = (TextView) v.findViewById(R.id.items_cost);

            rowContainer = (RelativeLayout)v.findViewById(R.id.row_container);
            messageContainer = (LinearLayout)v.findViewById(R.id.message_container);
        }
    }

    public KKMItemViewAdapter(Map<String,Product> kkmItemsList)
    {
        this.kkmItemsList = kkmItemsList;
    }

    @Override
    public KKMItemViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater
                .inflate(R.layout.recycler_kkm_items_list_layout, parent, false);
        return new KKMItemViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final KKMItemViewAdapter.ViewHolder holder, int position) {
        final Product currProduct = kkmItemsList.get((kkmItemsList.keySet().toArray())[position]);
        holder.codeTV.setText(currProduct.getCode());
        holder.nameTV.setText(currProduct.getName());

        String costTv = Helpers.formatNumData(currProduct.getPrice()) + " Р";
        holder.item_costTV.setText(costTv);
        holder.item_countTV.setText(Helpers.formatNumData(currProduct.count));

        Double allCost = currProduct.getPrice()*currProduct.count;
        String allCostTv = Helpers.formatNumData(allCost) + " Р";
        holder.items_costTV.setText(allCostTv);
    }


    @Override
    public int getItemCount() {
        return kkmItemsList.size();
    }

}
