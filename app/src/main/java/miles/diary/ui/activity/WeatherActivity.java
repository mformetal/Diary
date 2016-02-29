package miles.diary.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.Bind;
import icepick.State;
import miles.diary.R;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.ui.widget.TypefaceIconTextView;

/**
 * Created by mbpeele on 2/21/16.
 */
public class WeatherActivity extends BaseActivity {

    @Bind(R.id.activity_weather_temperature)
    TypefaceIconTextView iconTextView;
    @Bind(R.id.activity_weather_button)
    TypefaceButton button;

    @State String temperature;
    @State String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        if (intent != null) {
            temperature = intent.getStringExtra(NewEntryActivity.TEMPERATURE);
            address = intent.getStringExtra(NewEntryActivity.ADDRESS);
            iconTextView.setText(temperature + "\n" + address);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
