package com.armpk.goatregistrator.adapters;


import android.content.Context;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Goat;

import java.util.ArrayList;
import java.util.List;

public class GoatSearchResultAdapter extends BaseAdapter implements Filterable{
    private List<Goat> listGoats;
    private List<Goat> listOrigItems;
    private LayoutInflater mLayoutInflater;
    private GoatFilter filterGoat;
    private List<Goat> mSelectedItems;

    public GoatSearchResultAdapter(Context context, List<Goat> list) {

        listGoats = list;
        listOrigItems = list;
        mSelectedItems = new ArrayList<Goat>();

        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return listGoats.size();
    }

    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public Goat getItem(int position) {
        return listGoats.get(position);
    }

    @Override
    //get the position id of the item from the list
    public long getItemId(int position) {
        return listGoats.get(position).getId();
    }

    @Override

    public View getView(int position, View view, ViewGroup viewGroup) {

        // create a ViewHolder reference
        ViewHolder holder;

        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.row_goat_search_result, null);
            holder.textVetCode = (TextView) view.findViewById(R.id.text_vet_code);
            holder.textBreedingCode = (TextView) view.findViewById(R.id.text_breeding_code);

            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        Goat goat = listGoats.get(position);
        if (goat != null) {
            if (holder.textVetCode != null) {
                holder.textVetCode.setText(String.valueOf(goat.getFirstVeterinaryNumber()));
            }
            if (holder.textBreedingCode != null) {
                holder.textBreedingCode.setText(String.valueOf(goat.getFirstBreedingNumber()));
            }
        }

        //this method must return the view corresponding to the data at the specified position.
        return view;

    }

    public void remove(Goat goat) {
        listGoats.remove(goat);
        listOrigItems.remove(goat);
        mSelectedItems.remove(goat);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItems.contains(getItemId(position)));
    }

    public void removeSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItems.add(getItem(position));

        else
            mSelectedItems.remove(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItems.size();
    }

    public List<Goat> getSelected() {
        return mSelectedItems;
    }

    @Override
    public Filter getFilter() {
        if (filterGoat == null)
            filterGoat = new GoatFilter();
        return filterGoat;
    }
    /**
     * Static class used to avoid the calling of "findViewById" every time the getView() method is called,
     * because this can impact to your application performance when your list is too big. The class is static so it
     * cache all the things inside once it's created.
     */
    private static class ViewHolder {
        protected TextView textVetCode;
        protected TextView textBreedingCode;
    }

    private class GoatFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = listOrigItems;
                results.count = listOrigItems.size();
            } else {
                List<Goat> nGoatList = new ArrayList<Goat>();
                for (Goat g : listGoats) {
                    if (String.valueOf(g.getFirstVeterinaryNumber()).toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nGoatList.add(g);
                }
                results.values = nGoatList;
                results.count = nGoatList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                listGoats = (List<Goat>) results.values;
                notifyDataSetChanged();
            }

        }

    }
}