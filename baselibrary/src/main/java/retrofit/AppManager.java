package retrofit;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * activity堆栈式管理
 */
public class AppManager {

    private static List<Activity> activityList = null;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
            activityList = new ArrayList<Activity>();
        }
        return instance;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }


    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityList.size() <= 0 ? null : activityList.get(activityList.size() - 1);
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityList.get(activityList.size() - 1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityList != null && activityList.size() != 0) {
            activityList.remove(activity);

            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityList != null && activityList.size() != 0) {
            for (Activity activity : activityList) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                    break;
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityList.size(); i < size; i++) {
            if (null != activityList.get(i)) {
                finishActivity(activityList.get(i));
                break;
            }
        }
        activityList.clear();
    }
    /**
     * 结束所有Activity
     */
    public void finishExitAllActivity() {
        for (int i = 0, size = activityList.size(); i < size; i++) {
            if (null != activityList.get(i)) {
                finishActivity(activityList.get(i));
                break;
            }
        }
        activityList.clear();
        System.exit(0);
    }

    public Activity getActivity(Activity act){
        if (activityList != null && activityList.size() != 0){
            for (Activity activity : activityList){
                if (act == activity){
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public Activity getActivity(Class<?> cls) {
        if (activityList != null && activityList.size() != 0)
            for (Activity activity : activityList) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }


    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        if (activityList != null) {
            for (Activity ac : activityList) {
                if (!ac.isFinishing()) {
                    ac.finish();
                }
            }
        }
        if (context != null) {
            // 通知
            ((NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE))
                    .cancelAll();
            System.exit(0);
        }
        // 杀死程序
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}