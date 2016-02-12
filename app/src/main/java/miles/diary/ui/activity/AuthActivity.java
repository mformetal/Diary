package miles.diary.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.ui.auth.AuthSurface;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/11/16.
 */
public class AuthActivity extends BaseActivity {

    @Bind(R.id.activity_auth_surface) AuthSurface surface;
    @Bind(R.id.activity_auth_redo) FloatingActionButton redo;
    @Bind(R.id.activity_auth_undo) FloatingActionButton undo;
    @Bind(R.id.activity_auth_recognize) ImageButton recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        redo.setOnClickListener(v -> surface.redo());
        undo.setOnClickListener(v -> surface.undo());
        recognizer.setOnClickListener(v -> runImageRecognition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, openCVLoader);
    }

    private void runImageRecognition() {

    }

    private BaseLoaderCallback openCVLoader = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };
}
