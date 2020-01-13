package com.guohao.anything.viewtest;

import android.view.View;
import android.view.ViewGroup;

import com.guohao.anything.LogUtil;

public class ViewUtils {

    public static void listViewTree(View v){
        LogUtil.e("第0层：" + v.getClass().getName() );
        listViewTree(v,1);
    }

    private static void listViewTree(View vg,int layNum){

        if(vg instanceof ViewGroup){ }else{
            return;
        }

        int temp = layNum;

        //加横线
        StringBuilder line = new StringBuilder();
        if(((ViewGroup) vg).getChildCount()>0){
            for(int i = 1;i <= temp;i++){
                line.append("-");
            }
            //Log.d("guohao-v",line.toString());
        }

        // 加退格
        StringBuilder space = new StringBuilder();
        for(int i = 1;i <= temp;i++){
            space.append(" ");
        }

        for(int i = 0; i < ((ViewGroup) vg).getChildCount(); i++){
            View v = ((ViewGroup) vg).getChildAt(i);
//            LogUtil.e( "   \n");
//            LogUtil.e( line.toString() + "第" + temp + "层，" + "第" + i + "次循环：" + getLastName(v.getClass().getName())
//                    + "，w = " + lpCode2Str(v.getLayoutParams().width)
//                    + "，h = " + lpCode2Str(v.getLayoutParams().height) );

            LogUtil.e( line.toString() + "第" + temp + "层，" + "第" + (i + 1) + "个视图：" + getLastName(v.getClass().getName())
                     );

            listViewTree(v,temp + 1);
        }
    }

    // 布局中，-1 表示 match_parent，-2 表示 wrap_content
    public static String lpCode2Str(int i){
        if(i == -1){
            return "match_parent";
        }else if(i == -2){
            return "wrap_content";
        }else{
            return i + "";
        }

    }

    public static String MsCode2Str(int i){
        if(i == 1){
            return "EXACTLY";
        }else if(i == 2){
            return "AT_MOST";
        }else{
            return "UNSPECIFIED";
        }
    }

    public static void Ms2Str(int measureSpec,String tag){
        int mode = View.MeasureSpec.getMode(measureSpec) >>> 30;// 无符号的右移位运算
        int size = View.MeasureSpec.getSize(measureSpec);
        LogUtil.e(tag + " mode = " + MsCode2Str(mode));
        LogUtil.e(tag + " size = " + size);
    }


    private static String getLastName(String s){
        String ss[] = s.split("\\."); // 直接用"."会被当初正则表达式
        if(ss.length == 0) return "";
        return ss[ss.length - 1];
    }

}
