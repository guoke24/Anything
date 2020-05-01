# 说明

- ConstrainLayoutActivity 

   - 其布局文件 R.layout.activity_constrain_layout 是测试 ConstrainLayout 基本属性的
  
   - 在 布局文件 R.layout.activity_constrain_layout 内，引入 RectView，测试自定义View
  
      - RectView，参考于 [刘望舒 Android View体系（九）自定义View ](http://liuwangshu.cn/application/view/9-custom-view.html) 
     
         - 还用到自定义属性 values/attrs.xml 中的 rect_color 
        
        
- CanvasActivity
    - 用于测试 Canvas.clipRect 避免重复绘制的问题
    - RectView2，在 onDraw 中使用 canvas 测试 Canvas.clipRect
        - 顺便了解 Bitmap 的简单使用，参考：Bitmap详解与Bitmap的内存优化


- CusViewActivity

    - 测试类 CusLinearLayout 和 CusView 两个自定义类
    
    - CusView 实现类拖动的功能
    
    - CusLinearLayout 重写 onMeasure 输出一些参数而已
 
 
- scrollConflict包

    - 内涵一个滑动冲突解决案例


- RunWithoutUI
   
    - 可直接运行的类，用于测试 View 类中 MeasureSpace 的相关运算
   

- ViewUtils

     - 主要是一个打印 ViewTree 功能的函数，
        - MyActivity 中测试类该功能
    
     - 还有 布局参数代码转文字
   