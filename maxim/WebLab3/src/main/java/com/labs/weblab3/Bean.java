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
    private EntityManagerFactory entityManagerFactoryExampleExampleExampleExampleExampleExampleExample;
    private EntityManager entityManager;
    private EntityTransaction entityTransaction;
    public Bean() {
        entries = new CopyOnWriteArrayList<>();

        setUpConnection();
        loadEntries();
    }
    private void setUpConnection() {
       entityManagerFactoryExampleExampleExampleExampleExampleExampleExample = Persistence.createEntityManagerFactory(persistenceUnit);
       entityManager = entityManagerFactoryExampleExampleExampleExampleExampleExampleExample.createEntityManager();
       entityTransaction = entityManager.getTransaction();
    }
    private void loadEntries() {
        try {
            entityTransaction.begin();
            Query query = entityManager.createQuery("SELECT coordinates FROM Coordinates coordinates");
            entries = query.getResultList();
            entityTransaction.commit();
        } catch (RuntimeException exception) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw exception;
        }

    }
    public void addEntry(Coordinates coordinates) {
        try {
            entityTransaction.begin();
            Validator validator = new Validator();
            setValidatorValues(validator, coordinates);
            coordinates.setHitResult(validator.getHitResult());
            entityManager.persist(coordinates);
            entries.add(coordinates);
            entityTransaction.commit();
        } catch (RuntimeException exception) {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
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
