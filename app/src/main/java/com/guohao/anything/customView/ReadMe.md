# 画一个小黄人

项目源码参考于: [Android Canvas 绘制小黄人](https://github.com/samwangds/AndroidInterview/blob/master/Android%20Canvas%20%E7%BB%98%E5%88%B6%E5%B0%8F%E9%BB%84%E4%BA%BA.md)

点击文件夹图标时，会报错：

```
java.lang.IllegalStateException: ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)
        at android.support.v7.widget.RecyclerView$Adapter.createViewHolder(RecyclerView.java:6796)
```

注释下面代码的 `parent.addView(minionView)` 即可：

```
    private static class MainAdapter extends RecyclerView.Adapter<MinionsHolder> {

        @Override
        public MinionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final MinionView minionView = new MinionView(parent.getContext());
            //parent.addView(minionView);
            return new MinionsHolder(minionView);
        }
```