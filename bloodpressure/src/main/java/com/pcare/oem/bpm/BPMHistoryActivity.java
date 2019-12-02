package com.pcare.oem.bpm;

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
                }
            }

            @Override
            public int getItemCount() {
                return bpmList.size();
            }
        });
    }
    private class ItemHolder extends RecyclerView.ViewHolder{
        private TextView timeView,systolicView,diastolicView,meanAPView,pulseView;
        public ItemHolder(View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.bpm_time);
            systolicView = itemView.findViewById(R.id.bpm_systolic);
            diastolicView = itemView.findViewById(R.id.bpm_diastolic);
            meanAPView = itemView.findViewById(R.id.bpm_mean_ap);
            pulseView = itemView.findViewById(R.id.bpm_pulse);
        }
    }
}
