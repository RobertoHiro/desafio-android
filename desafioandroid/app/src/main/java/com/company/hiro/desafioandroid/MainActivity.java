package com.company.hiro.desafioandroid;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends AppCompatActivity {

    RepositoryListFragment repositoryListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repositoryListFragment = new RepositoryListFragment();

        setContentView(R.layout.activity_main);

        GlobalVariable.imageLoaderConfig  = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(GlobalVariable.imageLoaderConfig);

        GlobalVariable.replaceFragment(this, R.id.main_content, repositoryListFragment, null, false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            GlobalVariable.orientation = getResources().getConfiguration().orientation;
            setContentView(R.layout.activity_main);
            GlobalVariable.replaceFragment(this, R.id.main_content, repositoryListFragment, null, false);
        }
        else{
            GlobalVariable.orientation = getResources().getConfiguration().orientation;
            setContentView(R.layout.activity_main);
            repositoryListFragment.SetUrlAndReplaceFragmentLandscape();
        }
    }
}
