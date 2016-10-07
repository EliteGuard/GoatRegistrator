package com.armpk.goatregistrator.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Farm;

import java.util.ArrayList;
import java.util.List;

public class FarmAutocompleteAdapter extends ArrayAdapter<Farm> implements Filterable, ListAdapter{

    private final List<Farm> farms;
    private List<Farm> filteredFarms = new ArrayList<>();

    public FarmAutocompleteAdapter(Context context, List<Farm> farms) {
        super(context, 0, farms);
        this.farms = farms;
    }

    @Override
    public int getCount() {
        return filteredFarms.size();
    }

    @Override
    public Farm getItem(int position) {
        return filteredFarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new FarmsFilter(this, farms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Farm f = filteredFarms.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_autocomplete, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.text_name);
        tvName.setText(f.getCompanyName());
        return convertView;
    }

    private class FarmsFilter extends Filter {

        FarmAutocompleteAdapter adapter;
        List<Farm> originalList;
        List<Farm> filteredList;

        public FarmsFilter(FarmAutocompleteAdapter adapter, List<Farm> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>(0);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                // Your filtering logic goes in here
                for (final Farm f : originalList) {
                    if (f.getCompanyName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(f);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredFarms.clear();
            if(results.values!=null) adapter.filteredFarms.addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
