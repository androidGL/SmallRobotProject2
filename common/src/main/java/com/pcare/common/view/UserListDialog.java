package com.pcare.common.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pcare.common.R;
import com.pcare.common.entity.UserEntity;
import com.pcare.common.table.UserDao;

import java.util.List;

/**
 * @Author: gl
 * @CreateDate: 2019/11/19
 * @Description:
 */
public class UserListDialog extends Dialog {
    private List<UserEntity> userInfoList;
    private RecyclerView userListView;
    private RecyclerView.Adapter adapter;
    public UserListDialog(Context context) {
        super(context, R.style.UserListDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_userlist);
        setCanceledOnTouchOutside(true);
        initView();
    }

    @Override
    public void show() {
        super.show();
        refreshData();
    }

    private void initView() {
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        userListView = findViewById(R.id.list_user);
        userListView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dialog_userlist,parent,false);
                return new ItemHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if(holder instanceof ItemHolder){
                    ((ItemHolder) holder).imageView.setImageDrawable(getContext().getDrawable(UserDao.setImage(userInfoList.get(position).getUserType())));
                    ((ItemHolder) holder).textView.setText(userInfoList.get(position).getUserName());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(null != onClickItemListener){
                                UserDao.get(getContext()).setCurrentUser(userInfoList.get(position));
                                onClickItemListener.onItemClick(userInfoList.get(position).getUserName());
                                dismiss();
                            }
                        }
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

    private class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView;
        public ItemHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.user_img);
            textView = itemView.findViewById(R.id.user_text);

        }
    }

    private void refreshData() {
        userInfoList = UserDao.get(getContext()).getUserList();
    }
    public interface OnClickItemListener{
        <T> void onItemClick(String name);
    }
    private OnClickItemListener onClickItemListener;
    public UserListDialog setOnClickItemListener(OnClickItemListener listener) {
        this.onClickItemListener = listener;
        return this;
    }


}
