package com.udafil.dhruvamsharma.bakingandroidapp.detail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.udafil.dhruvamsharma.bakingandroidapp.data.model.RecipeModel;

public class DetailActivityViewModel extends AndroidViewModel {


    private RecipeModel ModelRecipe;
    private int PositionStep;

    private int IndexWindow= 0;
    private long PlayBackPosition = 0;
    private boolean playWhenReady = true;

    private int mStepPositionPrevious = -1;
    private boolean isLandscape = false;

    public DetailActivityViewModel(@NonNull Application application) {
        super(application);

    }

    public void init() {
        IndexWindow= 0;
        PlayBackPosition = 0;
        playWhenReady = false;
        mStepPositionPrevious = -1;

    }


    public boolean isLandscape() {
        return isLandscape;
    }

    public void setLandscape(boolean landscape) {
        isLandscape = landscape;
    }

    public int getWindowIndex() {
        return IndexWindow;
    }

    public void setWindowIndex(int windowIndex) {
        this.IndexWindow= windowIndex;
    }

    public long getPlayBackPosition() {
        return PlayBackPosition;
    }

    public void setPlayBackPosition(long PlayBackPosition) {
        this.PlayBackPosition = PlayBackPosition;
    }

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
    }


    public int getmStepPositionPrevious() {
        return mStepPositionPrevious;
    }

    public void setmStepPositionPrevious(int mStepPositionPrevious) {
        this.mStepPositionPrevious = mStepPositionPrevious;
    }


    public RecipeModel getRecipeModel() {
        return ModelRecipe;
    }

    public void setRecipeModel(RecipeModel ModelRecipe) {
        this.ModelRecipe = ModelRecipe;
    }

    public int getStepPosition() {
        return PositionStep;
    }

    public void setStepPosition(int PositionStep) {
        this.PositionStep = PositionStep;
    }




}
