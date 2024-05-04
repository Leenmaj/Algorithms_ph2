import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PortfolioAllocatorDP {
    static List<Asset> assetList = new ArrayList<>();
    static double[][] DP; // table for returns
    static double[][] riskDP; //table for risks
    static int[][] choiceDP; //table for chociese (how many units to allocte for each asset)
    static double riskTolerance;
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Enter the File name:");
        String fileName = input.next();
        assetList = readFromFile(fileName);

        System.out.println("Asset List:");
        printAssetList(assetList);

        System.out.println("Enter total investment amount:");
        int totalInvestment = input.nextInt();

        System.out.println("Enter risk tolerance level:");
        riskTolerance = input.nextDouble();
        allocateInvestment(totalInvestment);
        //printDPTables(); this is to the see tables contents
  
    }
    
    private static void printDPTables() {
        System.out.println("DP Table:");
        for (int i = 0; i < DP.length; i++) {
            for (int j = 0; j < DP[i].length; j++) {
                System.out.printf("%5.4f ", DP[i][j]);
            }
            System.out.println();
        }
        
        System.out.println("\nriskDP Table:");
        for (int i = 0; i < riskDP.length; i++) {
            for (int j = 0; j < riskDP[i].length; j++) {
                System.out.printf("%5.4f ", riskDP[i][j]);
            }
            System.out.println();
        }
        System.out.println("\nchoice Table:");
        for (int i = 0; i < choiceDP.length; i++) {
            for (int j = 0; j < choiceDP[i].length; j++) {
                System.out.printf("%5d ", choiceDP[i][j]);
            }
            System.out.println();
        }
    }
    


    public static void allocateInvestment(int totalInvestment) {
        // initialize dp tables for returns, risks, and choices
        DP = new double[assetList.size() + 1][totalInvestment + 1];
        riskDP = new double[assetList.size() + 1][totalInvestment + 1];
        choiceDP = new int[assetList.size() + 1][totalInvestment + 1];
    
        // iterate over each asset in the list
        for (int i = 1; i <= assetList.size(); i++) {
            Asset asset = assetList.get(i - 1); // get current asset
    
            // consider each possible investment level from 0 to total investment
            for (int j = 0; j <= totalInvestment; j++) {
                // try different quantities for the current asset
                for (int quantity = 0; quantity <= asset.quantity && quantity * 1 <= j; quantity++) {
                    double weight = (double) quantity / totalInvestment; // calculate weight of current asset quantity
                    double addReturn = weight * asset.expectedReturn; // calculate additional return from this quantity
                    double addRisk = weight * asset.riskLevel; // calculate additional risk from this quantity
    
                    // calculate potential return and risk with this quantity
                    double potReturn = DP[i - 1][j - quantity * 1] + addReturn;
                    double potRisk = riskDP[i - 1][j - quantity * 1] + addRisk;
                  
        
                    // update tables if this choice improves return without exceeding risk tolerance
                    if (potRisk <= riskTolerance && potReturn > DP[i][j]) {
                        DP[i][j] = potReturn; // update return table
                        riskDP[i][j] = potRisk; // update risk table
                        choiceDP[i][j] = quantity; // record this choice
                    }
                }
            }
        }
    
        // start backtracking to find optimal allocation
        int remInv = totalInvestment; // track remaining investment during backtracking
        List<Integer> optimalAllocation = new ArrayList<>();
        // backtrack from the last asset
        for (int i = assetList.size(); i > 0; i--) {
            int quantity = choiceDP[i][remInv]; // get chosen quantity for this asset
            optimalAllocation.add(0, quantity); // add it to optimal allocation
            remInv -= quantity * 1; // reduce remaining investment
        }
    
        // calculate final return and risk using optimal allocation
        double finalReturn = calculatePortfolio(optimalAllocation, false, totalInvestment);
        double finalRisk = calculatePortfolio(optimalAllocation, true, totalInvestment);
    
        // print optimal allocation, return, and risk
        System.out.println("optimal allocation: ");
        for (int i = 0; i < optimalAllocation.size(); i++) {
            System.out.println("asset " + assetList.get(i).id + ": " + optimalAllocation.get(i) + " units");
        }
        System.out.println("optimal return: " + finalReturn);
        System.out.println("optimal risk: " + finalRisk);
    }
    
  
    public static List<Asset> readFromFile(String fileName) {
        List<Asset> assetsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" : ");
                if (parts.length == 4) {
                    String assetID = parts[0].trim();
                    double expectedReturn = Double.parseDouble(parts[1].trim());
                    double riskLevel = Double.parseDouble(parts[2].trim());
                    int cost = Integer.parseInt(parts[3].trim());

                    Asset asset = new Asset(assetID, expectedReturn, riskLevel, cost);
                    assetsList.add(asset);
                } else {
                    System.err.println("Invalid format in line: " + line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error while reading file: " + e.getMessage());
            System.exit(1);
        }
        return assetsList;
    }
    public static double calculatePortfolio(List<Integer> allocatedAssets, boolean isRiskLevel, int totalUnits) {
        double result = 0;
        double weight;
     
        for (int i = 0; i < allocatedAssets.size(); i++) {
            if (i < assetList.size()) {
                weight = (double) allocatedAssets.get(i) / (double) totalUnits;
                  // calculates the return or risk based on the isRiskLevel flag
                if (isRiskLevel) {
                    result += weight * assetList.get(i).riskLevel;
                } else {
                    result += weight * assetList.get(i).expectedReturn;
                }
            }
        }
        
        return result; 
    }

    public static void printAssetList(List<Asset> assetsList) {// check if read correctly from file
        if (assetsList.isEmpty()) {
            System.out.println("The asset list is empty.");
        } else {
            for (Asset asset : assetsList) {
                System.out.println("Asset ID: " + asset.id +
                        ", Expected Return: " + asset.expectedReturn +
                        ", Risk Level: " + asset.riskLevel +
                        ", quantity: " + asset.quantity
                        );
            }
        }
    }

}



