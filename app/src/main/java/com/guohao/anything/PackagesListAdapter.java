package com.guohao.anything;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohao.anything.R;

import java.util.ArrayList;
import java.util.List;

public class PackagesListAdapter extends RecyclerView.Adapter<PackagesListAdapter.MyViewHolder> {

    List<PackageInfo> packageInfoList;
    List<ApplicationInfo> applicationInfoList;
    PackageManager mPm;
    Context mContext;

    public PackagesListAdapter(PackageManager mPm, Context context) {
        this.mPm = mPm;
        //packageInfoList = mPm.getInstalledPackages(0);
        this.packageInfoList = getAllApps(mPm);
        //applicationInfoList = mPm.getInstalledApplications(0);
        this.mContext = context;
    }

    // 每一行的item，用这个类保存
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View rootView;
        TextView textView;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            rootView = itemView;
            // 绑定每一行item内的控件
            textView = itemView.findViewById(R.id.text_view);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }


    // 创建每一行
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_1, parent, false);
        return new MyViewHolder(v);//一行一个MyViewHolder
    }

    // 设置每一行
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        String appName = (String) mPm.getApplicationLabel(packageInfoList.get(position).applicationInfo);
        holder.textView.setText(appName);

        Drawable drawable = mPm.getApplicationIcon(packageInfoList.get(position).applicationInfo);
        holder.imageView.setImageDrawable(drawable);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getLayoutPosition();
                //unInstallApp(packageInfoList.get(pos).packageName);
                startApp(packageInfoList.get(pos).packageName);

            }
        });
    }

    /**
     * 启动app
     * @param packageName
     */
    public void startApp(String packageName){
        try {

            Intent minIntent = mContext.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            mContext.startActivity(minIntent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 卸载app
     * @param packageName
     */
    public void unInstallApp(String packageName){
        Uri uri = Uri.fromParts("package", packageName , null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        //mContext.startActivity(intent);
    }


    // 一共多少行
    @Override
    public int getItemCount() {
        return packageInfoList.size();
    }


    public static List<PackageInfo> getAllApps(PackageManager pManager) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();

        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
//            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
//                // 0 & 1 = 0
//                // 0 | 1 = 1
//
//                // customs applications
//                apps.add(pak);
//            }
            apps.add(pak);
        }
        return apps;
    }


    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


}
