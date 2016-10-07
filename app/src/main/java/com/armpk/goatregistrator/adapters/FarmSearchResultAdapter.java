package com.armpk.goatregistrator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Farm;

import java.util.ArrayList;
import java.util.List;

public class FarmSearchResultAdapter extends BaseAdapter implements Filterable {
    private List<Farm> listFarms;
    private List<Farm> listOrigItems;
    private LayoutInflater mLayoutInflater;
    private FarmFilter filterFarm;
    private List<Farm> mSelectedItems;

    private Context mContext;

    public FarmSearchResultAdapter(Context context, List<Farm> list) {

        listFarms = list;
        listOrigItems = list;
        mSelectedItems = new ArrayList<Farm>();

        mContext = context;
        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return listFarms.size();
    }

    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public Farm getItem(int position) {
        return listFarms.get(position);
    }

    @Override
    //get the position id of the item from the list
    public long getItemId(int position) {
        return listFarms.get(position).getId();
    }

    @Override

    public View getView(int position, View view, ViewGroup viewGroup) {

        // create a ViewHolder reference
        ViewHolder holder;

        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.row_farm_search_result, null);
            holder.textCompanyBreedingPlaceNum = (TextView)view.findViewById(R.id.text_company_breeding_place_num);
            holder.textCompanyName = (TextView) view.findViewById(R.id.text_company_name);
            holder.textCompanyEik = (TextView) view.findViewById(R.id.text_company_eik);
            holder.textCompanyBulstat = (TextView) view.findViewById(R.id.text_company_bulstat);
            holder.textCompanyMol = (TextView) view.findViewById(R.id.text_company_mol);
            holder.textFarmerNames = (TextView) view.findViewById(R.id.text_farmer_names);
            holder.textFarmBreedingAddress = (TextView) view.findViewById(R.id.text_farm_breeding_address);

            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        Farm farm = listFarms.get(position);
        if (farm != null) {
            if(holder.textCompanyBreedingPlaceNum != null){
                holder.textCompanyBreedingPlaceNum.setText(mContext.getString(R.string.placeholder_breeding_place_num_sc,farm.getBreedingPlaceNumber()));
            }
            if (holder.textCompanyName != null) {
                holder.textCompanyName.setText(mContext.getString(R.string.placeholder_name_sc, farm.getCompanyName()));
            }
            if (holder.textCompanyEik != null) {
                holder.textCompanyEik.setText(mContext.getString(R.string.placeholder_eik_sc, farm.getEik()));
            }
            if (holder.textCompanyBulstat != null && !farm.getBulstat().isEmpty()) {
                holder.textCompanyBulstat.setVisibility(View.VISIBLE);
                holder.textCompanyBulstat.setText(mContext.getString(R.string.placeholder_bulstat_sc, farm.getBulstat()));
            }else if (holder.textCompanyBulstat != null && (farm.getBulstat().isEmpty() || farm.getBulstat()!=null) ){
                holder.textCompanyBulstat.setVisibility(View.INVISIBLE);
            }
            if (holder.textCompanyMol != null) {
                holder.textCompanyMol.setText(mContext.getString(R.string.placeholder_mol, farm.getMol()));
            }
            if (holder.textFarmerNames != null && farm.getFarmer()!=null) {
                holder.textFarmerNames.setText(mContext.getString(R.string.placeholder_farmer,
                                farm.getFarmer().getFirstName(),
                                farm.getFarmer().getSurName(),
                                farm.getFarmer().getFamilyName()
                ));
            }
            if (holder.textFarmBreedingAddress!= null && farm.getBreedingPlaceAddress()!=null) {
                /*String address = "Адрес животновъден обект: \n"+
                        "обл. "+farm.getBreedingPlaceAddress().getCity().getArea()+
                        ", общ. "+farm.getBreedingPlaceAddress().getCity().getMunicipality();*/
                String type = "";
                switch (farm.getBreedingPlaceAddress().getCity().getLocationType()){
                    case CITY:
                        type = "гр.";
                        break;
                    case VILLAGE:
                        type = "с.";
                        break;
                }
                /*address += farm.getBreedingPlaceAddress().getCity().getName()+
                        ", "+farm.getBreedingPlaceAddress().getStreet();
                holder.textFarmBreedingAddress.setText(address);*/
                holder.textFarmBreedingAddress.setText(mContext.getString(
                        R.string.placeholder_breeding_place_address,
                        farm.getBreedingPlaceAddress().getCity().getArea(),
                        farm.getBreedingPlaceAddress().getCity().getMunicipality(),
                        type,
                        farm.getBreedingPlaceAddress().getCity().getName(),
                        farm.getBreedingPlaceAddress().getStreet()
                        ));
            }
        }

        //this method must return the view corresponding to the data at the specified position.
        return view;

    }

    public void remove(Farm user) {
        listFarms.remove(user);
        listOrigItems.remove(user);
        mSelectedItems.remove(user);
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

    public List<Farm> getSelected() {
        return mSelectedItems;
    }

    @Override
    public Filter getFilter() {
        if (filterFarm == null)
            filterFarm = new FarmFilter();
        return filterFarm;
    }
    /**
     * Static class used to avoid the calling of "findViewById" every time the getView() method is called,
     * because this can impact to your application performance when your list is too big. The class is static so it
     * cache all the things inside once it's created.
     */
    private static class ViewHolder {
        protected TextView textCompanyBreedingPlaceNum;
        protected TextView textCompanyName;
        protected TextView textCompanyEik;
        protected TextView textCompanyBulstat;
        protected TextView textCompanyMol;
        protected TextView textFarmerNames;
        protected TextView textFarmBreedingAddress;
    }

    private class FarmFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = listOrigItems;
                results.count = listOrigItems.size();
            } else {
                List<Farm> nFarmList = new ArrayList<Farm>();
                for (Farm farm : listFarms) {
                    if (String.valueOf(farm.getCompanyName())
                            .toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nFarmList.add(farm);
                }
                results.values = nFarmList;
                results.count = nFarmList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                listFarms = (List<Farm>) results.values;
                notifyDataSetChanged();
            }

        }

    }
}