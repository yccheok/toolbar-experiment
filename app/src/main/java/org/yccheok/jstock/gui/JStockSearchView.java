package org.yccheok.jstock.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.yccheok.toolbar_experiment.R;

import java.lang.reflect.Method;

public class JStockSearchView extends LinearLayoutCompat {
    private JStockAutoCompleteTextView mSearchSrcTextView;
    private ImageView mCloseButton;
    private ProgressBar mProgressBar;
    static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER = new AutoCompleteTextViewReflector();

    public void setText(String text) {
        mSearchSrcTextView.setText(text);
    }

    public void requestTextViewFocus() {
        mSearchSrcTextView.requestFocus();
    }

    public JStockSearchView(Context context) {
        this(context, null);
    }
    
    public JStockSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.searchViewStyle);
    }

    public JStockSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.jstock_search_view, this, true);

        mSearchSrcTextView = (JStockAutoCompleteTextView) findViewById(R.id.search_src_text);
        mCloseButton = (ImageView) findViewById(R.id.search_close_btn);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mCloseButton.setImageDrawable(getResources().getDrawable(android.support.v7.appcompat.R.drawable.abc_ic_clear_mtrl_alpha));
        mCloseButton.setOnClickListener(mOnClickListener);

        mSearchSrcTextView.addTextChangedListener(mTextWatcher);

        // Sample data.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        mSearchSrcTextView.setAdapter(adapter);
    }

    private static final String[] COUNTRIES = new String[] {
        "Belgium", "France", "Frances", "Italy", "Germany", "Spain"
    };

    private final OnClickListener mOnClickListener = new OnClickListener() {

        public void onClick(View v) {
            if (v == mCloseButton) {
                onCloseClicked();
            }
        }
    };

    private void onCloseClicked() {
        mSearchSrcTextView.setText("");
        mSearchSrcTextView.requestFocus();
        setImeVisibility(true);
    }

    public void setImeVisibility(final boolean visible) {
        if (visible) {
            post(mShowImeRunnable);
        } else {
            removeCallbacks(mShowImeRunnable);
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    /*
     * SearchView can be set expanded before the IME is ready to be shown during
     * initial UI setup. The show operation is asynchronous to account for this.
     */
    private Runnable mShowImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                HIDDEN_METHOD_INVOKER.showSoftInputUnchecked(imm, JStockSearchView.this, 0);
            }
        }
    };

    /**
     * Callback to watch the text field for empty/non-empty
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int before, int after) { }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int after) {
            JStockSearchView.this.onTextChanged(s);
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private void onTextChanged(CharSequence newText) {
        updateCloseButton();
    }

    private void updateCloseButton() {
        final boolean hasText = !TextUtils.isEmpty(mSearchSrcTextView.getText());

        // Should we show the close button? It is not shown if there's no focus,
        // field is not iconified by default and there is no text in it.
        final boolean showClose = hasText;
        mCloseButton.setVisibility(showClose ? VISIBLE : INVISIBLE);
        final Drawable closeButtonImg = mCloseButton.getDrawable();
        if (closeButtonImg != null){
            closeButtonImg.setState(hasText ? ENABLED_STATE_SET : EMPTY_STATE_SET);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Let the standard measurements take effect in iconified state.
        if (false) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        final int mMaxWidth = -1;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                // If there is an upper limit, don't exceed maximum width (explicit or implicit)
                if (mMaxWidth > 0) {
                    width = Math.min(mMaxWidth, width);
                } else {
                    width = Math.min(getPreferredWidth(), width);
                }
                break;
            case MeasureSpec.EXACTLY:
                // If an exact width is specified, still don't exceed any specified maximum width
                if (mMaxWidth > 0) {
                    width = Math.min(mMaxWidth, width);
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                // Use maximum width, if specified, else preferred width
                width = mMaxWidth > 0 ? mMaxWidth : getPreferredWidth();
                break;
        }
        widthMode = MeasureSpec.EXACTLY;
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode), heightMeasureSpec);
    }

    private int getPreferredWidth() {
        return getContext().getResources()
                .getDimensionPixelSize(R.dimen.abc_search_view_preferred_width);
    }

    public void setOnEditTextImeBackListener(EditTextImeBackListener listener) {
        mSearchSrcTextView.setOnEditTextImeBackListener(listener);
    }

    public interface EditTextImeBackListener {
        void onImeBack(JStockAutoCompleteTextView ctrl, String text);
    }

    public static class JStockAutoCompleteTextView extends AutoCompleteTextView {
        private EditTextImeBackListener mOnImeBack;

        /**
         * Creates a new instance of JStockAutoComplete
         */
        public JStockAutoCompleteTextView(final Context context, final AttributeSet attrs, final int defStyle) {
            super(context, attrs, defStyle);
        }

        public JStockAutoCompleteTextView(final Context context, final AttributeSet attrs) {
            super(context, attrs);
        }

        public JStockAutoCompleteTextView(final Context context) {
            super(context);
        }

        // http://stackoverflow.com/questions/3425932/detecting-when-user-has-dismissed-the-soft-keyboard
        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (mOnImeBack != null) mOnImeBack.onImeBack(this, this.getText().toString());
            }
            return super.dispatchKeyEvent(event);
        }

        public void setOnEditTextImeBackListener(EditTextImeBackListener listener) {
            mOnImeBack = listener;
        }
    }

    private static class AutoCompleteTextViewReflector {
        private Method doBeforeTextChanged, doAfterTextChanged;
        private Method ensureImeVisible;
        private Method showSoftInputUnchecked;

        AutoCompleteTextViewReflector() {
            try {
                doBeforeTextChanged = AutoCompleteTextView.class
                        .getDeclaredMethod("doBeforeTextChanged");
                doBeforeTextChanged.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Ah well.
            }
            try {
                doAfterTextChanged = AutoCompleteTextView.class
                        .getDeclaredMethod("doAfterTextChanged");
                doAfterTextChanged.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Ah well.
            }
            try {
                ensureImeVisible = AutoCompleteTextView.class
                        .getMethod("ensureImeVisible", boolean.class);
                ensureImeVisible.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Ah well.
            }
            try {
                showSoftInputUnchecked = InputMethodManager.class.getMethod(
                        "showSoftInputUnchecked", int.class, ResultReceiver.class);
                showSoftInputUnchecked.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Ah well.
            }
        }

        void doBeforeTextChanged(AutoCompleteTextView view) {
            if (doBeforeTextChanged != null) {
                try {
                    doBeforeTextChanged.invoke(view);
                } catch (Exception e) {
                }
            }
        }

        void doAfterTextChanged(AutoCompleteTextView view) {
            if (doAfterTextChanged != null) {
                try {
                    doAfterTextChanged.invoke(view);
                } catch (Exception e) {
                }
            }
        }

        void ensureImeVisible(AutoCompleteTextView view, boolean visible) {
            if (ensureImeVisible != null) {
                try {
                    ensureImeVisible.invoke(view, visible);
                } catch (Exception e) {
                }
            }
        }

        void showSoftInputUnchecked(InputMethodManager imm, View view, int flags) {
            if (showSoftInputUnchecked != null) {
                try {
                    showSoftInputUnchecked.invoke(imm, flags, null);
                    return;
                } catch (Exception e) {
                }
            }

            // Hidden method failed, call public version instead
            imm.showSoftInput(view, flags);
        }
    }
}
