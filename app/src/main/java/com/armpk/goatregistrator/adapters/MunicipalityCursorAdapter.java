package com.armpk.goatregistrator.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.City;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class MunicipalityCursorAdapter extends CursorAdapter {
    private DatabaseHelper dbHelper;
    private String mArea = "";

    public MunicipalityCursorAdapter(Context context, Cursor c, int flags, DatabaseHelper databaseHelper) {
        super(context, c, flags);
        dbHelper = databaseHelper;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.row_autocomplete, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String keyword = cursor.getString(cursor.getColumnIndex("municipality"));
        TextView tv = (TextView)view.findViewById(R.id.text_name);
        tv.setText(keyword);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        //return super.convertToString(cursor);
        String value = "";
        value = cursor.getString(cursor.getColumnIndex("municipality"));
        return value;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String filter = "";
        if (constraint == null) filter = "";
        else
            filter = constraint.toString();
        //Cursor cursor = null;
        AndroidDatabaseResults results = null;
        try {
            Dao<City, Long> daoCity = dbHelper.getDaoCity();
            QueryBuilder<City, Long> cqb = dbHelper.getDaoCity().queryBuilder();
            cqb.selectColumns("_id", "municipality").groupByRaw("municipality")
                    .where().like("municipality", filter+"%")
                    .and()
                    .like("area", mArea);
            CloseableIterator<City> iterator = daoCity.iterator(cqb.prepare());
            results = (AndroidDatabaseResults) iterator.getRawResults();

            //iterator.closeQuietly();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results.getRawCursor();
    }

    public void setArea(String area){
        mArea = area;
    }
}
