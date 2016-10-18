package com.neo.satya.to_doweekly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by satya on 2016-09-09.
 */
public class TaskListAdapter extends BaseAdapter {

    // create a list of type Tasks which can be used to inflate listView with all tasks
    private Context mContext;
    private List<Tasks> mTaskList;
    private LayoutInflater inflater;

    // create constructor
    public TaskListAdapter(Context mContext, List<Tasks> mTaskList) {
        this.mContext = mContext;
        this.mTaskList = mTaskList;
    }

    @Override
    public int getCount() {
        return mTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTaskList.get(position).getTaskId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row, null);
            TextView tName = (TextView) convertView.findViewById(R.id.taskName);
            TextView tTime = (TextView) convertView.findViewById(R.id.taskTime);
            TextView tDate = (TextView) convertView.findViewById(R.id.taskDate);
            final CheckedTextView tStatus = (CheckedTextView) convertView.findViewById(R.id.taskStatus);
            tName.setText(mTaskList.get(position).getTaskName());
            tTime.setText(mTaskList.get(position).getTaskCreationTime());
            tDate.setText(mTaskList.get(position).getTaskCreationDate());
            Boolean flag2 = (mTaskList.get(position).getTaskStatus() == 1);
            tStatus.setChecked(flag2);
            tStatus.setOnClickListener((View.OnClickListener) mContext);
            convertView.setTag(mTaskList.get(position).getTaskId());
        }
        return convertView;
    }
}
