package com.bookkeeping.dao;

import com.bookkeeping.entity.Transaction;
import com.bookkeeping.util.DatabaseUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO class untuk Transaction entity
 */
public class TransactionDAO {
    
    /**
     * Menyimpan transaction baru atau update existing transaction
     */
    public Transaction save(Transaction transaction) {
        EntityManager em = DatabaseUtil.getEntityManager();
        EntityTransaction dbTransaction = em.getTransaction();
        
        try {
            dbTransaction.begin();
            
            if (transaction.getId() == null) {
                em.persist(transaction);
            } else {
                transaction = em.merge(transaction);
            }
            
            dbTransaction.commit();
            return transaction;
        } catch (Exception e) {
            if (dbTransaction.isActive()) {
                dbTransaction.rollback();
            }
            throw new RuntimeException("Error saving transaction: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mencari transaction berdasarkan ID
     */
    public Optional<Transaction> findById(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            Transaction transaction = em.find(Transaction.class, id);
            if (transaction != null) {
                // Eager load entries
                transaction.getEntries().size();
            }
            return Optional.ofNullable(transaction);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mencari transaction berdasarkan transaction number
     */
    public Optional<Transaction> findByTransactionNumber(String transactionNumber) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.transactionNumber = :transactionNumber", Transaction.class);
            query.setParameter("transactionNumber", transactionNumber);
            
            List<Transaction> results = query.getResultList();
            if (!results.isEmpty()) {
                Transaction transaction = results.get(0);
                // Eager load entries
                transaction.getEntries().size();
                return Optional.of(transaction);
            }
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan semua transactions
     */
    public List<Transaction> findAll() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t ORDER BY t.transactionDate DESC, t.transactionNumber DESC", Transaction.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan transactions berdasarkan tanggal
     */
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.transactionDate >= :startDate AND t.transactionDate <= :endDate " +
                "ORDER BY t.transactionDate DESC, t.transactionNumber DESC", Transaction.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan transactions berdasarkan tanggal tertentu
     */
    public List<Transaction> findByDate(LocalDate date) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.transactionDate = :date " +
                "ORDER BY t.transactionNumber DESC", Transaction.class);
            query.setParameter("date", date);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mencari transactions berdasarkan description (partial match)
     */
    public List<Transaction> findByDescriptionContaining(String description) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(:description) " +
                "ORDER BY t.transactionDate DESC, t.transactionNumber DESC", Transaction.class);
            query.setParameter("description", "%" + description + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Menghapus transaction
     */
    public boolean delete(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            Transaction transactionEntity = em.find(Transaction.class, id);
            if (transactionEntity != null) {
                em.remove(transactionEntity);
                transaction.commit();
                return true;
            }
            
            transaction.rollback();
            return false;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting transaction: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mengecek apakah transaction number sudah ada
     */
    public boolean existsByTransactionNumber(String transactionNumber) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.transactionNumber = :transactionNumber", Long.class);
            query.setParameter("transactionNumber", transactionNumber);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    /**
     * Generate transaction number otomatis
     */
    public String generateTransactionNumber() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                "SELECT t.transactionNumber FROM Transaction t ORDER BY t.transactionNumber DESC", String.class);
            query.setMaxResults(1);
            
            List<String> results = query.getResultList();
            if (results.isEmpty()) {
                return "TXN-000001";
            }
            
            String lastNumber = results.get(0);
            if (lastNumber.startsWith("TXN-")) {
                try {
                    int number = Integer.parseInt(lastNumber.substring(4));
                    return String.format("TXN-%06d", number + 1);
                } catch (NumberFormatException e) {
                    return "TXN-000001";
                }
            }
            
            return "TXN-000001";
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan transactions dengan limit
     */
    public List<Transaction> findRecentTransactions(int limit) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t ORDER BY t.transactionDate DESC, t.transactionNumber DESC", Transaction.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
