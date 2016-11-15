package com.armpk.goatregistrator.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.FarmSearchResultAdapter;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.syncs.SynchronizeBreeds;
import com.armpk.goatregistrator.syncs.SynchronizeExteriorMarks;
import com.armpk.goatregistrator.syncs.SynchronizeFarms;
import com.armpk.goatregistrator.syncs.SynchronizeGoatStatuses;
import com.armpk.goatregistrator.syncs.SynchronizeGoatsByFarm;
import com.armpk.goatregistrator.syncs.SynchronizeGoatsFromVetisByFarm;
import com.armpk.goatregistrator.syncs.SynchronizeHerds;
import com.armpk.goatregistrator.syncs.SynchronizeHerdsByFarm;
import com.armpk.goatregistrator.syncs.SynchronizeHerdsByRange;
import com.armpk.goatregistrator.syncs.SynchronizeMeasurements;
import com.armpk.goatregistrator.syncs.SynchronizeVisitActivities;
import com.armpk.goatregistrator.utilities.Globals;
import com.armpk.goatregistrator.utilities.RestConnection;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FarmSearchActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        RestConnection.OnConnectionCompleted,
        SynchronizeFarms.OnFarmSynchronize,
        SynchronizeMeasurements.OnMeasurementsSynchronize,
        SynchronizeGoatStatuses.OnGoatStatusesSynchronize,
        SynchronizeGoatsByFarm.OnGoatsByFarmSynchronize,
        SynchronizeBreeds.OnBreedsSynchronize,
        SynchronizeGoatsFromVetisByFarm.OnGoatsFromVetisByFarmSynchronize, SynchronizeHerdsByFarm.OnHerdByFarmSynchronize, SynchronizeVisitActivities.OnVisitActivitySynchronize, SynchronizeExteriorMarks.OnExteriorMarkSynchronize {

    private ListView listViewFarmResults;
    private Button mButtonSyncAllFarms;
    private FarmSearchResultAdapter adapterFarmResults;
    private DatabaseHelper dbHelper;
    private List<Farm> listFarms;
    private TextView mTextLastSync;
    private SharedPreferences mSharedPreferences;

    private Farm mFarmToSync;

    public static final int HERD_PER_PAGE = 20;
    public static int pageCounter = 0;

    private int farmCounter = 0;
    private boolean fullSync = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_search);

        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mTextLastSync = (TextView)findViewById(R.id.text_last_sync);
        listViewFarmResults = (ListView)findViewById(R.id.list_results);
        registerForContextMenu(listViewFarmResults);
        mButtonSyncAllFarms = (Button)findViewById(R.id.btn_sync_all_farms);
        mButtonSyncAllFarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullSync = true;
                RestConnection mRestFarms = new RestConnection(FarmSearchActivity.this, RestConnection.DataType.FARMS, FarmSearchActivity.this);
                mRestFarms.setAction(RestConnection.Action.GET);
                mRestFarms.execute((Void) null);
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(FarmSearchActivity.this);
        alert.setTitle("Внимание!!!");
        alert.setMessage("Желаете ли синхронизация на ФЕРМИ и техните стада?");
        alert.setPositiveButton("СИНХРОНИЗИРАЙ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RestConnection mRestFarms = new RestConnection(FarmSearchActivity.this, RestConnection.DataType.FARMS, FarmSearchActivity.this);
                mRestFarms.setAction(RestConnection.Action.GET);
                mRestFarms.execute((Void) null);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("НЕ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    listFarms = dbHelper.getDaoFarm().queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adapterFarmResults = new FarmSearchResultAdapter(FarmSearchActivity.this, listFarms);
                listViewFarmResults.setAdapter(adapterFarmResults);
                adapterFarmResults.notifyDataSetChanged();

                mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
                mTextLastSync.setBackgroundColor(Color.YELLOW);
                dialog.dismiss();
            }
        });
        alert.show();




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_farm, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, FarmSearchActivity.class)));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_farm_search_result, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.create_protocol:
                /*Intent visitProtocolAddActivity = new Intent(this, VisitProtocolsAddActivity.class);
                startActivity(visitProtocolAddActivity);
                finish();*/
                return true;
            case R.id.sync_goats:
                mFarmToSync = (Farm) listViewFarmResults.getAdapter().getItem(info.position);
                getHerdsByFarm(mFarmToSync);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        displayResults(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        /*if (!newText.isEmpty()){
            displayResults(newText);
        } else {
            listViewGoatResults.setAdapter(adapterGoatResults);
        }*/
        displayResults(newText);
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            /*String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();*/

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            /*String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();*/
        }
    }

    private void displayResults(String query) {
        adapterFarmResults.getFilter().filter(query);

        // Click listener for the searched item that was selected
        listViewFarmResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }

    private void deleteSelectedItems(){
        List<Farm> toDelete = adapterFarmResults.getSelected();
        for(int i = 0; i<toDelete.size(); i++){
            try {
                dbHelper.getDaoFarm().delete(toDelete.get(i));
                adapterFarmResults.remove(toDelete.get(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        adapterFarmResults.notifyDataSetChanged();
    }

    @Override
    public void onResultReceived(RestConnection.Action action, RestConnection.DataType dataType, String result) {
        if(result!=null) {
            if (action == RestConnection.Action.GET) {
                switch (dataType) {
                    case FARMS:
                        synchronizeFarms(result);
                        break;
                    case MEASUREMENTS:
                        synchronizeMeasurements(result);
                        break;
                    case GOAT_STATUS:
                        synchronizeGoatStatuses(result);
                        break;
                    case BREEDS:
                        synchronizeBreeds(result);
                        break;
                    case GOATVETISBYFARM:
                        synchronizeGoatsFromVetisByFarm(result);
                        break;
                    case EXTERIOR_MARK:
                        synchronizeExteriorMarks(result);
                        break;
                    case VISIT_ACTIVITY:
                        synchronizeVisitActivities(result);
                        break;
                }
            } else if(action == RestConnection.Action.POST) {
                switch (dataType){
                    case GOATSBYFARM:
                        synchronizeGoatsByFarm(result);
                        break;
                    case HERDSBYFARM:
                        synchronizeHerdsByFarm(result);
                        break;
                }
            }else{
                RestConnection.closeProgressDialog();
            }
        }else{
            if(fullSync){
                RestConnection.setStaticMessage("Няма намерени данни за ферма: \n"+mFarmToSync.getCompanyName()
                        +"\nМоля проверете системата и опитайте отново!");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RestConnection.closeProgressDialog();
                        farmCounter++;
                        if(farmCounter<listViewFarmResults.getAdapter().getCount()) {
                            mFarmToSync = (Farm) listViewFarmResults.getAdapter().getItem(farmCounter);
                            getHerdsByFarm(mFarmToSync);
                        }
                    }
                }, 2000);

            }else {
                RestConnection.closeProgressDialog();
                processFarmSyncResult(false);
            }
        }
    }

    private void synchronizeFarms(String result){
        RestConnection.closeProgressDialog();
        SynchronizeFarms sf = new SynchronizeFarms(this, result, dbHelper, this);
        sf.execute((Void) null);
    }

    @Override
    public void onFarmSynchronizeFinished(boolean success) {
        processFarmSyncResult(success);
    }

    private void processFarmSyncResult(boolean success){
        try {
            listFarms = dbHelper.getDaoFarm().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapterFarmResults = new FarmSearchResultAdapter(this, listFarms);
        listViewFarmResults.setAdapter(adapterFarmResults);
        adapterFarmResults.notifyDataSetChanged();

        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success) {
            getMeasurements();
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    private void getHerdsByFarm(Farm farm){
        RestConnection mRestHerds = new RestConnection(this, RestConnection.DataType.HERDSBYFARM, this);
        mRestHerds.setAction(RestConnection.Action.POST);
        JSONObject farmId = new JSONObject();
        try {
            farmId.put("farmid", farm.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRestHerds.setJSONData(farmId);
        mRestHerds.execute((Void) null);
    }

    private void synchronizeHerdsByFarm(String result){
        RestConnection.closeProgressDialog();
        SynchronizeHerdsByFarm sh= new SynchronizeHerdsByFarm(this, result, dbHelper, this);
        sh.execute((Void) null);
    }


    @Override
    public void onHerdByFarmSynchronizeFinished(boolean success) {
        processHerdByFarmSyncResult(success);
    }

    private void processHerdByFarmSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            getGoatsByFarm(mFarmToSync);
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    /*private void getHerds(){
        RestConnection mRestHerds = new RestConnection(this, RestConnection.DataType.HERDS, this);
        mRestHerds.setAction(RestConnection.Action.GET);
        mRestHerds.execute((Void) null);
    }

     private void synchronizeHerds(String result){
        RestConnection.closeProgressDialog();
        SynchronizeHerds sh= new SynchronizeHerds(this, result, dbHelper, this);
        sh.execute((Void) null);
    }

    @Override
    public void onHerdSynchronizeFinished(boolean success) {
        processHerdSyncResult(success);
    }
    */



    /*private void getHerdsByRange(){
        pageCounter = 0;
        RestConnection mRestHerds = new RestConnection(this, RestConnection.DataType.HERDS_RANGE, this);
        mRestHerds.setAction(RestConnection.Action.POST);
        mRestHerds.setJSONData(formRange(pageCounter));
        mRestHerds.execute((Void) null);
    }

    private JSONObject formRange(int currentPage){
        JSONObject result = new JSONObject();
        try {
            result.put("first", currentPage*HERD_PER_PAGE);
            result.put("pagesize", HERD_PER_PAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void synchronizeHerdsByRange(String result){
        RestConnection.closeProgressDialog();
        SynchronizeHerdsByRange sh= new SynchronizeHerdsByRange(this, result, dbHelper, this);
        sh.execute((Void) null);
    }


    @Override
    public void onHerdByRangeSynchronizeFinished(int success) {
        if(success==2) {
            RestConnection mRestHerds = new RestConnection(this, RestConnection.DataType.HERDS_RANGE, this);
            mRestHerds.setAction(RestConnection.Action.POST);
            pageCounter+=1;
            mRestHerds.setJSONData(formRange(pageCounter));
            mRestHerds.execute((Void) null);

        }else if(success==1){
            mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
            getMeasurements();
        }else if(success == 0){
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    private void processHerdSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            getMeasurements();
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }*/

    private void getMeasurements(){
        RestConnection mRestMeasurements = new RestConnection(this, RestConnection.DataType.MEASUREMENTS, this);
        mRestMeasurements.setAction(RestConnection.Action.GET);
        mRestMeasurements.execute((Void) null);
    }

    private void synchronizeMeasurements(String result){
        RestConnection.closeProgressDialog();
        SynchronizeMeasurements sm = new SynchronizeMeasurements(this, result, dbHelper, this);
        sm.execute((Void) null);
    }

    @Override
    public void onMeasurementsSynchronizeFinished(boolean success) {
        processMeasurementsSyncResult(success);
    }

    private void processMeasurementsSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            getGoatStatuses();
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    private void getGoatStatuses(){
        RestConnection mRestGoatStatuses = new RestConnection(this, RestConnection.DataType.GOAT_STATUS, this);
        mRestGoatStatuses.setAction(RestConnection.Action.GET);
        mRestGoatStatuses.execute((Void) null);
    }

    private void synchronizeGoatStatuses(String result){
        RestConnection.closeProgressDialog();
        SynchronizeGoatStatuses sgs = new SynchronizeGoatStatuses(this, result, dbHelper, this);
        sgs.execute((Void) null);
    }

    @Override
    public void onGoatStatusesSynchronizeFinished(boolean success) {
        processGoatStatusesSyncResult(success);
    }

    private void processGoatStatusesSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            getBreeds();
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    private void getBreeds(){
        RestConnection mRestBreeds = new RestConnection(this, RestConnection.DataType.BREEDS, this);
        mRestBreeds.setAction(RestConnection.Action.GET);
        mRestBreeds.execute((Void) null);
    }

    private void synchronizeBreeds(String result){
        RestConnection.closeProgressDialog();
        SynchronizeBreeds sgs = new SynchronizeBreeds(this, result, dbHelper, this);
        sgs.execute((Void) null);
    }

    @Override
    public void onBreedsSynchronizeFinished(boolean success) {
        processBreedsSyncResult(success);
    }

    private void processBreedsSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            //mTextLastSync.setBackgroundColor(Color.GREEN);
            getExteriorMarks();
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    private void getExteriorMarks(){
        RestConnection mRestExteriorMarks = new RestConnection(this, RestConnection.DataType.EXTERIOR_MARK, this);
        mRestExteriorMarks.setAction(RestConnection.Action.GET);
        mRestExteriorMarks.execute((Void) null);
    }

    private void synchronizeExteriorMarks(String result){
        RestConnection.closeProgressDialog();
        SynchronizeExteriorMarks sem = new SynchronizeExteriorMarks(this, result, dbHelper, this);
        sem.execute((Void) null);
    }

    @Override
    public void onExteriorMarkSynchronizeFinished(boolean success) {
        processExteriorMarkSyncResult(success);
    }

    private void processExteriorMarkSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            //mTextLastSync.setBackgroundColor(Color.GREEN);
            getVisitActivities();
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    private void getVisitActivities(){
        RestConnection mRestVisitActivities = new RestConnection(this, RestConnection.DataType.VISIT_ACTIVITY, this);
        mRestVisitActivities.setAction(RestConnection.Action.GET);
        mRestVisitActivities.execute((Void) null);
    }

    private void synchronizeVisitActivities(String result){
        RestConnection.closeProgressDialog();
        SynchronizeVisitActivities sva = new SynchronizeVisitActivities(this, result, dbHelper, this);
        sva.execute((Void) null);
    }

    @Override
    public void onVisitActivitiesSynchronizeFinished(boolean success) {
        processVisitActivitiesSyncResult(success);
    }

    private void processVisitActivitiesSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));

        if(!fullSync) {
            if (success) {
                mTextLastSync.setBackgroundColor(Color.GREEN);
            } else {
                mTextLastSync.setBackgroundColor(Color.YELLOW);
            }
        }else{
            if (success) {
                mFarmToSync = (Farm) listViewFarmResults.getAdapter().getItem(farmCounter);
                getHerdsByFarm(mFarmToSync);
            } else {
                mTextLastSync.setBackgroundColor(Color.YELLOW);
            }
        }
    }

    private void getGoatsByFarm(Farm farm){
        RestConnection mRestGoatsByFarm = new RestConnection(this, RestConnection.DataType.GOATSBYFARM, this);
        mRestGoatsByFarm.setAction(RestConnection.Action.POST);
        if(fullSync) {
            mRestGoatsByFarm.setMessage("Сваляне на кози във ферма "+(farmCounter+1)+" от "+listViewFarmResults.getAdapter().getCount()
                    +"\n"+farm.getCompanyName());
        }else{
            mRestGoatsByFarm.setMessage("Сваляне на кози във ферма");
        }
        JSONObject farmId = new JSONObject();
        try {
            farmId.put("farmid", farm.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRestGoatsByFarm.setJSONData(farmId);
        mRestGoatsByFarm.execute((Void) null);
    }

    private void synchronizeGoatsByFarm(String result){
        RestConnection.closeProgressDialog();
        SynchronizeGoatsByFarm sg = new SynchronizeGoatsByFarm(FarmSearchActivity.this, result, mFarmToSync, dbHelper, this);
        if(fullSync) {
            sg.setMessage("Синхронизиране на кози във ферма "+(farmCounter+1)+" от "+listViewFarmResults.getAdapter().getCount()
                    +"\n"+mFarmToSync.getCompanyName());
        }else{
            sg.setMessage("Синхронизиране на кози във ферма");
        }
        sg.execute((Void) null);
    }

    @Override
    public void onGoatsByFarmSynchronizeFinished(boolean success) {
        processGoatsByFarmSyncResult(success);
    }

    private void processGoatsByFarmSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            getGoatsFromVetisByFarm(String.valueOf(mFarmToSync.getId()));
            //mTextLastSync.setBackgroundColor(Color.GREEN);
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
    }

    private void getGoatsFromVetisByFarm(String farmId){
        RestConnection mRestGoatsFromVetisByFarm = new RestConnection(this, RestConnection.DataType.GOATVETISBYFARM, farmId, this);
        if(fullSync){
            mRestGoatsFromVetisByFarm.setMessage("Сваляне на кози от ВетИС във ферма "+(farmCounter+1)+" от "+listViewFarmResults.getAdapter().getCount());
        }else{
            mRestGoatsFromVetisByFarm.setMessage("Сваляне на кози от ВетИС");
        }
        mRestGoatsFromVetisByFarm.setAction(RestConnection.Action.GET);
        mRestGoatsFromVetisByFarm.execute((Void) null);
    }

    private void synchronizeGoatsFromVetisByFarm(String result){
        RestConnection.closeProgressDialog();
        SynchronizeGoatsFromVetisByFarm sgfvs = new SynchronizeGoatsFromVetisByFarm(this, result, dbHelper, this);
        if(fullSync) {
            sgfvs.setMessage("Синхронизиране на кози от ВетИС във ферма "+(farmCounter+1)+" от "+listViewFarmResults.getAdapter().getCount());
        }else{
            sgfvs.setMessage("Синхронизиране на кози от ВетИС");
        }
        sgfvs.execute((Void) null);
    }

    @Override
    public void onGoatsFromVetisByFarmSynchronizeFinished(boolean success) {
        processGoatsFromVetisByFarmSyncResult(success);
    }

    private void processGoatsFromVetisByFarmSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));

        if(!fullSync) {
            if (success) {
                mTextLastSync.setBackgroundColor(Color.GREEN);
            } else {
                mTextLastSync.setBackgroundColor(Color.YELLOW);
            }
        }else{
            if (success) {
                farmCounter++;
                if(farmCounter<listViewFarmResults.getAdapter().getCount()) {
                    mFarmToSync = (Farm) listViewFarmResults.getAdapter().getItem(farmCounter);
                    getHerdsByFarm(mFarmToSync);
                }else{
                    farmCounter = 0;
                    fullSync = false;
                    mTextLastSync.setBackgroundColor(Color.GREEN);
                }
            } else {
                fullSync = false;
                mTextLastSync.setBackgroundColor(Color.YELLOW);
            }
        }
    }



}
