package retrofit.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间工具类
 */
public class DateUtil {

    /**
     * 获取24小时格式的时间
     *
     * @param format
     * @return
     */
    public static String get24Time(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 获取格式化的时间
     *
     * @param time      时间字符串 比如2013-08-13 15:41
     * @param isEverDay 是否格式化每一天
     * @return 返回 time:今天 15:41 time:昨天 15:45  time:08-11 15:43
     */
    @SuppressWarnings("unused")
    public static String formatDateTime(String time, boolean isEverDay) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (time == null || "".equals(time)) {
            return "";
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        if (current.after(today)) {
            return "今天 " + time.split(" ")[1];
        } else if (current.before(today) && current.after(yesterday)) {

            return "昨天 " + time.split(" ")[1];
        } else {
            if (isEverDay) {
                int index = time.indexOf("-") + 1;
                return time.substring(index, time.length());
            } else {
                return null;
            }
        }
    }


    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * 将Long转换成string时间
     *
     * @param currentTime
     * @param formatType  formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * @return
     */
    public static String longToString(long currentTime, String formatType) {
        Date date;
        String strTime = null;
        try {
            date = longToDate(currentTime, formatType);
            strTime = dateToString(date, formatType); // date类型转成String
        } catch (ParseException e) {
            e.printStackTrace();
        } // long类型转成Date类型
        return strTime;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    public static long stringToLong2(String strTime, String formatType) {
        Date date = null; // String类型转成date类型
        try {
            date = stringToDate(strTime, formatType);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static String numToDate(int number, String formatType) {
        Date date = new Date(number);
        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(date);
    }

    /**
     * 计算两个日期的时间差
     *
     * @param startTime "yyyy-MM-dd"
     * @param endTime   "yyyy-MM-dd"
     * @return
     */
    public static Long timePoor(String startTime, String endTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long start = 0;
        long end = 0;
        try {
            start = df.parse(startTime).getTime();
            end = df.parse(endTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long poor = (end - start);
        if (poor < 0) {
            return 0L;
        } else {
            return poor / (1000 * 60 * 60 * 24);
        }

    }

    /**
     * 获取系统当前时间
     *
     * @param formatType 格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * @return
     */
    public static String getSysTime(String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        String time = formatter.format(getSysTime());
        return time;
    }

    /**
     * 获取系统的当前时间 Date
     *
     * @return
     */
    public static Date getSysTime() {
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return curDate;
    }

    public static Long getSysTimeL(){
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间的月份
     *
     * @return
     */
    public static int getMonth() {
        Calendar c = Calendar.getInstance();
        // 系统取出的月份是从0开始-11月
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * .
     * 获取当前所处的年份
     *
     * @return
     */
    public static int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取当前连月日周几
     *
     * @return
     */
    public static String StringData() {
        String mYear;
        String mMonth;
        String mDay;
        String mWay;
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return mYear + "年" + mMonth + "月" + mDay + "日" + "/星期" + mWay;
    }


    /**
     * 日期变量转成对应的星期字符串
     *
     * @param date
     * @return
     */

    public static final int WEEKDAYS = 7;
    public static String[] WEEKS = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 把时间转换成星期几
     *
     * @param date
     * @return 星期天
     */
    public static String DateToWeeks(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }
        return WEEKS[dayIndex - 1];
    }

    /**
     * 获得指定日期的前一天yy-MM-dd
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String format, String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }

    /**
     * 获得指定日期的后一天yy-MM-dd
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String format, String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat(format).format(c.getTime());
        return dayAfter;
    }

    /**
     * 获得指定日期的上个月yy-MM-dd
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedMonthBefore(String format, String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        // 当前时间前去一个月，即一个月前的时间
        c.add(Calendar.MONTH, -1);
        String dayAfter = new SimpleDateFormat(format).format(c.getTime());
        return dayAfter;
    }

    /**
     * 获得指定日期的下个月yy-MM-dd
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedMonthAfter(String format, String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        // 当前时间前去一个月，即一个月前的时间
        c.add(Calendar.MONTH, +1);
        String dayAfter = new SimpleDateFormat(format).format(c.getTime());
        return dayAfter;
    }

    /**
     * 获取当前时间的前一天
     *
     * @return
     */
    public static Date getNextDay() {
        Date date = new Date(System.currentTimeMillis());// 获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }


    /**
     * 根据日期获取出是周几
     *
     * @param times yyyy-MM-dd
     * @return
     */
    public static String getWeeks(String formats, String times) {
        String weeks = "";
        SimpleDateFormat format = new SimpleDateFormat(formats);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(times));
            String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
            if ("1".equals(mWay)) {
                mWay = "日";
            } else if ("2".equals(mWay)) {
                mWay = "一";
            } else if ("3".equals(mWay)) {
                mWay = "二";
            } else if ("4".equals(mWay)) {
                mWay = "三";
            } else if ("5".equals(mWay)) {
                mWay = "四";
            } else if ("6".equals(mWay)) {
                mWay = "五";
            } else if ("7".equals(mWay)) {
                mWay = "六";
            }
            weeks = "星期" + mWay;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weeks;
    }
}
