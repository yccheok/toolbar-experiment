package org.yccheok.jstock.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import org.yccheok.toolbar_experiment.R;

public class JStockSearchView extends LinearLayoutCompat {
    private JStockAutoCompleteTextView mSearchSrcTextView;
    private ImageView mCloseButton;

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

        mCloseButton.setImageDrawable(getResources().getDrawable(android.support.v7.appcompat.R.drawable.abc_ic_clear_mtrl_alpha));

        mSearchSrcTextView.addTextChangedListener(mTextWatcher);
    }

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
        mCloseButton.setVisibility(showClose ? VISIBLE : GONE);
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

    public static class JStockAutoCompleteTextView extends AutoCompleteTextView {
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
    }
}
