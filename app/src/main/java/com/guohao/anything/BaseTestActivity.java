package com.guohao.anything;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.guohao.anything.R;

import java.util.List;

public abstract class BaseTestActivity extends Activity {
	public static final int SHOW_MSG = 0;

	public static final String LKL_SERVICE_ACTION = "com.android.topwise.mposusdk.MposUsdkService";
	
	private int showLineNum = 0;

	private LinearLayout linearLayout;
	private ScrollView scrollView;
	private TextView textView1;
	private TextView textView2;
	private long oldTime = -1;
	public static final long DELAY_TIME = 200;
	public LinearLayout rightButArea = null;

	public EditText et_money;
	public LinearLayout ll_input_edits;
	public EditText et_order;
	public EditText et_psw;
	public EditText et_name;
	

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			String msg1 = bundle.getString("msg1");
			String msg2 = bundle.getString("msg2");
			int color = bundle.getInt("color");
			updateView(msg1, msg2, color);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// super.setContentView(R.layout.base_activity);
		linearLayout = (LinearLayout) this.findViewById(R.id.tipLinearLayout);
		scrollView = (ScrollView) this.findViewById(R.id.tipScrollView);
		//rightButArea = (LinearLayout) this.findViewById(R.id.main_linearlayout);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	/**
	 * 显示信息
	 * 
	 * @param msg1
	 * @param msg2
	 * @param color
	 * @createtor：Administrator
	 * @date:2014-9-15 下午9:45:18
	 */
	public void updateView(final String msg1, final String msg2, final int color) {
		if ((showLineNum % 300 == 0) && (showLineNum > 0)) { // 显示够20行的时候重新开始
			if(linearLayout != null) {
				linearLayout.removeAllViews();
			} else {
				linearLayout = (LinearLayout) findViewById(R.id.tipLinearLayout);
			}
			showLineNum = 0;
		}
		showLineNum++;
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.show_item, null);
		textView1 = (TextView) v.findViewById(R.id.tip1);
		textView2 = (TextView) v.findViewById(R.id.tip2);
		textView1.setText(msg1);
		textView2.setText(msg2);
		textView1.setTextColor(Color.BLACK);
		textView2.setTextColor(color);
		textView1.setTextSize(20);
		textView2.setTextSize(20);
		linearLayout.addView(v);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

	}

	/**
	 * 更新UI
	 * 
	 * @param msg1
	 * @param msg2
	 * @param color
	 * @createtor：Administrator
	 * @date:2014-11-29 下午7:01:16
	 */
	public void showMessage(final String msg1, final String msg2,
                            final int color) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("msg1", msg1);
		bundle.putString("msg2", msg2);
		bundle.putInt("color", color);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	// 显示单条信息
	public void showMessage(final String msg1, final int color) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("msg1", msg1);
		bundle.putString("msg2", "");
		bundle.putInt("color", color);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public void showMessage(String str) {
		this.showMessage(str, Color.BLACK);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		oldTime = -1;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent  
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
   
        // Make sure only one match was found  
        if (resolveInfo == null || resolveInfo.size() != 1) {  
            return null;  
        }  
   
        // Get component info and create ComponentName  
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
   
        // Create a new intent. Use the old one for extras and such reuse  
        Intent explicitIntent = new Intent(implicitIntent);
   
        // Set the component to be explicit  
        explicitIntent.setComponent(component);  
   
        return explicitIntent;  
    }

	/**
	 * 初始化输入弹框（一个输入框）
	 * @param listener
	 * @return
	 */
	public AlertDialog.Builder initDialog(String title, String hint, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		View view = getLayoutInflater().inflate(
				R.layout.dialog_input_money_layout, null, false);

		et_money = (EditText) view.findViewById(R.id.money);
		et_money.setHint(hint);
		ll_input_edits = (LinearLayout) findViewById(R.id.ll_input_edits);

		builder.setTitle(title).setView(view).setNegativeButton("取消", null)
				.setPositiveButton("确定", listener);

		return builder;
	}

	/**
	 * 初始化输入弹框（2个输入框）
	 * @param listener
	 * @return
	 */
	public AlertDialog.Builder initDialog2(String title, String hint1, String hint2, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		View view = getLayoutInflater().inflate(
				R.layout.dialog_input_money_layout2, null, false);

		et_money = (EditText) view.findViewById(R.id.money);
		et_money.setHint(hint1);

		et_order = (EditText) view.findViewById(R.id.order);
		et_order.setHint(hint2);

		builder.setTitle(title).setView(view).setNegativeButton("取消", null)
				.setPositiveButton("确定", listener);

		return builder;
	}

	public AlertDialog.Builder initDialog2(String title, String hint1, String hint2, int inputType, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = initDialog2(title, hint1, hint2, listener);
		et_money.setInputType(inputType);
		return builder;
	}

	/**
	 * 初始化输入弹框（3个输入框）
	 * @param listener
	 * @return
	 */
	public AlertDialog.Builder initDialog3(String title, String hint1, String hint2, String hint3, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		View view = getLayoutInflater().inflate(
				R.layout.dialog_input_money_layout3, null, false);

		et_money = (EditText) view.findViewById(R.id.money);
		et_money.setHint(hint1);

		et_order = (EditText) view.findViewById(R.id.order);
		et_order.setHint(hint2);

		et_psw = (EditText) view.findViewById(R.id.psw);
		et_psw.setHint(hint3);

		builder.setTitle(title).setView(view).setNegativeButton("取消", null)
				.setPositiveButton("确定", listener);

		return builder;
	}

	/**
	 * 初始化输入弹框（4个输入框）
	 * @param listener
	 * @return
	 */
	public AlertDialog.Builder initDialog4(String title, String hint1, String hint2, String hint3, String hint4, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		View view = getLayoutInflater().inflate(
				R.layout.dialog_input_money_layout4, null, false);

		et_money = (EditText) view.findViewById(R.id.money);
		et_money.setHint(hint1);

		et_order = (EditText) view.findViewById(R.id.order);
		et_order.setHint(hint2);

		et_psw = (EditText) view.findViewById(R.id.psw);
		et_psw.setHint(hint3);

		et_name = (EditText) view.findViewById(R.id.name);
		et_name.setHint(hint4);

		builder.setTitle(title).setView(view).setNegativeButton("取消", null)
				.setPositiveButton("确定", listener);

		return builder;
	}

	synchronized boolean isNormalVelocityClick(long time) {
		long newTime = System.currentTimeMillis();
		if (oldTime == -1) {
			oldTime = newTime;
			return true;
		} else {
			android.util.Log.v("asewang","newTime : " + newTime + " , oldTime : " + oldTime);
			if ((newTime - oldTime) <= time) {
				oldTime = newTime;
				return false;
			}
			oldTime = newTime;
		}
		return true;
	}

	public void showOnUiThread(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showMessage(str);
			}
		});
	}
}
