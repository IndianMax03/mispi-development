package com.labs.weblab3;

import javax.enterprise.inject.Model;
import javax.faces.bean.ApplicationScoped;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Model
@ApplicationScoped
public class Bean implements Serializable  {
    private static final String persistenceUnit = "default";
    private List<Coordinates> entries;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction transaction;
    public Bean() {
        entries = new CopyOnWriteArrayList<>();

        connection();
        loadEntries();
    }
    private void connection() {
       entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
       entityManager = entityManagerFactory.createEntityManager();
       transaction = entityManager.getTransaction();
    }
    private void loadEntries() {
        try {
            transaction.begin();
            Query query = entityManager.createQuery("SELECT coordinates FROM Coordinates coordinates");
            entries = query.getResultList();
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }

    }
    public void addEntry(Coordinates coordinates) {
        try {
            transaction.begin();
            Validator validator = new Validator();
            setValidatorValues(validator, coordinates);
            coordinates.setHitResult(validator.getHitResult());
            entityManager.persist(coordinates);
            entries.add(coordinates);
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }

    }

    private void setValidatorValues(Validator validator, Coordinates coordinates) {
        validator.setValueX(coordinates.getValueX());
        validator.setValueY(coordinates.getValueY());
        validator.setValueR(coordinates.getValueR());
        validator.setHitResult(coordinates.getHitResult());
        validator.checkHit();
    }

    public List<Coordinates> getEntries() {
        return entries;
    }
    public void setEntries(List<Coordinates> entries) {
        this.entries = entries;
    }

}
