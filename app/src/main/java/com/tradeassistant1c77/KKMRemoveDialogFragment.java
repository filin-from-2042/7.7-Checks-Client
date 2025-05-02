package com.tradeassistant1c77;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.tradeassistant1c77.Entites.Check;


public class KKMRemoveDialogFragment extends DialogFragment {

    private static final String ARG_ITEM_ID = "item_id";
    private FragmentActivity fragmentActivity;

    public static KKMRemoveDialogFragment newInstance(String itemID) {
        KKMRemoveDialogFragment frag = new KKMRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemID);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        fragmentActivity = getActivity();
        final String itemID = (String)getArguments().getSerializable(ARG_ITEM_ID);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.kkm_remove_dialog_title)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncFabRemove().execute(itemID);
                    }
                })
                .setNegativeButton(android.R.string.cancel,null).create();

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

    public class AsyncFabRemove extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            Check.remove(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fragmentActivity.finish();

            Toast.makeText(fragmentActivity, R.string.kkm_remove_dialog_toast ,Toast.LENGTH_SHORT).show();
        }
    }

}
