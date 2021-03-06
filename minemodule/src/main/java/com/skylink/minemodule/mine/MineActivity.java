package com.skylink.minemodule.mine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.skylink.android.commonlibrary.base.BaseActivity;
import com.skylink.android.commonlibrary.base.BaseResponse;
import com.skylink.android.commonlibrary.constant.SPContants;
import com.skylink.android.commonlibrary.dialog.ConfirmDialogFragment;
import com.skylink.android.commonlibrary.entity.LoginResponse;
import com.skylink.android.commonlibrary.netutil.RetrofitCallback;
import com.skylink.android.commonlibrary.netutil.RetrofitUtils;
import com.skylink.android.commonlibrary.ui.AppHeader;
import com.skylink.android.commonlibrary.util.SPUtils;
import com.skylink.minemodule.R;
import com.skylink.minemodule.common.Constant;
import com.skylink.minemodule.common.MineService;
import com.skylink.minemodule.common.PluginInfo;
import com.skylink.minemodule.datamanagerment.DataMangerActivity;
import com.skylink.minemodule.modifypassword.ModifyPasswordActivity;

import java.io.File;
import java.util.List;

import retrofit2.Call;


/**
 * 我的模块主界面
 * @author Administrator
 */
public class MineActivity extends BaseActivity {

    private AppHeader mHeader;

    private ImageView minePic;

    private TextView mineName;

    private TextView minePhone;

    private TextView mineCompany;

    private RelativeLayout mine_rl_modifyPassword;

    private RelativeLayout mine_rl_modifyPhone;

    private RelativeLayout mine_rl_exit;

    private LoginResponse loginResponse;

    private RelativeLayout mine_rl_printConfig;

    private RelativeLayout mine_rl_dataSyncConfig;

    private RelativeLayout mine_rl_pricesetting;

    private CheckBox mine_cb_priceConfig;

    private boolean auto_convert_price;

    private RadioGroup mRadioGroup;

    private RadioButton wq_rb;

    private RadioButton cc_rb;

    private View line_price;

    private String currentApplicationid;

    private List<PluginInfo> pluginInfoList;

