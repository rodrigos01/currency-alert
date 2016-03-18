package com.combah.currencyalert.view;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.combah.currencyalert.R;
import com.combah.currencyalert.content.Provider;
import com.combah.currencyalert.content.SyncManager;
import com.combah.currencyalert.databinding.ActivityMainBinding;
import com.combah.currencyalert.viewmodel.MainViewModel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SyncManager manager;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new SyncManager(this);
        manager.getSyncAccount();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        binding.refresher.setOnRefreshListener(this);

        MainViewModel viewModel = new MainViewModel(this);
        CurrenciesAdapter adapter = new CurrenciesAdapter(viewModel);
        binding.currencyList.setLayoutManager(new LinearLayoutManager(this));
        binding.currencyList.setAdapter(adapter);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrencyInput();
            }
        });
    }

    private void showCurrencyInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Currency Code");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String symbol = input.getText().toString();
                addSymbol(symbol);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addSymbol(String symbol) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> currencies = preferences.getStringSet("currencies", new LinkedHashSet<String>());
        currencies.add(symbol);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("currencies", currencies);
        editor.apply();
        manager.requestSync();
    }

    @Override
    public void onRefresh() {
        manager.requestSync();
        binding.refresher.setRefreshing(false);
    }
}
