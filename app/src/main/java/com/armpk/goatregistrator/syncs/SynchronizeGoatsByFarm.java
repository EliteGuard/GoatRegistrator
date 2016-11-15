package com.armpk.goatregistrator.syncs;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.FarmGoat;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.utilities.Globals;
import com.armpk.goatregistrator.utilities.RestConnection;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizeGoatsByFarm extends AsyncTask<Void, Integer, Boolean>{

    private OnGoatsByFarmSynchronize mGoatsByFarmSynchronizeListener;

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String data = null;
    private JSONArray jsonData;
    private DatabaseHelper dbHelper;
    private Farm mFarm;

    private int divisioner=10;

    public SynchronizeGoatsByFarm(Context ctx, String result, Farm farm, DatabaseHelper databaseHelper, OnGoatsByFarmSynchronize goatsByFarmSynchronizeListener) {
        mContext = ctx;
        data = result;
        mProgressDialog = new ProgressDialog(mContext);
        dbHelper = databaseHelper;
        mGoatsByFarmSynchronizeListener = goatsByFarmSynchronizeListener;
        mFarm = farm;
    }

    @Override
    protected void onPreExecute() {
        //mProgressDialog.setMessage("Синхронизиране на кози по ферми");
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
        long startTime = System.currentTimeMillis();

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
                for(FarmGoat fg : dbHelper.getDaoFarmGoat().queryBuilder().where().eq("farm_id", mFarm.getId()).query()){
                    dbHelper.getDaoGoat().delete(fg.getGoat());
                }
                //DeleteBuilder<FarmGoat, Long> dbFG = dbHelper.getDaoFarmGoat().deleteBuilder();
                DeleteBuilder<FarmGoat, Long> dbQb= dbHelper.getDaoFarmGoat().deleteBuilder();
                dbQb.where().eq("farm_id", mFarm.getId());
                dbQb.delete();
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
                                if(dbHelper.updateGoatByDate(jsonData.getJSONObject(j), mFarm)){
                                    updateGoatBonitirovka(jsonData.getJSONObject(j));
                                }
                                if (j % divisioner == 0 || j == jsonData.length())
                                    publishProgress(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        success[0] = true;
                    }

                    private void updateGoatBonitirovka(JSONObject goatJson) {
                        Goat g = Globals.jsonToObject(goatJson, Goat.class);
                        /*RestConnection mRestGoatBonitirovka = new RestConnection(mContext,
                                RestConnection.DataType.BONITIROVKA, String.valueOf(g.getId()),
                                new RestConnection.OnConnectionCompleted() {
                            @Override
                            public void onResultReceived(RestConnection.Action action, RestConnection.DataType dataType, String result) {
                                if(result!=null) {
                                    if (action == RestConnection.Action.GET) {
                                        switch (dataType) {
                                            case BONITIROVKA:
                                                synchronizeGoatMeasurement(result);
                                                break;
                                        }
                                    }
                                }
                            }
                        });
                        mRestGoatBonitirovka.setIsBackground(true);
                        mRestGoatBonitirovka.setAction(RestConnection.Action.GET);
                        mRestGoatBonitirovka.execute((Void) null);*/
                        getGoatBonitirovka(String.valueOf(g.getId()));

                    }
                    private void getGoatBonitirovka(String goatId){
                        String result = null;
                        if(hasInternetConnection()) {
                            InputStream is = null;
                            try {
                                //setDataType(additionalDataArg);
                                URL url = new URL("http://94.156.222.197:8081/goats/armpkrest/members/goats/"+goatId+"/measurements/bonitirovka");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setReadTimeout(15000);
                                conn.setConnectTimeout(15000);
                                conn.setUseCaches(false);
                                conn.setRequestMethod("GET");
                                conn.setDoInput(true);
                                conn.setRequestProperty("Content-Type","application/json");
                                conn.connect();
                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    //publishProgress(1);
                                    is = conn.getInputStream();
                                    result = readInputStream(is);
                                    //publishProgress(2);
                                }
                                if(result!=null) synchronizeGoatMeasurement(new JSONArray(result));
                            } catch (UnsupportedEncodingException | ProtocolException | MalformedURLException | JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (is != null) try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    private String readInputStream(InputStream stream) throws IOException {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        int c;
                        for (c = stream.read(); c != '\n' && c != -1 ; c = stream.read()) {
                            byteArrayOutputStream.write(c);
                        }
                        if (c == -1 && byteArrayOutputStream.size() == 0) {
                            return null;
                        }
                        return byteArrayOutputStream.toString("UTF-8");
                    }
                    private boolean hasInternetConnection(){
                        boolean active = false;
                        ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                        active = networkInfo != null && networkInfo.isConnected();
                        return active;
                    }
                    private void synchronizeGoatMeasurement(JSONArray result) {
                        //DatabaseHelper dbHelper = new DatabaseHelper(mContext);
                        try {
                            for(int i = 0; i<result.length(); i++){
                                dbHelper.updateGoatBonitirovka(result.getJSONObject(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
            //Log.d("TIME TO PROCESS", String.valueOf(System.currentTimeMillis()-startTime));
        }
        return success[0];
    }

    /*private void updateGoatBonitirovka(JSONObject goatJson) {

        Goat g = Globals.jsonToObject(goatJson, Goat.class);

        RestConnection mRestGoatBonitirovka = new RestConnection(mContext, RestConnection.DataType.BONITIROVKA, "", this);
        mRestGoatBonitirovka.setIsBackground(true);
        mRestGoatBonitirovka.setAction(RestConnection.Action.GET);
        mRestGoatBonitirovka.execute((Void) null);

    }*/

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
            //Toast.makeText(mContext, "Успешно актуализиране на кози по ферма!", Toast.LENGTH_LONG).show();
            //SharedPreferences sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
            Globals.savePreferences(Globals.SYNC_FARMS_LAST_DATE, System.currentTimeMillis(), mContext);
        }else{
            Toast.makeText(mContext, "Възникна грешка...", Toast.LENGTH_LONG).show();
        }
        mGoatsByFarmSynchronizeListener.onGoatsByFarmSynchronizeFinished(success);
    }

    public void setMessage(String msg){
        mProgressDialog.setMessage(msg);
    }

    public interface OnGoatsByFarmSynchronize{
        void onGoatsByFarmSynchronizeFinished(boolean success);
    }
}
