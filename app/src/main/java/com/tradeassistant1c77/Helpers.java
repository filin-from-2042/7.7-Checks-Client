package com.tradeassistant1c77;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.tradeassistant1c77.Entites.User;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Helpers {
    // форматирование числа с плавающей точкой в формат вида 000 000.000
    public static String formatNumData(float number)
    {
        DecimalFormat dfPr = new DecimalFormat("###,###.##");
        DecimalFormatSymbols formatSymbols = dfPr.getDecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        dfPr.setDecimalFormatSymbols(formatSymbols);
        return dfPr.format(number);
    }

    public static String formatNumData(Double number)
    {
        return formatNumData(number.floatValue());
    }

    /**
     * завершает активность и перенаправляет на активность с логином
     * @param activity - текущая активность
     */
    public static void redirectToLogin(FragmentActivity activity)
    {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * Переинициализация объекта с данными о текущем пользователе
     * @param connection - объект Conntction для соединения класса User
     * @return true если переинициализация прошла успешно, false - наоборот
     */
    @Nullable
    public static boolean reinitUserBySavedData(Connection connection){
        MyApplication app = MyApplication.getInstance();
        Context context = app.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences("Auth", Context.MODE_PRIVATE);
        String login = sharedPref.getString(context.getString(R.string.ea_login_key), "not-exists");
        String password = sharedPref.getString(context.getString(R.string.ea_password_key), "not-exists");
        if (!login.equals("not-exists") && !password.equals("not-exists")) {
            User usr = new User(connection, login, password);
            DataHolder.setData("LoggedUser",usr);
            return true;
        }
        return  false;
    }
}
