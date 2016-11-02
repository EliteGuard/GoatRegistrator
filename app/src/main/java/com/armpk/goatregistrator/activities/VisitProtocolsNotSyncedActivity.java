package com.armpk.goatregistrator.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.LocalVisitProtocolSearchResultAdapter;
import com.armpk.goatregistrator.adapters.VisitProtocolSearchResultAdapter;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.Measurement;
import com.armpk.goatregistrator.database.VisitActivity;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.database.enums.MeasurementType;
import com.armpk.goatregistrator.database.mobile.LocalGoat;
import com.armpk.goatregistrator.database.mobile.LocalGoatMeasurement;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocol;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocolVisitActivity;
import com.armpk.goatregistrator.utilities.Globals;
import com.armpk.goatregistrator.utilities.RestConnection;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VisitProtocolsNotSyncedActivity extends AppCompatActivity implements  RestConnection.OnConnectionCompleted{

    private static final String ARG_FARM = "farm";
    private static final String ARG_VISIT_PROTOCOL = "visit_protocol";
    private static final String ARG_SYNCED = "visit_protocol_synced";
    private static final String ARG_LOCAL_VP_ID = "local_visit_protocol_id";

    private DatabaseHelper dbHelper;
    private SharedPreferences mSharedPreferences;

    private ListView listViewResults;
    private LocalVisitProtocolSearchResultAdapter adapterResults;
    private List<LocalVisitProtocol> listVisitProtocol;
    //Set<String> tempProtocols;

    ArrayList<LocalGoat> orderedGoatsForUpload = new ArrayList<LocalGoat>();
    int goatCounter = 1;

    LocalVisitProtocol mVPtoDELETE;

    HashMap<String, VisitActivity> mapVisitActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_protocols_not_synced);
        setTitle(R.string.text_not_synced_protocols);

        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        listViewResults = (ListView)findViewById(R.id.list_results);
        registerForContextMenu(listViewResults);
        listVisitProtocol = new ArrayList<>(0);

        InitActivity ia = new InitActivity(this);
        ia.execute((Void) null);

        /*try {
            Set<String> tempProtocols = mSharedPreferences.getStringSet(Globals.TEMPORARY_VISIT_PROTOCOLS, new HashSet<String>());
            for(String visitProtocol : tempProtocols){
                listVisitProtocol.add(loadProtocol(visitProtocol));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(listVisitProtocol.size()>0){
            adapterResults = new VisitProtocolSearchResultAdapter(this, listVisitProtocol);
            listViewResults.setAdapter(adapterResults);
            adapterResults.notifyDataSetChanged();
        }*/

        try {
            mapVisitActivities = new HashMap<String, VisitActivity>();
            for(VisitActivity va : dbHelper.getDaoVisitActivity().queryForAll()){
                mapVisitActivities.put(String.valueOf(va.getId()), va);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapterResults != null) adapterResults.notifyDataSetChanged();;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_visit_protocols_not_synced, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final LocalVisitProtocol lvp = mVPtoDELETE = (LocalVisitProtocol) listViewResults.getAdapter().getItem(info.position);
        switch (item.getItemId()) {
            case R.id.upload_protocol:
                uploadProtocol(lvp);
                return true;
            case R.id.continue_protocol:
                Intent intent = new Intent(this, GoatAddReaderActivity.class);
                Bundle args = new Bundle();
                args.putSerializable(ARG_VISIT_PROTOCOL, lvp);
                args.putSerializable(ARG_FARM, lvp.getFarm());
                args.putLong(ARG_LOCAL_VP_ID, lvp.getId());
                intent.putExtras(args);
                startActivity(intent);
                return true;
            case R.id.show_goats_for_farm:
                Intent listsFromBook = new Intent(this, GoatsListsFromBookActivity.class);
                Bundle argsLists = new Bundle();
                argsLists.putSerializable(ARG_VISIT_PROTOCOL, lvp);
                listsFromBook.putExtras(argsLists);
                startActivity(listsFromBook);
                return true;
            case R.id.show_goats_lists:
                Intent lists = new Intent(this, VisitProtocolGoatsListsActivity.class);
                Bundle args_lists = new Bundle();
                args_lists.putSerializable(ARG_VISIT_PROTOCOL, lvp);
                args_lists.putBoolean(ARG_SYNCED, false);
                args_lists.putLong(ARG_LOCAL_VP_ID, lvp.getId());
                lists.putExtras(args_lists);
                startActivity(lists);
                return true;
            case R.id.delete_protocol:

                AlertDialog.Builder alert = new AlertDialog.Builder(VisitProtocolsNotSyncedActivity.this);
                alert.setTitle("Внимание!!!");
                alert.setMessage("Сигурни ли сте, че искате да изтриете този протокол?");
                alert.setPositiveButton("ИЗТРИЙ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*try {
                            Set<String> tempProtocols = mSharedPreferences.getStringSet(Globals.TEMPORARY_VISIT_PROTOCOLS, new HashSet<String>());
                            if(tempProtocols.contains(Globals.objectToJson(vp).toString())) {
                                if(tempProtocols.remove(Globals.objectToJson(vp).toString())){
                                    Globals.savePreferences(Globals.TEMPORARY_VISIT_PROTOCOLS, tempProtocols, VisitProtocolsNotSyncedActivity.this);
                                    String keyActivities = Globals.TEMPORARY_ACTIVITIES_FOR_PROTOCOL+String.valueOf(mVPtoDELETE.getFarm().getId())+"_"+String.valueOf(mVPtoDELETE.getDateAddedToSystem().getTime());
                                    String keyGoats = Globals.TEMPORARY_GOATS_FOR_PROTOCOL+String.valueOf(mVPtoDELETE.getFarm().getId())+"_"+String.valueOf(mVPtoDELETE.getDateAddedToSystem().getTime());
                                    mSharedPreferences.edit().remove(keyActivities).apply();
                                    mSharedPreferences.edit().remove(keyGoats).apply();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listVisitProtocol.remove(vp);
                        adapterResults.notifyDataSetChanged();*/

                        try {

                            DeleteBuilder<LocalVisitProtocolVisitActivity, Long> dbLvpLa = dbHelper.getDaoLocalVisitProtocolVisitActivity().deleteBuilder();
                            dbLvpLa.where().eq("localVisitProtocol_id", lvp.getId());
                            dbLvpLa.delete();

                            for(LocalGoat lg : dbHelper.getDaoLocalGoat().queryBuilder().where().eq("localVisitProtocol_id", lvp.getId()).query()){
                                for(LocalGoatMeasurement lgm : lg.getLst_goatMeasurements()) {
                                    dbHelper.getDaoLocalGoatMeasurements().delete(lgm);
                                }
                                dbHelper.getDaoLocalGoat().delete(lg);
                            }

                            DeleteBuilder<LocalGoat, Long> dbLg = dbHelper.getDaoLocalGoat().deleteBuilder();
                            dbLg.where().eq("localVisitProtocol_id", lvp.getId());
                            dbLg.delete();

                            if(dbHelper.getDaoLocalVisitProtocol().deleteById(lvp.getId())==1){
                                listVisitProtocol.remove(lvp);
                                adapterResults.notifyDataSetChanged();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("НЕ", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void uploadProtocol(LocalVisitProtocol lvp){
        RestConnection mRestPutVisitProtocol = new RestConnection(this, RestConnection.DataType.VISIT_PROTOCOL_PARTED, VisitProtocolsNotSyncedActivity.this);
        mRestPutVisitProtocol.setAction(RestConnection.Action.POST);
        try {
            JSONObject data = new JSONObject();

            VisitProtocol vp = new VisitProtocol();
            lvp.getFarm().setLst_visitProtocol(null);
            if(lvp.getFarm()!=null) vp.setFarm(lvp.getFarm());
            if(lvp.getVisitDate()!=null) vp.setVisitDate(lvp.getVisitDate());
            if(lvp.getNotes()!=null) vp.setNotes(lvp.getNotes());
            else vp.setNotes("");
            if(lvp.getEmployFirst()!=null) vp.setEmployFirst(lvp.getEmployFirst());
            if(lvp.getEmploySecond()!=null) vp.setEmploySecond(lvp.getEmploySecond());
            if(lvp.getDateAddedToSystem()!=null) vp.setDateAddedToSystem(lvp.getDateAddedToSystem());
            if(lvp.getDateLastUpdated()!=null) vp.setDateLastUpdated(lvp.getDateLastUpdated());
            if(lvp.getLastUpdatedByUser()!=null) vp.setLastUpdatedByUser(lvp.getLastUpdatedByUser());
            data = Globals.objectToJson(vp);
            /*String keyActivities = Globals.TEMPORARY_ACTIVITIES_FOR_PROTOCOL+String.valueOf(vp.getFarm().getId())+"_"+String.valueOf(vp.getDateAddedToSystem().getTime());
            Set<String> pva = mSharedPreferences.getStringSet(keyActivities, new HashSet<String>());
            JSONArray localActivities = new JSONArray();
            for(String s : pva)localActivities.put(Globals.objectToJson(mapVisitActivities.get(s)));
            data.put("lst_visitActivities", localActivities);*/
            JSONArray localActivities = new JSONArray();
            for(LocalVisitProtocolVisitActivity lvpva :
                    dbHelper.getDaoLocalVisitProtocolVisitActivity().queryBuilder().where().eq("localVisitProtocol_id", lvp.getId()).query()){
                localActivities.put(Globals.objectToJson(lvpva.getVisitActivity()));
            }
            data.put("lst_visitActivities", localActivities);
            mRestPutVisitProtocol.setJSONData(data);
        } catch (JSONException | SQLException e) {
            e.printStackTrace();
        }
        mRestPutVisitProtocol.execute((Void) null);
    }

    private JSONArray processGoats(VisitProtocol vp) throws JSONException {
        String keyGoats = Globals.TEMPORARY_GOATS_FOR_PROTOCOL+String.valueOf(vp.getFarm().getId())+"_"+String.valueOf(vp.getDateAddedToSystem().getTime());
        Set<String> pg = mSharedPreferences.getStringSet(keyGoats, new HashSet<String>());
        JSONArray localGoats = new JSONArray();

        String pid = String.valueOf(vp.getFarm().getId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vp.getVisitDate());
        if(cal.get(Calendar.DAY_OF_MONTH) < 10) pid += "0";
        pid += cal.get(Calendar.DAY_OF_MONTH);
        if(cal.get(Calendar.MONTH)+1 < 10) pid += "0";
        pid += (cal.get(Calendar.MONTH)+1);
        pid += (cal.get(Calendar.YEAR)-2000);
        int goatCounter = 1;

        for(String sg : pg) {
            JSONObject goatO = new JSONObject(sg);

            Goat keygoat = Globals.jsonToObject(new JSONObject(sg), Goat.class);
            StringBuffer key = new StringBuffer();
            if(keygoat.getFirstVeterinaryNumber()!=null) key.append(keygoat.getFirstVeterinaryNumber());
            if(keygoat.getSecondVeterinaryNumber()!=null) key.append(keygoat.getSecondVeterinaryNumber());
            if(keygoat.getFirstBreedingNumber()!=null) key.append(keygoat.getFirstBreedingNumber());
            if(keygoat.getSecondBreedingNumber()!=null) key.append(keygoat.getSecondBreedingNumber());
            if(mSharedPreferences.getString(key.toString(), "").length()>0){
                JSONArray measurements = new JSONArray(mSharedPreferences.getString(key.toString(), ""));
                goatO.put("lst_goatMeasurements", measurements);
            }

            JSONArray farmsA = new JSONArray();
            key.append("_farm");
            farmsA.put(
                    mSharedPreferences.getString(
                            key.toString(),
                            Globals.objectToJson(vp.getFarm()).toString()));
            goatO.put("lst_farms", farmsA);
            if(keygoat.getId()!=null) goatO.put("condition", "old");
            else goatO.put("condition", "new");

            String asd = pid + goatCounter;
            goatO.put("processed_id", pid+goatCounter);
            goatCounter++;

            localGoats.put(goatO);
        }

        return localGoats;
    }

    private VisitProtocol loadProtocol(String prot) throws JSONException {
        JSONObject jsonVP = null;
        if(prot.length()>0){
            jsonVP = new JSONObject(prot);
        }
        return Globals.jsonToObject(jsonVP, VisitProtocol.class);
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
    public void onResultReceived(RestConnection.Action action, RestConnection.DataType dataType, String result) {
        if(result!=null){
            if (action == RestConnection.Action.GET) {
                switch (dataType) {
                    default:
                        break;
                }
            } else if(action == RestConnection.Action.POST) {
                switch (dataType){
                    case VISIT_PROTOCOL_PARTED:
                        synchronizeVisitProtocol(result);
                        break;
                    case VISIT_PROTOCOL_GOATS:
                        if(goatCounter>=orderedGoatsForUpload.size()){
                            listVisitProtocol.remove(mVPtoDELETE);
                            adapterResults.notifyDataSetChanged();
                            RestConnection.closeProgressDialog();
                            Toast.makeText(VisitProtocolsNotSyncedActivity.this, "УСПЕШНО импортиране на КОЗИ от протокол!", Toast.LENGTH_LONG).show();
                        }else if(goatCounter<orderedGoatsForUpload.size()){
                            continueUploadingGoats(result, goatCounter);
                        }else{
                            RestConnection.closeProgressDialog();
                            Toast.makeText(VisitProtocolsNotSyncedActivity.this, "Грешка при импортиране на коза в системата!", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }else{
                RestConnection.closeProgressDialog();
            }
        }else {
            RestConnection.closeProgressDialog();
        }
    }

    private void synchronizeVisitProtocol(String result){
        try {
            if(dbHelper.updateVisitProtocolByDate(new JSONObject(result))) {
                //delete after successful upload
                    /*try {
                        Set<String> tempProtocols = mSharedPreferences.getStringSet(Globals.TEMPORARY_VISIT_PROTOCOLS, new HashSet<String>());
                        if(tempProtocols.contains(Globals.objectToJson(mVPtoDELETE).toString())) {
                            if(tempProtocols.remove(Globals.objectToJson(mVPtoDELETE).toString())) {
                                Globals.savePreferences(Globals.TEMPORARY_VISIT_PROTOCOLS, tempProtocols, this);
                                String keyActivities = Globals.TEMPORARY_ACTIVITIES_FOR_PROTOCOL+String.valueOf(mVPtoDELETE.getFarm().getId())+"_"+String.valueOf(mVPtoDELETE.getDateAddedToSystem().getTime());
                                String keyGoats = Globals.TEMPORARY_GOATS_FOR_PROTOCOL+String.valueOf(mVPtoDELETE.getFarm().getId())+"_"+String.valueOf(mVPtoDELETE.getDateAddedToSystem().getTime());
                                mSharedPreferences.edit().remove(keyActivities).apply();
                                mSharedPreferences.edit().remove(keyGoats).apply();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                /*listVisitProtocol.remove(mVPtoDELETE);
                adapterResults.notifyDataSetChanged();*/
                goatCounter = 0;
                orderedGoatsForUpload.clear();
                startUploadingGoats(result);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startUploadingGoats(String result) throws JSONException {
        VisitProtocol vp = Globals.jsonToObject(new JSONObject(result), VisitProtocol.class);

        /*String keyGoats = Globals.TEMPORARY_GOATS_FOR_PROTOCOL+String.valueOf(vp.getFarm().getId())+"_"+String.valueOf(vp.getDateAddedToSystem().getTime());
        Set<String> spg = mSharedPreferences.getStringSet(keyGoats, new HashSet<String>());
        for (String str : spg) orderedGoatsForUpload.add(str);*/
        try {
            boolean foundForProtocol = false;
            for(LocalGoat lg : dbHelper.getDaoLocalVisitProtocol().queryBuilder()
                    .where().eq("real_id", vp.getId())
                    .queryForFirst().getLst_localGoat() ){
                orderedGoatsForUpload.add(lg);
                foundForProtocol = true;
            }

            if(!foundForProtocol){
                for(LocalGoat lg : dbHelper.getDaoLocalGoat().queryBuilder()
                        .where().eq("farm_id", vp.getFarm().getId()).query()){
                    orderedGoatsForUpload.add(lg);
                }
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONArray localGoats = new JSONArray();
        String pid = String.valueOf(vp.getFarm().getId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vp.getVisitDate());
        if(cal.get(Calendar.DAY_OF_MONTH) < 10) pid += "0";
        pid += cal.get(Calendar.DAY_OF_MONTH);
        if(cal.get(Calendar.MONTH)+1 < 10) pid += "0";
        pid += (cal.get(Calendar.MONTH)+1);
        pid += (cal.get(Calendar.YEAR)-2000);


        for(int i=0; i<orderedGoatsForUpload.size(); i++){
            JSONArray farmsA = new JSONArray();
            Farm tf = new Farm();
            if(orderedGoatsForUpload.get(i).getFarm()!=null) {
                tf.setId(orderedGoatsForUpload.get(i).getFarm().getId());
                farmsA.put(Globals.objectToJson(tf));
            }else{
                tf.setId(vp.getFarm().getId());
                farmsA.put(Globals.objectToJson(tf));
            }

            JSONArray measurements = new JSONArray();
            try {
                QueryBuilder<Measurement, Integer> mqb = dbHelper.getDaoMeasurement().queryBuilder();
                mqb.selectColumns("_id", "name", "codeName", "minValue", "maxValue", "allowedValues", "description")
                        .orderBy("name", true)
                        .where()
                        .eq("type", MeasurementType.BONITIROVKA);

                QueryBuilder<LocalGoatMeasurement, Long> lgmQb = dbHelper.getDaoLocalGoatMeasurements().queryBuilder();
                lgmQb.where()
                        .eq("goat_id", orderedGoatsForUpload.get(i));

                lgmQb.join(mqb);

                for(LocalGoatMeasurement lgm : lgmQb.query()){
                    lgm.setId(null);
                    measurements.put(Globals.objectToJson(lgm));
                }
                    /*measurements = new JSONArray(
                            dbHelper.getDaoLocalGoatMeasurements().queryBuilder()
                                    .where().eq("goat_id", orderedGoatsForUpload.get(i).getRealId()).query()
                    );*/
            } catch (SQLException e) {
                e.printStackTrace();
            }

            orderedGoatsForUpload.get(i).setLst_localGoatMeasurements(null);
            orderedGoatsForUpload.get(i).setLocalVisitProtocol(null);
            JSONObject goatO = new JSONObject(Globals.objectToJson(orderedGoatsForUpload.get(i)).toString());

            if(goatO.opt("id")==null) {
                goatO.put("id", JSONObject.NULL);
            }

            goatO.put("processed_id", pid+goatCounter);


            if(orderedGoatsForUpload.get(i).getRealId()!=null) goatO.put("condition", "old");
            else goatO.put("condition", "new");

            goatO.put("lst_farms", farmsA);

            goatO.put("lst_goatMeasurements", measurements);

            localGoats.put(goatO);
            goatCounter++;

            //if(goatCounter==10 || i==orderedGoatsForUpload.size()-1){
                RestConnection mRestPutVisitProtocol = new RestConnection(this, RestConnection.DataType.VISIT_PROTOCOL_GOATS,
                        String.valueOf(vp.getId()), VisitProtocolsNotSyncedActivity.this);
                mRestPutVisitProtocol.setAction(RestConnection.Action.POST);
                /*try {
                    JSONObject data = new JSONObject();
                    data.put("lst_vpGoats", localGoats);

                    mRestPutVisitProtocol.setJSONData(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                mRestPutVisitProtocol.setJSONArray(localGoats);
                mRestPutVisitProtocol.setMessage("Импортиране на "+goatCounter+" от "+orderedGoatsForUpload.size()+" кози");
                mRestPutVisitProtocol.execute((Void) null);
                break;
            //}

        }

    }

    private void continueUploadingGoats(String result, int counter){
        try {
            VisitProtocol vp = Globals.jsonToObject(new JSONObject(result), VisitProtocol.class);

            JSONArray localGoats = new JSONArray();
            String pid = String.valueOf(vp.getFarm().getId());
            Calendar cal = Calendar.getInstance();
            cal.setTime(vp.getVisitDate());
            if(cal.get(Calendar.DAY_OF_MONTH) < 10) pid += "0";
            pid += cal.get(Calendar.DAY_OF_MONTH);
            if(cal.get(Calendar.MONTH)+1 < 10) pid += "0";
            pid += (cal.get(Calendar.MONTH)+1);
            pid += (cal.get(Calendar.YEAR)-2000);


            //for(int i=counter+1; i<orderedGoatsForUpload.size(); i++){

                JSONArray farmsA = new JSONArray();
                Farm tf = new Farm();
                if(orderedGoatsForUpload.get(counter).getFarm()!=null) {
                    tf.setId(orderedGoatsForUpload.get(counter).getFarm().getId());
                    farmsA.put(Globals.objectToJson(tf));
                }else{
                    tf.setId(vp.getFarm().getId());
                    farmsA.put(Globals.objectToJson(tf));
                }

                JSONArray measurements = new JSONArray();
                try {
                    QueryBuilder<Measurement, Integer> mqb = dbHelper.getDaoMeasurement().queryBuilder();
                    mqb.selectColumns("_id", "name", "codeName", "minValue", "maxValue", "allowedValues", "description")
                            .orderBy("name", true)
                            .where()
                            .eq("type", MeasurementType.BONITIROVKA);

                    QueryBuilder<LocalGoatMeasurement, Long> lgmQb = dbHelper.getDaoLocalGoatMeasurements().queryBuilder();
                    lgmQb.where()
                            .eq("goat_id", orderedGoatsForUpload.get(counter));

                    lgmQb.join(mqb);

                    for(LocalGoatMeasurement lgm : lgmQb.query()){
                        lgm.setId(null);
                        lgm.setLocalGoat(null);
                        measurements.put(Globals.objectToJson(lgm));
                    }
                    /*measurements = new JSONArray(
                            dbHelper.getDaoLocalGoatMeasurements().queryBuilder()
                                    .where().eq("goat_id", orderedGoatsForUpload.get(i).getRealId()).query()
                    );*/
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                orderedGoatsForUpload.get(counter).setLst_localGoatMeasurements(null);
                orderedGoatsForUpload.get(counter).setLocalVisitProtocol(null);
                orderedGoatsForUpload.get(counter).setFarm(null);

                JSONObject goatO = new JSONObject(Globals.objectToJson(orderedGoatsForUpload.get(counter)).toString());

                if(goatO.opt("id")==null) {
                    goatO.put("id", JSONObject.NULL);
                }

                goatO.put("processed_id", pid+goatCounter);


                if(orderedGoatsForUpload.get(counter).getRealId()!=null) goatO.put("condition", "old");
                else goatO.put("condition", "new");

                goatO.put("lst_farms", farmsA);

                goatO.put("lst_goatMeasurements", measurements);

                localGoats.put(goatO);

                goatCounter++;

                /*JSONObject goatO = new JSONObject(orderedGoatsForUpload.get(i));
                if(goatO.opt("id")==null) {
                    goatO.put("id", JSONObject.NULL);
                }

                Goat keygoat = Globals.jsonToObject(new JSONObject(orderedGoatsForUpload.get(i)), Goat.class);
                StringBuffer key = new StringBuffer();
                if(keygoat.getFirstVeterinaryNumber()!=null) key.append(keygoat.getFirstVeterinaryNumber());
                if(keygoat.getSecondVeterinaryNumber()!=null) key.append(keygoat.getSecondVeterinaryNumber());
                if(keygoat.getFirstBreedingNumber()!=null) key.append(keygoat.getFirstBreedingNumber());
                if(keygoat.getSecondBreedingNumber()!=null) key.append(keygoat.getSecondBreedingNumber());
                if(mSharedPreferences.getString(key.toString(), "").length()>0){
                    JSONArray measurements = new JSONArray(mSharedPreferences.getString(key.toString(), ""));
                    goatO.put("lst_goatMeasurements", measurements);
                }

                JSONArray farmsA = new JSONArray();
                key.append("_farm");
                JSONObject fjo = new JSONObject(mSharedPreferences.getString(
                        key.toString(),
                        Globals.objectToJson(vp.getFarm()).toString()));
                Farm tf = new Farm();
                tf.setId(fjo.optLong("id"));
                farmsA.put(Globals.objectToJson(tf));
                goatO.put("lst_farms", farmsA);

                if(keygoat.getId()!=null) goatO.put("condition", "old");
                else goatO.put("condition", "new");

                goatO.put("processed_id", pid+goatCounter);
                goatCounter++;

                localGoats.put(goatO);*/

                //if(goatCounter%10==0 || i>=orderedGoatsForUpload.size()-1){
                    RestConnection mRestPutVisitProtocol = new RestConnection(this, RestConnection.DataType.VISIT_PROTOCOL_GOATS,
                            String.valueOf(vp.getId()), VisitProtocolsNotSyncedActivity.this);
                    mRestPutVisitProtocol.setAction(RestConnection.Action.POST);
                    /*try {
                        JSONObject data = new JSONObject();
                        //data.put("protocol_id", vp.getId());
                        data.put("lst_vpGoats", localGoats);

                        mRestPutVisitProtocol.setJSONData(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    mRestPutVisitProtocol.setJSONArray(localGoats);
                    mRestPutVisitProtocol.setMessage("Импортиране на "+counter+" от "+orderedGoatsForUpload.size()+" кози");
                    mRestPutVisitProtocol.execute((Void) null);
                    //break;
                //}
            //}


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class InitActivity extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;

        public InitActivity(Context ctx) {
            mContext = ctx;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Зареждане на данни");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            //mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final boolean[] success = {false};

            /*try {
                Set<String> tempProtocols = mSharedPreferences.getStringSet(Globals.TEMPORARY_VISIT_PROTOCOLS, new HashSet<String>());
                for (String visitProtocol : tempProtocols) {
                    listVisitProtocol.add(loadProtocol(visitProtocol));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sortSingleListByLastUpdated(listVisitProtocol);*/
            try {
                QueryBuilder<LocalVisitProtocol, Long> qbLvp = dbHelper.getDaoLocalVisitProtocol().queryBuilder();
                qbLvp.selectColumns("_id", "real_id", "farm_id",  "farm2_id",
                        "visitDate",  "notes",
                        "employFirst_id",  "employSecond_id",
                        "dateAddedToSystem",  "dateLastUpdated", "lastUpdatedByUser_id")
                        .orderBy("dateLastUpdated", false);
                listVisitProtocol = qbLvp.query();
                success[0] = true;
                //listVisitProtocol = dbHelper.getDaoLocalVisitProtocol().queryBuilder().orderBy("dateLastUpdated", false).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            return success[0];
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(Boolean success) {

            if(listVisitProtocol.size()>0){
                adapterResults = new LocalVisitProtocolSearchResultAdapter(VisitProtocolsNotSyncedActivity.this, listVisitProtocol);
                listViewResults.setAdapter(adapterResults);
                adapterResults.notifyDataSetChanged();
            }

            if (mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
        }

        private void sortSingleListByLastUpdated(List<VisitProtocol> list){
            Collections.sort(list, new Comparator<VisitProtocol>() {
                @Override
                public int compare(VisitProtocol vp, VisitProtocol vp2) {

                    long lut1 = 0;
                    long lut2 = 0;
                    if(vp.getDateLastUpdated()!=null) lut1 = vp.getDateLastUpdated().getTime();
                    if(vp2.getDateLastUpdated()!=null) lut2 = vp2.getDateLastUpdated().getTime();

                    int result = 0;
                    result = (int) (lut2 - lut1);

                    return result;
                }
            });
        }
    }
}
