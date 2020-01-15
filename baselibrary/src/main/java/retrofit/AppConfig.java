package retrofit;


import java.io.File;

import retrofit.utils.FileUtil;

/**
 * app的配置
 * Created by aojiaoqiang on 2018/2/5.
 */

public class AppConfig {
    public static final String APP_PATH = FileUtil.getSDRoot()+ "skio_base" + File.separator;
    public static final String IMAGE_PATH = APP_PATH + "image" + File.separator;
    public static final String LOG_PATH = APP_PATH + "log" + File.separator;
}
