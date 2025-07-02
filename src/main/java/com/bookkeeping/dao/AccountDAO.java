package com.bookkeeping.dao;

import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.AccountType;
import com.bookkeeping.util.DatabaseUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * DAO class untuk Account entity
 */
public class AccountDAO {
    
    /**
     * Menyimpan account baru atau update existing account
     */
    public Account save(Account account) {
        EntityManager em = DatabaseUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            if (account.getId() == null) {
                em.persist(account);
            } else {
                account = em.merge(account);
            }
            
            transaction.commit();
            return account;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving account: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mencari account berdasarkan ID
     */
    public Optional<Account> findById(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            Account account = em.find(Account.class, id);
            return Optional.ofNullable(account);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mencari account berdasarkan account code
     */
    public Optional<Account> findByAccountCode(String accountCode) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                "SELECT a FROM Account a WHERE a.accountCode = :accountCode", Account.class);
            query.setParameter("accountCode", accountCode);
            
            List<Account> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan semua accounts
     */
    public List<Account> findAll() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                "SELECT a FROM Account a ORDER BY a.accountCode", Account.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan accounts berdasarkan type
     */
    public List<Account> findByAccountType(AccountType accountType) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                "SELECT a FROM Account a WHERE a.accountType = :accountType ORDER BY a.accountCode", Account.class);
            query.setParameter("accountType", accountType);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mendapatkan accounts yang aktif
     */
    public List<Account> findActiveAccounts() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                "SELECT a FROM Account a WHERE a.isActive = true ORDER BY a.accountCode", Account.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Mencari accounts berdasarkan nama (partial match)
     */
    public List<Account> findByAccountNameContaining(String name) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                "SELECT a FROM Account a WHERE LOWER(a.accountName) LIKE LOWER(:name) ORDER BY a.accountCode", Account.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Menghapus account
     */
    public boolean delete(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            Account account = em.find(Account.class, id);
            if (account != null) {
                em.remove(account);
                transaction.commit();
                return true;
            }
            
            transaction.rollback();
            return false;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting account: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Mengecek apakah account code sudah ada
     */
    public boolean existsByAccountCode(String accountCode) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Account a WHERE a.accountCode = :accountCode", Long.class);
            query.setParameter("accountCode", accountCode);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    /**
     * Update balance account
     */
    public void updateBalance(Long accountId, java.math.BigDecimal newBalance) {
        EntityManager em = DatabaseUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            Account account = em.find(Account.class, accountId);
            if (account != null) {
                account.setBalance(newBalance);
                em.merge(account);
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating account balance: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
