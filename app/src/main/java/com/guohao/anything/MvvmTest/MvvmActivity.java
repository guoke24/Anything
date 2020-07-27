package com.guohao.anything.MvvmTest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.guohao.anything.R;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class MvvmActivity extends AppCompatActivity {

    //ActivityMainBinding是系统自动生成的
    //private ViewDataBinding viewDataBinding;
    //ActivityMvvmBinding activityMvvmBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvvm);
        //activityMvvmBinding = DataBindingUtil.setContentView(this, R.layout.activity_mvvm);

        //User user = new User("guohao");
        //activityMvvmBinding.setUser(user);

        //SharedPreferences sharedPreferences = getSharedPreferences();

        ReentrantLock reentrantLock;

        String s;
        StringBuilder s2;
        StringBuffer s3;

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        readWriteLock.readLock().lock();

        LiveData<Integer> num = new MutableLiveData<>();

    }

    public void test_1(View v){

    }

    public void test_2(View v){

    }

    class NodeList{

        Node head = new Node();
        Node tail = new Node();

        public void insert(Node node){
            tail.next = node;
            node.prev = tail;
            tail = node;
        }

        public Node find(int i){

            Node temp = head;

            if( head.i != i ){
                temp = temp.next;
                if(temp == null){
                    return null;
                }
            }

            return temp;
        }

        public void delete(Node node){
            // 先遍历，找到前节点，再删除
        }
    }

    class Node{
        int i;
        Node prev;
        Node next;
    }

}
