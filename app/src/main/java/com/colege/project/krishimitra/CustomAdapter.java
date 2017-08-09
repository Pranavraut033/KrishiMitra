package com.colege.project.krishimitra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.preons.pranav.util.FirebaseTasks;
import com.preons.pranav.util.ImageViewHelper;

import java.io.File;
import java.text.MessageFormat;

/**
 * Created on 23-03-17 at 17:09 by Pranav Raut.
 * For MumbaiLocal
 */

class CustomAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private final ImageViewHelper helper;
    private final Activity a;
    private String[][] strings;
    private int[] ints = new int[]{
            R.id.name, R.id.disp, R.id.contact,
            R.id.email, R.id.price
    };
    private int[] ints2 = new int[]{R.id.link1, R.id.link2, R.id.link3, R.id.link4, R.id.link5};
    private FirebaseTasks[] tasks = new FirebaseTasks[ints2.length];
    @Nullable
    private
    onCompleteListener listener;

    CustomAdapter(Activity a, String[][] strings) {
        inflater = (LayoutInflater) a.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.strings = strings;
        helper = new ImageViewHelper(a);
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
        if(strings[0][position]!=null &&!strings[0][position].isEmpty()) {
            final Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.ad_item, null);
            int i;
            for (i = 0; i < ints.length; i++) {
                holder.textViews[i] = (TextView) rowView.findViewById(ints[i]);
                holder.textViews[i].setText(strings[i][position]);
            }
            holder.textViews[4].setText(MessageFormat.format("₹{0}", holder.textViews[4].getText().toString()));

            for (int j = 0; j < ints2.length; j++) {
                holder.imageViews[j] = (ImageView) rowView.findViewById(ints2[j]);
                if (strings[j + i][position] != null&&!strings[j + i][position].isEmpty()) {
                    tasks[j] = new FirebaseTasks();
                    final int finalJ = j;
                    tasks[j].setDownloadListener(new FirebaseTasks.DownloadListener() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        }

                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }

                        @Override
                        public void onProgress(int progress) {

                        }

                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task, File file) {
                            holder.imageViews[finalJ].setImageBitmap(helper.decodeSampledBitmap(file.getPath(), .2f));
                            if (listener != null) listener.completed();
                        }
                    });
                    tasks[j].downloadFile(strings[j + i][position]);
                }else if(listener != null) listener.completed();
            }
            return rowView;
        }
        return new Space(a);
    }

    void setListener(@NonNull onCompleteListener listener) {
        this.listener = listener;
    }

    private class Holder {
        TextView[] textViews = new TextView[ints.length];
        ImageView[] imageViews = new ImageView[ints2.length];
    }
    interface onCompleteListener{
        void completed();
    }
}
