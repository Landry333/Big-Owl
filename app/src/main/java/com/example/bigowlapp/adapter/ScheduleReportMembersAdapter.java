package com.example.bigowlapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bigowlapp.R;

import java.util.Map;

public class ScheduleReportMembersAdapter extends BaseAdapter {
    private final Context context;
    private final Map<String, Boolean> memberNameAttendMap;
    private final String[] mapKeys;
    private static final String SCHEDULE_ATTENDED = "Attended";
    private static final String SCHEDULE_Missed = "Missed";

    public ScheduleReportMembersAdapter(Context context, Map<String, Boolean> data) {
        this.context = context;
        this.memberNameAttendMap = data;
        this.mapKeys = memberNameAttendMap.keySet().toArray(new String[data.size()]);
    }

    @Override
    public int getCount() {
        return memberNameAttendMap.size();
    }

    @Override
    public Boolean getItem(int position) {
        return memberNameAttendMap.get(mapKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.fragment_schedule_report_members, parent, false);
        }

        TextView scheduleReportMemberName = convertView.findViewById(R.id.schedule_report_member_name);
        TextView scheduleReportMemberAttendance = convertView.findViewById(R.id.schedule_report_member_attendance);

        scheduleReportMemberName.setText(mapKeys[position]);

        if (getItem(position)) {
            scheduleReportMemberAttendance.setText(SCHEDULE_ATTENDED);
            scheduleReportMemberAttendance.setTextColor(Color.GREEN);
        } else {
            scheduleReportMemberAttendance.setText(SCHEDULE_Missed);
            scheduleReportMemberAttendance.setTextColor(Color.RED);

        }

        return convertView;
    }
}