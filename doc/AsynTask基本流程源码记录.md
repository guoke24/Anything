### 此文档的目的，找到以下几个函数在父类调用的地方

* 1,onPreExecute
* 2,onPostExecute
* 3,onProgressUpdate
* 4,doInBackground
* 5,onCancelled
* 6,onCancelled(String)

#### 起点
代码中调用
```
iAsynTask.execute("test8");
```

来到 //AsyncTask.java
代码段2
```
@MainThread
    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }
```

先关注，sDefaultExecutor 是什么。
来到 //AsyncTask.java
代码段3
```
public static final Executor SERIAL_EXECUTOR = new SerialExecutor();
......
private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;
```
代码段4
```
    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }
```

接着回到代码段2，可知 sDefaultExecutor 是 SerialExecutor 类的实例。
接着，来看函数：executeOnExecutor(sDefaultExecutor, params);
代码段5
```
    @MainThread
    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
            Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        mStatus = Status.RUNNING;

        onPreExecute();//5-1 回调 1,onPreExecute

        mWorker.mParams = params;
        exec.execute(mFuture); //5-2 即 sDefaultExecutor.execute(mFuture) 对应 代码段4

        return this;
    }
```
由5-1处，结合代码段4，可知，mFuture 的 run 函数会被执行。接着就去找它的 run 函数：
代码段6
```
    mFuture = new FutureTask<Result>(mWorker) { //6-1
                @Override
                protected void done() {
                    try {
                        postResultIfNotInvoked(get());
                    } catch (InterruptedException e) {
                        android.util.Log.w(LOG_TAG, e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException("An error occurred while executing doInBackground()",
                                e.getCause());
                    } catch (CancellationException e) {
                        postResultIfNotInvoked(null);
                    }
                }
            };
```
6_2
```
    public class FutureTask<V> implements RunnableFuture<V>

    //构造函数
    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }
```
6_3
```
    //
        public void run() {
            ....
            try {
                Callable<V> c = callable;
                if (c != null && state == NEW) {
                    V result;
                    boolean ran;
                    try {
                        result = c.call();// 6_3-1 实际调用 callable 的 call 函数
                        ran = true;
                    } catch (Throwable ex) {
                        result = null;
                        ran = false;
                        setException(ex);
                    }
                    if (ran)
                        set(result);
                }
            } finally {
                ......
            }
        }

```
由此可知，实际调用的是代码段6的6-1处的 mWorker 实例的 call 函数，接着看
代码段7
```
    mWorker = new WorkerRunnable<Params, Result>() {
                public Result call() throws Exception {
                    mTaskInvoked.set(true);
                    Result result = null;
                    try {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        //noinspection unchecked
                        result = doInBackground(mParams); // 调用 4,doInBackground
                        Binder.flushPendingCommands();
                    } catch (Throwable tr) {
                        mCancelled.set(true);
                        throw tr;
                    } finally {
                        postResult(result);
                    }
                    return result;
                }
            };
```
代码段7_1
```
    private Result postResult(Result result) {
        @SuppressWarnings("unchecked")
        Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result));// 7_1-1
        message.sendToTarget();
        return result;
    }

    private Handler getHandler() {
            return mHandler;
    }

    mHandler = callbackLooper == null || callbackLooper == Looper.getMainLooper()
            ? getMainHandler()
            : new Handler(callbackLooper);

    private static Handler getMainHandler() {
        synchronized (AsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler(Looper.getMainLooper());
            }
            return sHandler;
        }
    }

```
代码段7_2
```
    private static class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;//7_2-1 此处 result 就是 7_1-1处 的 AsyncTaskResult
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);//7_2-2 AsyncTaskResult 的 mTask 就是 AsyncTask，由 代码段7_3 可知
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);//7_2-3 调用了 3,onProgressUpdate
                    break;
            }
        }
    }
```

代码段7_3
```
    private static class AsyncTaskResult<Data> {
        final AsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(AsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }
```

根据 7_2-2 处，接着看 finish 函数
代码段8
```
    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);//8-1 调用了 6,onCancelled(String)
        } else {
            onPostExecute(result);//8-2 调用了 2,onPostExecute
        }
        mStatus = Status.FINISHED;
    }

    protected void onCancelled(Result result) {
        onCancelled();//8-3 调用了 5,onCancelled(String)
    }
```

至此，可以得出结论：
* 1,onPreExecute  在 代码段5 的 5-1 ，主线程，也成UI线程
* 2,onPostExecute 在 代码段8 的 8-2 ，InternalHandler 的 handleMessage函数中调用，UI线程
* 3,onProgressUpdate 在 代码段7_2 的 7_2-3，InternalHandler 的 handleMessage函数中调用，UI线程
* 4,doInBackground   在 代码段7_2 的 7_2-2，WorkerRunnable 类的 call 函数中调用，子线程
* 5,onCancelled   在 代码段8 的 8-1 ，InternalHandler 的 handleMessage函数中调用，UI线程
* 6,onCancelled(String) 在 代码段8 的 8-3 ，InternalHandler 的 handleMessage函数中调用，UI线程

