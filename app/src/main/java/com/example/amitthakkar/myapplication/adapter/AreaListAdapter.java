package com.example.amitthakkar.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.R;
import com.example.amitthakkar.myapplication.activity.SearchAreaListActivity;
import com.example.amitthakkar.myapplication.model.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by amit.thakkar on 7/14/2015.
 */
public class AreaListAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Area>originalData = null;
    private ArrayList<Area>filteredData = null;
    Activity activity;
    ArrayList<Area> areaList;
    private String[] mKeys;
    private ItemFilter mFilter = new ItemFilter();

    public AreaListAdapter(Activity activity, ArrayList<Area> areaList) {

        this.activity = activity;
        this.areaList = areaList;

        originalData = new ArrayList<>();
        filteredData =  new ArrayList<>();


        for (Area area: areaList) {
            originalData.add(area);
            filteredData.add(area);
        }



    }
    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Area getItem(int position) {
        return filteredData.get(position);
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.place_list_child, null);

        Area area = filteredData.get(position);
        TextView txt_state = (TextView) convertView.findViewById(R.id.text1);
        //txt_state.setText(filteredData.get(position));

        String filter = SearchAreaListActivity.edSearch.getText().toString();
        String itemValue = area.area_name+", "+area.getArea_city();

        int startPos = itemValue.toLowerCase(Locale.US).indexOf(filter.toLowerCase(Locale.US));
        int endPos = startPos + filter.length();

        if (startPos != -1) // This should always be true, just a sanity check
        {
            Spannable spannable = new SpannableString(itemValue);
            ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.parseColor("#8FC54D") });
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);

            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_state.setText(spannable);
        }
        else
            txt_state.setText(itemValue);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Area> list = originalData;

            int count = list.size();
            final ArrayList<Area> nlist = new ArrayList<Area>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).area_name + ", "+list.get(i).area_city;
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Area>) results.values;
            notifyDataSetChanged();
        }

    }

}
