/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skio.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.ViewCompat;

import com.lyx.skio.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Helper for layouts that want to support percentage based dimensions.
 * <p/>
 * <p>This class collects utility methods that are involved in extracting percentage based dimension
 * attributes and applying them to ViewGroup's children. If you would like to implement a layout
 * that supports percentage based dimensions, you need to take several steps:
 * <p/>
 * <ol>
 * <li> You need a {@link ViewGroup.LayoutParams} subclass in your ViewGroup that implements
 * {@link PxLayoutHelper.PercentLayoutParams}.
 * <li> In your {@code LayoutParams(Context c, AttributeSet attrs)} constructor create an instance
 * of {@link PxLayoutInfo} by calling
 * {@link PxLayoutHelper#getPxLayoutInfo(Context, AttributeSet)}. Return this
 * object from {@code public PercentLayoutHelper.PxLayoutInfo getPxLayoutInfo()}
 * method that you implemented for {@link PxLayoutHelper.PercentLayoutParams} interface.
 * <li> Override
 * {@link ViewGroup.LayoutParams#(TypedArray, int, int)}
 * with a single line implementation {@code PercentLayoutHelper.fetchWidthAndHeight(this, a,
 * widthAttr, heightAttr);}
 * <li> In your ViewGroup override {@link ViewGroup#generateLayoutParams(AttributeSet)} to return
 * your LayoutParams.
 * <li> In your {@link ViewGroup#(int, int)} override, you need to implement following
 * pattern:
 * <pre class="prettyprint">
 * protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 * mHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec);
 * super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 * if (mHelper.handleMeasuredStateTooSmall()) {
 * super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 * }
 * }
 * </pre>
 * <li>In your {@link ViewGroup#(boolean, int, int, int, int)} override, you need to
 * implement following pattern:
 * <pre class="prettyprint">
 * protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
 * super.onLayout(changed, left, top, right, bottom);
 * mHelper.restoreOriginalParams();
 * }
 * </pre>
 * </ol>
 */
public class PxLayoutHelper
{
    private static final String TAG = "PercentLayout";

    private final ViewGroup mHost;

    private static int mWidthScreen;
    private static int mHeightScreen;
    private static int designWidth;
    private static int designHeight;

    public PxLayoutHelper(ViewGroup host)
    {
        mHost = host;
        getScreenSize();
    }

    private void getScreenSize()
    {
        WindowManager wm = (WindowManager) mHost.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidthScreen = outMetrics.widthPixels;
        //分屏保证分辨率以第一次获取分辨率为主不变动
        if(mHeightScreen<outMetrics.heightPixels){
            mHeightScreen = outMetrics.heightPixels;
        }
        designWidth=getIntMetaData(mHost.getContext(),"design_width");
        designHeight=getIntMetaData(mHost.getContext(),"design_height");
        isWidthRatio= getBooleanMetaData(mHost.getContext(),"width_ratio");
        widthRatio = new BigDecimal((float)mWidthScreen/designWidth).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        heightRatio = new BigDecimal((float)mHeightScreen/designHeight).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    //获取Meta-data的值
    public static int getIntMetaData(Context context, String key) {
        int value = 0;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            value = appInfo.metaData.getInt(key);
        } catch (PackageManager.NameNotFoundException e) {//当包名不正确的时候，异常抛出
            e.printStackTrace();
        }
        return value;
    }
    //获取Meta-data的值
    public static boolean getBooleanMetaData(Context context, String key) {
        boolean value = true;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            value = appInfo.metaData.getBoolean(key);
        } catch (PackageManager.NameNotFoundException e) {//当包名不正确的时候，异常抛出
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Helper method to be called from {@link ViewGroup.LayoutParams#} override
     * that reads layout_width and layout_height attribute values without throwing an exception if
     * they aren't present.
     */
    public static void fetchWidthAndHeight(ViewGroup.LayoutParams params, TypedArray array,
                                           int widthAttr, int heightAttr)
    {
        params.width = array.getLayoutDimension(widthAttr, 0);
        params.height = array.getLayoutDimension(heightAttr, 0);
    }

    /**
     * Iterates over children and changes their width and height to one calculated from percentage
     * values.
     *
     * @param widthMeasureSpec  Width MeasureSpec of the parent ViewGroup.
     * @param heightMeasureSpec Height MeasureSpec of the parent ViewGroup.
     */
    public void adjustChildren(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "adjustChildren: " + mHost + " widthMeasureSpec: "
                    + View.MeasureSpec.toString(widthMeasureSpec) + " heightMeasureSpec: "
                    + View.MeasureSpec.toString(heightMeasureSpec));
        }
        int widthHint = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightHint = View.MeasureSpec.getSize(heightMeasureSpec);

//        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "widthHint = " + widthHint + " , heightHint = " + heightHint);

        for (int i = 0, N = mHost.getChildCount(); i < N; i++)
        {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();

            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "should adjust " + view + " " + params);
            }

            if (params instanceof PercentLayoutParams)
            {
                PxLayoutInfo info =
                        ((PercentLayoutParams) params).getPxLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "using " + info);
                }
                if (info != null)
                {
                    supportTextSize(widthHint, heightHint, view, info);
                    supportPadding(widthHint, heightHint, view, info);
                    supportMinOrMaxDimesion(widthHint, heightHint, view, info);

                    if (params instanceof ViewGroup.MarginLayoutParams)
                    {
                        info.fillMarginLayoutParams((ViewGroup.MarginLayoutParams) params,
                                widthHint, heightHint);
                    } else
                    {
                        info.fillLayoutParams(params, widthHint, heightHint);
                    }
                }
            }
        }


    }

    private void supportPadding(int widthHint, int heightHint, View view, PxLayoutInfo info)
    {
        int left = view.getPaddingLeft(), right = view.getPaddingRight(), top = view.getPaddingTop(), bottom = view.getPaddingBottom();
        PxLayoutInfo.PercentVal percentVal = info.paddingLeftPercent;
        if (percentVal != null)
        {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            left = (int) (widthRatio* percentVal.percent);
        }
        percentVal = info.paddingRightPercent;
        if (percentVal != null)
        {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            right = (int) (widthRatio * percentVal.percent);

        }

        percentVal = info.paddingTopPercent;
        if (percentVal != null)
        {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            Log.w("屏幕宽高",""+mHeightScreen+"==="+percentVal.percent);

            if(!isWidthRatio){
                top = (int) (heightRatio * percentVal.percent);
            }else {
                top = (int) (widthRatio * percentVal.percent);
            }
        }

        percentVal = info.paddingBottomPercent;
        if (percentVal != null)
        {
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            if(!isWidthRatio){
                bottom = (int) (heightRatio * percentVal.percent);
            }else {
                bottom = (int) (widthRatio * percentVal.percent);
            }

        }
        view.setPadding(left, top, right, bottom);


    }

    private void supportMinOrMaxDimesion(int widthHint, int heightHint, View view, PxLayoutInfo info)
    {
        try
        {
            Class clazz = view.getClass();
            invokeMethod("setMaxWidth", widthHint, heightHint, view, clazz, info.maxWidthPercent);
            invokeMethod("setMaxHeight", widthHint, heightHint, view, clazz, info.maxHeightPercent);
            invokeMethod("setMinWidth", widthHint, heightHint, view, clazz, info.minWidthPercent);
            invokeMethod("setMinHeight", widthHint, heightHint, view, clazz, info.minHeightPercent);

        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

    }

    private void invokeMethod(String methodName, int widthHint, int heightHint, View view, Class clazz, PxLayoutInfo.PercentVal percentVal) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, methodName + " ==> " + percentVal);
        if (percentVal != null)
        {
            Method setMaxWidthMethod = clazz.getMethod(methodName, int.class);
            setMaxWidthMethod.setAccessible(true);
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            setMaxWidthMethod.invoke(view, (int) (widthRatio * percentVal.percent));
        }
    }

    private void supportTextSize(int widthHint, int heightHint, View view, PxLayoutInfo info)
    {
        //textsize percent support

        PxLayoutInfo.PercentVal textSizePercent = info.textSizePercent;
        if (textSizePercent == null) return;

        int base = getBaseByModeAndVal(widthHint, heightHint, textSizePercent.basemode);
        float textSize = (int) (widthRatio * textSizePercent.percent);

        //Button 和 EditText 是TextView的子类
        if (view instanceof TextView)
        {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    private static int getBaseByModeAndVal(int widthHint, int heightHint, PxLayoutInfo.BASEMODE basemode)
    {
        switch (basemode)
        {
            case BASE_HEIGHT:
                return heightHint;
            case BASE_WIDTH:
                return widthHint;
            case BASE_SCREEN_WIDTH:
                return mWidthScreen;
            case BASE_SCREEN_HEIGHT:
                return mHeightScreen;
        }
        return 0;
    }


    /**
     * Constructs a PxLayoutInfo from attributes associated with a View. Call this method from
     * {@code LayoutParams(Context c, AttributeSet attrs)} constructor.
     */
    public static PxLayoutInfo getPxLayoutInfo(Context context,
                                                         AttributeSet attrs)
    {
        PxLayoutInfo info = null;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Px_Layout_Layout);
        designWidth = array.getInteger(R.styleable.Px_Layout_Layout_layout_width_design,720);
        designHeight = array.getInteger(R.styleable.Px_Layout_Layout_layout_height_design,1280);
        isWidthRatio=array.getBoolean(R.styleable.Px_Layout_Layout_layout_is_radio,false);
        info = setWidthAndHeightVal(array, info);

        info = setMarginRelatedVal(array, info);

        info = setTextSizeSupportVal(array, info);

        info = setMinMaxWidthHeightRelatedVal(array, info);

        info = setPaddingRelatedVal(array, info);


        array.recycle();

        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "constructed: " + info);
        }
        return info;
    }

    private static PxLayoutInfo setWidthAndHeightVal(TypedArray array, PxLayoutInfo info)
    {
        PxLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_width_px, true);
        if (percentVal != null)
        {
//            if (Log.isLoggable(TAG, Log.VERBOSE))
//            {
//            }
            info = checkForInfoExists(info);
            info.widthPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_height_px, false);

        if (percentVal != null)
        {
//            if (Log.isLoggable(TAG, Log.VERBOSE))
//            {
                Log.v(TAG, "percent height: " + percentVal.percent);
//            }
            info = checkForInfoExists(info);
            info.heightPercent = percentVal;
        }

        return info;
    }

    private static PxLayoutInfo setTextSizeSupportVal(TypedArray array, PxLayoutInfo info)
    {
        //textSizePercent 默认以高度作为基准
        PxLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_textSize_px, false);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent text size: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.textSizePercent = percentVal;
        }

        return info;
    }

    private static PxLayoutInfo setMinMaxWidthHeightRelatedVal(TypedArray array, PxLayoutInfo info)
    {
        //maxWidth
        PxLayoutInfo.PercentVal percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_maxWidth_px,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.maxWidthPercent = percentVal;
        }
        //maxHeight
        percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_maxHeight_px,
                false);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.maxHeightPercent = percentVal;
        }
        //minWidth
        percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_minWidth_px,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.minWidthPercent = percentVal;
        }
        //minHeight
        percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_minHeight_px,
                false);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.minHeightPercent = percentVal;
        }

        return info;
    }

    private static PxLayoutInfo setMarginRelatedVal(TypedArray array, PxLayoutInfo info)
    {
        //默认margin参考宽度
        PxLayoutInfo.PercentVal percentVal =
                getPercentVal(array,
                        R.styleable.Px_Layout_Layout_layout_margin_px,
                        true);

        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.leftMarginPercent = percentVal;
            info.topMarginPercent = percentVal;
            info.rightMarginPercent = percentVal;
            info.bottomMarginPercent = percentVal;
        }

        percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_marginLeft_px, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent left margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.leftMarginPercent = percentVal;
        }

        percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_marginTop_px, false);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent top margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.topMarginPercent = percentVal;
        }

        percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_marginRight_px, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent right margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.rightMarginPercent = percentVal;
        }

        percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_marginBottom_px, false);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent bottom margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.bottomMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_marginStart_px, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent start margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.startMarginPercent = percentVal;
        }

        percentVal = getPercentVal(array, R.styleable.Px_Layout_Layout_layout_marginEnd_px, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent end margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.endMarginPercent = percentVal;
        }

        return info;
    }

    /**
     * 设置paddingPercent相关属性
     *
     * @param array
     * @param info
     */
    private static PxLayoutInfo setPaddingRelatedVal(TypedArray array, PxLayoutInfo info)
    {
        //默认padding以宽度为标准
        PxLayoutInfo.PercentVal percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_padding_px,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingLeftPercent = percentVal;
            info.paddingRightPercent = percentVal;
            info.paddingBottomPercent = percentVal;
            info.paddingTopPercent = percentVal;
        }


        percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_paddingLeft_px,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingLeftPercent = percentVal;
        }

        percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_paddingRight_px,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingRightPercent = percentVal;
        }

        percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_paddingTop_px,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingTopPercent = percentVal;
        }

        percentVal = getPercentVal(array,
                R.styleable.Px_Layout_Layout_layout_paddingBottom_px,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingBottomPercent = percentVal;
        }

        return info;
    }

    private static PxLayoutInfo.PercentVal getPercentVal(TypedArray array, int index, boolean baseWidth)
    {
        String sizeStr = array.getString(index);
        PxLayoutInfo.PercentVal percentVal = getPercentVal(sizeStr, baseWidth);
        return percentVal;
    }


    @NonNull
    private static PxLayoutInfo checkForInfoExists(PxLayoutInfo info)
    {
        info = info != null ? info : new PxLayoutInfo();
        return info;
    }


    private static final String REGEX_PERCENT = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)l([s]?[wh]?)$";

    /**
     * widthStr to PercentVal
     * <br/>
     * eg: 35%w => new PercentVal(35, true)
     *
     * @param percentStr
     * @param isOnWidth
     * @return
     */
    private static PxLayoutInfo.PercentVal getPercentVal(String percentStr, boolean isOnWidth)
    {
        //valid param
        if (percentStr == null)
        {
            return null;
        }
        Pattern p = Pattern.compile(REGEX_PERCENT);
        Matcher matcher = p.matcher(percentStr);
        if (!matcher.matches())
        {
            throw new RuntimeException("the value of layout_xxxPercent invalid! ==>" + percentStr);
        }
        int len = percentStr.length();
        //extract the float value
        String floatVal = matcher.group(1);
        String lastAlpha = percentStr.substring(len - 1);

        float percent = Float.parseFloat(floatVal);

        PxLayoutInfo.PercentVal percentVal = new PxLayoutInfo.PercentVal();
        percentVal.percent = percent;
        if (percentStr.endsWith(PxLayoutInfo.BASEMODE.SW))
        {
            percentVal.basemode = PxLayoutInfo.BASEMODE.BASE_SCREEN_WIDTH;
        } else if (percentStr.endsWith(PxLayoutInfo.BASEMODE.SH))
        {
            percentVal.basemode = PxLayoutInfo.BASEMODE.BASE_SCREEN_HEIGHT;
        } else if (percentStr.endsWith(PxLayoutInfo.BASEMODE.PERCENT))
        {
            if (isOnWidth)
            {
                percentVal.basemode = PxLayoutInfo.BASEMODE.BASE_WIDTH;
            } else
            {
                percentVal.basemode = PxLayoutInfo.BASEMODE.BASE_HEIGHT;
            }
        } else if (percentStr.endsWith(PxLayoutInfo.BASEMODE.W))
        {
            percentVal.basemode = PxLayoutInfo.BASEMODE.BASE_WIDTH;
        } else if (percentStr.endsWith(PxLayoutInfo.BASEMODE.H))
        {
            percentVal.basemode = PxLayoutInfo.BASEMODE.BASE_HEIGHT;
        } else
        {
            throw new IllegalArgumentException("the " + percentStr + " must be endWith [l|w|h|sw|sh]");
        }

        return percentVal;
    }

    /**
     * Iterates over children and restores their original dimensions that were changed for
     * percentage values. Calling this method only makes sense if you previously called
     * {@link PxLayoutHelper#adjustChildren(int, int)}.
     */

    public void restoreOriginalParams()
    {
        for (int i = 0, N = mHost.getChildCount(); i < N; i++)
        {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "should restore " + view + " " + params);
            }
            if (params instanceof PercentLayoutParams)
            {
                PxLayoutInfo info =
                        ((PercentLayoutParams) params).getPxLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "using " + info);
                }
                if (info != null)
                {
                    if (params instanceof ViewGroup.MarginLayoutParams)
                    {
                        info.restoreMarginLayoutParams((ViewGroup.MarginLayoutParams) params);
                    } else
                    {
                        info.restoreLayoutParams(params);
                    }
                }
            }
        }
    }
    //是否值根据宽度比率适配
    private static boolean isWidthRatio=true;

    /**
     * Iterates over children and checks if any of them would like to get more space than it
     * received through the percentage dimension.
     * <p/>
     * If you are building a layout that supports percentage dimensions you are encouraged to take
     * advantage of this method. The developer should be able to specify that a child should be
     * remeasured by adding normal dimension attribute with {@code wrap_content} value. For example
     * he might specify child's attributes as {@code app:layout_widthPercent="60%p"} and
     * {@code android:layout_width="wrap_content"}. In this case if the child receives too little
     * space, it will be remeasured with width set to {@code WRAP_CONTENT}.
     *
     * @return True if the measure phase needs to be rerun because one of the children would like
     * to receive more space.
     */
    public boolean handleMeasuredStateTooSmall()
    {
        boolean needsSecondMeasure = false;
        for (int i = 0, N = mHost.getChildCount(); i < N; i++)
        {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "should handle measured state too small " + view + " " + params);
            }
            if (params instanceof PercentLayoutParams)
            {
                PxLayoutInfo info =
                        ((PercentLayoutParams) params).getPxLayoutInfo();
                if (info != null)
                {
                    if (shouldHandleMeasuredWidthTooSmall(view, info))
                    {
                        needsSecondMeasure = true;
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    if (shouldHandleMeasuredHeightTooSmall(view, info))
                    {
                        needsSecondMeasure = true;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
            }
        }
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "should trigger second measure pass: " + needsSecondMeasure);
        }
        return needsSecondMeasure;
    }

    private static boolean shouldHandleMeasuredWidthTooSmall(View view, PxLayoutInfo info)
    {
        int state = ViewCompat.getMeasuredWidthAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.widthPercent == null)
        {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.widthPercent.percent >= 0 &&
                info.mPreservedParams.width == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private static boolean shouldHandleMeasuredHeightTooSmall(View view, PxLayoutInfo info)
    {
        int state = ViewCompat.getMeasuredHeightAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.heightPercent == null)
        {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.heightPercent.percent >= 0 &&
                info.mPreservedParams.height == ViewGroup.LayoutParams.WRAP_CONTENT;
    }


    /**
     * Container for information about percentage dimensions and margins. It acts as an extension
     * for {@code LayoutParams}.
     */
    public static class PxLayoutInfo
    {

        private enum BASEMODE
        {

            BASE_WIDTH, BASE_HEIGHT, BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT;

            /**
             * width_parent
             */
            public static final String PERCENT = "l";
            /**
             * width_parent
             */
            public static final String W = "w";
            /**
             * height_parent
             */
            public static final String H = "h";
            /**
             * width_screen
             */
            public static final String SW = "sw";
            /**
             * height_screen
             */
            public static final String SH = "sh";
        }

        public static class PercentVal
        {

            public float percent = -1;
            public BASEMODE basemode;

            public PercentVal()
            {
            }

            public PercentVal(float percent, BASEMODE baseMode)
            {
                this.percent = percent;
                this.basemode = baseMode;
            }

            @Override
            public String toString()
            {
                return "PercentVal{" +
                        "percent=" + percent +
                        ", basemode=" + basemode.name() +
                        '}';
            }
        }

        public PercentVal widthPercent;
        public PercentVal heightPercent;

        public PercentVal leftMarginPercent;
        public PercentVal topMarginPercent;
        public PercentVal rightMarginPercent;
        public PercentVal bottomMarginPercent;
        public PercentVal startMarginPercent;
        public PercentVal endMarginPercent;

        public PercentVal textSizePercent;

        //1.0.4 those attr for some views' setMax/min Height/Width method
        public PercentVal maxWidthPercent;
        public PercentVal maxHeightPercent;
        public PercentVal minWidthPercent;
        public PercentVal minHeightPercent;

        //1.0.6 add padding supprot
        public PercentVal paddingLeftPercent;
        public PercentVal paddingRightPercent;
        public PercentVal paddingTopPercent;
        public PercentVal paddingBottomPercent;


        /* package */ final ViewGroup.MarginLayoutParams mPreservedParams;


        public PxLayoutInfo()
        {
            mPreservedParams = new ViewGroup.MarginLayoutParams(0, 0);
        }

        /**
         * Fills {@code ViewGroup.LayoutParams} dimensions based on percentage values.
         */
        public void fillLayoutParams(ViewGroup.LayoutParams params, int widthHint,
                                     int heightHint)
        {
            // Preserve the original layout params, so we can restore them after the measure step.
            mPreservedParams.width = params.width;
            mPreservedParams.height = params.height;


            if (widthPercent != null)
            {
                int base = getBaseByModeAndVal(widthHint, heightHint, widthPercent.basemode);
                params.width = (int) (widthRatio * widthPercent.percent);
            }
            if (heightPercent != null)
            {
                int base = getBaseByModeAndVal(widthHint, heightHint, heightPercent.basemode);
                if(!isWidthRatio){
                    switch (heightPercent.basemode){
                        case BASE_HEIGHT:
                            params.height = (int) (heightRatio * heightPercent.percent);
                            break;
                        case BASE_WIDTH:
                            params.height = (int) (widthRatio * heightPercent.percent);
                            break;
                    }
                }else {
                    params.height = (int) (widthRatio * heightPercent.percent);
                }

            }

//            if (Log.isLoggable(TAG, Log.DEBUG))
//            {
                Log.d(TAG, "after fillLayoutParams: (" + params.width + ", " + params.height + ")");
//            }
        }

        /**
         * Fills {@code ViewGroup.MarginLayoutParams} dimensions and margins based on percentage
         * values.
         */
        public void fillMarginLayoutParams(ViewGroup.MarginLayoutParams params, int widthHint,
                                           int heightHint)
        {
            fillLayoutParams(params, widthHint, heightHint);

            // Preserver the original margins, so we can restore them after the measure step.
            mPreservedParams.leftMargin = params.leftMargin;
            mPreservedParams.topMargin = params.topMargin;
            mPreservedParams.rightMargin = params.rightMargin;
            mPreservedParams.bottomMargin = params.bottomMargin;
            MarginLayoutParamsCompat.setMarginStart(mPreservedParams,
                    MarginLayoutParamsCompat.getMarginStart(params));
            MarginLayoutParamsCompat.setMarginEnd(mPreservedParams,
                    MarginLayoutParamsCompat.getMarginEnd(params));

            if (leftMarginPercent != null)
            {
                int base = getBaseByModeAndVal(widthHint, heightHint, leftMarginPercent.basemode);

                params.leftMargin = (int) (widthRatio * leftMarginPercent.percent);
            }
            if (topMarginPercent != null)
            {

                int base = getBaseByModeAndVal(widthHint, heightHint, topMarginPercent.basemode);
                if(!isWidthRatio){
                    params.topMargin = (int) (heightRatio * topMarginPercent.percent);
                }else {
                    params.topMargin = (int) (widthRatio * topMarginPercent.percent);
                }

            }
            if (rightMarginPercent != null)
            {
                int base = getBaseByModeAndVal(widthHint, heightHint, rightMarginPercent.basemode);
                params.rightMargin = (int) (widthRatio * rightMarginPercent.percent);
            }
            if (bottomMarginPercent != null)
            {
                int base = getBaseByModeAndVal(widthHint, heightHint, bottomMarginPercent.basemode);
                if(!isWidthRatio){
                    params.bottomMargin = (int) (heightRatio * bottomMarginPercent.percent);
                }else {
                    params.bottomMargin = (int) (widthRatio * bottomMarginPercent.percent);
                }

            }
            if (startMarginPercent != null)
            {
                int base = getBaseByModeAndVal(widthHint, heightHint, startMarginPercent.basemode);
                MarginLayoutParamsCompat.setMarginStart(params,
                        (int) (widthRatio * startMarginPercent.percent));
            }
            if (endMarginPercent != null)
            {
                int base = getBaseByModeAndVal(widthHint, heightHint, endMarginPercent.basemode);
                MarginLayoutParamsCompat.setMarginEnd(params,
                        (int) (widthRatio * endMarginPercent.percent));
            }
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "after fillMarginLayoutParams: (" + params.width + ", " + params.height
                        + ")");
            }
        }

        @Override
        public String toString()
        {
            return "PxLayoutInfo{" +
                    "widthPercent=" + widthPercent +
                    ", heightPercent=" + heightPercent +
                    ", leftMarginPercent=" + leftMarginPercent +
                    ", topMarginPercent=" + topMarginPercent +
                    ", rightMarginPercent=" + rightMarginPercent +
                    ", bottomMarginPercent=" + bottomMarginPercent +
                    ", startMarginPercent=" + startMarginPercent +
                    ", endMarginPercent=" + endMarginPercent +
                    ", textSizePercent=" + textSizePercent +
                    ", maxWidthPercent=" + maxWidthPercent +
                    ", maxHeightPercent=" + maxHeightPercent +
                    ", minWidthPercent=" + minWidthPercent +
                    ", minHeightPercent=" + minHeightPercent +
                    ", paddingLeftPercent=" + paddingLeftPercent +
                    ", paddingRightPercent=" + paddingRightPercent +
                    ", paddingTopPercent=" + paddingTopPercent +
                    ", paddingBottomPercent=" + paddingBottomPercent +
                    ", mPreservedParams=" + mPreservedParams +
                    '}';
        }

        /**
         * Restores original dimensions and margins after they were changed for percentage based
         * values. Calling this method only makes sense if you previously called
         * {@link PxLayoutInfo#fillMarginLayoutParams}.
         */
        public void restoreMarginLayoutParams(ViewGroup.MarginLayoutParams params)
        {
            restoreLayoutParams(params);
            params.leftMargin = mPreservedParams.leftMargin;
            params.topMargin = mPreservedParams.topMargin;
            params.rightMargin = mPreservedParams.rightMargin;
            params.bottomMargin = mPreservedParams.bottomMargin;
            MarginLayoutParamsCompat.setMarginStart(params,
                    MarginLayoutParamsCompat.getMarginStart(mPreservedParams));
            MarginLayoutParamsCompat.setMarginEnd(params,
                    MarginLayoutParamsCompat.getMarginEnd(mPreservedParams));
        }

        /**
         * Restores original dimensions after they were changed for percentage based values. Calling
         * this method only makes sense if you previously called
         * {@link PxLayoutInfo#fillLayoutParams}.
         */
        public void restoreLayoutParams(ViewGroup.LayoutParams params)
        {
            params.width = mPreservedParams.width;
            params.height = mPreservedParams.height;
        }
    }
    //宽高比率
    private static double widthRatio,heightRatio;

    /**
     * If a layout wants to support percentage based dimensions and use this helper class, its
     * {@code LayoutParams} subclass must implement this interface.
     * <p/>
     * Your {@code LayoutParams} subclass should contain an instance of {@code PxLayoutInfo}
     * and the implementation of this interface should be a simple accessor.
     */
    public interface PercentLayoutParams
    {
        PxLayoutInfo getPxLayoutInfo();
    }
}
