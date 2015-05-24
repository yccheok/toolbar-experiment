package org.yccheok.toolbar_experiment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.yccheok.jstock.gui.JStockSearchView;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private JStockSearchView jStockSearchView;

    private MenuItem searchMenuItem;
    private MenuItem settingsMenuItem;

    private volatile boolean arrowVisible = false;

    private void displaySearchView() {
        searchMenuItem.setVisible(false);
        settingsMenuItem.setVisible(false);

        jStockSearchView.setVisibility(View.VISIBLE);
        jStockSearchView.setText("");
        jStockSearchView.requestTextViewFocus();
        jStockSearchView.setImeVisibility(true);

        animateHamburgerToArrow();
    }

    private void hideSearchView() {
        animateArrowToHamburger();

        jStockSearchView.setImeVisibility(false);
        jStockSearchView.setVisibility(View.GONE);

        settingsMenuItem.setVisible(true);
        searchMenuItem.setVisible(true);
    }

    private void animateArrowToHamburger() {
        ValueAnimator anim = ValueAnimator.ofFloat(1f, 0f);
        anim.addListener(new ValueAnimator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                arrowVisible = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                actionBarDrawerToggle.onDrawerSlide(null, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        // You can change this duration to more closely match that of the default animation.
        anim.setDuration(300);
        anim.start();
    }

    private void animateHamburgerToArrow() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addListener(new ValueAnimator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                arrowVisible = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                actionBarDrawerToggle.onDrawerSlide(null, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        // You can change this duration to more closely match that of the default animation.
        anim.setDuration(300);
        anim.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.jStockSearchView = (JStockSearchView)toolbar.findViewById(R.id.search_view);

        this.drawerLayout = (DrawerLayout)this.findViewById(R.id.drawer_layout);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.app_name, R.string.app_name);

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrowVisible) {
                    hideSearchView();
                    return;
                }

                if (drawerLayout.isDrawerOpen(findViewById(R.id.fragment_drawer)))
                    drawerLayout.closeDrawer(findViewById(R.id.fragment_drawer));
                else
                    drawerLayout.openDrawer(findViewById(R.id.fragment_drawer));
            }
        });
    }

    @Override
    public void onBackPressed () {
        if (arrowVisible) {
            hideSearchView();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.searchMenuItem = menu.findItem(R.id.action_search);
        this.settingsMenuItem = menu.findItem(R.id.action_settings);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            animateHamburgerToArrow();
            return true;
        } else if (id == R.id.action_search) {
            displaySearchView();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}
