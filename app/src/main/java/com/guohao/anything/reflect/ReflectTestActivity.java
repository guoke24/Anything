package com.guohao.anything.reflect;


import android.os.Bundle;
import android.view.View;

import com.guohao.anything.BaseTestActivity;
import com.guohao.anything.R;

import java.lang.reflect.Field;
//参考：https://blog.csdn.net/kai_zone/article/details/80217219
public class ReflectTestActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_reflect_test);
        super.onCreate(savedInstanceState);

    }

    public void test_1(View v){
        showMessage("test1");
        try {
            Test5();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demo3: 通过Java反射机制，用Class 创建类对象[这也就是反射存在的意义所在]
     */
    public static void Test3() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> class1 = null;
        class1 = Class.forName("com.guohao.anything.reflect.Person");
        //由于这里不能带参数，所以你要实例化的这个类Person，一定要有无参构造函数
        Person person = (Person) class1.newInstance();
        person.setAge(26);
        person.setName("kaiven");
        System.out.println("Test3: " + person.getName() + " : " + person.getAge());
    }

    /**
     * Demo5: 通过Java反射机制操作成员变量, set 和 get
     */
    public static void Test5() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException, InstantiationException, ClassNotFoundException {
        Class<?> class1 = null;
        class1 = Class.forName("com.guohao.anything.reflect.Person");
        Object obj = class1.newInstance();

        Field nameField = class1.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(obj, "cyy");

        System.out.println("Test5: 修改属性之后得到属性变量的值：" + nameField.get(obj));

    }

}
