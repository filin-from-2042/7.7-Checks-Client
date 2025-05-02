package com.tradeassistant1c77;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradeassistant1c77.Entites.Product;

import java.util.ArrayList;
import java.util.Map;
// адаптер номенклатуры в чеке
public class CheckDetailAdapter extends RecyclerView.Adapter<CheckDetailAdapter.ViewHolder> {
    private Map<String,Product> checkItems;
    private final Fragment context;
    private ArrayList<String> selectedItems = new ArrayList<>();

    private boolean selectingEnabled = true;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView codeTV;
        public TextView nameTV;
        public TextView remainsTV;

        public LinearLayout item_costLL;
        public TextView item_costTV;
        public TextView item_countTV;
        public TextView items_costTV;

        public RelativeLayout rowContainer;
        public LinearLayout messageContainer;
        public RecyclerView remainsList;


        public ViewHolder(View v) {
            super(v);
            this.codeTV = (TextView) v.findViewById(R.id.code);
            this.nameTV = (TextView) v.findViewById(R.id.name);
            this.remainsTV = (TextView) v.findViewById(R.id.remains);

            this.item_costLL = (LinearLayout) v.findViewById(R.id.cost_container);
            this.item_costTV = (TextView) v.findViewById(R.id.item_cost);
            this.item_countTV = (TextView) v.findViewById(R.id.item_count);
            this.items_costTV = (TextView) v.findViewById(R.id.items_cost);

            rowContainer = (RelativeLayout)v.findViewById(R.id.row_container);
            messageContainer = (LinearLayout)v.findViewById(R.id.message_container);

            remainsList = (RecyclerView) v.findViewById(R.id.remainsList);
            remainsList.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));

        }
    }

    public CheckDetailAdapter(Map<String,Product> inputItems, Fragment currContext) {
        checkItems = inputItems;
        context = currContext;
    }

    public CheckDetailAdapter(Map<String,Product> inputItems, Fragment currContext, boolean enableSelecting)
    {
        checkItems = inputItems;
        context = currContext;
        selectingEnabled=enableSelecting;
    }

    @Override
    public CheckDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Product currProduct = checkItems.get((checkItems.keySet().toArray())[position]);
        holder.codeTV.setText(currProduct.getCode());
        holder.nameTV.setText(currProduct.getName());

        String costTv = Helpers.formatNumData(currProduct.getPrice()) + " Р";
        holder.item_costTV.setText(costTv);
        holder.item_countTV.setText(Helpers.formatNumData(currProduct.count));

        Double allCost = currProduct.getPrice()*currProduct.count;
        String allCostTv = Helpers.formatNumData(allCost) + " Р";
        holder.items_costTV.setText(allCostTv);

        // пометка выделенных элементов
        if(selectedItems.contains(currProduct.id))
            holder.rowContainer.setBackgroundColor(Color.parseColor("#ECEFF1"));
        else
            holder.rowContainer.setBackgroundColor(ContextCompat.getColor(context.getContext(),android.R.color.background_light));

        View.OnClickListener listener = null;
        if(selectingEnabled) {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedItems.contains(currProduct.id))
                        selectedItems.remove(currProduct.id);
                    else
                        selectedItems.add(currProduct.id);

                    CheckDetailFragment activity = (CheckDetailFragment)context;
                    // активность должна иметь актуальный список выделенных элементов
                    activity.setSelectedItems(selectedItems);

                    if (selectedItems.isEmpty())
                        activity.changeToolbar(Mode.NORMAL);
                    else if (selectedItems.size() == 1)
                        activity.changeToolbar(Mode.SINGLE);
                    else if (selectedItems.size() > 1)
                        activity.changeToolbar(Mode.MULTIPLE);

                    notifyDataSetChanged();
                }
            };
            holder.rowContainer.setOnClickListener(listener);
            holder.messageContainer.setOnClickListener(listener);
            holder.item_costLL.setOnClickListener(listener);
        }

        String[] strRemains = currProduct.getRemainsArray();
        if(strRemains!=null) {
            holder.remainsList.setVisibility(View.VISIBLE);
            holder.remainsList.setAdapter(new ItemsAdapter(strRemains, listener));

        } else{
            holder.remainsTV.setText(currProduct.getRemainsStr());
            holder.remainsTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return checkItems.size();
    }

    // адаптер остатков
    public class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        View.OnClickListener listener;
        String[] remains ;

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.recycler_remains_item, parent, false);
            return new ItemViewHolder(itemView);
        }

        public ItemsAdapter(String[] iRemains, View.OnClickListener clickListener) {
            this.remains = iRemains;
            this.listener = clickListener;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.title.setText(remains[position]);
            if(listener!=null) holder.container.setOnClickListener(listener);
        }

        @Override
        public int getItemCount() {
            return remains.length;
        }
    }

    public  class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final LinearLayout container;

        private ItemViewHolder(View itemView) {
            super(itemView);

            this.title = (TextView)itemView.findViewById(R.id.title);
            this.container = (LinearLayout)itemView.findViewById(R.id.remainContainer);
        }
    }
}