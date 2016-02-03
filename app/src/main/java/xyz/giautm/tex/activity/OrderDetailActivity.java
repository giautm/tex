package xyz.giautm.tex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.ValueEventListener;

import xyz.giautm.tex.FirebaseHelper;
import xyz.giautm.tex.R;
import xyz.giautm.tex.model.Order;
import xyz.giautm.tex.fragment.OrderDetailFragment;
import xyz.giautm.tex.utilities.CauseDialog;

public class OrderDetailActivity extends AppCompatActivity implements OrderDetailFragment.OrderActionListener {

    private static final String LOG_TAG = "OrderDetailActivity";

    private ValueEventListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            OrderDetailFragment fragment = OrderDetailFragment.newInstance(
                    getIntent().getStringExtra(OrderDetailFragment.ARG_ORDER_CODE),
                    getIntent().getBooleanExtra(OrderDetailFragment.ARG_SHOW_ACTION, false));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.order_detail_container, fragment)
                    .commit();
        }
    }

    private void navigateUp() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // create new task
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            // Stay in same task
            NavUtils.navigateUpTo(this, upIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeliveredClick(Order order) {
        FirebaseHelper.moveToUpdated(order.getOrderCode(), 1, null);
        navigateUp();
    }

    @Override
    public void onCancelledClick(final Order order) {
        CauseDialog.show(this, order, new CauseDialog.causeDialogListener() {
            @Override
            public void onConfirmed(int cause, String causeNote) {
                FirebaseHelper.moveToUpdated(order.getOrderCode(), cause, causeNote);
                navigateUp();
            }

            @Override
            public void onCancelled() {
            }
        });
    }
}