package com.jintao.vipmanager.myview;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jintao.vipmanager.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: zhanghui
 * CreateDate: 2024/5/29 17:43
 * Description:
 */
public class MySpinnerView extends AppCompatTextView {

    private MySpinnerAdapter mySpinnerAdapter;
    private PopupWindow popupWindow;
    private RecyclerView recyclerView;
    private OnItemSelectedListener listener;
    private int defaultPosition = -1;
    private Drawable arrowDrawable;

    private String spinnerHintText;
    private int spinnerTextColor;
    private float spinnerTextSize,itemTextSize;
    private int spinnerArrowColor;
    private int spinnerArrowPadding;
    private int spinnerPaddingLeft,spinnerPaddingTop,spinnerPaddingRight,spinnerPaddingBottom;
    private int itemPaddingLeft,itemPaddingTop,itemPaddingRight,itemPaddingBottom;
    private int popupPaddingTop,popupPaddingBottom;
    private boolean itemShowSelectImage;
    private int itemSelectColor,itemNormalColor;

    public MySpinnerView(@NonNull Context context) {
        this(context, null);
    }

    public MySpinnerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySpinnerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int defaultBlackColor = 0xFF333333;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MySpinnerView);
        spinnerHintText = ta.getString(R.styleable.MySpinnerView_spinner_hint_text);
        if (TextUtils.isEmpty(spinnerHintText)) {
            spinnerHintText = "请选择";
        }
        spinnerTextColor = ta.getColor(R.styleable.MySpinnerView_spinner_text_color, defaultBlackColor);
        spinnerTextSize = ta.getInteger(R.styleable.MySpinnerView_spinner_text_size, 13);

        spinnerArrowColor = ta.getColor(R.styleable.MySpinnerView_spinner_arrow_color, defaultBlackColor);
        spinnerArrowPadding = (int) ta.getDimension(R.styleable.MySpinnerView_spinner_arrow_padding, dp2px(5));

        spinnerPaddingLeft = (int) ta.getDimension(R.styleable.MySpinnerView_spinner_padding_left, dp2px(6));
        spinnerPaddingRight = (int) ta.getDimension(R.styleable.MySpinnerView_spinner_padding_right, dp2px(6));
        spinnerPaddingTop = (int) ta.getDimension(R.styleable.MySpinnerView_spinner_padding_top, dp2px(3));
        spinnerPaddingBottom = (int) ta.getDimension(R.styleable.MySpinnerView_spinner_padding_bottom, dp2px(3));

        popupPaddingTop = (int) ta.getDimension(R.styleable.MySpinnerView_popup_padding_top, dp2px(8));
        popupPaddingBottom = (int) ta.getDimension(R.styleable.MySpinnerView_popup_padding_bottom, dp2px(8));

        itemShowSelectImage = ta.getBoolean(R.styleable.MySpinnerView_item_show_select_image,true);
        itemSelectColor = ta.getColor(R.styleable.MySpinnerView_item_select_color, 0xFF00AB50);
        itemNormalColor = ta.getColor(R.styleable.MySpinnerView_item_normal_color, defaultBlackColor);
        itemTextSize = ta.getInteger(R.styleable.MySpinnerView_item_text_size, 12);

        itemPaddingLeft = (int) ta.getDimension(R.styleable.MySpinnerView_item_padding_left, dp2px(12));
        itemPaddingTop = (int) ta.getDimension(R.styleable.MySpinnerView_item_padding_top, dp2px(5));
        itemPaddingRight = (int) ta.getDimension(R.styleable.MySpinnerView_item_padding_right, dp2px(12));
        itemPaddingBottom = (int) ta.getDimension(R.styleable.MySpinnerView_item_padding_bottom, dp2px(5));
        ta.recycle();

        initView();
        initPopupWindow();
    }

    private void initView() {
        arrowDrawable = getContext().getDrawable(R.drawable.spinner_arrow_drawable).mutate();
        arrowDrawable.setColorFilter(spinnerArrowColor, PorterDuff.Mode.SRC_IN);
        setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);

        setPadding(spinnerPaddingLeft,spinnerPaddingTop,spinnerPaddingRight,spinnerPaddingBottom);
        setCompoundDrawablePadding(spinnerArrowPadding);

        setText(spinnerHintText);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, spinnerTextSize);
        setTextColor(spinnerTextColor);

        setClickable(true);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        setBackgroundResource(R.drawable.spinner_background_selector);
    }

    private void initPopupWindow() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setPadding(0,popupPaddingTop,0,popupPaddingBottom);

        popupWindow = new PopupWindow(getContext());
        popupWindow.setContentView(recyclerView);

        popupWindow.setElevation(dp2px(6));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.MySpinnerWindowStyle);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                animateArrow(false);
            }
        });
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mInterceptorListener==null) {
                if (!checkItemsEmpty()) {
                    if (isEnabled() && isClickable()) {
                        if (!popupWindow.isShowing() && canShowPopup()) {
                            if (defaultPosition == -1) {
                                recyclerView.scrollToPosition(0);
                            }else {
                                if (mySpinnerAdapter.getSelectedIndex()!=-1) {
                                    recyclerView.scrollToPosition(mySpinnerAdapter.getSelectedIndex());
                                }
                            }
                            popupWindow.showAsDropDown(this,spinnerPaddingLeft,dp2px(3));
                            animateArrow(true);
                        } else {
                            popupWindow.dismiss();
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onItemSelected(mySpinnerAdapter.getLastIndex(),-1, "");
                    }
                }
            }else {
                mInterceptorListener.interceptor();
            }
        }
        return super.onTouchEvent(event);
    }

    public void showSelectPopup() {
        if (!checkItemsEmpty()) {
            if (isEnabled() && isClickable()) {
                if (!popupWindow.isShowing() && canShowPopup()) {
                    if (mySpinnerAdapter.getSelectedIndex()!=-1) {
                        recyclerView.scrollToPosition(mySpinnerAdapter.getSelectedIndex());
                    }else {
                        recyclerView.scrollToPosition(0);
                    }
                    popupWindow.showAsDropDown(this,spinnerPaddingLeft,dp2px(3));
                    animateArrow(true);
                } else {
                    popupWindow.dismiss();
                }
            }
        } else {
            if (listener != null) {
                listener.onItemSelected(mySpinnerAdapter.getLastIndex(),-1, "");
            }
        }
    }

    private OnInterceptorListener mInterceptorListener;
    public void setOnInterceptorListener(OnInterceptorListener interceptorListener) {
        this.mInterceptorListener = interceptorListener;
    }
    public interface OnInterceptorListener {
        void interceptor();
    }

    private boolean canShowPopup() {
        Activity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        boolean isLaidOut = isLaidOut();
        if (!isLaidOut) {
            isLaidOut = getWidth() > 0 && getHeight() > 0;
        }
        return isLaidOut;
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : 10000;
        int end = shouldRotateUp ? 10000 : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end);
        animator.setDuration(320);
        animator.start();
    }

    public void setItems(@NonNull String... items) {
        setItems(Arrays.asList(items));
    }

    private int getContentLenght(String content) {
        int lenght = 0;
        for (char cha : content.toCharArray()) {
            //是汉字
            if (cha >= 0x4E00 && cha <= 0x9FFF) {
                lenght+=2;
            }else {
                lenght+=1;
            }
        }
        if (lenght%2!=0) {
            return lenght/2+1;
        }else {
            return lenght/2;
        }
    }

    public <T extends MySpinnerBean> void setListBean(List<T> list) {
        ArrayList<String> items = new ArrayList<>();
        int maxTextLenght = 0;
        for (int i = 0; i < list.size(); i++) {
            String showName = list.get(i).getShowName();
            int contentLenght = getContentLenght(showName);
            if (contentLenght > maxTextLenght) {
                maxTextLenght = contentLenght;
            }
            items.add(showName);
        }
        for (int i = 0; i < list.size(); i++) {
            String itemStr = items.get(i);
            if (maxTextLenght > itemStr.length()) {
                int buweiLenght = maxTextLenght - itemStr.length();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(itemStr);
                for (int j = 0; j < buweiLenght; j++) {
                    stringBuffer.append(getContext().getString(R.string.common_kong_ge));
                }
                items.set(i,stringBuffer.toString());
            }
        }

        setItems(items);
    }

    public <T extends MySpinnerBean> void addHeadItem(T itemInfo) {
        mySpinnerAdapter.getItems().add(0,itemInfo.getShowName());
        mySpinnerAdapter.notifyDataSetChanged();
    }

    public void setSelectedIndex(int position) {
        if (!checkItemsEmpty()) {
            if (position >= 0 && position < mySpinnerAdapter.getItems().size()) {
                setText(mySpinnerAdapter.getItems().get(position).replaceAll(getContext().getString(R.string.common_kong_ge),""));
                mySpinnerAdapter.setSelectedIndex(position);
            }else {
                if (position == -1) {
                    mySpinnerAdapter.setSelectedIndex(position);
                    setText(spinnerHintText);
                }
            }
        }else {
            setText(spinnerHintText);
            defaultPosition = position;
        }
    }

    public void setItems(List<String> list) {
        mySpinnerAdapter = new MySpinnerAdapter(list)
                .setShowSelectImage(itemShowSelectImage)
                .setPadding(itemPaddingLeft,itemPaddingTop,itemPaddingRight,itemPaddingBottom)
                .setTextColor(itemSelectColor,itemNormalColor)
                .setTextSize(itemTextSize);
        if (defaultPosition >= 0 && defaultPosition < list.size()) {
            setText(list.get(defaultPosition));
            mySpinnerAdapter.setSelectedIndex(defaultPosition);
        }
        if (list!=null && list.size()!=0) {
            setPopupWindowSize(list);
        }
        mySpinnerAdapter.setOnSpinnerClickListener(new MySpinnerAdapter.OnSpinnerClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (!checkItemsEmpty()) {
                    mySpinnerAdapter.setSelectedIndex(position);
                    String selectText = mySpinnerAdapter.getItems().get(position).replaceAll(getContext().getString(R.string.common_kong_ge),"");
                    setText(selectText);

                    if (listener!=null) {
                        listener.onItemSelected(mySpinnerAdapter.getLastIndex(), position,selectText);
                    }
                }else {
                    if (listener!=null) {
                        listener.onItemSelected(mySpinnerAdapter.getLastIndex(),-1,"");
                    }
                }
                popupWindow.dismiss();
            }
        });
        recyclerView.setAdapter(mySpinnerAdapter);
    }

    private void setPopupWindowSize(List<String> list) {
        int maxTextLenght = 0;
        for (int i = 0; i < list.size(); i++) {
            int length = list.get(i).length();
            if (length > maxTextLenght) {
                maxTextLenght = length;
            }
        }

        if (list.size() > 5) {//超长设置固定高度
            int itemHeight = itemPaddingTop + dp2px(38) + itemPaddingBottom;
            popupWindow.setHeight(itemHeight * 5);
        }
//        int screenwidth = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth());
//        if (itemShowSelectImage) {
//            int mywidth = dp2px(25) * maxTextLenght + itemPaddingLeft + itemPaddingRight + dp2px(25);
//            if (mywidth > screenwidth) {
//                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//            }else {
//                popupWindow.setWidth(mywidth);
//            }
//        }else {
//            int mywidth = dp2px(30) * maxTextLenght + itemPaddingLeft + itemPaddingRight;
//            if (mywidth > screenwidth) {
//                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//            }else {
//                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//            }
//        }
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private boolean checkItemsEmpty() {
        return mySpinnerAdapter == null || mySpinnerAdapter.getItems() == null || mySpinnerAdapter.getItems().size() == 0;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int lastPosition, int spinnerPosition, String item);
    }

    public interface MySpinnerBean {
        String getShowName();
    }

    private int dp2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
