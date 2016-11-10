package com.armpk.goatregistrator.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.enums.Sex;
import com.armpk.goatregistrator.database.mobile.LocalGoat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisitProtocolGoatListExpandableAdapter  extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<LocalGoat>> _listDataChild;

    public VisitProtocolGoatListExpandableAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<LocalGoat>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public LocalGoat getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();
            view = infalInflater.inflate(R.layout.row_visit_protocol_goats_lists, null);
            holder.textViewNumber = (TextView) view.findViewById(R.id.textNumber);
            holder.textViewAppliedForSC = (TextView) view.findViewById(R.id.textAppliedForSC);
            holder.textViewBreedCodeName = (TextView) view.findViewById(R.id.textBreedName);
            holder.textViewVetNum1 = (TextView) view.findViewById(R.id.textVetNum1);
            holder.textViewVetNum2 = (TextView) view.findViewById(R.id.textVetNum2);
            holder.textViewBreedNum1 = (TextView) view.findViewById(R.id.textBreedNum1);
            holder.textViewBreedNum2 = (TextView) view.findViewById(R.id.textBreedNum2);
            holder.textViewNewOld = (TextView) view.findViewById(R.id.textNewOld);
            holder.textViewGender = (TextView) view.findViewById(R.id.textGender);
            holder.textVetIS = (TextView) view.findViewById(R.id.textVetIS);
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        LocalGoat goat = (LocalGoat) getChild(groupPosition, childPosition);
        if (goat != null) {
            if (holder.textViewNumber != null) {
                holder.textViewNumber.setText(String.valueOf(childPosition+1));
            }
            if (holder.textViewAppliedForSC != null){
                if(goat.getAppliedForSelectionControlYear()!=null) {
                    holder.textViewAppliedForSC.setText("Да");
                }else{
                    holder.textViewAppliedForSC.setText("Не");
                }
            }
            if (holder.textViewBreedCodeName != null){
                if(goat.getBreed()!=null) holder.textViewBreedCodeName.setText(goat.getBreed().getBreedName());
                else holder.textViewBreedCodeName.setText("Не е посочена");
            }
            if (holder.textViewVetNum1 != null){
                if(goat.getFirstVeterinaryNumber()!=null) holder.textViewVetNum1.setText(goat.getFirstVeterinaryNumber());
                else holder.textViewVetNum1.setText("");
            }
            if (holder.textViewVetNum2 != null){
                if(goat.getSecondVeterinaryNumber()!=null) holder.textViewVetNum2.setText(goat.getSecondVeterinaryNumber());
                else holder.textViewVetNum2.setText("");
            }
            if (holder.textViewBreedNum1 != null){
                if(goat.getFirstBreedingNumber()!=null) holder.textViewBreedNum1.setText(goat.getFirstBreedingNumber());
                else holder.textViewBreedNum1.setText("");
            }
            if (holder.textViewBreedNum2 != null ){
                if(goat.getSecondBreedingNumber()!=null) holder.textViewBreedNum2.setText(goat.getSecondBreedingNumber());
                else holder.textViewBreedNum2.setText("");
            }
            if (holder.textViewNewOld != null){
                if(goat.getRealId() != null){
                    holder.textViewNewOld.setText("Стара");
                }else{
                    holder.textViewNewOld.setText("Нова");
                }
            }
            if (holder.textViewGender != null ){
                if(goat.getSex()!=null) {
                    if (goat.getSex() == Sex.MALE) {
                        holder.textViewGender.setText("Мъжка");
                    } else {
                        holder.textViewGender.setText("Женска");
                    }
                }else{
                    holder.textViewGender.setText("Неизбран");
                }
            }

            if (holder.textVetIS != null ){
                if (goat.isInVetIS()) {
                    holder.textVetIS.setText("ДА");
                } else {
                    holder.textVetIS.setText("НЕ");
                }
            }
        }

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_goats_lists_separator, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.textHeaderTitle);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    private static class ViewHolder {
        protected TextView textViewNumber;
        protected TextView textViewAppliedForSC;
        protected TextView textViewBreedCodeName;
        protected TextView textViewVetNum1;
        protected TextView textViewVetNum2;
        protected TextView textViewBreedNum1;
        protected TextView textViewBreedNum2;
        protected TextView textViewNewOld;
        protected TextView textViewGender;
        protected TextView textVetIS;
    }
}