    @Override
    protected void receiveParms(Bundle parms) {

    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initView(View view) {

        minePic = f(R.id.mine_img_head);
        mineName = f(R.id.mine_tv_name);
        minePhone = f(R.id.mine_tv_loginname);
        mineCompany = f(R.id.mine_tv_company);
        mine_rl_modifyPassword = findViewById(R.id.mine_rl_modifypassword);
        mine_rl_modifyPhone = findViewById(R.id.mine_rl_modifyphone);
        mine_rl_exit = findViewById(R.id.mine_rl_exit);
        mHeader = findViewById(R.id.mine_li_header);
        mine_rl_printConfig = f(R.id.mine_rl_printsetting);
        mine_rl_dataSyncConfig = f(R.id.mine_rl_datamanagement);
        mine_cb_priceConfig = f(R.id.mine_cbx_pricesetting);
        wq_rb = f(R.id.printsetting_rb_wq);
        cc_rb = f(R.id.printsetting_rb_cc);
        mRadioGroup = f(R.id.mine_rg_choosebussness);
        mine_rl_pricesetting = f(R.id.mine_rl_pricesetting);
        line_price = f(R.id.mine_line_price);
    }

    @Override
    protected void initData() {

        String loginInfoStr = SPUtils.getInstance(SPContants.APP_SP_NAME).getString(SPContants.LOGIN_INFO);
        if (null != loginInfoStr) {
            try {
                loginResponse = new Gson().fromJson(loginInfoStr, new TypeToken<LoginResponse>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            mineName.setText(loginResponse.getUserinfo().getUsername());
            minePhone.setText(loginResponse.getUserinfo().getMobileno());
            mineCompany.setText(loginResponse.getOrginfo().getEname());
        }

        auto_convert_price = SPUtils.getInstance().getBoolean(Constant.SPUtilsKey.KEY_AUTO_PRICE_CONVERT, false);
        mine_cb_priceConfig.setChecked(auto_convert_price);

        currentApplicationid = SPUtils.getInstance().getString(Constant.SP_NAME.PLUGIN_CURRENT);


        String pluginData = SPUtils.getInstance().getString(Constant.SP_NAME.PLUGIN_INFO);
        pluginInfoList = new Gson().fromJson(pluginData, new TypeToken<List<PluginInfo>>() {
        }.getType());


        if (currentApplicationid.equals("com.skylink.venderorder")){
            mRadioGroup.check(R.id.printsetting_rb_wq);
        }else if (currentApplicationid.equals("com.skylink.pdastock")){
            mRadioGroup.check(R.id.printsetting_rb_cc);
            mine_rl_pricesetting.setVisibility(View.GONE);
            line_price.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {
        //修改密码
        mine_rl_modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toModifyPassword();
            }
        });
        //修改手机号
        mine_rl_modifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toModifyPhone();
            }
        });
        //退出登录
        mine_rl_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginout();
            }

        });

        mHeader.setHeaderClickListener(new AppHeader.OnHeaderButtonClickListener() {
            @Override
            public void onLeftButtonClick() {
                finish();
            }

            @Override
            public void onMiddleButtonClick() {

            }

            @Override
            public void onRightButtonClick() {

            }
        });
        //打印设置
        mine_rl_printConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPrintConfig();
            }
        });

        //数据管理
        mine_rl_dataSyncConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDataSyncManager();
            }
        });

        //价格自动换算
        mine_cb_priceConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.getInstance().put(Constant.SPUtilsKey.KEY_AUTO_PRICE_CONVERT, isChecked);
                auto_convert_price = isChecked;
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.printsetting_rb_wq) {
                    String plugin_name = "com.skylink.venderorder";
                    if (checkPluginExists(plugin_name)) {
                        Intent intent = new Intent();
                        intent.setClassName(plugin_name, "com.skylink.venderorder.OrderHomePageActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        SPUtils.getInstance().put(Constant.SP_NAME.PLUGIN_CURRENT, "com.skylink.venderorder");
                    }
                } else if (checkedId == R.id.printsetting_rb_cc) {
                    String plugin_name = "com.skylink.pdastock";
                    if (checkPluginExists(plugin_name)) {
                        Intent intent = new Intent();
                        intent.setClassName(plugin_name, "com.skylink.pdastock.home.view.StockHomePageActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        SPUtils.getInstance().put(Constant.SP_NAME.PLUGIN_CURRENT, "com.skylink.pdastock");
                    }
                }
            }
        });
    }

    private boolean checkPluginExists(String pulgin_name) {
        int count = 0;
        for (PluginInfo info : pluginInfoList) {
            if (info.getApplicationid().equals(pulgin_name)) {
                File plugin = new File(info.getPluginlocalpath());
                if (!plugin.exists()) {
                    Toast.makeText(this, "插件不存在,不能进入程序!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                count++;
            }
            if (count==pluginInfoList.size()){
                Toast.makeText(this, "插件不存在,不能进入程序!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /***
     * 数据管理
     */
    private void toDataSyncManager() {
        startActivity(DataMangerActivity.class);
    }

    /***
     * 打印设置
     */
    private void toPrintConfig() {
    }

    /***
     * 修改手机
     */
    private void toModifyPhone() {
    }

    /***
     * 修改密码
     */
    private void toModifyPassword() {
        startActivity(ModifyPasswordActivity.class);
    }

    /***
     * 退出登录
     */
    private void exit() {
        String token = loginResponse.getToken();
        Call<BaseResponse<String>> baseCall = RetrofitUtils.getDefaultRetrofitInstance().create(MineService.class).loginOut(token);
        RetrofitUtils.requestData(baseCall, new RetrofitCallback<BaseResponse<String>>() {
            @Override
            public void onSuccess(BaseResponse<String> response) {
            }

            @Override
            public void onFail(String message) {

            }

            @Override
            public void onFinish() {
                exitApp();
            }
        });
    }

    private void exitApp(){
        if (loginResponse != null) {
            loginResponse.setToken("");
            loginResponse.getUserinfo().setPassword("");
            String loginResponseStr = new Gson().toJson(loginResponse);
            SPUtils.getInstance(SPContants.APP_SP_NAME).put(SPContants.LOGIN_INFO, loginResponseStr);
        }
        Intent intent = new Intent();
        intent.setClassName("com.skylink.pdavender", "com.skylink.pdavender.login.LoginActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        showToast("退出成功");
        finish();
    }

    /***
     * 无论网络请求是否成功,都退出登录
     */
    private void loginout() {
        final ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance();
        confirmDialogFragment.setPositiveButtonName("确定");
        confirmDialogFragment.setNegativeButtonName("取消");
        confirmDialogFragment.setMeassage("您确定要退出登录吗?");
        confirmDialogFragment.setButtonClickListener(new ConfirmDialogFragment.OnButtonClickListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onConfirm() {
                exit();
            }
        });
        confirmDialogFragment.show(getSupportFragmentManager(), null);
    }

}
