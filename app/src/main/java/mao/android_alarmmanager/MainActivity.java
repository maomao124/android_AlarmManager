package mao.android_alarmmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "MainActivity";


    private final String ALARM_ACTION = "mao.android_alarmmanager.alarm";
    private AlarmReceiver alarmReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.Button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendAlarm();
                toastShow("3秒后触发");
            }
        });
    }

    public class AlarmReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent == null)
            {
                return;
            }
            Log.d(TAG, "onReceive: ");
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        alarmReceiver = new AlarmReceiver();
        IntentFilter intentFilter = new IntentFilter(ALARM_ACTION);
        registerReceiver(alarmReceiver, intentFilter);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(alarmReceiver);
    }


    private void sendAlarm()
    {
        Intent intent = new Intent(ALARM_ACTION); // 创建一个广播事件的意图
        // 创建一个用于广播的延迟意图
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 从系统服务中获取闹钟管理器
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long delayTime = System.currentTimeMillis() + 3 * 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            // 允许在空闲时发送广播，Android6.0之后新增的方法
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delayTime, pendingIntent);
        }
        else
        {
            // 设置一次性闹钟，延迟若干秒后，携带延迟意图发送闹钟广播
            // （但Android6.0之后，set方法在暗屏时不保证发送广播，必须调用setAndAllowWhileIdle方法）
            alarmManager.set(AlarmManager.RTC_WAKEUP, delayTime, pendingIntent);
        }
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}