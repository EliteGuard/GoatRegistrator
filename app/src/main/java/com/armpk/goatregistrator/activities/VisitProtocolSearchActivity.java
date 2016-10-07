package com.armpk.goatregistrator.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.FarmSearchResultAdapter;
import com.armpk.goatregistrator.adapters.VisitProtocolSearchResultAdapter;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.syncs.SynchronizeVisitProtocols;
import com.armpk.goatregistrator.utilities.Globals;
import com.armpk.goatregistrator.utilities.RestConnection;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class VisitProtocolSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, RestConnection.OnConnectionCompleted, SynchronizeVisitProtocols.OnVisitProtocolSynchronize{

    private static final String ARG_VISIT_PROTOCOL = "visit_protocol";
    private static final String ARG_SYNCED = "visit_protocol_synced";

    private ListView listViewResults;
    private VisitProtocolSearchResultAdapter adapterResults;
    private DatabaseHelper dbHelper;
    private List<VisitProtocol> listVisitProtocol;
    private TextView mTextLastSync;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_protocol_search);

        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        /*RestConnection mRestFarms = new RestConnection(this, RestConnection.DataType.VISIT_PROTOCOL, this);
        mRestFarms.setAction(RestConnection.Action.GET);
        mRestFarms.execute((Void) null);*/

        mTextLastSync = (TextView)findViewById(R.id.text_last_sync);
        listViewResults = (ListView)findViewById(R.id.list_results);
        registerForContextMenu(listViewResults);

        AlertDialog.Builder alert = new AlertDialog.Builder(VisitProtocolSearchActivity.this);
        alert.setTitle("Внимание!!!");
        alert.setMessage("Желаете ли синхронизация на ПРОТОКОЛИ ЗА ПОСЕЩЕНИЕ?");
        alert.setPositiveButton("СИНХРОНИЗИРАЙ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RestConnection mRestVisitProts = new RestConnection(VisitProtocolSearchActivity.this, RestConnection.DataType.VISIT_PROTOCOL, VisitProtocolSearchActivity.this);
                mRestVisitProts.setAction(RestConnection.Action.GET);
                mRestVisitProts.execute((Void) null);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("НЕ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    listVisitProtocol = dbHelper.getDaoVisitProtocol().queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adapterResults = new VisitProtocolSearchResultAdapter(VisitProtocolSearchActivity.this, listVisitProtocol);
                listViewResults.setAdapter(adapterResults);
                adapterResults.notifyDataSetChanged();

                mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
                mTextLastSync.setBackgroundColor(Color.YELLOW);
                dialog.dismiss();
            }
        });
        alert.show();

        /*listViewResults.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewResults.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = listViewResults.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                adapterResults.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.user_result_context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deleteSelectedItems();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapterResults.removeSelection();
            }
        });*/

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_visit_protocols, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final VisitProtocol vp = (VisitProtocol) listViewResults.getAdapter().getItem(info.position);
        switch (item.getItemId()) {
            case R.id.show_goats_for_farm:
                Intent listsFromBook = new Intent(this, GoatsListsFromBookActivity.class);
                Bundle argsLists = new Bundle();
                argsLists.putSerializable(ARG_VISIT_PROTOCOL, vp);
                listsFromBook.putExtras(argsLists);
                startActivity(listsFromBook);
                return true;
            case R.id.show_goats_lists:
                Intent lists = new Intent(this, VisitProtocolGoatsListsActivity.class);
                Bundle args_lists = new Bundle();
                args_lists.putSerializable(ARG_VISIT_PROTOCOL, vp);
                args_lists.putBoolean(ARG_SYNCED, true);
                lists.putExtras(args_lists);
                startActivity(lists);
                return true;
            case R.id.sync_goats:
                getGoatsForProtocol(vp);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_visit_protocol, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, VisitProtocolSearchActivity.class)));
        searchView.setIconifiedByDefault(false);
        return true;
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
        adapterResults.getFilter().filter(query);

        // Click listener for the searched item that was selected
        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }

    private void deleteSelectedItems(){
        List<VisitProtocol> toDelete = adapterResults.getSelected();
        for(int i = 0; i<toDelete.size(); i++){
            try {
                dbHelper.getDaoVisitProtocol().delete(toDelete.get(i));
                adapterResults.remove(toDelete.get(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        adapterResults.notifyDataSetChanged();
    }

    private void synchronizeVisitProtocols(String result){
        RestConnection.closeProgressDialog();
        SynchronizeVisitProtocols sf = new SynchronizeVisitProtocols(this, result, dbHelper, this);
        sf.execute((Void) null);
    }

    @Override
    public void onResultReceived(RestConnection.Action action, RestConnection.DataType dataType, String result) {
        if(result!=null) {
            if (action == RestConnection.Action.GET) {
                switch (dataType) {
                    case VISIT_PROTOCOL:
                        synchronizeVisitProtocols(result);
                        break;
                    case VISIT_PROTOCOL_BYID:
                        synchronizeGoatsForProtocol(result);
                        break;
                }
            } else if(action == RestConnection.Action.POST) {

            } else {
                RestConnection.closeProgressDialog();
            }
        }else{
            RestConnection.closeProgressDialog();
            processSyncResult(false);
        }
    }

    @Override
    public void onSynchronizeFinished(boolean success) {
        processSyncResult(success);
    }

    private void processSyncResult(boolean success){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        if(success){
            mTextLastSync.setBackgroundColor(Color.GREEN);
        }else{
            mTextLastSync.setBackgroundColor(Color.YELLOW);
        }
        try {
            listVisitProtocol = dbHelper.getDaoVisitProtocol().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapterResults = new VisitProtocolSearchResultAdapter(this, listVisitProtocol);
        listViewResults.setAdapter(adapterResults);
        adapterResults.notifyDataSetChanged();
    }

    private void getGoatsForProtocol(VisitProtocol visitProtocol){
        RestConnection mRestGoatsForProtocol = new RestConnection(this, RestConnection.DataType.VISIT_PROTOCOL_BYID, String.valueOf(visitProtocol.getId()), this);
        mRestGoatsForProtocol.setAction(RestConnection.Action.GET);
        mRestGoatsForProtocol.execute((Void) null);
    }

    private void synchronizeGoatsForProtocol(String result){
        mTextLastSync.setText(getString(R.string.text_last_sync_from_sc, Globals.getDateTime(mSharedPreferences.getLong(Globals.SYNC_FARMS_LAST_DATE, 0))));
        try {
            JSONObject data = new JSONObject(result);
            JSONArray goatsArray = new JSONArray(data.getString("lst_vpGoats"));
            data.remove("lst_vpGoats");
            VisitProtocol vp = Globals.jsonToObject(data, VisitProtocol.class);
            String key = Globals.TEMPORARY_GOATS_FOR_PROTOCOL + String.valueOf(vp.getFarm().getId()) + "_" + String.valueOf(vp.getDateAddedToSystem().getTime())+"_synced";
            Set<String> goats = mSharedPreferences.getStringSet(key, new HashSet<String>());

            if(goats.size()>0) goats.clear();

            for(int i=0; i<goatsArray.length(); i++){
                goats.add(goatsArray.get(i).toString());
            }
            Globals.savePreferences(key, goats, getApplicationContext());

            mTextLastSync.setBackgroundColor(Color.GREEN);
        } catch (JSONException e) {
            mTextLastSync.setBackgroundColor(Color.YELLOW);
            e.printStackTrace();
        }
        RestConnection.closeProgressDialog();
    }
}
