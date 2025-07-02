package com.bookkeeping.entity;

/**
 * Enum untuk jenis-jenis akun dalam Chart of Accounts
 */
public enum AccountType {
    ASSET("Asset", "Harta"),
    LIABILITY("Liability", "Hutang"),
    EQUITY("Equity", "Modal"),
    REVENUE("Revenue", "Pendapatan"),
    EXPENSE("Expense", "Beban"),
    COST_OF_GOODS_SOLD("Cost of Goods Sold", "Harga Pokok Penjualan");

    private final String englishName;
    private final String indonesianName;

    AccountType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    @Override
    public String toString() {
        return englishName;
    }

    /**
     * Mengecek apakah akun ini adalah debit normal
     */
    public boolean isDebitNormal() {
        return this == ASSET || this == EXPENSE || this == COST_OF_GOODS_SOLD;
    }

    /**
     * Mengecek apakah akun ini adalah credit normal
     */
    public boolean isCreditNormal() {
        return this == LIABILITY || this == EQUITY || this == REVENUE;
    }
}
