package com.armpk.goatregistrator.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.GoatSearchResultAdapter;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Goat;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

public class GoatSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView listViewGoatResults;
    private GoatSearchResultAdapter adapterGoatResults;
    private DatabaseHelper dbHelper;
    private List<Goat> listGoats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goat_search);

        dbHelper = new DatabaseHelper(this);

        listViewGoatResults = (ListView)findViewById(R.id.list_results);
        try {
            listGoats = dbHelper.getDaoGoat().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapterGoatResults = new GoatSearchResultAdapter(this, listGoats);

        listViewGoatResults.setAdapter(adapterGoatResults);
        listViewGoatResults.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewGoatResults.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = listViewGoatResults.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                adapterGoatResults.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.goat_result_context_menu, menu);
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
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapterGoatResults.removeSelection();
            }
        });

        dbHelper = new DatabaseHelper(this);

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
        inflater.inflate(R.menu.menu_search_goat, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, GoatSearchActivity.class)));
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
        adapterGoatResults.getFilter().filter(query);

        // Click listener for the searched item that was selected
        listViewGoatResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }

    private void deleteSelectedItems(){
        List<Goat> toDelete = adapterGoatResults.getSelected();
        for(int i = 0; i<toDelete.size(); i++){
            try {
                /*DeleteBuilder<Goat, Integer> deleteBuilder = dbHelper.getDaoGoat().deleteBuilder();
                SelectArg idArg = new SelectArg(toDelete.get(i));
                deleteBuilder.where().eq("id", idArg);
                dbHelper.getDaoGoat().delete(deleteBuilder.prepare());*/
                dbHelper.getDaoGoat().delete(toDelete.get(i));
                adapterGoatResults.remove(toDelete.get(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        adapterGoatResults.notifyDataSetChanged();
    }
}
