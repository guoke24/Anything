前言：
进入函数： SystemServer 类的函数：startOtherServices：
代码段0:
/frameworks/base/services/java/com/android/server/SystemServer.java
```
private void startOtherServices() {
	......
	mActivityManagerService.systemReady(new Runnable() {
            @Override
            public void run() {
                Slog.i(TAG, "Making services ready");
		......
		try {
                    startSystemUi(context);//0-1
                } catch (Throwable e) {
                    reportWtf("starting System UI", e);
                }
		......
	    }
	}
	......
}
```
//0-1,进入函数：startSystemUi(context)
代码段0-1:
/frameworks/base/services/java/com/android/server/SystemServer.java
```
    static final void startSystemUi(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.systemui",//0-1-1
                    "com.android.systemui.SystemUIService"));
        intent.addFlags(Intent.FLAG_DEBUG_TRIAGED_MISSING);
        //Slog.d(TAG, "Starting service: " + intent);
        context.startServiceAsUser(intent, UserHandle.SYSTEM);
    }
```
根据//0-0-1处，找到
/frameworks/base/packages/SystemUI/AndroidManifest.xml
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:androidprv="http://schemas.android.com/apk/prv/res/android"
        package="com.android.systemui"
        android:sharedUserId="android.uid.system"
        coreApp="true">
```
由此可知 SystemUI 的源码位置为：/frameworks/base/packages/SystemUI




启动：
代码段1:
/frameworks/base/packages/SystemUI/src/com/android/systemui/SystemUIService.java
```
    @Override
    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication) getApplication()).startServicesIfNeeded();//1-1
	......
    }
```

//1-1，进入函数：startServicesIfNeeded()
代码段1-1:
/frameworks/base/packages/SystemUI/src/com/android/systemui/SystemUIApplication.java
```
    public void startServicesIfNeeded() {
        startServicesIfNeeded(SERVICES);//1-1-0，传进服务类
    }
```
```
    private void startServicesIfNeeded(Class<?>[] services) {
	......

        final int N = services.length;
	// 循环
        for (int i=0; i<N; i++) {
            Class<?> cl = services[i];//1-1-1 循环的取服务的类名
            if (DEBUG) Log.d(TAG, "loading: " + cl);
            try {
                Object newService = SystemUIFactory.getInstance().createInstance(cl);
		//1-1-2 创建服务
                mServices[i] = (SystemUI) ((newService == null) ? cl.newInstance() : newService);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            }

            mServices[i].mContext = this;
            mServices[i].mComponents = mComponents;
            if (DEBUG) Log.d(TAG, "running: " + mServices[i]);
            mServices[i].start(); //1-1-3 启动服务

            if (mBootCompleted) {
                mServices[i].onBootCompleted();
            }
        }
        mServicesStarted = true;
    }
```
//1-1-1处，services[i]来自于入参，追溯到//1-1-0处，函数：startServicesIfNeeded(SERVICES)，发现 services[i] 就是这个类：SERVICES
查看 SERVICES 类的代码：
代码段1-1-0：
/frameworks/base/packages/SystemUI/src/com/android/systemui/SystemUIApplication.java
```
private final Class<?>[] SERVICES = new Class[] { com.android.systemui.tuner.TunerService.class,//定制service暂时不清楚干什么的，没用过
        com.android.systemui.keyguard.KeyguardViewMediator.class,//锁屏
        com.android.systemui.recents.Recents.class,//最近进程
        com.android.systemui.volume.VolumeUI.class,//音量控制
        Divider.class,//好像是分屏
        com.android.systemui.statusbar.SystemBars.class,//系统状态栏，其中的重点服务
        com.android.systemui.usb.StorageNotification.class,//存储通知
        com.android.systemui.power.PowerUI.class,//电量
        com.android.systemui.media.RingtonePlayer.class,//铃声播放
        com.android.systemui.keyboard.KeyboardUI.class,//键盘相关，没用过
        com.android.systemui.tv.pip.PipUI.class,//暂时不清楚没用过
        com.android.systemui.shortcut.ShortcutKeyDispatcher.class//这个也没用过
};
```
到此可知，代码段1-1的 startServicesIfNeeded 函数，就是循环的把 SERVICES 内的服务都启动起来，通过调用其 start() 函数。
接着追踪系统状态栏：SystemBars.class 的流程，从函数：start() 开始：

代码段2:
/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/SystemBars.java
```
    @Override
    public void start() {
        if (DEBUG) Log.d(TAG, "start");
        mServiceMonitor = new ServiceMonitor(TAG, DEBUG,
                mContext, Settings.Secure.BAR_SERVICE_COMPONENT, this);
	//2-1
        mServiceMonitor.start();  // will call onNoService if no remote service is found
    }
