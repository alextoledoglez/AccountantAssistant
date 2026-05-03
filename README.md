## Intro

<p align="center"> 
  <img src="app/screenshots/Accountant assistant - Overview.png"/>
</p>

All do you need to organize your finances: cards, purchases, bills to pay...etc.
This project is built using **Jetpack Compose**, providing a modern, reactive, and fluid user interface.

## Home feature

<p align="center"> 
  <img width = "250" src="app/screenshots/Accountant Assistant - Home.png"/>
</p>

Financial state summary: you can consult simples stats (but useful) based on: your incomes,
expenses period and total expenses. The goal is you know if with that budget you can pay your
expenses (purchases, bills,...), according to the chosen period.

## Wallet feature

<p align="center"> 
  <img width = "250" src="app/screenshots/Accountant Assistant - Wallet.png"/>
</p>

Digital wallet: where you define your credit/debit cards or your incoming in general. Saving your
available budget to show an assertive financial state summary at Home screen.

## Buys feature

<p align="center"> 
  <img width = "250" src="app/screenshots/Accountant Assistant - Buys.png"/>
</p>

When the person goes to the market to buy, they usually carry a paper list with the things they
needed and that list, most of times, is very similar, it does not vary much, so, he always has to
write everything on a piece of paper and then, as he picks up the products at the market, he
crosses off the items from the paper to calculate the sum with the smartphone calculator.

Workaround of this process is done with the smartphone notes app, but likewise, every time you
have to be editing the notes, adjusting prices and once again you have the job of adding
everything manually in the smartphone calculator to compute the sum.

Our app gathers all these functionality. The idea is semi-automate this process giving you a
shopping list, as you pick up the products, you mark with a switch that considers said product in
the sum and thus, at the end of marking the desired, you get the total to pay. You can also adjust
the quantities of products according to the money you have at that time, you prioritize what
product you carry and what not.

Use the **scan** button to read a product barcode and auto-fill its name, or the **+** button to
add items manually.

## Bills feature

<p align="center"> 
  <img width = "250" src="app/screenshots/Accountant Assistant - Bills.png"/>
</p>

Periodically bills: water bills, electricity, internet ...This functionality is very similar to
the shopping list, with smalls differences, since bills have an expiration date and, so as not to
forget them, the app notify to you before the expiration of a bill. You can switch on/off bills
according to your plan to pay them and leave saved for next month or desired date.

Use the **scan** button to read a QR code from a bill slip and auto-fill company and payment
details, or the **+** button to add bills manually.

## Scanner Flow Feature

<p align="center"> 
  <img width = "250" src="app/screenshots/Accountant Assistant - Scanner.png"/>
</p>

A powerful tool to streamline data entry for both **Buys** and **Bills**.

- **Multi-Code Support**: Scan both QR Codes and Barcodes using ML Kit.
- **Smart Data Enrichment**:
    - **For Buys**: Automatically identifies product names from barcodes, saving you the effort of manual typing.
    - **For Bills**: Recognizes company information and payment details directly from the scanned code.
- **Seamless Integration**: Once scanned, items are instantly added to your lists with their respective details.
- **Manual Entry Option**: Quick toggle to manual entry if preferred.

Built with a fully reactive flow using **Compose** and **StateFlow**, ensuring a smooth transition from scanning to item management.