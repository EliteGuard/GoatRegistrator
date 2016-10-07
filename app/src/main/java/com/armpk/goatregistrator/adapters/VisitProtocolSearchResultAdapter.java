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
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.VisitActivity;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.utilities.Globals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VisitProtocolSearchResultAdapter extends BaseAdapter implements Filterable {
    private List<VisitProtocol> listItems;
    private List<VisitProtocol> listOrigItems;
    private LayoutInflater mLayoutInflater;
    private VisitProtocolFilter filterVisitProtocol;
    private List<VisitProtocol> mSelectedItems;
    //private DatabaseHelper dbHelper;

    private List<VisitActivity> listActivities;

    public VisitProtocolSearchResultAdapter(Context context, List<VisitProtocol> list) {

        listItems = list;
        listOrigItems = list;
        mSelectedItems = new ArrayList<VisitProtocol>();
        /*dbHelper = dbh;
        try {
            listActivities = dbHelper.getDaoVisitActivity().queryBuilder().selectColumns("_id", "name").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return listItems.size();
    }

    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public VisitProtocol getItem(int position) {
        return listItems.get(position);
    }

    @Override
    //get the position id of the item from the list
    public long getItemId(int position) {
        if(listItems.get(position).getId()==null){
            return 0;
        }
        return listItems.get(position).getId();
    }

    @Override

    public View getView(int position, View view, ViewGroup viewGroup) {

        // create a ViewHolder reference
        ViewHolder holder;

        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.row_visit_protocol_search_result, null);
            holder.textVisitProtocolId = (TextView)view.findViewById(R.id.text_visit_protocol_id);
            holder.textVisitProtocolDate = (TextView) view.findViewById(R.id.text_visit_protocol_date);
            holder.textCompanyName = (TextView) view.findViewById(R.id.text_company_name);
            holder.textUser1 = (TextView) view.findViewById(R.id.text_user_1);
            holder.textUser2 = (TextView) view.findViewById(R.id.text_user_2);
            holder.textVisitProtocolLastUpdated = (TextView) view.findViewById(R.id.text_visit_protocol_last_updated);
            holder.textVisitActivities = (TextView)view.findViewById(R.id.textVisitActivities);

            // the setTag is used to store the data within this view
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        VisitProtocol vp = listItems.get(position);
        if (vp != null) {
            if(holder.textVisitProtocolId != null){
                holder.textVisitProtocolId.setText("Номер: "+String.valueOf(vp.getId()));
                if(vp.getId()==null) holder.textVisitProtocolId.setVisibility(View.INVISIBLE);
            }
            if(holder.textVisitProtocolDate != null && vp.getVisitDate()!=null){
                holder.textVisitProtocolDate.setText(Globals.getDateTime(vp.getVisitDate()));
            }
            if (holder.textCompanyName != null && vp.getFarm()!=null) {
                holder.textCompanyName.setText("Ферма: "+vp.getFarm().getCompanyName());
            }
            if (holder.textUser1 != null && vp.getEmployFirst()!=null) {
                holder.textUser1.setText("Служител 1: "+vp.getEmployFirst().getFirstName()+" "+vp.getEmployFirst().getFamilyName());
                holder.textUser1.setVisibility(View.VISIBLE);
            }
            if (holder.textUser2 != null && vp.getEmploySecond() != null) {
                holder.textUser2.setText("Служител 2: "+vp.getEmploySecond().getFirstName()+" "+vp.getEmploySecond().getFamilyName());
                holder.textUser2.setVisibility(View.VISIBLE);
            }
            if(holder.textVisitProtocolLastUpdated != null && vp.getDateLastUpdated()!=null){
                holder.textVisitProtocolLastUpdated.setText(Globals.getDateTime(vp.getDateLastUpdated()));
            }
        }

        //this method must return the view corresponding to the data at the specified position.
        return view;

    }

    public void remove(VisitProtocol user) {
        listItems.remove(user);
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

    public List<VisitProtocol> getSelected() {
        return mSelectedItems;
    }

    @Override
    public Filter getFilter() {
        if (filterVisitProtocol == null)
            filterVisitProtocol = new VisitProtocolFilter();
        return filterVisitProtocol;
    }
    /**
     * Static class used to avoid the calling of "findViewById" every time the getView() method is called,
     * because this can impact to your application performance when your list is too big. The class is static so it
     * cache all the things inside once it's created.
     */
    private static class ViewHolder {
        protected TextView textVisitProtocolId;
        protected TextView textVisitProtocolDate;
        protected TextView textCompanyName;
        protected TextView textUser1;
        protected TextView textUser2;
        protected TextView textVisitProtocolLastUpdated;
        protected TextView textVisitActivities;
    }

    private class VisitProtocolFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = listOrigItems;
                results.count = listOrigItems.size();
            } else {
                List<VisitProtocol> nVisitProtocolList = new ArrayList<VisitProtocol>();
                for (VisitProtocol vp : listItems) {
                    if (String.valueOf(vp.getFarm().getCompanyName())
                            .toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nVisitProtocolList.add(vp);
                }
                results.values = nVisitProtocolList;
                results.count = nVisitProtocolList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0 && constraint.length()==0)
                notifyDataSetInvalidated();
            else if(results.count == 0 && constraint.length()>0){
                notifyDataSetChanged();
            }else if (results.count>0 && constraint.length()>0){
                listItems = (List<VisitProtocol>) results.values;
                notifyDataSetChanged();
            }

        }

    }
}