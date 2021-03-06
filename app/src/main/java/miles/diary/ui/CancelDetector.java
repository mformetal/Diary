package miles.diary.ui;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.Arrays;

/**
 * Created by mbpeele on 2/6/16.
 */
public class CancelDetector {

    private EditText widget;
    private Drawable[] hideDrawables;
    private Drawable canceler;

    public CancelDetector(EditText editText) {
        widget = editText;
        widget.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                widget.addTextChangedListener(textWatcher);
                widget.setOnTouchListener(touchListener);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                widget.removeOnAttachStateChangeListener(this);
                widget.removeTextChangedListener(textWatcher);
                widget.setOnTouchListener(null);
            }
        });

        hideDrawables = new Drawable[4];

        PreDrawer.addPreDrawer(widget, new PreDrawer.OnPreDrawListener<EditText>() {
            @Override
            public boolean onPreDraw(EditText view) {
                Drawable[] drawables = view.getCompoundDrawables();
                hideDrawables = Arrays.copyOf(drawables, drawables.length);
                canceler = hideDrawables[2];
                return true;
            }
        });
    }

    private void showOrHideCancel(boolean visible) {
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
            showOrHideCancel(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (canceler != null && event.getX() > widget.getWidth() - widget.getPaddingRight() -
                    canceler.getIntrinsicWidth()) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (widget instanceof AutoCompleteTextView) {
                        ((AutoCompleteTextView) widget).setText("", false);
                    } else {
                        widget.setText("");
                    }
                    showOrHideCancel(false);
                }
            }
            return false;
        }
    };
}
