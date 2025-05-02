package com.tradeassistant1c77;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.tradeassistant1c77.dbconnection.ConnectionClass;
import com.tradeassistant1c77.Entites.User;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    EditText edtuserid,edtpass;
    ProgressDialog pd;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, MyPreferenceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AvailableTask initTask = new AvailableTask(new TaskListener() {
            @Override
            public void onSuccess() {
                User loggedUser = (DataHolder.getData("LoggedUser")!=null) ? (User) DataHolder.getData("LoggedUser") : null;
                // если пользователь не залогинен вообще
                if (loggedUser == null) {
                    SharedPreferences sharedPref = getSharedPreferences("Auth", Context.MODE_PRIVATE);
                    String login = sharedPref.getString(getString(R.string.ea_login_key), "not-exists");
                    String password = sharedPref.getString(getString(R.string.ea_password_key), "not-exists");
                    // если есть сохраненные учетные данные
                    if (!login.equals("not-exists") && !password.equals("not-exists")) {
                        DoSavedUserLogin savedUserLogin = new DoSavedUserLogin();
                        savedUserLogin.execute(login, password);
                    } else initLoginForm();
                } else goToMain();
            }

            @Override
            public void onError() {
                Toast.makeText(LoginActivity.this,R.string.server_not_available,Toast.LENGTH_SHORT).show();
                initLoginForm();
            }
        });
        initTask.execute();
    }
    // инициализации формы с полями ввода логина и пароля
    public void initLoginForm()
    {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);

        edtuserid = (EditText) findViewById(R.id.edtuserid);
        edtpass = (EditText) findViewById(R.id.edtpass);
        final Button btnlogin = (Button) findViewById(R.id.btnlogin);
        Button btnPreference = (Button) findViewById(R.id.preferenceBtn);
        pd = new ProgressDialog(this);
        pd.setMessage("Вход...");
        edtpass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE))
                {
                    btnlogin.callOnClick();
                }
                return false;
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AvailableTask initTask = new AvailableTask(new TaskListener() {
                    @Override
                    public void onSuccess() {
                        if(!ConnectionClass.isPoolExist())  ConnectionClass.initConnectionPool();
                        DoLogin doLogin = new DoLogin();
                        doLogin.execute("");
                    }
                    @Override
                    public void onError() {
                        Toast.makeText(LoginActivity.this,R.string.server_not_available,Toast.LENGTH_SHORT).show();
                    }
                });
                initTask.execute();
            }
        });
        btnPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MyPreferenceActivity.class);
                startActivity(intent);
            }
        });
    }
    // переход на главную активность
    public void goToMain()
    {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
    // удаляет сохраненные учетные данные
    public void removeSPData()
    {
        SharedPreferences sharedPref = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
    // аутентификация по сохраненным ранее данным
    public class DoSavedUserLogin extends AsyncTask<String,String,String>
    {
        User usr ;
        @Override
        protected String doInBackground(String... strings) {
            Connection connection = ConnectionClass.getConnection();
            usr = new User(connection, strings[0], strings[1]);
            try {
                connection.close();
            }
            catch (SQLException ex){
                Crashlytics.logException(ex);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (usr!=null  && usr.getUserID()!=null && !usr.getUserID().isEmpty()) {
                DataHolder.setData("LoggedUser", usr);
                goToMain();
            }
            else removeSPData();
            setTheme(R.style.AppTheme);
        }
    }
    // аутентификация по введеным в поля формы данным
    public class DoLogin extends AsyncTask<String,String,String>
    {
        String message = "";
        Boolean isSuccess = false;
        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected void onPostExecute(String r) {
            pd.hide();
            Toast.makeText(LoginActivity.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess) {
                String inputLogin = edtuserid.getText().toString();
                String inputPassword = edtpass.getText().toString();

                // сохранение логина и пароля в файл настроек
                SharedPreferences sharedPref = getSharedPreferences("Auth", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.ea_login_key), inputLogin);
                editor.putString(getString(R.string.ea_password_key), inputPassword);
                editor.putString(getString(R.string.ea_create_date_key), String.valueOf(System.currentTimeMillis()));
                editor.apply();

                goToMain();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(MyApplication.IsFirstRun(LoginActivity.this))
                ConnectionClass.initDbFunctionality(LoginActivity.this);
            if(userid==null || userid.trim().equals(""))
                message = getString(R.string.user_fill_data_msg);
            else
            {
                Connection connection = ConnectionClass.getConnection();
                try {
                    if (connection==null) {
                            message= getString(R.string.server_connection_error);
                    } else {
                        User usr = new User(connection, userid,password);
                        if(usr.getUserID()!=null && !usr.getUserID().isEmpty())
                        {
                            DataHolder.setData("LoggedUser", usr);
                            message = getString(R.string.user_authorization_success);
                            isSuccess = true;
                        }
                        else{
                            message = getString(R.string.user_incorrect_login_password);
                            isSuccess = false;
                        }
                        return message;
                    }
                }
                catch (Exception ex)
                {
                    Crashlytics.logException(ex);
                    ex.printStackTrace();
                    isSuccess = false;
                    message = ex.getMessage();
                }finally {
                    try {
                        if(connection!=null) connection.close();
                    }catch (SQLException ex){
                        Crashlytics.logException(ex);
                    }
                }
            }
            return message;
        }
    }

    // таск проверяет сервер на доступность и по окончании выполняет переданный в интефрейсе код
    class AvailableTask extends AsyncTask<String, Integer, String>{
        // объект с обработчиками, которые будут вызваны по умолчанию
        private final TaskListener taskListener;
        // флаг доступности сервера
        private boolean available = false;

        public AvailableTask(TaskListener listener) {
            this.taskListener = listener;
        }

        @Override
        protected String doInBackground(String... arg) {
            // инициализация и сохранение пула соединений
            ConnectionClass.initConnectionPool();
            available = ConnectionClass.checkAvailability();
            return  "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(available) taskListener.onSuccess();
            else taskListener.onError();

        }
    }

    // интерфейс для передачи в syncTask роверки сервера на доступность
    public interface TaskListener {
        void onSuccess();
        void onError();
    }
}