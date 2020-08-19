package com.example.ver2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Post> postArrayLists;
    private EditText editText;
    private AdapterRecyclerView adapterRecycleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        editText = findViewById(R.id.editText);
        Loader();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchData(editable.toString());
            }
        });


    }

    private void searchData(String s){
        ArrayList<Post> filterList = new ArrayList<>();

        for(Post item : postArrayLists){
            if(item.getTitle().toLowerCase().contains(s.toLowerCase())){
                filterList.add(item);
            }
        }
        adapterRecycleView.filterList(filterList);
    }

    public void Loader() {

        Log.i("REMEMBER","Loader");
        Retrofit retrofit = MyRetrofit.getInstance("https://jsonplaceholder.typicode.com");
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getAllPost().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postList = response.body();
                ConvertData(postList);
//                Log.i("KAKAK", postList.size() + "");
//                ArrayList<Post> postArrayList = new ArrayList<>();
//                postArrayList.addAll(postList);
//                for (int i = 0; i < postArrayList.size(); i++) {
//                    Log.i("HIHI", postArrayList.get(i).getTitle());
//                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });
    }

    private void ConvertData(List<Post> postList) {
        Log.i("REMEMBER","ConvertData");
        postArrayLists = new ArrayList<>();
        postArrayLists.addAll(postList);
        ListView(postArrayLists);
    }


    private void ListView(ArrayList<Post> postArrayList) {

        Log.i("REMEMBER","ListView");
        //      swipeRefreshLayout.setRefreshing(true);
        recyclerView.setHasFixedSize(true);
        Context context;
        LinearLayout linearLayout = new LinearLayout(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        adapterRecycleView = new AdapterRecyclerView(this, postArrayList);
        recyclerView.setAdapter(adapterRecycleView);
        AdapterRecyclerView.ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new AdapterRecyclerView.ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_progressbar);

                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ProgressBar progressBar = dialog.findViewById(R.id.progressMy);
                progressBar.setIndeterminate(true);
                progressBar.setProgress(100);

            }
        });
        //    swipeRefreshLayout.setRefreshing(false);

    }
}