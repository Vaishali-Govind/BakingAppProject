package com.udafil.dhruvamsharma.bakingandroidapp.detail;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udafil.dhruvamsharma.bakingandroidapp.R;
import com.udafil.dhruvamsharma.bakingandroidapp.data.model.RecipeModel;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    private PlayerView mViewPlayer;
    private SimpleExoPlayer mPlayerExo;

    private static final DefaultBandwidthMeter DEFAULT_BANDWIDTH_METER = new DefaultBandwidthMeter();

    private TextView TextDescription;
    private FloatingActionButton RecipeButtonChange;
    private ImageView mNoImageViewFood;

    private ImageView mFullScreenIcon;

    private DetailActivityViewModel mActivityViewModelDetail;


    /*** TODO
     * Handle the small member variable data in onSaveInstanceState or View Models in almost every activity.
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

       //Getting the instance of View Model for This activity
        mActivityViewModelDetail = ViewModelProviders.of(this).get(DetailActivityViewModel.class);

        //Getting intent from Recipe Detail Activity
        Intent intent = getIntent();

        if(intent.hasExtra(getPackageName()) && intent.hasExtra("position") && savedInstanceState == null) {

            //setting data for the view model
            mActivityViewModelDetail.setStepPosition(
                    intent.getIntExtra("position", 0));
            mActivityViewModelDetail.setRecipeModel(
                    Parcels.unwrap(intent.getParcelableExtra(getPackageName())));

        }

        setUpActivity();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putBoolean("isPlaying", mActivityViewModelDetail.isPlayWhenReady());

    }

    /**
     * This method takes care of setting up the activity
     */
    private void setUpActivity() {

        mNoImageViewFood = findViewById(R.id.no_video_image_detail_iv);

        mViewPlayer = findViewById(R.id.video_view);
        TextDescription = findViewById(R.id.description_for_step_tv);

        RecipeButtonChange = findViewById(R.id.chnage_recipe_step_btn);
        mFullScreenIcon = mViewPlayer.findViewById(R.id.exo_fullscreen_icon);


        //This method checks if the phone is in portrait or landscape mode
        setPortraitOrLandscapeConfigurations();


        handleActivityInteractions();

    }

    private void setPortraitOrLandscapeConfigurations() {

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mFullScreenIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_skrink));

            RecipeButtonChange.setVisibility(View.GONE);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            //Set the playerView to full screen width and height
            android.view.ViewGroup.LayoutParams params = mViewPlayer.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mViewPlayer.setLayoutParams(params);



        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            RecipeButtonChange.setVisibility(View.VISIBLE);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            //Set the playerView to full screen width and height
            android.view.ViewGroup.LayoutParams params = mViewPlayer.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = 800;
            mViewPlayer.setLayoutParams(params);


            mFullScreenIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_expand));
        }

    }


    /**
     * This method handles all the interactions happening inside the activity
     */
    private void handleActivityInteractions() {

        mFullScreenIcon.setOnClickListener(view -> {

            toggleFullScreen();

        });


        // This portion of code handles the changing of recipe step
        RecipeButtonChange.setOnClickListener((view -> {

            if( mActivityViewModelDetail.getStepPosition() < mActivityViewModelDetail.getRecipeModel().getSteps().size()-1) {

                mActivityViewModelDetail.setmStepPositionPrevious( mActivityViewModelDetail.getStepPosition());
                mActivityViewModelDetail.setStepPosition( mActivityViewModelDetail.getmStepPositionPrevious() + 1);
            }
            else {

                mActivityViewModelDetail.setStepPosition(0);
                mActivityViewModelDetail.setmStepPositionPrevious(-1);
            }

            Log.e("step position after", mActivityViewModelDetail.getStepPosition()+"");
            releasePlayer();
            initializePlayer();



        }));

    }


    /**
     * This method initializes the exo player as and when required
     */
    private void initializePlayer() {

        if( mActivityViewModelDetail.getRecipeModel() != null && mActivityViewModelDetail.getmStepPositionPrevious() != mActivityViewModelDetail.getStepPosition()) {

            TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(DEFAULT_BANDWIDTH_METER);

            mPlayerExo = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());

            mViewPlayer.setPlayer(mPlayerExo);

            MediaSource mediaSource = buildMediaSource();

            mActivityViewModelDetail.setPlayWhenReady(true);

            /** TODO
             * Dynamically create constraints depending upon the whether the video is there or not!
             *
             **/


            if( mediaSource == null ) {

                mViewPlayer.setVisibility(View.INVISIBLE);
                mNoImageViewFood.setVisibility(View.VISIBLE);


            } else {

                mViewPlayer.setVisibility(View.VISIBLE);
                mNoImageViewFood.setVisibility(View.INVISIBLE);

                mPlayerExo.prepare(mediaSource, true, false);

                mPlayerExo.setPlayWhenReady( mActivityViewModelDetail.isPlayWhenReady());
                mPlayerExo.seekTo( mActivityViewModelDetail.getWindowIndex(), mActivityViewModelDetail.getPlayBackPosition());
            }


            TextDescription.setText( mActivityViewModelDetail.getRecipeModel().getSteps().get( mActivityViewModelDetail.getStepPosition()).getDescription());

        }
        else {
            //TODO handle error condition

        }



    }


    private String parseDescriptionForRecipe() {

        String[] linesOfDescription = mActivityViewModelDetail.getRecipeModel().getSteps().get( mActivityViewModelDetail.getStepPosition()).getDescription().split(".");

        StringBuilder parsedDescription = new StringBuilder();



        boolean success;

        for( String line : linesOfDescription) {

            parsedDescription.append(line);



            Toast.makeText(this, line, Toast.LENGTH_SHORT).show();

        }

        return new String(parsedDescription);

    }


    /**
     * his method is called by initializePlayer method and returns a Media source
     * @return
     */
    private MediaSource buildMediaSource() {

        if ( mActivityViewModelDetail.getRecipeModel().getSteps().get( mActivityViewModelDetail.getStepPosition()).getVideoURL().equals("")) {
            return null;
        }

        Uri uri = Uri.parse( mActivityViewModelDetail.getRecipeModel().getSteps().get( mActivityViewModelDetail.getStepPosition()).getVideoURL());

        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(Util.getUserAgent(this, getResources().getString(R.string.app_name)))).
                createMediaSource(uri);


    }


    /**
     * Toggle full screen in phones
     */
    private void toggleFullScreen() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {


            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(getResources().getConfiguration().orientation == newConfig.orientation) {

        } else {

        }

    }

    /**
     * Capturing the playback position, playWhenReady and windowIndex when teh app goes offScreen
     * and releasing the shared resources.
     */
    private void releasePlayer() {
        if (mPlayerExo != null) {

            mActivityViewModelDetail.setPlayBackPosition(mPlayerExo.getCurrentPosition());
            mActivityViewModelDetail.setPlayWhenReady(false);
            mActivityViewModelDetail.setWindowIndex(mPlayerExo.getCurrentWindowIndex());

            mActivityViewModelDetail.setmStepPositionPrevious( mActivityViewModelDetail.getmStepPositionPrevious());
            mActivityViewModelDetail.setStepPosition( mActivityViewModelDetail.getStepPosition());

            //Toast.makeText(getApplicationContext(), mActivityViewModelDetail.getStepPosition()+" in release", Toast.LENGTH_SHORT).show();


            mPlayerExo.release();
            mPlayerExo = null;

        }
    }




    /**
     * Handling releasing player nd codecs properly and gaining them as soon as in onStart.
     * Since API 24, Multiwindow concept came into play so initializing the player in onStart rather than in onResume
     */
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    /**
     * Initializing player in onResume for API > 24
     */
    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || mPlayerExo == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        mViewPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**
     * Releasing player in onResume before API 24
     */
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    /**
     *  Multiwindow concept.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
