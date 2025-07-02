@echo off
echo ========================================
echo   LOADING SAMPLE DATA TO DATABASE
echo ========================================
echo.

echo Compiling and running data loader...
mvn compile exec:java -Dexec.mainClass="com.bookkeeping.util.SampleDataLoader"

echo.
echo Sample data loading completed!
echo.
echo Data yang telah dimuat:
echo - Chart of Accounts (CoA) lengkap
echo - Transaksi modal awal
echo - Transaksi pembelian peralatan
echo - Transaksi penjualan tunai dan kredit
echo - Transaksi beban-beban operasional
echo.
echo Silakan jalankan aplikasi untuk melihat data.
pause
