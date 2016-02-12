package miles.diary.ui;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**
 * Created by mbpeele on 2/6/16.
 */
public class CancelListener {

    private EditText widget;
    private Drawable[] hideDrawables;
    private Drawable canceler;

    public CancelListener(EditText editText) {
        widget = editText;
        widget.addTextChangedListener(textWatcher);
        View.OnTouchListener touchListener = (v, event) -> {
            if (canceler != null && event.getX() > widget.getWidth() - widget.getPaddingRight() -
                    canceler.getIntrinsicWidth()) {
                if (widget instanceof AutoCompleteTextView) {
                    ((AutoCompleteTextView) widget).setText("", false);
                } else {
                    widget.setText("");
                }
                setCancelVisible(false);
            }
            return false;
        };
        widget.setOnTouchListener(touchListener);
        widget.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                widget.removeOnAttachStateChangeListener(this);
                widget.removeTextChangedListener(textWatcher);
                widget.setOnTouchListener(null);
            }
        });

        hideDrawables = new Drawable[4];

        new PreDrawer<EditText>(widget) {
            @Override
            public void notifyPreDraw(EditText view) {
                Drawable[] drawables = widget.getCompoundDrawables();
                for (int i = 0; i < drawables.length; i++) {
                    Drawable drawable = drawables[i];
                    if (drawable != null) {
                        hideDrawables[i] = drawable;
                    }
                }

                canceler = hideDrawables[2];
            }
        };
    }

    public void showOrHideCancel() {
        setCancelVisible(widget.getText().length() > 0);
    }

    private void setCancelVisible(boolean visible) {
        if (visible) {
            widget.setCompoundDrawablesWithIntrinsicBounds(hideDrawables[0], hideDrawables[1],
                    hideDrawables[2], hideDrawables[3]);
        } else {
            widget.setCompoundDrawablesWithIntrinsicBounds(hideDrawables[0], hideDrawables[1],
                    null, hideDrawables[3]);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            showOrHideCancel();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
