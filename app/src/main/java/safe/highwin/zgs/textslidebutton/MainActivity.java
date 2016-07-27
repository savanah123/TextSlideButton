package safe.highwin.zgs.textslidebutton;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import zgs.highwin.library.TextSlideButton;

public class MainActivity extends AppCompatActivity {

    private TextSlideButton tsb;
    private int mLocation;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tsb = (TextSlideButton) findViewById(R.id.tsb);
        btn = ((Button) findViewById(R.id.btn));
        tsb.setTexts(new String[]{"面议","自定义","个人","公司"});
        tsb.setOnGetLocationListener(new TextSlideButton.OnGetLocationListener() {
            @Override
            public void showLocation(float location,String text) {
                Log.d("MainActivity", "location:" + location+" text:"+text);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsb.setChooseRectLocation(judgeLocation());
            }
        });
        tsb.setChooseRectLocation(1);
        tsb.setUnChooseTextColor(Color.BLUE,Color.BLACK,Color.YELLOW);
        tsb.setChooseTextColor(Color.RED,Color.GREEN,Color.CYAN);
        tsb.setChooseTextSize(45);
        tsb.setUnChooseTextSize(45);
    }

    private int judgeLocation() {
        mLocation = tsb.getLocation()[0];
        mLocation++;
        if(mLocation>=tsb.getTextCount()){
            mLocation = 0;
        }
        return mLocation;
    }
}
