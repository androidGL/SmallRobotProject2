package com.pcare.bloodglu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.util.CommonUtil;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/12/3
 * @Description:
 */
public class GlucoseRecycleAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<GlucoseEntity> glucoseEntities;

    public GlucoseRecycleAdapter(Context context, List<GlucoseEntity> entities) {
        this.mContext = context;
        this.glucoseEntities =entities;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_feature_gls_item, parent, false);
        return new GluViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GluViewHolder) {
            ((GluViewHolder) holder).timeView.setText(CommonUtil.getDateStr(glucoseEntities.get(position).getTimeDate()));

            try {
                ((GluViewHolder) holder).detailsView.setText(mContext.getResources().getStringArray(R.array.gls_type)[glucoseEntities.get(position).getSampleType()]);
            } catch (final ArrayIndexOutOfBoundsException e) {
                ((GluViewHolder) holder).detailsView.setText(mContext.getResources().getStringArray(R.array.gls_type)[0]);
            }
            ((GluViewHolder) holder).concentrationView.setText(mContext.getString(R.string.gls_value,
                    Float.parseFloat(glucoseEntities.get(position).getGlucoseConcentration()) * 1000.0f)+mContext.getString(R.string.gls_unit_mmolpl));
            try {
                ((GluViewHolder) holder).locationView.setText( mContext.getResources().getStringArray(R.array.gls_location)[glucoseEntities.get(position).getSampleLocation()]);
            } catch (final ArrayIndexOutOfBoundsException e) {
                ((GluViewHolder) holder).locationView.setText(mContext.getResources().getStringArray(R.array.gls_location)[0]);
            }
        }
    }

    @Override
    public int getItemCount() {
        return glucoseEntities.size();
    }

    private class GluViewHolder extends RecyclerView.ViewHolder {
        private TextView timeView, detailsView, locationView, concentrationView;

        public GluViewHolder(@NonNull View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.time);
            detailsView = itemView.findViewById(R.id.details);
            locationView = itemView.findViewById(R.id.gls_location);
            concentrationView = itemView.findViewById(R.id.gls_concentration);

        }
    }
}
