package com.its5314.project.beontime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.its5314.project.beontime.PI_ShiftDetails;
import com.its5314.project.beontime.R;

import java.util.List;

/**
 * Created by pineshmenat on 2017-12-30.
 */

public class PI_Adapter_GetShift extends RecyclerView.Adapter<PI_Adapter_GetShift.ViewHolder> {

    private Context context;
    private List<PI_POJO_GetShifts> shifts;

    public PI_Adapter_GetShift(Context context, List<PI_POJO_GetShifts> shifts){
        this.context=context;
        this.shifts=shifts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.pi_get_shift_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final PI_POJO_GetShifts pi_pojo_getShifts = shifts.get(position);

        holder.mShiftId.setText(pi_pojo_getShifts.getShiftId());
        holder.mCompanyName.setText(pi_pojo_getShifts.getCompanyName());
        holder.mStartTime.setText(pi_pojo_getShifts.getShiftStartTime());
        holder.mEndTime.setText(pi_pojo_getShifts.getShiftEndTime());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, PI_ShiftDetails.class).putExtra("shiftId",pi_pojo_getShifts.getShiftId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mShiftId,mCompanyName,mStartTime,mEndTime;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mShiftId=(TextView) itemView.findViewById(R.id.shiftId);
            mCompanyName=(TextView) itemView.findViewById(R.id.companyName);
            mStartTime=(TextView) itemView.findViewById(R.id.startTime);
            mEndTime=(TextView) itemView.findViewById(R.id.endTime);
            linearLayout=(LinearLayout) itemView.findViewById(R.id.mainLayoutShiftAdapter);

        }
    }
}
