package com.colege.project.krishimitra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Space;
import android.widget.TextView;

import java.text.MessageFormat;

/**
 * Created on 23-03-17 at 17:09 by Pranav Raut.
 * For MumbaiLocal
 */

class CustomAdapter2 extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private final Activity a;
    private String[][] strings;
    private int[] ints = new int[]{
            R.id.name, R.id.disp,  R.id.price
    };

    CustomAdapter2(Activity a, String[][] strings) {
        inflater = (LayoutInflater) a.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.strings = strings;
        this.a = a;
    }

    @Override
    public int getCount() {
        return strings[1].length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(!strings[0][position].isEmpty()) {
            final Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.history_item, null);
            int i;
            for (i = 0; i < ints.length; i++) {
                holder.textViews[i] = (TextView) rowView.findViewById(ints[i]);
                holder.textViews[i].setText(strings[i][position]);
            }
            holder.textViews[2].setText(MessageFormat.format("₹{0}", holder.textViews[2].getText().toString()));
            return rowView;
        }
        return new Space(a);
    }

    private class Holder {
        TextView[] textViews = new TextView[ints.length];

    }
}