```

 代码段3
/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/ServiceMonitor.java
```
    public void start() {
	......
        mHandler.sendEmptyMessage(MSG_START_SERVICE);
    }
```
```
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_START_SERVICE:
                    startService();
                    break;
                case MSG_CONTINUE_START_SERVICE:
                    continueStartService();
                    break;
                case MSG_STOP_SERVICE:
                    stopService();
                    break;
                case MSG_PACKAGE_INTENT:
                    packageIntent((Intent)msg.obj);
                    break;
                case MSG_CHECK_BOUND:
                    checkBound();
                    break;
                case MSG_SERVICE_DISCONNECTED:
                    serviceDisconnected((ComponentName)msg.obj);
                    break;
            }
        }
    };
```
```
    private void startService() {
        mServiceName = getComponentNameFromSetting();
        if (mDebug) Log.d(mTag, "startService mServiceName=" + mServiceName);
        if (mServiceName == null) {
            mBound = false;
            mCallbacks.onNoService();//3-1
        } else {
            long delay = mCallbacks.onServiceStartAttempt();
            mHandler.sendEmptyMessageDelayed(MSG_CONTINUE_START_SERVICE, delay);
        }
    }
```
由代码段2的//2-1，可知会走//3-1处的逻辑：mCallbacks.onNoService()，mCallbacks 就是指向 SystemBars，
于是回到 SystemBars类的函数： onNoService，代码如下：
代码段4:
```
    @Override
    public void onNoService() {
        if (DEBUG) Log.d(TAG, "onNoService");
        createStatusBarFromConfig();  // fallback to using an in-process implementation
    }
```
```
    private void createStatusBarFromConfig() {
        if (DEBUG) Log.d(TAG, "createStatusBarFromConfig");
        final String clsName = mContext.getString(R.string.config_statusBarComponent);
        // config_statusBarComponent 对应的值：com.android.systemui.statusbar.phone.PhoneStatusBar

        if (clsName == null || clsName.length() == 0) {
            throw andLog("No status bar component configured", null);
        }
        Class<?> cls = null;
        try {
            cls = mContext.getClassLoader().loadClass(clsName);// 加载到类：PhoneStatusBar
        } catch (Throwable t) {
            throw andLog("Error loading status bar component: " + clsName, t);
        }
        try {
            mStatusBar = (BaseStatusBar) cls.newInstance();// 新建类：PhoneStatusBar
        } catch (Throwable t) {
            throw andLog("Error creating status bar component: " + clsName, t);
        }
        mStatusBar.mContext = mContext;
        mStatusBar.mComponents = mComponents;
        mStatusBar.start();//4-3 调用 PhoneStatusBar类的函数：start()
        if (DEBUG) Log.d(TAG, "started " + mStatusBar.getClass().getSimpleName());
    }
```
由//4-3处，可知调用 PhoneStatusBar类的函数：start()，

代码段5:
/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/PhoneStatusBar.java
```
    public void start() {
	......
        super.start(); // calls createAndAddWindows()
	......
	addNavigationBar();// 设置导航栏
	......
	startKeyguard();// 设置锁屏
    }
```
PhoneStatusBar的父类是BaseStatusBar，进入其start()函数：
代码段5-1:
/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/BaseStatusBar.java
```
    public void start() {
        createAndAddWindows();	
    }

    protected abstract void createAndAddWindows();
```
实际调用的是子类的 createAndAddWindows() 函数：
代码段5-2:
/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/phone/PhoneStatusBar.java
```
    @Override
    public void createAndAddWindows() {
	......
        addStatusBarWindow();
    }
```
```
    private void addStatusBarWindow() {
        makeStatusBarView();//5-2-1
        mStatusBarWindowManager = new StatusBarWindowManager(mContext);
        mRemoteInputController = new RemoteInputController(mStatusBarWindowManager,
                mHeadsUpManager);
        mStatusBarWindowManager.add(mStatusBarWindow, getStatusBarHeight());
    }
