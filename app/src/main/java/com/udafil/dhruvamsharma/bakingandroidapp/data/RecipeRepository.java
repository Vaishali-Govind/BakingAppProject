package com.udafil.dhruvamsharma.bakingandroidapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.udafil.dhruvamsharma.bakingandroidapp.R;
import com.udafil.dhruvamsharma.bakingandroidapp.data.model.RecipeModel;
import com.udafil.dhruvamsharma.bakingandroidapp.utils.GsonInstance;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


final public class RecipeRepository {

    private static RecipeRepository recipeRepository;
    private List<RecipeModel> model;

    private RecipeRepository() {

    }


    
    public void getRecipeData(Context context) throws IOException {


        //initializing FAN library for network requests
        AndroidNetworking.initialize(context);

        AndroidNetworking.get(context.getResources().getString(R.string.list_recipe))
                .setPriority(Priority.HIGH)
                .build()
                .getAsObjectList(RecipeModel.class, new ParsedRequestListener<List<RecipeModel>>() {
                    @Override
                    public void onResponse(List<RecipeModel> respond) {

                        Toast.makeText(context, respond.get(0).getIngredients().get(0).getIngredient() + "here ", Toast.LENGTH_SHORT).show();

                        //method to set the respond to the list because final variable couldn't do much!
                        setValue(respond);
                        storeRecipeDataInFile(context, respond);

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });




    }
    
    private void storeRecipeDataInFile(Context context, List<RecipeModel> model) {

        SharedPreferences prefer = context.getSharedPreferences(context.getString(R.string.RECIPE_DATA_PREFERENCE_FILE), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefer.edit();

        Set<String> dataset = new HashSet<>();

        for (int i = 0; i < model.size(); i++) {

            RecipeModel recipeModel = model.get(i);
            String json = GsonInstance.getGsonInstance().toJson(recipeModel);

            dataset.add(json);


        }

        edit.putStringSet( context.getString(R.string.ingredients_recipe), dataset);



        edit.apply();

    }

    public String getRecipe(int modelPosition, Context context) {

        Set<String> dataset;

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.RECIPE_DATA_PREFERENCE_FILE), Context.MODE_PRIVATE);
        dataset = sharedPreferences.getStringSet(context.getString(R.string.ingredients_recipe), null);


        Iterator<String> iterator;
        String respond = null;
        int pos = 1;


        if (dataset != null) {

            iterator = dataset.iterator();

            while(iterator.hasNext()) {

                respond = iterator.next();

                if(pos == modelPosition) {

                    break;
                }

                pos++;
            }

        }


        return respond;

    }

    public Set<String> getRecipeSet(Context context) {


        Set<String> dataset;

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.RECIPE_DATA_PREFERENCE_FILE), Context.MODE_PRIVATE);
        dataset = sharedPreferences.getStringSet(context.getString(R.string.ingredients_recipe), null);

        return dataset;

    }


    /**
     * This methods makes the RecipeRepository class a singleton
     * @RecipeRepository
     */
    public static RecipeRepository getInstance() {

        if( recipeRepository == null ) {
            recipeRepository = new RecipeRepository();
        }

        return recipeRepository;
    }


    public void setValue(List<RecipeModel> value) {
        this.model = value;
    }
}
