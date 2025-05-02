package com.tradeassistant1c77;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.dbconnection.ConnectionClass;
import com.tradeassistant1c77.Entites.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchableItemActivity extends ListActivity {
    ListView list;
    ProgressBar pb;
    TextView noData;
    AsyncFillProductData fillTsk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable_item);


        list = (ListView)findViewById(android.R.id.list);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Product product = (Product) parent.getItemAtPosition(position);
                    startFilling(product);
                }catch(Exception ex){
                    Crashlytics.logException(ex);
                    ex.printStackTrace();
                }
            }
        });
        noData = (TextView)findViewById(R.id.emptyCustom);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String search = intent.getStringExtra(SearchManager.QUERY);
            new AsyncGetItems().execute(search);
        }
    }

    /**
     * запуск заполнения данными выбранную номенклатуру
     * @param product номенклатура, выбранная из списка с поиском
     */
    public void startFilling(final Product product)
    {
        fillTsk = new AsyncFillProductData();
        fillTsk.execute(product);
    }

    public class AsyncGetItems extends AsyncTask<String,Void,ArrayList<Product>>{
        @Override
        protected ArrayList<Product> doInBackground(String... params) {
            String search = params[0];
            Connection connection = ConnectionClass.getConnection();
            String query = "SELECT [ID]" +
                    "      ,[CODE]" +
                    "      ,RTRIM(LTRIM([DESCR]))" +
                    "      ,[SP131] "+
                    "  FROM [SC148]" +
                    "  WHERE (CODE LIKE ? OR DESCR LIKE ?) AND ISMARK=0 AND ISFOLDER=2" +
                    "  ORDER BY ROW_ID DESC";
            ArrayList<Product> items = new ArrayList<>();
            try {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1,"%"+search+"%");
                ps.setString(2,"%"+search+"%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    items.add(new Product(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)));
                }
                ps.close();
                rs.close();
            }catch (Exception ex){
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

        @Override
        protected void onPostExecute(ArrayList<Product> items) {
            super.onPostExecute(items);

            if(items.size()>0) {
                ArrayAdapter<Product> adapter = new ArrayAdapter<>(SearchableItemActivity.this,
                        android.R.layout.simple_list_item_1, items);

                list.setAdapter(adapter);
                list.setVisibility(View.VISIBLE);
            } else noData.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }

    public class AsyncFillProductData extends AsyncTask<Product,Void,Product>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }

        @Override
        protected Product doInBackground(Product... params) {
            Product productItem = params[0];
            productItem.fillRemains();
            productItem.fillPrice();
            return productItem;
        }

        @Override
        protected void onPostExecute(Product product) {
            super.onPostExecute(product);
            DataHolder.setData("newCheckItem", product);
            finish();
        }
    }
}