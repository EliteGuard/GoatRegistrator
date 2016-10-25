package com.armpk.goatregistrator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.enums.Sex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class VisitProtocolGoatsListsAdapter extends ArrayAdapter<Goat> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private List<Goat> listData;
    private LayoutInflater mLayoutInflater;

    private Context mContext;

    public VisitProtocolGoatsListsAdapter(Context context, List<Goat> list) {
        super(context, -1, list);
        listData = list;
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    public TreeSet<Integer> getSectionsHeader(){
        return this.sectionHeader;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addSectionHeaderItem() {
        listData.add(null);
        sectionHeader.add(listData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return listData.size();
    }

    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public Goat getItem(int position) {
        return listData.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);


        if (view == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    view = mLayoutInflater.inflate(R.layout.row_visit_protocol_goats_lists, null);
                    holder.textViewNumber = (TextView) view.findViewById(R.id.textNumber);
                    holder.textViewAppliedForSC = (TextView) view.findViewById(R.id.textAppliedForSC);
                    holder.textViewBreedCodeName = (TextView) view.findViewById(R.id.textBreedName);
                    holder.textViewVetNum1 = (TextView) view.findViewById(R.id.textVetNum1);
                    holder.textViewVetNum2 = (TextView) view.findViewById(R.id.textVetNum2);
                    holder.textViewBreedNum1 = (TextView) view.findViewById(R.id.textBreedNum1);
                    holder.textViewBreedNum2 = (TextView) view.findViewById(R.id.textBreedNum2);
                    holder.textViewNewOld = (TextView) view.findViewById(R.id.textNewOld);
                    holder.textViewGender = (TextView) view.findViewById(R.id.textGender);
                    /*holder.linearHeader = (LinearLayout)view.findViewById(R.id.linearHeader);
                    holder.textHeaderTitle = (TextView)view.findViewById(R.id.textHeaderTitle);*/
                    break;
                case TYPE_SEPARATOR:
                    view = mLayoutInflater.inflate(R.layout.row_goats_lists_separator, null);
                    holder.linearHeader = (LinearLayout)view.findViewById(R.id.linearHeader);
                    holder.textHeaderTitle = (TextView)view.findViewById(R.id.textHeaderTitle);
                    break;
            }
            view.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        Goat goat = listData.get(position);
        if (goat != null) {
            if (holder.textViewNumber != null) {
                int pos = 0;
                List<Integer> sectionPositions = new ArrayList<>(sectionHeader);
                if(position<sectionPositions.get(0)) pos = position+1;
                else if(position<sectionPositions.get(1)) pos = position;
                else if(position<sectionPositions.get(2)) pos = position-1;
                else if(position<sectionPositions.get(3)) pos = position-2;
                else if(position<sectionPositions.get(4)) pos = position-3;
                else if(position<sectionPositions.get(5)) pos = position-4;
                else if(position<sectionPositions.get(6)) pos = position-5;
                else pos = position-6;

                holder.textViewNumber.setText(String.valueOf(pos));
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
                if(goat.getId() != null){
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
            //holder.linearHeader.setVisibility(View.GONE);
        }else{
            //holder.linearHeader.setVisibility(View.VISIBLE);
            //holder.textHeaderTitle.setText("Списък");
            List<Integer> sectionPositions = new ArrayList<>(getSectionsHeader());
            if(position==sectionPositions.get(0))
                holder.textHeaderTitle.setText("Налични кози без промени");
            else if(position==sectionPositions.get(1))
                holder.textHeaderTitle.setText("Налични кози с променени ветеринарни марки, които се водят във ВетИС");
            else if(position==sectionPositions.get(2))
                holder.textHeaderTitle.setText("Кози с липсващи ветеринарни марки, които са вписани в родословна книга");
            else if(position==sectionPositions.get(3))
                holder.textHeaderTitle.setText("Нови кози с ветеринарни марки, които се водят във ВетИС");
            else if(position==sectionPositions.get(4))
                holder.textHeaderTitle.setText("Налични кози с ветеринарни марки, които не се водят във ВетИС");
            else if(position==sectionPositions.get(5))
                holder.textHeaderTitle.setText("Кози, липсващи при проверка");
            else if(position==sectionPositions.get(6))
                holder.textHeaderTitle.setText("Кози с особени случаи, за допълнителна проверка");
            /*else
                holder.textHeaderTitle.setText("Други");*/
        }

        //this method must return the view corresponding to the data at the specified position.
        return view;
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
        protected LinearLayout linearHeader;
        protected TextView textHeaderTitle;
    }
}