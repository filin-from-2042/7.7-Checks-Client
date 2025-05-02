package com.tradeassistant1c77.Entites;

import java.util.HashMap;
import java.util.Map;

public class CheckAddStatus {
    public static String SUCCESS = "Чек успешно добавлен";
    public static String UPDATE_SUCCESS = "Чек успешно обновлен";
    public static String ERROR_1SJOURN = "Не удалось добавить чек в журнал документов";
    public static String ERROR_1SUPDTS = "Не удалось добавить данные в список изменений для УРБД";
    public static String ERROR_1SUIDCTL = "Не удалось установить новый максимальный id для чеков";
    public static String ERROR_DH1473 = "Не удалось добавить реквизиты чека";
    public static String ERROR_1SJOURN_EXIST = "Чек с таким номером документа уже существует";
    public static String ERROR_CLOSED = "Чек уже проведен";
    public static String ERROR_LOGGED_USER = "Нет данных о пользователе";
    public static String ERROR_CHECK_NUMBER = "Не удалось получить номер для чека";

    public static Map<Integer,String> AddStatus = new HashMap<Integer,String>(){{
        put(0,CheckAddStatus.SUCCESS);
        put(1,CheckAddStatus.ERROR_1SJOURN);
        put(2,CheckAddStatus.ERROR_1SUPDTS);
        put(3,CheckAddStatus.ERROR_1SUIDCTL);
        put(4,CheckAddStatus.ERROR_DH1473);
        put(5,CheckAddStatus.ERROR_1SJOURN_EXIST);
        put(6,CheckAddStatus.ERROR_LOGGED_USER);
        put(7,CheckAddStatus.ERROR_CHECK_NUMBER);
    }};

    public static Map<Integer,String> UpdateStatus = new HashMap<Integer,String>(){{
        put(0,CheckAddStatus.UPDATE_SUCCESS);
        put(1,CheckAddStatus.ERROR_CLOSED);
        put(2,CheckAddStatus.ERROR_1SUPDTS);
        put(3,CheckAddStatus.ERROR_LOGGED_USER);
    }};
}
