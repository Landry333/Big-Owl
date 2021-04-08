package com.example.bigowlapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bigowlapp.R;
import com.example.bigowlapp.model.Schedule;
import com.example.bigowlapp.model.UserScheduleResponse;

import java.util.Map;

public class ScheduleReportMembersAdapter extends BaseAdapter {
    private final Context context;
    private final Schedule schedule;
    private final String[] mapKeys;
    private final Map<String, String> memberNameMap;

    public ScheduleReportMembersAdapter(Context context, Map<String, String> memberNameMap, Schedule schedule) {
        this.context = context;
        this.memberNameMap = memberNameMap;
        this.schedule = schedule;
        this.mapKeys = schedule.getUserScheduleResponseMap().keySet().toArray(new String[0]);
    }

    @Override
    public int getCount() {
        return schedule.getUserScheduleResponseMap().size();
    }

    @Override
    public UserScheduleResponse getItem(int position) {
        try {
            return schedule.getUserScheduleResponseMap().get(mapKeys[position]);
        } catch (Exception e) {
            Log.e("kek2", Log.getStackTraceString(e));
            e.printStackTrace();
            System.err.println("kek2");
            return null;
        }
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
        try {
            scheduleReportMemberName.setText(memberNameMap.get(mapKeys[position]));
        } catch (Exception e) {
            Log.e("kek3", Log.getStackTraceString(e));
            e.printStackTrace();
            System.err.println("kek3");

        }

        TextView scheduleReportMemberAttendance = convertView.findViewById(R.id.schedule_report_member_attendance);
        TextView scheduleReportMemberAttendanceTime = convertView.findViewById(R.id.schedule_report_member_attendance_time);

        Map<String, Object> map = null;
        try {
            map = schedule.scheduleMemberResponseAttendanceMap(mapKeys[position]);
        } catch (Exception e) {
            Log.e("kek4", Log.getStackTraceString(e));
            e.printStackTrace();
            System.err.println("kek4");

        }
        try {
            scheduleReportMemberAttendance.setText((String) map.get("responseText"));
        } catch (Exception e) {
            Log.e("kek5", Log.getStackTraceString(e));
            e.printStackTrace();
            System.err.println("kek5");

        }
        try {
            scheduleReportMemberAttendance.setTextColor((int) map.get("responseColor"));
        } catch (Exception e) {
            Log.e("kek6", Log.getStackTraceString(e));
            e.printStackTrace();
            System.err.println("kek6");

        }
        try {
            if (map.get("attendanceTime") != null) {
                scheduleReportMemberAttendanceTime.setVisibility(View.VISIBLE);
                scheduleReportMemberAttendanceTime.setText((String) map.get("attendanceTime"));
            }
        } catch (Exception e) {
            Log.e("kek7", Log.getStackTraceString(e));
            e.printStackTrace();
            System.err.println("kek7");

        }

        return convertView;
    }

}