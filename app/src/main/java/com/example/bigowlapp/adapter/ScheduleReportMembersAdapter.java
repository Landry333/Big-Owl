package com.example.bigowlapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.UserScheduleResponse;

import java.util.Map;

public class ScheduleReportMembersAdapter extends BaseAdapter {
    private final Context context;
    private final Map<String, UserScheduleResponse> userScheduleResponseMap;
    private final String[] mapKeys;
    private static final String SCHEDULE_ATTENDED = "Attended";
    private static final String SCHEDULE_Missed = "Missed";
    private final Map<String, String> memberNameMap;

    public ScheduleReportMembersAdapter(Context context, Map<String, String> memberNameMap, Map<String, UserScheduleResponse> userScheduleResponseMap) {
        this.context = context;
        this.memberNameMap = memberNameMap;
        this.userScheduleResponseMap = userScheduleResponseMap;
        this.mapKeys = userScheduleResponseMap.keySet().toArray(new String[0]);
    }

    @Override
    public int getCount() {
        return userScheduleResponseMap.size();
    }

    @Override
    public UserScheduleResponse getItem(int position) {
        return userScheduleResponseMap.get(mapKeys[position]);
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
        scheduleReportMemberName.setText(memberNameMap.get(mapKeys[position]));

        TextView scheduleReportMemberAttendance = convertView.findViewById(R.id.schedule_report_member_attendance);
        if (getItem(position).getAttendance().didAttend()) {
            scheduleReportMemberAttendance.setText(SCHEDULE_ATTENDED);
            scheduleReportMemberAttendance.setTextColor(Color.GREEN);
        } else {
            scheduleReportMemberAttendance.setText(SCHEDULE_Missed);
            scheduleReportMemberAttendance.setTextColor(Color.RED);

        }

        return convertView;
    }
}