package com.pcare.rebot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.table.UserDao;
import com.pcare.common.view.CommonAlertDialog;
import com.pcare.rebot.R;
import com.pcare.rebot.contract.UserListContract;
import com.pcare.rebot.presenter.UserListPresenter;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/11/20
 * @Description:
 */
public class UserListActivity extends BaseActivity<UserListPresenter> implements UserListContract.View {
    private List<UserEntity> userInfoList;
    private RecyclerView userListView;
    private RecyclerView.Adapter adapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_user_list;
    }

    @Override
    public void start() {
        super.start();
        userInfoList = UserDao.get(getApplicationContext()).getUserList();
        userListView = findViewById(R.id.activity_list_user);
        userListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userListView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_userlist,parent,false);
                return new ItemHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if(holder instanceof ItemHolder){
                    ((ItemHolder) holder).headImg.setImageDrawable(getDrawable(UserDao.setImage(userInfoList.get(position).getUserType())));
                    ((ItemHolder) holder).textView.setText(userInfoList.get(position).getUserName());
                    holder.itemView.setOnClickListener(v -> {
//                            if(null != onClickItemListener){
//                                onClickItemListener.onItemClick(position,userInfoList.get(position));
//                            }
                    });
                    ((ItemHolder) holder).deleteImg.setOnClickListener(v -> {
                        CommonAlertDialog.Builder(getSelfActivity())
                                .setMessage("是否确定删除该用户？")
                                .setOnConfirmClickListener(new CommonAlertDialog.OnConfirmClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UserDao.get(getApplicationContext()).deleteUserById(userInfoList.get(position).getUserId());
//                            presenter.deleteUser(userInfoList.get(position).getUserId());
                                        userInfoList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }).build().shown();

                    });
                    ((ItemHolder) holder).editImg.setOnClickListener(v -> {
                        Intent intent = new Intent(UserListActivity.this,RegisterActivity.class);
                        intent.putExtra("userId",userInfoList.get(position).getUserId());
                        startActivity(intent);
                    });

                }

            }

            @Override
            public int getItemCount() {
                return userInfoList.size();
            }

        };
        userListView.setAdapter(adapter);
    }

    @Override
    protected UserListPresenter bindPresenter() {
        return new UserListPresenter((UserListContract.View) getSelfActivity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfoList = UserDao.get(getApplicationContext()).getUserList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshList(String msg) {

    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView headImg, editImg, deleteImg;
        private TextView textView;
        public ItemHolder(View itemView) {
            super(itemView);
            headImg = itemView.findViewById(R.id.user_img);
            editImg = itemView.findViewById(R.id.user_list_edit);
            deleteImg = itemView.findViewById(R.id.user_list_delete);
            textView = itemView.findViewById(R.id.user_text);
        }
    }

}
