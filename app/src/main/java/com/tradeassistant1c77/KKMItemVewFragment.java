package com.tradeassistant1c77;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.Entites.Check;
import com.tradeassistant1c77.Entites.DocumentTypes;
import com.tradeassistant1c77.Entites.KKMItemView;
import com.tradeassistant1c77.dbconnection.ConnectionClass;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class KKMItemVewFragment extends Fragment {

    private static String ARG_ITEM_ID = "kkm_item_id";
    public static String ARG_VIEW_TYPE_ID = "kkm_view_type_id";

    // пометка о том что декоратор уже был назначен RecyclerView
    private boolean flagDecoration = false;

    private View layoutView;
    int viewType;

    public static KKMItemVewFragment newInstance(String itemID, int type)
    {
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID,itemID);
        args.putInt(ARG_VIEW_TYPE_ID,type);
        KKMItemVewFragment fragment = new KKMItemVewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // не обновлять список чеков после простого просмотра объекта ккм
        DataHolder.setData("resetRefreshList","true");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kkm_item_view, container, false);
        layoutView = v;
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        String itemID = getArguments().getString(ARG_ITEM_ID);
        viewType = getArguments().getInt(ARG_VIEW_TYPE_ID);
        new AsyncCreate().execute(itemID);
    }

    public void showDHTab(View v, KKMItemView item )
    {
        LinearLayout dhLayout = (LinearLayout) v.findViewById(R.id.dhLayout);
        dhLayout.setVisibility(View.VISIBLE);

        // заголовок документа
        TextView checkCodeTitle = (TextView) v.findViewById(R.id.checkCodeTitle);
        String checkCodeText = "";

        TextView companyNameValue = (TextView) v.findViewById(R.id.companyNameValue);

        TextView userNameValue = (TextView) v.findViewById(R.id.userNameValue);

        TextView stockNameValue = (TextView) v.findViewById(R.id.stockNameValue);

        TextView totalValue = (TextView) v.findViewById(R.id.totalValue);

        TextView NDSValue = (TextView) v.findViewById(R.id.NDSValue);

        switch (item.getType()) {
            case 1473: {
                checkCodeText = "Чек ";
                // НДС
                RelativeLayout NDSLayout = (RelativeLayout) v.findViewById(R.id.NDSLayout);
                NDSLayout.setVisibility(View.GONE);

            }
            break;
            case 802: {
                checkCodeText = "Отчет ККМ ";
                // касса
                RelativeLayout cashboxNameLayout = (RelativeLayout) v.findViewById(R.id.cashboxNameLayout);
                cashboxNameLayout.setVisibility(View.VISIBLE);
                TextView chashboxNameValue = (TextView) v.findViewById(R.id.chashboxNameValue);
                chashboxNameValue.setText(item.getCashboxkName());
            }
            break;
            case 536: {
                checkCodeText = "Возврат от покупателя ";
                // основание для возврата
                RelativeLayout returnReasonLayout = (RelativeLayout) v.findViewById(R.id.returnReasonLayout);
                returnReasonLayout.setVisibility(View.VISIBLE);
                TextView reasonContent = (TextView) v.findViewById(R.id.reasonContent);
                reasonContent.setText(item.getCause());

            }
            break;
        }
        // номер документа
        checkCodeText += item.getDocno();
        // дата
        if(item.getDate()!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.FRANCE);
            checkCodeText += " от " + sdf.format(item.getDate());
        }

        checkCodeTitle.setText(checkCodeText);
        // пользователь
        userNameValue.setText(item.getUsername());
        // фирма
        companyNameValue.setText(item.getCompanyName());
        // склад
        stockNameValue.setText(item.getStockName());
        // всего
        String totalStr = Helpers.formatNumData(item.getSumm()) + " Р";
        totalValue.setText(totalStr);
        // НДС
        String ndsStr = Helpers.formatNumData(item.getSummNDS()) + " Р";
        NDSValue.setText(ndsStr);
    }

    public void showDTTab(View v, KKMItemView item )
    {
        LinearLayout dtLayout = (LinearLayout) v.findViewById(R.id.dtLayout);
        dtLayout.setVisibility(View.VISIBLE);
        // список номенклатуры в объекте
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.kkm_items_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // чекам на текущую дату показывать остатки
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        RecyclerView.Adapter mAdapter;
        // разные адаптеры с разными макетами нужны из-за ресурсоемкости операции по получению остатков. Быстрее использовать разные макеты
        if(item.getDate()!=null && item.getDate().compareTo(cal.getTime())==0) {
            mAdapter = new CheckDetailAdapter(item.getItemsDT(), this, false);
        } else mAdapter = new KKMItemViewAdapter(item.getItemsDT());

        if (!flagDecoration) {
            RecyclerView.ItemDecoration itemDecoration = new
                    DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(itemDecoration);
            flagDecoration = true;
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public class AsyncCreate extends AsyncTask<String,Void,KKMItemView>{

        // тег для фрагмента измененеия кол-ва
        private static final String REMOVE_DIALOG_TAG = "removeDialog";

        @Override
        protected KKMItemView doInBackground(String... params) {
            if(DataHolder.getData("LoggedUser")==null){
                Connection connection = ConnectionClass.getConnection();
                if(!Helpers.reinitUserBySavedData(connection)){
                    try{
                        connection.close();
                    }catch (Exception ex){
                        Crashlytics.logException(ex);
                    }
                    return null;
                }
            }
            return new KKMItemView(params[0]);
        }

        @Override
        protected void onPostExecute(KKMItemView kkmItem) {
            super.onPostExecute(kkmItem);
            FragmentActivity  fActivity = getActivity();
            final KKMItemView item = kkmItem;
            // если актиивность закрылась до завершения таска
            if(fActivity!=null) {
                if(item==null || item.getDocno()==null){
                    DataHolder.setData("resetRefreshList", "false");
                    fActivity.finish();
                }
                String title = item.getDocno();
                if (kkmItem.getType()== DocumentTypes.CHECK)
                    title +=" - Чек";
                else if(kkmItem.getType()==DocumentTypes.REPORT_KKM)
                    title += " - Отчет ККМ";
                else if(kkmItem.getType()==DocumentTypes.BUYER_RETURN)
                    title += " - Возврат";
                ((AppCompatActivity)fActivity).getSupportActionBar().setTitle(title);
                // отображать кнопку с возможностью удаления только для непроведенных чеков и только создателю
                if (DataHolder.getData("LoggedUser") != null) {
                    if (item.isEditable()) {
                        FloatingActionButton fabRemove = (FloatingActionButton) layoutView.findViewById(R.id.fab_remove);
                        fabRemove.setVisibility(View.VISIBLE);
                        fabRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DataHolder.setData("resetRefreshList", "false");
                                KKMRemoveDialogFragment dialog = KKMRemoveDialogFragment.newInstance(item.getId());
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                dialog.show(manager, REMOVE_DIALOG_TAG);
                            }
                        });

                        FloatingActionButton fabEdit = (FloatingActionButton) layoutView.findViewById(R.id.fab_edit);
                        fabEdit.setVisibility(View.VISIBLE);
                        fabEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DataHolder.setData("resetRefreshList", "false");
                                // создаем чек по ККМ объекту и заполняем остатки у товаров в нем
                                Check newCheck = new Check(item);
                                DataHolder.setData("newCheck", newCheck);
                                FragmentActivity activity = getActivity();
                                Intent i = new Intent(activity, CheckDetailActivity.class);
                                startActivity(i);
                            }
                        });
                    }
                }

                CardView card = (CardView) layoutView.findViewById(R.id.cardView);
                card.setVisibility(View.VISIBLE);
                if (viewType == 0)
                    showDHTab(layoutView, item);
                else if (viewType == 1)
                    showDTTab(layoutView, item);

                ProgressBar initPB = (ProgressBar) layoutView.findViewById(R.id.initProgressBar);
                initPB.setVisibility(View.GONE);
            }
        }
    }

}