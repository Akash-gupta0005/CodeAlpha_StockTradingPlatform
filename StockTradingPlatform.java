import java.util.*;

// Represents a stock with symbol, name, and price
class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return symbol + " (" + name + ") - $" + String.format("%.2f", price);
    }
}

// Simulates the stock market and manages stocks
class Market {
    private List<Stock> stocks;
    private Random random;

    public Market() {
        stocks = new ArrayList<>();
        random = new Random();
        // Add some sample stocks
        stocks.add(new Stock("AAPL", "Apple Inc.", 170.00));
        stocks.add(new Stock("GOOGL", "Alphabet Inc.", 2800.00));
        stocks.add(new Stock("MSFT", "Microsoft Corp.", 320.00));
        stocks.add(new Stock("TSLA", "Tesla Inc.", 700.00));
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public Stock getStockBySymbol(String symbol) {
        for (Stock stock : stocks) {
            if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                return stock;
            }
        }
        return null;
    }

    // Simulate price changes for all stocks
    public void updatePrices() {
        for (Stock stock : stocks) {
            double changePercent = (random.nextDouble() - 0.5) * 0.1; // -5% to +5%
            double newPrice = stock.getPrice() * (1 + changePercent);
            stock.setPrice(Math.max(1.0, newPrice)); // Price can't go below $1
        }
    }
    
    public void displayMarket() {
        System.out.println("\n--- Market Data ---");
        for (Stock stock : stocks) {
            System.out.println(stock);
        }
    }
}

// Represents a user's portfolio (holdings and performance)
class Portfolio {
    private Map<String, Integer> holdings; // symbol -> shares

    public Portfolio() {
        holdings = new HashMap<>();
    }

    public void addStock(String symbol, int shares) {
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + shares);
    }

    public void removeStock(String symbol, int shares) {
        int current = holdings.getOrDefault(symbol, 0);
        if (current <= shares) {
            holdings.remove(symbol);
        } else {
            holdings.put(symbol, current - shares);
        }
    }

    public int getShares(String symbol) {
        return holdings.getOrDefault(symbol, 0);
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    public double getTotalValue(Market market) {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = market.getStockBySymbol(entry.getKey());
            if (stock != null) {
                total += stock.getPrice() * entry.getValue();
            }
        }
        return total;
    }

    public void display(Market market) {
        System.out.println("\n--- Portfolio ---");
        if (holdings.isEmpty()) {
            System.out.println("No holdings.");
            return;
        }
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = market.getStockBySymbol(entry.getKey());
            if (stock != null) {
                System.out.println(stock + ", Shares: " + entry.getValue());
            }
        }
        System.out.println("Total Portfolio Value: $" + String.format("%.2f", getTotalValue(market)));
    }
}

// Represents a user with a portfolio and cash balance
class User {
    private String name;
    private double cash;
    private Portfolio portfolio;
    private List<Transaction> transactions;

    public User(String name, double initialCash) {
        this.name = name;
        this.cash = initialCash;
        this.portfolio = new Portfolio();
        this.transactions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public double getCash() {
        return cash;
    }

    public void addCash(double amount) {
        cash += amount;
    }

    public boolean deductCash(double amount) {
        if (cash >= amount) {
            cash -= amount;
            return true;
        }
        return false;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public boolean buyStock(Market market, String symbol, int shares) {
        Stock stock = market.getStockBySymbol(symbol);
        if (stock == null || shares <= 0) return false;
        double totalCost = stock.getPrice() * shares;
        if (cash >= totalCost) {
            cash -= totalCost;
            portfolio.addStock(symbol, shares);
            transactions.add(new Transaction("BUY", symbol, shares, stock.getPrice()));
            return true;
        }
        return false;
    }

    public boolean sellStock(Market market, String symbol, int shares) {
        Stock stock = market.getStockBySymbol(symbol);
        if (stock == null || shares <= 0) return false;
        int owned = portfolio.getShares(symbol);
        if (owned >= shares) {
            double totalProceeds = stock.getPrice() * shares;
            cash += totalProceeds;
            portfolio.removeStock(symbol, shares);
            transactions.add(new Transaction("SELL", symbol, shares, stock.getPrice()));
            return true;
        }
        return false;
    }

    public void display(Market market) {
        System.out.println("\nUser: " + name);
        System.out.println("Cash Balance: $" + String.format("%.2f", cash));
        portfolio.display(market);
        System.out.println("\n--- Transaction History ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }
    }
}

// Represents a buy/sell transaction
class Transaction {
    private String type; // "BUY" or "SELL"
    private String symbol;
    private int shares;
    private double price;
    private Date date;

    public Transaction(String type, String symbol, int shares, double price) {
        this.type = type;
        this.symbol = symbol;
        this.shares = shares;
        this.price = price;
        this.date = new Date();
    }

    @Override
    public String toString() {
        return date + ": " + type + " " + shares + " shares of " + symbol + " at $" + String.format("%.2f", price);
    }
}
// Main class for the Stock Trading Platform
public class StockTradingPlatform {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Market market = new Market();
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        User user = new User(name, 10000.0); // Start with $10,000
        System.out.println("\nWelcome to the Stock Trading Platform, " + name + "!");

        while (true) {
            System.out.println("\n-----------------------------");
            System.out.println("1. View Market Data");
            System.out.println("2. View Portfolio");
            System.out.println("3. Buy Stock");
            System.out.println("4. Sell Stock");
            System.out.println("5. Update Market Prices");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = -1;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }
            switch (choice) {
                case 1:
                    market.displayMarket();
                    break;
                case 2:
                    user.display(market);
                    break;
                case 3:
                    market.displayMarket();
                    System.out.print("Enter stock symbol to buy: ");
                    String buySymbol = sc.nextLine().toUpperCase();
                    System.out.print("Enter number of shares: ");
                    int buyShares = Integer.parseInt(sc.nextLine());
                    if (user.buyStock(market, buySymbol, buyShares)) {
                        System.out.println("Successfully bought " + buyShares + " shares of " + buySymbol);
                    } else {
                        System.out.println("Buy failed. Check symbol, shares, or cash balance.");
                    }
                    break;
                case 4:
                    user.getPortfolio().display(market);
                    System.out.print("Enter stock symbol to sell: ");
                    String sellSymbol = sc.nextLine().toUpperCase();
                    System.out.print("Enter number of shares: ");
                    int sellShares = Integer.parseInt(sc.nextLine());
                    if (user.sellStock(market, sellSymbol, sellShares)) {
                        System.out.println("Successfully sold " + sellShares + " shares of " + sellSymbol);
                    } else {
                        System.out.println("Sell failed. Check symbol or number of shares owned.");
                    }
                    break;
                case 5:
                    market.updatePrices();
                    System.out.println("Market prices updated!");
                    break;
                case 6:
                    System.out.println("Thank you for using the Stock Trading Platform. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
