package xyz.giautm.tex.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.AuthData;

import java.util.ArrayList;

import xyz.giautm.tex.App;
import xyz.giautm.tex.FirebaseHelper;
import xyz.giautm.tex.R;
import xyz.giautm.tex.fragment.OrderDetailFragment;
import xyz.giautm.tex.fragment.TransportFragment;
import xyz.giautm.tex.model.Order;
import xyz.giautm.tex.utilities.CauseDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TransportFragment.OnItemClickListener,
        OrderDetailFragment.OrderActionListener {

    private static final String LOG_TAG = "MainActivity";
    private String mUserId;

    private Toolbar mToolbar = null;
    private String mTransport = null;

    private boolean startDefault = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (savedInstanceState == null && loginCheck()) {
            selectItem(0);
        }
    }

    private int mTransportTitles[] = {
            R.string.title_transport_current,
            R.string.title_transport_updated,
            R.string.title_transport_success,
            R.string.title_transport_archive
    };
    private String mTransportName[] = {
            "current", "updated", "success", "archive"
    };

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        Fragment fragment = null;
        mTransport = mTransportName[position];
        fragment = TransportFragment.newInstance(mTransport, mUserId);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        mToolbar.setTitle(mTransportTitles[position]);
        loginCheck();
    }

    private boolean loginCheck() {
        AuthData authData = App.getFirebaseRootRef().getAuth();
        if (authData == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN);
            return false;
        } else {
            mUserId = authData.getUid();
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                mUserId = data.getStringExtra(LoginActivity.USER_ID);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        ArrayList<Integer> postions = new ArrayList<>(4);
        postions.add(R.id.nav_transport_current);
        postions.add(R.id.nav_transport_updated);
        postions.add(R.id.nav_transport_success);
        postions.add(R.id.nav_transport_archive);

        int position = postions.indexOf(item.getItemId());
        if (position > -1) {
            selectItem(position);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemViewClick(Order order) {
        if (order != null) {
            boolean showAction = "current".equals(mTransport);
            if (findViewById(R.id.order_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                Fragment fragment = OrderDetailFragment.newInstance(order.getOrderCode(), showAction);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.order_detail_container, fragment)
                        .replace(R.id.order_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(MainActivity.this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailFragment.ARG_ORDER_CODE, order.getOrderCode());
                intent.putExtra(OrderDetailFragment.ARG_SHOW_ACTION, showAction);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemCallClick(Order order) {
        if (order != null) {
            String number = order.getRecipientPhone();
            if (number != null) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onItemSendClick(Order order) {
        if (order != null) {
            String number = order.getRecipientPhone();
            if (number != null) {
                // TODO: Sent message here
            }
        }
    }

    @Override
    public void onDeliveredClick(Order order) {
        FirebaseHelper.moveToUpdated(order.getOrderCode(), 1, null);
    }

    @Override
    public void onCancelledClick(final Order order) {
        CauseDialog.show(this, order, new CauseDialog.causeDialogListener() {
            @Override
            public void onConfirmed(int cause, String causeNote) {
                FirebaseHelper.moveToUpdated(order.getOrderCode(), cause, causeNote);
            }

            @Override
            public void onCancelled() {
            }
        });
    }
}
