package com.armpk.goatregistrator.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.utilities.Globals;
import com.armpk.goatregistrator.utilities.RestConnection;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.PipedReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity  implements RestConnection.OnConnectionCompleted{

    private SharedPreferences mSharedPreferences;
    private DatabaseHelper dbHelper;
    Handler updateProgressHandler;

    private static int part;//, threadCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        updateProgressHandler = new Handler();


        Button buttonSynchronizeCities = (Button)findViewById(R.id.buttonSynchronizeCities);
        if (buttonSynchronizeCities != null) {
            buttonSynchronizeCities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCities();
                }
            });
        }

        Button buttonSynchronizeBreeds = (Button)findViewById(R.id.buttonSynchronizeBreeds);
        if (buttonSynchronizeBreeds != null) {
            buttonSynchronizeBreeds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBreeds();
                }
            });
        }

        Button buttonSynchronizeGoatStatuses = (Button)findViewById(R.id.buttonSynchronizeGoatStatuses);
        if (buttonSynchronizeGoatStatuses != null) {
            buttonSynchronizeGoatStatuses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getGoatStatuses();
                }
            });
        }

        Button buttonSynchronizeExteriorMarks = (Button)findViewById(R.id.buttonSynchronizeExteriorMarks);
        if (buttonSynchronizeExteriorMarks != null) {
            buttonSynchronizeExteriorMarks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getExteriorMarks();
                }
            });
        }

        Button buttonSynchronizeVisitActivity = (Button)findViewById(R.id.buttonSynchronizeVisitActivities);
        if (buttonSynchronizeVisitActivity != null) {
            buttonSynchronizeVisitActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getVisitActivities();
                }
            });
        }

        /*Button buttonAddCityVillage = (Button)findViewById(R.id.buttonAddCityVillage);
        buttonAddCityVillage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CityAddActivity.class);
                startActivity(intent);
            }
        });*/

        Button buttonSignOut = (Button)findViewById(R.id.buttonSignOut);
        if (buttonSignOut != null) {
            buttonSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Globals.savePreferences(Globals.SETTING_SIGNED_IN, false, SettingsActivity.this);
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

    }

    @Override
    public void onResultReceived(RestConnection.Action action, RestConnection.DataType dataType, String result) {
        if(result!=null) {
            if (action == RestConnection.Action.GET) {
                switch (dataType) {
                    case CITIES:
                        synchronizeCities(result);
                        break;
                    case BREEDS:
                        synchronizeBreeds(result);
                        break;
                    case GOAT_STATUS:
                        synchronizeGoatStatuses(result);
                        break;
                    case EXTERIOR_MARK:
                        synchronizeExteriorMarks(result);
                        break;
                    case VISIT_ACTIVITY:
                        synchronizeVisitActivities(result);
                        break;
                }
            } else {
                RestConnection.closeProgressDialog();
            }
        }else{
            RestConnection.closeProgressDialog();
        }
    }

    private void getCities(){
        RestConnection mRestCities = new RestConnection(this, RestConnection.DataType.CITIES, this);
        mRestCities.setAction(RestConnection.Action.GET);
        mRestCities.execute((Void) null);
    }

    private void getBreeds() {
        RestConnection mRestBreeds = new RestConnection(this, RestConnection.DataType.BREEDS, this);
        mRestBreeds.setAction(RestConnection.Action.GET);
        mRestBreeds.execute((Void) null);
    }

    private void getGoatStatuses(){
        RestConnection mRestGoatStatuses = new RestConnection(this, RestConnection.DataType.GOAT_STATUS, this);
        mRestGoatStatuses.setAction(RestConnection.Action.GET);
        mRestGoatStatuses.execute((Void) null);
    }

    private void getExteriorMarks(){
        RestConnection mRestExteriorMarks = new RestConnection(this, RestConnection.DataType.EXTERIOR_MARK, this);
        mRestExteriorMarks.setAction(RestConnection.Action.GET);
        mRestExteriorMarks.execute((Void) null);
    }

    private void getVisitActivities(){
        RestConnection mRestVisitActivities = new RestConnection(this, RestConnection.DataType.VISIT_ACTIVITY, this);
        mRestVisitActivities.setAction(RestConnection.Action.GET);
        mRestVisitActivities.execute((Void) null);
    }

    private void synchronizeCities(String result){
        RestConnection.closeProgressDialog();
        SynchronizeCitiesTask sct = new SynchronizeCitiesTask(this, result);
        sct.execute((Void) null);
    }

    private void synchronizeBreeds(String result){
        RestConnection.closeProgressDialog();
        SynchronizeBreedsTask sct = new SynchronizeBreedsTask(this, result);
        sct.execute((Void) null);
    }

    private void synchronizeGoatStatuses(String result){
        RestConnection.closeProgressDialog();
        SynchronizeGoatStatusesTask sgst = new SynchronizeGoatStatusesTask(this, result);
        sgst.execute((Void) null);
    }

    private void synchronizeExteriorMarks(String result){
        RestConnection.closeProgressDialog();
        SynchronizeExteriorMarksTask semt = new SynchronizeExteriorMarksTask(this, result);
        semt.execute((Void) null);
    }

    private void synchronizeVisitActivities(String result){
        RestConnection.closeProgressDialog();
        SynchronizeVisitActivitiesTask svat = new SynchronizeVisitActivitiesTask(this, result);
        svat.execute((Void) null);
    }





    public class SynchronizeCitiesTask extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        private String data = null;
        private JSONArray jsonData;

        public SynchronizeCitiesTask(Context ctx, String result) {
            mContext = ctx;
            data = result;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Синхронизиране");
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
            //int cores = Runtime.getRuntime().availableProcessors();
            int JOBS_COUNT = Runtime.getRuntime().availableProcessors();
            part = jsonData.length()/(JOBS_COUNT-1);
            int remainder = jsonData.length()%(JOBS_COUNT-1);
            final Pair<Integer, Integer>[] range = new Pair[JOBS_COUNT];
            for(int i=0; i<JOBS_COUNT;i++){
                if(i<JOBS_COUNT-1) {
                    range[i] = new Pair<Integer, Integer>(part * i, part * (i + 1));
                }else{
                    range[i] = new Pair<Integer, Integer>(part * i, jsonData.length());
                }
            }
            // create a pool of threads, 10 max jobs will execute in parallel
            ExecutorService threadPool = Executors.newFixedThreadPool(JOBS_COUNT);
            // submit jobs to be executing by the pool
            for (int t = 0; t < JOBS_COUNT; t++) {
                final int finalT = t;
                threadPool.submit(new Runnable() {
                    public void run() {
                        for (int j = range[finalT].first; j<range[finalT].second; j++) {
                            try {
                                dbHelper.createOrUpdateCities(jsonData.getJSONObject(j));
                                if(j%500==0 || j==jsonData.length()) publishProgress(j);
                                //publishProgress(j);
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
            //Log.d("TIME TO PROCESS", String.valueOf(System.currentTimeMillis()-startTime));

            return success[0];
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(progress[0]==0){
                mProgressDialog.setMax(jsonData.length());
            }
            mProgressDialog.incrementProgressBy(500);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
            if(success){
                Toast.makeText(getApplicationContext(), "Успешно синхронизиране на градове!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Възникна грешка...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SynchronizeBreedsTask extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        private String data = null;
        private JSONArray jsonData;

        public SynchronizeBreedsTask(Context ctx, String result) {
            mContext = ctx;
            data = result;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Синхронизиране");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;
            try {
                jsonData = new JSONArray(data);
                for (int i = 0; i < jsonData.length(); i++) {
                    dbHelper.createOrUpdateBreeds(jsonData.getJSONObject(i));
                    publishProgress(i);
                }
                success = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(progress[0]==0){
                mProgressDialog.setMax(jsonData.length());
            }
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
            if(success){
                Toast.makeText(getApplicationContext(), "Успешно синхронизиране на породи!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Възникна грешка...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SynchronizeGoatStatusesTask extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        private String data = null;
        private JSONArray jsonData;

        public SynchronizeGoatStatusesTask(Context ctx, String result) {
            mContext = ctx;
            data = result;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Синхронизиране");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;
            try {
                jsonData = new JSONArray(data);
                for (int i = 0; i < jsonData.length(); i++) {
                    dbHelper.createOrUpdateGoatStatuses(jsonData.getJSONObject(i));
                    publishProgress(i);
                }
                success = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(progress[0]==0){
                mProgressDialog.setMax(jsonData.length());
            }
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
            if(success){
                Toast.makeText(getApplicationContext(), "Успешно синхронизиране на статуси на кози!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Възникна грешка...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SynchronizeVisitActivitiesTask extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        private String data = null;
        private JSONArray jsonData;

        public SynchronizeVisitActivitiesTask(Context ctx, String result) {
            mContext = ctx;
            data = result;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Синхронизиране");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;
            try {
                jsonData = new JSONArray(data);
                for (int i = 0; i < jsonData.length(); i++) {
                    dbHelper.createOrUpdateVisitActivities(jsonData.getJSONObject(i));
                    publishProgress(i);
                }
                success = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(progress[0]==0){
                mProgressDialog.setMax(jsonData.length());
            }
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
            if(success){
                Toast.makeText(getApplicationContext(), "Успешно синхронизиране на дейности за протокол!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Възникна грешка...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SynchronizeExteriorMarksTask extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        private String data = null;
        private JSONArray jsonData;

        public SynchronizeExteriorMarksTask(Context ctx, String result) {
            mContext = ctx;
            data = result;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Синхронизиране");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;
            try {
                jsonData = new JSONArray(data);
                for (int i = 0; i < jsonData.length(); i++) {
                    dbHelper.createOrUpdateExteriorMarks(jsonData.getJSONObject(i));
                    publishProgress(i);
                }
                success = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(progress[0]==0){
                mProgressDialog.setMax(jsonData.length());
            }
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
            if(success){
                Toast.makeText(getApplicationContext(), "Успешно синхронизиране на забележки и дефекти!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Възникна грешка...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
