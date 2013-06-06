package br.com.condesales.listeners;

import java.util.ArrayList;

import br.com.condesales.models.Tip;

public interface TipsResquestListener extends ErrorListener {
	
	public void onTipsFetched(ArrayList<Tip> tips);
	
}
