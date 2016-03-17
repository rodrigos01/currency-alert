package com.combah.currencyalert.view;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.combah.currencyalert.R;
import com.combah.currencyalert.databinding.CurrencyCardBinding;
import com.combah.currencyalert.model.Currency;
import com.combah.currencyalert.viewmodel.CurrencyViewModel;
import com.combah.currencyalert.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by rodrigo on 3/17/16.
 */
public class CurrenciesAdapter extends RecyclerView.Adapter<CurrenciesAdapter.ViewHolder> implements Observer {

    private List<Currency> currencies;

    public CurrenciesAdapter(MainViewModel mainViewModel) {
        currencies = mainViewModel.getCurrencies();
        mainViewModel.addObserver(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CurrencyCardBinding binding = DataBindingUtil.inflate(inflater, R.layout.currency_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Currency currency = currencies.get(position);
        CurrencyViewModel viewModel = new CurrencyViewModel(currency);
        holder.binding.setViewModel(viewModel);

    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    @Override
    public void update(Observable observable, Object data) {
        currencies = (List<Currency>) data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CurrencyCardBinding binding;

        public ViewHolder(CurrencyCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
