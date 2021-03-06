package com.bigstark.cycler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.bigstark.cycler.mvp.BasePresenter;

import java.util.HashSet;
import java.util.Set;

import butterknife.Unbinder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by bigstark on 2016. 11. 24..
 */

public class CyclerActivity extends AppCompatActivity implements CyclerManager {

    private Set<Cycler> cyclers = new HashSet<>();
    private Set<BasePresenter> presenters = new HashSet<>();
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private Unbinder unbinder;

    private boolean stopped = false;

    @Override
    public boolean isStopped() {
        return stopped;
    }

    /**
     * Add {@param cycler} in {@link CyclerActivity}
     *
     * It helps object that implements cycler take Activity's Life Cycle.
     */
    protected void addCycler(Cycler cycler) {
        if (cycler == null) {
            return;
        }

        // prevent the duplicated cycler
        cyclers.remove(cycler);
        cyclers.add(cycler);

        cycler.onAttached();
    }


    /**
     * Remove {@param cycler} from {@link CyclerActivity}
     */
    protected void removeCycler(Cycler cycler) {

        if (cycler == null) {
            return;
        }

        cyclers.remove(cycler);
    }


    /**
     * Add {@param presenter} in {@link CyclerActivity}
     */
    protected void addPresenter(BasePresenter presenter) {
        if (presenter == null) {
            return;
        }

        // prevent the duplicated presenter
        presenters.remove(presenter);
        presenters.add(presenter);
    }


    /**
     * Remove {@param presenter} from {@link CyclerActivity}
     */
    protected void removePresenter(BasePresenter presenter) {
        if (presenter == null) {
            return;
        }

        presenters.remove(presenter);
    }


    /**
     * Add {@param subscription} in {@link CyclerActivity}
     */
    protected void addSubscription(Subscription subscription) {
        if (subscription == null) {
            return;
        }

        subscriptions.add(subscription);
    }


    /**
     * Remove {@param subscription} from {@link CyclerActivity}
     */
    protected void removeSubscription(Subscription subscription) {
        if (subscription == null) {
            return;
        }

        subscriptions.remove(subscription);
    }


    /**
     * Set {@param unbinder} in {@link CyclerActivity} after {@link #setContentView(int)}
     */
    public void setUnbinder(Unbinder unbinder) {
        this.unbinder = unbinder;
    }


    @Override
    protected void onStart() {
        super.onStart();
        stopped = false;

        for (Cycler cycler : cyclers) {
            cycler.onLifeCycleStarted();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Cycler cycler : cyclers) {
            cycler.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onStop() {
        for (Cycler cycler : cyclers) {
            cycler.onLifeCycleStopped();
        }

        stopped = true;
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        for (Cycler cycler : cyclers) {
            cycler.onLifeCycleDestroyed();
        }

        cyclers.clear();

        for (BasePresenter presenter : presenters) {
            presenter.stopRequest();
        }

        if (unbinder != null) {
            unbinder.unbind();
        }

        subscriptions.unsubscribe();

        super.onDestroy();
    }

}
