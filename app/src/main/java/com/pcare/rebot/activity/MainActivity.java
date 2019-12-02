package com.pcare.rebot.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pcare.common.base.BaseActivity;
import com.pcare.common.base.IPresenter;
import com.pcare.common.util.APPConstant;
import com.pcare.rebot.R;

@Route(path = "app/main")
public class MainActivity extends BaseActivity {
    private int position = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected IPresenter bindPresenter() {
        return null;
    }

    public void toInquiryPage(View view) {
        new AlertDialog.Builder(this)
                .setTitle("请选择您要咨询的类型")
                .setSingleChoiceItems(getResources().getStringArray(R.array.list_type), position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ARouter.getInstance().build("/inquiry/main")
                                .withString("inquiryType",getResources().getStringArray(R.array.list_type)[position])
                                .navigation();
                    }
                }).create().show();

    }

    public void toECGPage(View view) {
//        ARouter.getInstance().build("/ecg/main")
//                .withString("key1","心电检测")
//                .navigation();
        Intent intent = getPackageManager().getLaunchIntentForPackage(APPConstant.INQUIRY);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    public void toPLUPage(View view) {
//        Intent intent = getPackageManager().getLaunchIntentForPackage(APPConstant.PLU);
//        if (intent != null) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
        ARouter.getInstance().build("/glu/main")
                .withString("key1","血糖检测")
                .navigation();
    }

    public void toBPPage(View view) {
//        Intent intent = getPackageManager().getLaunchIntentForPackage(APPConstant.BP);
//        if (intent != null) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
        ARouter.getInstance().build("/bp/main")
                .withString("key1","血压检测")
                .navigation();
    }
}
