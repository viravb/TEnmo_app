package com.techelevator;

import com.techelevator.exceptions.InvalidNumberException;
import com.techelevator.exceptions.InvalidSkuSelectedException;
import com.techelevator.filereader.LogFileWriter;
import com.techelevator.items.CandyStoreItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/*
    This class should encapsulate all the functionality of the Candy Store application, meaning it should
    contain all the "work"
 */
public class CandyStore {

    Account account = new Account();
    Inventory inventory = new Inventory();
    private Map<String,Integer> inventoryWithQuantity;
    private Map<String, CandyStoreItem> inventoryWithProperties;
    Cart cart = new Cart();
    Receipt receipt = new Receipt(cart.getCurrentCart());
    Change change = new Change();

    LogFileWriter logFileWriter = new LogFileWriter();

    public CandyStore() throws IOException {
    }

    public void initialPopulateInventory() throws FileNotFoundException {
        this.inventoryWithProperties = inventory.populateInventory();
        this.inventoryWithQuantity = inventory.populateQuantities();
    }

    public double accountBalance(){
        return account.getBalance();
    }

    public Map<String, Integer> getInventoryWithQuantity() {
        return inventoryWithQuantity;
    }
    public Map<String, CandyStoreItem> getInventoryWithProperties() {
        return inventoryWithProperties;
    }

    public void takeMoney(double amount) throws InvalidNumberException, IOException {
        account.deposit(amount);
        logFileWriter.logEntryMoneyAdded(amount, account.getBalance());
    }

    public void selectProductsProcess(String sku, int quantityToBePurchased) throws InvalidNumberException, InvalidSkuSelectedException, IOException {
        if(inventoryWithProperties.containsKey(sku)) {
            int currentlyInStock = inventoryWithQuantity.get(sku);
            double purchaseAmount = inventoryWithProperties.get(sku).getPrice() * quantityToBePurchased;

            if (currentlyInStock >= quantityToBePurchased) {
                if(purchaseAmount <= account.getBalance()){
                    removeFromInventory(sku, quantityToBePurchased);
                    account.withdraw(purchaseAmount);
                    cart.addToCart(sku,quantityToBePurchased);
                    logFileWriter.logEntryItemAdded(quantityToBePurchased, inventoryWithProperties.get(sku).getName(),sku,
                           purchaseAmount, account.getBalance());
                } else {
                    throw new InvalidNumberException("Not enough funds to complete purchase");
                }
            } else{
                throw new InvalidNumberException("Not enough quantity in stock for your selection");
            }
        } else{
            throw new InvalidSkuSelectedException("Please Enter In a Valid Sku");
        }

    }

    private void removeFromInventory(String sku, int amountToRemove){
        int currentlyInStock = inventoryWithQuantity.get(sku);
        int newQuantity = currentlyInStock - amountToRemove;

        inventoryWithQuantity.put(sku,newQuantity);

    }

    public double getTotalCost(){
        return receipt.getTotalCost(inventoryWithProperties);
    }


    public Change getChangeObject(){
        return change;
    }

    public Cart getCart() {
        return cart;
    }

    public double getBalance(){
        return account.getBalance();
    }

    public void makeChange() throws IOException {
        change.determineChange(account.getBalance());
        logFileWriter.logEntryMakeChange(account.getBalance());
    }

    public void reset(){
        account = new Account();
        cart = new Cart();
        receipt = new Receipt();
        change = new Change();
    }
}
