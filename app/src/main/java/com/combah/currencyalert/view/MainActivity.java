package com.combah.currencyalert.view;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.combah.currencyalert.R;
import com.combah.currencyalert.content.SyncManager;
import com.combah.currencyalert.databinding.ActivityMainBinding;
import com.combah.currencyalert.viewmodel.MainViewModel;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> currencies = new HashSet<>();
        currencies.add("BRL=X");
        editor.putStringSet("currencies", currencies);
        editor.apply();

        SyncManager manager = new SyncManager(this);
        manager.getSyncAccount();

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        MainViewModel viewModel = new MainViewModel(this);
        CurrenciesAdapter adapter = new CurrenciesAdapter(viewModel);
        binding.currencyList.setLayoutManager(new LinearLayoutManager(this));
        binding.currencyList.setAdapter(adapter);
    }
}
