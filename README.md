# Bookkeeping Application - Complete Accounting System

Sistem akunting lengkap yang dibangun dengan JavaFX, Maven, SQLite, JPA, dan Hibernate. Aplikasi ini menyediakan fitur Chart of Accounts (CoA), manajemen transaksi, dan laporan keuangan dalam format PDF.

## Fitur Utama

### 📊 Chart of Accounts (CoA)
- Manajemen akun dengan kode, nama, tipe, dan deskripsi
- Support untuk semua tipe akun: Asset, Liability, Equity, Revenue, Expense, Cost of Goods Sold
- CRUD operations lengkap untuk akun
- Filter dan pencarian akun
- Default Chart of Accounts siap pakai

### 💼 Manajemen Transaksi
- Double-entry bookkeeping system
- Validasi otomatis untuk transaksi yang balance
- Support multiple entries per transaksi
- Pencarian dan filter transaksi berdasarkan tanggal dan deskripsi
- Auto-generated transaction numbers

### 📈 Laporan Keuangan
- **Trial Balance** - Ringkasan semua akun dengan saldo debit/credit
- **Balance Sheet** - Laporan posisi keuangan (Asset, Liability, Equity)
- **Income Statement (P&L)** - Laporan laba rugi dengan periode tertentu
- Export ke PDF untuk semua laporan
- Format laporan yang profesional

### 🎨 User Interface
- Design minimalist dengan GitHub Light theme
- Interface yang clean dan mudah digunakan
- Responsive layout
- Intuitive navigation dengan tab-based interface

## Teknologi yang Digunakan

- **JavaFX 21** - Framework UI
- **Maven** - Build tool dan dependency management
- **SQLite** - Database ringan untuk penyimpanan data
- **JPA (Jakarta Persistence API)** - ORM specification
- **Hibernate 6.4** - JPA implementation
- **iText 5.5** - PDF generation
- **Java 21** - Programming language

## Struktur Database

### Tabel `accounts`
```sql
- id (Primary Key)
- account_code (Unique)
- account_name
- account_type (ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE, COST_OF_GOODS_SOLD)
- parent_id
- balance
- description
- is_active
```

### Tabel `transactions`
```sql
- id (Primary Key)
- transaction_number (Unique)
- transaction_date
- description
- reference
- total_amount
- created_at
- updated_at
```

### Tabel `transaction_entries`
```sql
- id (Primary Key)
- transaction_id (Foreign Key)
- account_id (Foreign Key)
- debit_amount
- credit_amount
- description
```

## Instalasi dan Menjalankan Aplikasi

### Prerequisites
1. **Java 21** atau lebih tinggi
2. **Maven 3.6** atau lebih tinggi
3. **JavaFX SDK 21** - Download dari [OpenJFX](https://openjfx.io/)

### Langkah Instalasi

1. **Clone atau download project ini**

2. **Setup JavaFX SDK**
   - Extract JavaFX SDK ke `C:\Java\javafx-sdk-21.0.7\`
   - Atau sesuaikan path di `run.bat` dan `pom.xml`

3. **Compile project**
   ```bash
   mvn clean compile
   ```

4. **Load sample data (opsional)**
   ```bash
   # Untuk Windows
   load-sample-data.bat
   
   # Atau manual
   mvn compile exec:java -Dexec.mainClass="com.bookkeeping.util.SampleDataLoader"
   ```

5. **Jalankan aplikasi**
   ```bash
   # Untuk Windows
   run.bat
   
   # Atau manual
   mvn clean javafx:run
   ```

### Sample Data

Project ini menyertakan data sample yang dapat dimuat otomatis:

**Chart of Accounts:**
- Assets: Kas, Bank, Piutang, Persediaan, Peralatan, Kendaraan
- Liabilities: Hutang Usaha, Hutang Bank, Hutang Gaji, PPh, PPN
- Equity: Modal Pemilik, Laba Ditahan, Prive
- Revenue: Pendapatan Penjualan, Pendapatan Jasa
- Expenses: Beban Gaji, Listrik, Sewa, Penyusutan, dll

**Sample Transactions:**
- Modal awal Rp 50,000,000
- Pembelian peralatan Rp 5,000,000
- Penjualan tunai Rp 15,000,000
- Pembayaran gaji Rp 8,000,000
- Beban operasional
- Penjualan kredit Rp 12,000,000

### Commands Berguna

```bash
# Load sample data
load-sample-data.bat

# Clear all data
clear-data.bat

# Compile only
mvn clean compile

# Run application
run.bat
```

## Penggunaan Aplikasi

### 1. Chart of Accounts
- Buka tab "Chart of Accounts"
- Klik "New Account" untuk menambah akun baru
- Double-click pada akun untuk mengedit
- Gunakan filter untuk mencari akun berdasarkan tipe atau nama

### 2. Transaksi
- Buka tab "Transactions"
- Klik "New Transaction" untuk membuat transaksi baru
- Isi informasi transaksi (tanggal, deskripsi, referensi)
- Tambahkan entries dengan memilih akun dan amount (debit atau credit)
- Pastikan total debit = total credit sebelum save

### 3. Laporan
- Buka tab "Reports"
- Pilih tanggal untuk Trial Balance dan Balance Sheet
- Pilih periode untuk Income Statement
- Klik tombol generate untuk menghasilkan PDF

## Default Chart of Accounts

Aplikasi sudah dilengkapi dengan Chart of Accounts default:

**ASSETS (1000-1999)**
- 1100 - Cash
- 1200 - Accounts Receivable
- 1300 - Inventory
- 1400 - Prepaid Expenses
- 1510 - Equipment
- 1530 - Vehicles

**LIABILITIES (2000-2999)**
- 2100 - Accounts Payable
- 2200 - Accrued Expenses
- 2300 - Unearned Revenue
- 2510 - Bank Loan

**EQUITY (3000-3999)**
- 3100 - Capital
- 3200 - Retained Earnings
- 3300 - Owner's Drawings

**REVENUE (4000-4999)**
- 4100 - Sales Revenue
- 4200 - Service Revenue
- 4300 - Other Income

**EXPENSES (5000-5999)**
- 5100 - Salaries Expense
- 5200 - Rent Expense
- 5300 - Utilities Expense
- 5400 - Office Supplies Expense
- 5500 - Depreciation Expense

**COST OF GOODS SOLD (6000-6999)**
- 6100 - Purchases
- 6200 - Freight In

## Troubleshooting

### Error: Module not found
- Pastikan JavaFX SDK sudah di-download dan path sudah benar
- Periksa bahwa Maven menggunakan Java 21

### Database error
- File database `bookkeeping.db` akan dibuat otomatis di folder project
- Jika ada error, hapus file database dan restart aplikasi

### PDF generation error
- Pastikan folder output dapat di-write
- Periksa bahwa tidak ada PDF yang sama sedang terbuka

## Kontribusi

Project ini dibuat untuk keperluan pembelajaran dan penggunaan praktis. Silakan fork dan modifikasi sesuai kebutuhan.

## Lisensi

MIT License - silakan gunakan untuk keperluan komersial maupun non-komersial.

---

**Selamat menggunakan Bookkeeping Application! 📊💼**
