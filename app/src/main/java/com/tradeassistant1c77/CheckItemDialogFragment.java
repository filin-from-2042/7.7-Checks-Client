package com.tradeassistant1c77;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

public class CheckItemDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_COUNT = "count";

    public void setFragmentContext(CheckDetailFragment fragmentContext) {
        this.fragmentContext = fragmentContext;
    }

    private CheckDetailFragment fragmentContext;

    public static CheckItemDialogFragment newInstance(CheckDetailFragment context,String product_id, Double count ) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, product_id);
        args.putSerializable(ARG_COUNT, count);
        CheckItemDialogFragment fragment = new CheckItemDialogFragment();
        fragment.setArguments(args);
        fragment.setFragmentContext(context);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int countInt = ((Double) getArguments().getSerializable(ARG_COUNT)).intValue();
        final Double countDouble = (Double) getArguments().getSerializable(ARG_COUNT);

        final String priceId = (String) getArguments().getSerializable(ARG_PRODUCT_ID);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_item_count, null);

        final NumberPicker numberPickerBP = (NumberPicker) v.findViewById(R.id.numberPickerCount);
        numberPickerBP.setMaxValue(999999);
        numberPickerBP.setMinValue(0);
        numberPickerBP.setValue(countInt);

        final EditText textDouble = (EditText) v.findViewById(R.id.TextCount);
        textDouble.setText(String.format("%s",countDouble));

        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_int:
                        numberPickerBP.setVisibility(View.VISIBLE);
                        textDouble.setVisibility(View.GONE);
                        break;
                    case R.id.radio_double:
                        numberPickerBP.setVisibility(View.GONE);
                        textDouble.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.check_item_dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Double newCount = Double.parseDouble(textDouble.getText().toString());
                                if (radioGroup.getCheckedRadioButtonId() == R.id.radio_int)
                                    newCount = (double)numberPickerBP.getValue();

                                fragmentContext.setCheckItemCount(priceId,newCount);

                            }
                })
                .setView(v)
                .setNegativeButton(android.R.string.cancel,null)
                .create();

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                Context context = getContext();
                if(context!=null) {
                    int cc = ContextCompat.getColor(context, R.color.secondaryDarkColor);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cc);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(cc);
                }
            }
        });

        return dialog;
    }
}
