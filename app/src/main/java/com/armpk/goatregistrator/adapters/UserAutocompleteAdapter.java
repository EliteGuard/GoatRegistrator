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
import com.armpk.goatregistrator.database.User;

import java.util.ArrayList;
import java.util.List;

public class UserAutocompleteAdapter extends ArrayAdapter<User> implements Filterable, ListAdapter{

    private final List<User> data;
    private List<User> filteredData = new ArrayList<>();

    public UserAutocompleteAdapter(Context context, List<User> data) {
        super(context, 0, data);
        this.data = data;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public User getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new UserFilter(this, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User u = filteredData.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_autocomplete, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.text_name);
        tvName.setText(u.getFirstName()+" "+u.getFamilyName());
        return convertView;
    }

    private class UserFilter extends Filter {

        UserAutocompleteAdapter adapter;
        List<User> originalList;
        List<User> filteredList;

        public UserFilter(UserAutocompleteAdapter adapter, List<User> originalList) {
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
                for (final User u : originalList) {
                    String n = u.getFirstName()+" "+u.getFamilyName();
                    if (n.toLowerCase().contains(filterPattern)) {
                        filteredList.add(u);
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
