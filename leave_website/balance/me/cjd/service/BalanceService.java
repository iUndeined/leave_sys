package me.cjd.service;

import me.cjd.pojo.Balance;

public class BalanceService {
	
	public final static Balance findByEmplId(int id){
		return Balance.me.findFirst("select * from balance i where i.empl_id = ? ", id);
	}
	
}
