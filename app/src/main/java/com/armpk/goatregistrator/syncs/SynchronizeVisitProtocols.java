package com.armpk.goatregistrator.syncs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.database.VisitProtocolVisitActivity;
import com.armpk.goatregistrator.utilities.Globals;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizeVisitProtocols extends AsyncTask<Void, Integer, Boolean> {

    private OnVisitProtocolSynchronize mVisitProtocolSynchronizeListener;

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String data = null;
    private JSONArray jsonData;
    private DatabaseHelper dbHelper;

    private int divisioner=10;

    public SynchronizeVisitProtocols(Context ctx, String result, DatabaseHelper databaseHelper, OnVisitProtocolSynchronize visitProtocolSynchronizeListener) {
        mContext = ctx;
        data = result;
        mProgressDialog = new ProgressDialog(mContext);
        dbHelper = databaseHelper;
        mVisitProtocolSynchronizeListener = visitProtocolSynchronizeListener;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Синхронизиране на протоколи за посещение");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final boolean[] success = {false};
        //long startTime = System.currentTimeMillis();

        try {
            jsonData = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        };
        if(jsonData!=null) {
            int JOBS_COUNT = Runtime.getRuntime().availableProcessors();
            int part = jsonData.length()/(JOBS_COUNT-1);
            int remainder = jsonData.length() % (JOBS_COUNT - 1);
            if (jsonData.length() >= 2000) divisioner = 500;
            else if (jsonData.length() >= 500) divisioner = 100;
            else divisioner = 1;
            final Pair<Integer, Integer>[] range = new Pair[JOBS_COUNT];
            for (int i = 0; i < JOBS_COUNT; i++) {
                if (i < JOBS_COUNT - 1) {
                    range[i] = new Pair<Integer, Integer>(part * i, part * (i + 1));
                } else {
                    range[i] = new Pair<Integer, Integer>(part * i, jsonData.length());
                }
            }

            try {
                //QueryBuilder<FarmGoat, Long> qbFG = dbHelper.getDaoFarmGoat().queryBuilder();
                for (VisitProtocolVisitActivity vpva : dbHelper.getDaoVisitProtocolVisitActivity().queryForAll()) {
                    dbHelper.getDaoVisitProtocolVisitActivity().delete(vpva);
                }
                for(VisitProtocol vp : dbHelper.getDaoVisitProtocol().queryForAll()){
                    dbHelper.getDaoVisitProtocol().delete(vp);
                }
                //DeleteBuilder<FarmGoat, Long> dbFG = dbHelper.getDaoFarmGoat().deleteBuilder();
                //dbHelper.getDaoVisitProtocolVisitActivity().deleteBuilder().delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // create a pool of threads, 10 max jobs will execute in parallel
            ExecutorService threadPool = Executors.newFixedThreadPool(JOBS_COUNT);
            // submit jobs to be executing by the pool
            for (int t = 0; t < JOBS_COUNT; t++) {
                final int finalT = t;
                threadPool.submit(new Runnable() {
                    public void run() {
                        for (int j = range[finalT].first; j < range[finalT].second; j++) {
                            try {
                                //dbHelper.createOrUpdateCities(jsonData.getJSONObject(j));
                                dbHelper.updateVisitProtocolByDate(jsonData.getJSONObject(j));
                                if (j % divisioner == 0 || j == jsonData.length())
                                    publishProgress(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        success[0] = true;
                    }
                });
            }
            // once you've submitted your last job to the service it should be shut down
            threadPool.shutdown();
            // wait for the threads to finish if necessary
            try {
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return success[0];
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if(progress[0]==0){
            mProgressDialog.setMax(jsonData.length());
        }
        mProgressDialog.incrementProgressBy(divisioner);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(mProgressDialog.isShowing()){
            mProgressDialog.cancel();
        }
        if(success){
            Toast.makeText(mContext, "Успешно актуализиране на протоколите за посещение!", Toast.LENGTH_LONG).show();
            Globals.savePreferences(Globals.SYNC_VISIT_PROTOCOLS_LAST_DATE, System.currentTimeMillis(), mContext);
        }else{
            Toast.makeText(mContext, "Възникна грешка...", Toast.LENGTH_LONG).show();
        }
        mVisitProtocolSynchronizeListener.onSynchronizeFinished(success);
    }

    public interface OnVisitProtocolSynchronize{
        void onSynchronizeFinished(boolean success);
    }
}
