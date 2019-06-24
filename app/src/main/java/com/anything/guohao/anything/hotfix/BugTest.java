package com.anything.guohao.anything.hotfix;

import android.content.Context;
import android.widget.Toast;

public class BugTest {
    public void getBug(Context context) {
        //模拟一个bug
        int i = 10;
        int a = 0;
        // a=1;
        // 补丁中的加入了上一行代码，修复了bug
        // 制作补丁的办法：
        // 修复bug后，rebuild项目，
        // 在该路径： app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes/com/anything/guohao/anything/hotfix
        // 找到：BugTest.class，然后把 com/anything/guohao/anything/hotfix/BugTest.class 复制到任意路径：*/hotfix
        // 注意一定要保有原来的文件路径结构
        // 然后在该路径 {sdk根路径}/build-tools/28.0.3
        // 找到：dx，这是一个可执行脚本
        // 执行命令，将BugTest.class打包为classes.dex
        // 在 */hotfix 路径下的终端，
        // 执行命令：{sdk根路径}/build-tools/28.0.3/dx --dex --output */hotfix/classes.dex */hotfix/
        // 之后会在 */hotfix/的路径下，生成打包好的文件补丁：classes.dex
        Toast.makeText(context, "Hello:" + i / a, Toast.LENGTH_SHORT).show();
    }
}
