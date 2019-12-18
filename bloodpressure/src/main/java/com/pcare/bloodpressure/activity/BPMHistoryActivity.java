package com.pcare.bloodpressure.activity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pcare.bloodpressure.R;
import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.BPMEntity;
import com.pcare.common.table.BPMTableController;
import com.pcare.common.table.UserDao;
import com.pcare.common.util.CommonUtil;

import java.util.Calendar;
import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/11/26
 * @Description:
 */
public class BPMHistoryActivity extends BaseActivity {

    private RecyclerView bpmListView;
    private List<BPMEntity> bpmList;
    @Override
    public int getLayoutId() {
        return R.layout.activity_bpm_history;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    @Override
    public void start() {
        super.start();
        //获取当前用户的血压记录
        bpmList = BPMTableController.getInstance(getApplicationContext()).searchByUserId(UserDao.getCurrentUserId());
        bpmListView = findViewById(R.id.view_list_bpm);
        if(bpmList.size()<=0){
            findViewById(R.id.null_view).setVisibility(View.VISIBLE);
            bpmListView.setVisibility(View.GONE);
            return;
        }

        bpmListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bpmListView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_bpm,parent,false);
                return new ItemHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if(holder instanceof ItemHolder){
                    BPMEntity entity = bpmList.get(position);
                    ((ItemHolder) holder).timeView.setText(CommonUtil.getDateStr(entity.getTimeData()));
                    ((ItemHolder) holder).systolicView.setText(entity.getSystolicData()+entity.getUnit());
                    ((ItemHolder) holder).diastolicView.setText(entity.getDiastolicData()+entity.getUnit());
                    ((ItemHolder) holder).meanAPView.setText(entity.getMeanAPData()+entity.getUnit());
                    ((ItemHolder) holder).pulseView.setText(entity.getPulseData()+"bpm");
                    if(Double.parseDouble(entity.getSystolicData())>Double.parseDouble(getResources().getString(R.string.systolic_top))
                    ||Double.parseDouble(entity.getDiastolicData())>Double.parseDouble(getResources().getString(R.string.diastolic_top))){
                        ((ItemHolder) holder).typeView.setText("高压");
                        ((ItemHolder) holder).typeView.setBackgroundResource(R.mipmap.circle_not_normal);
                    }else  if(Double.parseDouble(entity.getSystolicData())<Double.parseDouble(getResources().getString(R.string.systolic_bottom))
                            ||Double.parseDouble(entity.getDiastolicData())<Double.parseDouble(getResources().getString(R.string.diastolic_bottom))){
                        ((ItemHolder) holder).typeView.setText("低压");
                        ((ItemHolder) holder).typeView.setBackgroundResource(R.mipmap.circle_not_normal2);
                    }else {
                        ((ItemHolder) holder).typeView.setText("正常");
                        ((ItemHolder) holder).typeView.setBackgroundResource(R.mipmap.circle_normal);
                    }
                }
            }

            @Override
            public int getItemCount() {
                return bpmList.size();
            }
        });
    }

    public void toChartActivity(View view) {
        startActivity(new Intent(this,BPMTrendChartActivity.class));
    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        private TextView timeView,systolicView,diastolicView,meanAPView,pulseView,typeView;
        public ItemHolder(View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.bpm_time);
            systolicView = itemView.findViewById(R.id.bpm_systolic);
            diastolicView = itemView.findViewById(R.id.bpm_diastolic);
            meanAPView = itemView.findViewById(R.id.bpm_mean_ap);
            pulseView = itemView.findViewById(R.id.bpm_pulse);
            typeView = itemView.findViewById(R.id.bpm_type);

        }
    }
}
