package retrofit.base.mvp;

/**
 * @description: IBaseView 定义常用的方法，其他页面View接口继承基类
 * @data 2018/1/25-11:30
 * @author: AoJiaoQiang
 */
public interface IBaseView {
    /**
     * 显示loading
     */
    void showLoading();

    /**
     * 关闭loading
     */
    void closeLoading();

    /**
     * 显示toast
     *
     * @param msg
     */
    void showToast(String msg);
}
