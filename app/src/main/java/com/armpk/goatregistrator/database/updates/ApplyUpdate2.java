package com.armpk.goatregistrator.database.updates;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.armpk.goatregistrator.database.DatabaseHelper;


public class ApplyUpdate2 extends AsyncTask <Void, Integer, Boolean>{
    private OnApplyUpdate2 mApplyUpdate2Listener;

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private DatabaseHelper dbHelper;

    public ApplyUpdate2(Context ctx, DatabaseHelper databaseHelper, OnApplyUpdate2 update2Listener) {
        mContext = ctx;
        mProgressDialog = new ProgressDialog(mContext);
        dbHelper = databaseHelper;
        mApplyUpdate2Listener = update2Listener;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Обновяване на данните");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        //mProgressDialog.setProgress(0);
        mProgressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        final boolean[] success = {false};

        if(dbHelper.applyUpdate2()) success[0] = true;

        return success[0];
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(mProgressDialog.isShowing()){
            mProgressDialog.cancel();
        }
        if(success){
            Toast.makeText(mContext, "Успешно обновяване на данните!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(mContext, "Възникна грешка...", Toast.LENGTH_LONG).show();
        }
        mApplyUpdate2Listener.onApplyUpdate2Finished(success);
    }

    public interface OnApplyUpdate2{
        void onApplyUpdate2Finished(boolean success);
    }
}
