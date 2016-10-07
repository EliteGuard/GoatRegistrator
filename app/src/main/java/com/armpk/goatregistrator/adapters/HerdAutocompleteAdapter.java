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
import com.armpk.goatregistrator.database.Herd;
import com.armpk.goatregistrator.database.User;

import java.util.ArrayList;
import java.util.List;

public class HerdAutocompleteAdapter extends ArrayAdapter<Herd> implements Filterable, ListAdapter{

    private final List<Herd> data;
    private List<Herd> filteredData = new ArrayList<>();

    public HerdAutocompleteAdapter(Context context, List<Herd> data) {
        super(context, 0, data);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Herd getItem(int position) {
        return filteredData.get(position);
    }

    public Herd getFirst(){
        return data.get(0);
    }

    public Herd getLast(){
        return data.get(data.size()-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new HerdFilter(this, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Herd h = filteredData.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_autocomplete, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.text_name);
        tvName.setText(String.valueOf(h.getHerdNumber()));
        return convertView;
    }

    private class HerdFilter extends Filter {

        HerdAutocompleteAdapter adapter;
        List<Herd> originalList;
        List<Herd> filteredList;

        public HerdFilter(HerdAutocompleteAdapter adapter, List<Herd> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
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
                for (final Herd h : originalList) {
                    String n = String.valueOf(h.getHerdNumber());
                    if (n.toLowerCase().contains(filterPattern)) {
                        filteredList.add(h);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredData.clear();
            adapter.filteredData.addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
