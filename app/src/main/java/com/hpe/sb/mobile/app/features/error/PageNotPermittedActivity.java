package com.hpe.sb.mobile.app.features.error;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.features.home.MainActivity;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;

public class PageNotPermittedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_permitted_screen);

        final TextView actionButton = (TextView) findViewById(R.id.error_action);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageNotPermittedActivity.this, MainActivity.class);
                startActivity (intent);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
