package com.combah.currencyalert.view;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.combah.currencyalert.R;
import com.combah.currencyalert.databinding.ActivityMainBinding;
import com.combah.currencyalert.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    MainViewModel viewModel;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        binding.refresher.setOnRefreshListener(this);

        viewModel = new MainViewModel(this);
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

        // Set up the input
        final EditText input = (EditText) getLayoutInflater()
                .inflate(R.layout.add_currency_input, (ViewGroup) binding.getRoot(), false);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_currency_title))
                .setView(input)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String symbol = input.getText().toString();
                        viewModel.addSymbol(symbol);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onRefresh() {
        viewModel.sync();
        binding.refresher.setRefreshing(false);
    }
}