```
```
    // ================================================================================
    // Constructing the view
    // ================================================================================
    protected PhoneStatusBarView makeStatusBarView() {
	final Context context = mContext;

	......

        inflateStatusBarWindow(context);//5-2-2，赋值给变量 mStatusBarWindow

	......

        mNotificationPanel = (NotificationPanelView) mStatusBarWindow.findViewById(
                R.id.notification_panel);
        mNotificationPanel.setStatusBar(this);
        mNotificationPanel.setGroupManager(mGroupManager);

        mStatusBarView = (PhoneStatusBarView) mStatusBarWindow.findViewById(R.id.status_bar);
        mStatusBarView.setBar(this);
        mStatusBarView.setPanel(mNotificationPanel);


        // Set up the quick settings tile panel
        AutoReinflateContainer container = (AutoReinflateContainer) mStatusBarWindow.findViewById(
                R.id.qs_auto_reinflate_container);
        if (container != null) {
            final QSTileHost qsh = SystemUIFactory.getInstance().createQSTileHost(mContext, this,
                    mBluetoothController, mLocationController, mRotationLockController,
                    mNetworkController, mZenModeController, mHotspotController,
                    mCastController, mFlashlightController,
                    mUserSwitcherController, mUserInfoController, mKeyguardMonitor,
                    mSecurityController, mBatteryController, mIconController,
                    mNextAlarmController,
                    /// M: add HotKnot in quicksetting
                    mHotKnotController, mFontController);

            mBrightnessMirrorController = new BrightnessMirrorController(mStatusBarWindow);

            container.addInflateListener(new InflateListener() {
                @Override
                public void onInflated(View v) {
                    QSContainer qsContainer = (QSContainer) v.findViewById(
                            R.id.quick_settings_container);
                    qsContainer.setHost(qsh);
                    mQSPanel = qsContainer.getQsPanel();
                    mQSPanel.setBrightnessMirror(mBrightnessMirrorController);
                    mKeyguardStatusBar.setQSPanel(mQSPanel);
                    mHeader = qsContainer.getHeader();
                    initSignalCluster(mHeader);
                    mHeader.setActivityStarter(PhoneStatusBar.this);
                }
            });
        }

	......

        return mStatusBarView;
    }
```
//5-2-2，
```
    protected void inflateStatusBarWindow(Context context) {
        mStatusBarWindow = (StatusBarWindowView) View.inflate(context,
                R.layout.super_status_bar, null);
    }
```

R.layout.super_status_bar 对应的xml文件为：super_status_bar.xml，其代码如下所示：
代码段6:
```
<!-- This is the combined status bar / notification panel window. -->
<com.android.systemui.statusbar.phone.StatusBarWindowView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:sysui="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <com.android.systemui.statusbar.BackDropView
            android:id="@+id/backdrop"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:visibility="gone"
            sysui:ignoreRightInset="true"
            >
        <ImageView android:id="@+id/backdrop_back"
                   android:layout_width="match_parent"
                   android:scaleType="centerCrop"
                   android:layout_height="match_parent" />
        <ImageView android:id="@+id/backdrop_front"
                   android:layout_width="match_parent"
                   android:layout_height="match_parent"
                   android:scaleType="centerCrop"
                   android:visibility="invisible" />
    </com.android.systemui.statusbar.BackDropView>

    <com.android.systemui.statusbar.ScrimView android:id="@+id/scrim_behind"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:importantForAccessibility="no"
        sysui:ignoreRightInset="true"
        />

    <com.android.systemui.statusbar.AlphaOptimizedView
        android:id="@+id/heads_up_scrim"
        android:layout_width="match_parent"
        android:layout_height="@dimen/heads_up_scrim_height"
        android:background="@drawable/heads_up_scrim"
        sysui:ignoreRightInset="true"
        android:importantForAccessibility="no"/>

    <include layout="@layout/status_bar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/status_bar_height" />

    <include layout="@layout/brightness_mirror" />

    <ViewStub android:id="@+id/fullscreen_user_switcher_stub"
              android:layout="@layout/car_fullscreen_user_switcher"
              android:layout_width="match_parent"
              android:layout_height="match_parent"/>

    <include layout="@layout/status_bar_expanded"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone" />

    <com.android.systemui.statusbar.ScrimView android:id="@+id/scrim_in_front"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:importantForAccessibility="no"
        sysui:ignoreRightInset="true"
        />

</com.android.systemui.statusbar.phone.StatusBarWindowView>
```
代码段6中，可以看到 include 了 status_bar 和 status_bar_expanded 的布局文件

关于 status_bar：
/frameworks/base/packages/SystemUI/res/layout/status_bar.xml
<!-- 参考：https://img-blog.csdn.net/2018050218372435?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI4ODUyMDEx/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 -->
<com.android.systemui.statusbar.phone.PhoneStatusBarView
    ......
    android:id="@+id/status_bar"

==========================================================================================

关于 status_bar_expanded：
/frameworks/base/packages/SystemUI/res/layout/status_bar_expanded.xml
<!-- 参考: https://upload-images.jianshu.io/upload_images/2935925-a5ad048cee9865b9.png?imageMogr2/auto-orient/ -->
<!-- (这个更准切)参考: https://img-blog.csdn.net/20180503110757621?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI4ODUyMDEx/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 -->
<com.android.systemui.statusbar.phone.NotificationPanelView 
    ......
    android:id="@+id/notification_panel"

==========================================================================================
