package com.techelevator.items;

public class Chocolate extends CandyStoreItem{

    public Chocolate(String sku, String name, String hasWrapper, double price){
        super(sku, name,hasWrapper, price);
    }

    @Override
    public String getProductType(){
        return ("Chocolate Confectionery");
    }
}
