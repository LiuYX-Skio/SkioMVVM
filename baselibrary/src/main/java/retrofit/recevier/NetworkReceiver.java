package retrofit.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @class describe 监控网络的广播
 * @anthor aojiaoqiang
 * @time 2018/8/17 17:48
 */
public class NetworkReceiver extends BroadcastReceiver {
    NetWorkChangeListener mListener;

    public NetworkReceiver(NetWorkChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener == null)
            return;
        // 判断当前网络是否可用
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            //当前网络状态可用
            mListener.onNetWorkChange(true);
        } else {
            //当前网络不可用
            mListener.onNetWorkChange(false);
        }
    }

    public interface NetWorkChangeListener {
        /**
         * 网络状态改变
         *
         * @param isConnect true 网络可用 false网络不可用
         */
        void onNetWorkChange(boolean isConnect);
    }
}
