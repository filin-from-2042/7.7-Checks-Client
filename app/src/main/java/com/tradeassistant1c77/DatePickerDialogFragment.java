package com.tradeassistant1c77;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerDialogFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.tradeassistant1c77.dialogFragmentDate";

    public static DatePickerDialogFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date)getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date_picker, null);

        final DatePicker dPicker = (DatePicker)v.findViewById(R.id.datePicker);
        dPicker.init(year,month,day,null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(getTargetFragment()==null) return;

                        int year = dPicker.getYear();
                        int month = dPicker.getMonth();
                        int day = dPicker.getDayOfMonth();

                        GregorianCalendar selectedCalendar = new GregorianCalendar(year,month,day);
                        Date newDate = selectedCalendar.getTime();

                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_DATE,newDate);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent );
                    }
                })
                .create();
    }
}
