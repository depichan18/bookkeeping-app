package com.bookkeeping.dao;

import com.bookkeeping.entity.TransactionEntry;
import com.bookkeeping.util.DatabaseUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO class untuk TransactionEntry entity
 */
public class TransactionEntryDAO {
    
    /**
     * Menyimpan transaction entry baru atau update existing entry
     */
    public TransactionEntry save(TransactionEntry entry) {
        EntityManager em = DatabaseUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            if (entry.getId() == null) {
                em.persist(entry);
            } else {
                entry = em.merge(entry);
            }
            
            transaction.commit();
            return entry;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving transaction entry: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mencari transaction entry berdasarkan ID
     */
    public Optional<TransactionEntry> findById(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TransactionEntry entry = em.find(TransactionEntry.class, id);
            return Optional.ofNullable(entry);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan semua entries untuk transaction tertentu
     */
    public List<TransactionEntry> findByTransactionId(Long transactionId) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<TransactionEntry> query = em.createQuery(
                "SELECT te FROM TransactionEntry te WHERE te.transaction.id = :transactionId", TransactionEntry.class);
            query.setParameter("transactionId", transactionId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan semua entries untuk account tertentu
     */
    public List<TransactionEntry> findByAccountId(Long accountId) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<TransactionEntry> query = em.createQuery(
                "SELECT te FROM TransactionEntry te " +
                "JOIN FETCH te.transaction t " +
                "WHERE te.account.id = :accountId " +
                "ORDER BY t.transactionDate DESC, t.transactionNumber DESC", TransactionEntry.class);
            query.setParameter("accountId", accountId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan entries untuk account dalam periode tertentu
     */
    public List<TransactionEntry> findByAccountIdAndDateRange(Long accountId, LocalDate startDate, LocalDate endDate) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<TransactionEntry> query = em.createQuery(
                "SELECT te FROM TransactionEntry te " +
                "JOIN FETCH te.transaction t " +
                "WHERE te.account.id = :accountId " +
                "AND t.transactionDate >= :startDate " +
                "AND t.transactionDate <= :endDate " +
                "ORDER BY t.transactionDate ASC, t.transactionNumber ASC", TransactionEntry.class);
            query.setParameter("accountId", accountId);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan semua debit entries
     */
    public List<TransactionEntry> findDebitEntries() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<TransactionEntry> query = em.createQuery(
                "SELECT te FROM TransactionEntry te " +
                "JOIN FETCH te.transaction t " +
                "WHERE te.debitAmount IS NOT NULL AND te.debitAmount > 0 " +
                "ORDER BY t.transactionDate DESC", TransactionEntry.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan semua credit entries
     */
    public List<TransactionEntry> findCreditEntries() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<TransactionEntry> query = em.createQuery(
                "SELECT te FROM TransactionEntry te " +
                "JOIN FETCH te.transaction t " +
                "WHERE te.creditAmount IS NOT NULL AND te.creditAmount > 0 " +
                "ORDER BY t.transactionDate DESC", TransactionEntry.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Menghitung total debit untuk account tertentu
     */
    public BigDecimal calculateTotalDebitForAccount(Long accountId) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                "SELECT COALESCE(SUM(te.debitAmount), 0) FROM TransactionEntry te " +
                "WHERE te.account.id = :accountId AND te.debitAmount IS NOT NULL", BigDecimal.class);
            query.setParameter("accountId", accountId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Menghitung total credit untuk account tertentu
     */
    public BigDecimal calculateTotalCreditForAccount(Long accountId) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                "SELECT COALESCE(SUM(te.creditAmount), 0) FROM TransactionEntry te " +
                "WHERE te.account.id = :accountId AND te.creditAmount IS NOT NULL", BigDecimal.class);
            query.setParameter("accountId", accountId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Menghitung balance untuk account tertentu
     */
    public BigDecimal calculateAccountBalance(Long accountId) {
        BigDecimal totalDebit = calculateTotalDebitForAccount(accountId);
        BigDecimal totalCredit = calculateTotalCreditForAccount(accountId);
        return totalDebit.subtract(totalCredit);
    }
    
    /**
     * Menghapus transaction entry
     */
    public boolean delete(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            TransactionEntry entry = em.find(TransactionEntry.class, id);
            if (entry != null) {
                em.remove(entry);
                transaction.commit();
                return true;
            }
            
            transaction.rollback();
            return false;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting transaction entry: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan entries untuk periode tertentu (untuk laporan)
     */
    public List<TransactionEntry> findByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<TransactionEntry> query = em.createQuery(
                "SELECT te FROM TransactionEntry te " +
                "JOIN FETCH te.transaction t " +
                "JOIN FETCH te.account a " +
                "WHERE t.transactionDate >= :startDate AND t.transactionDate <= :endDate " +
                "ORDER BY t.transactionDate ASC, a.accountCode ASC", TransactionEntry.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
