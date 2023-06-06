package com.labs.weblab3;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import static org.junit.Assert.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class BeanTest {

    private final String persistenceUnit = "default";
    private final String queryString = "SELECT e FROM Coordinates e";

    @Mock
    private EntityManagerFactory entityManagerFactoryExampleExampleExampleExampleExample;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction entityTransaction;

    @Mock
    private Query query;

    @Before
    public void init() {
        openMocks(this);
        when(entityManagerFactoryExampleExampleExampleExampleExample.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(entityManager.createQuery(queryString)).thenReturn(query);
    }

    /*
     *
     * Инициализация без исключения приводит к выполнению следующих операций:
     *
     *  */
    @Test
    public void whenInitializeBeanWithNoExceptionThenMethodInvocationSequence() {

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);

            new Bean();

            persistence.verify(() -> Persistence.createEntityManagerFactory(persistenceUnit));
            verify(entityManagerFactoryExampleExampleExampleExampleExample, times(1)).createEntityManager();
            verify(entityManager, times(1)).getTransaction();
            verify(entityTransaction, times(1)).begin();
            verify(entityManager, times(1)).createQuery(queryString);
            verify(query, times(1)).getResultList();
            verify(entityTransaction, times(1)).commit();
        }

    }

    /*
     *
     * Инициализация с исключением и RuntimeException приводит к его пробрасыванию наверх
     *
     *  */
    @Test(expected = RuntimeException.class)
    public void whenInitializeBeanWithExceptionThenRuntimeException() {

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);
            when(query.getResultList()).thenThrow(RuntimeException.class);

            new Bean();

        }

    }

    /*
     *
     * Инициализация с исключением и активной транзакцией приводит к ее откату (rollback)
     *
     *  */
    @Test
    public void whenInitializeBeanWithExceptionCatchingAndActiveTransactionThenRollbackInvocation() {

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);
            when(query.getResultList()).thenThrow(RuntimeException.class);
            when(entityTransaction.isActive()).thenReturn(true);

            try {

                new Bean();

            } catch (RuntimeException ignore) {}

            verify(entityTransaction, times(1)).rollback();
        }

    }

    /*
     *
     * Инициализация с исключением и неактивной транзакцией никогда не приводит к ее откату (rollback)
     *
     *  */
    @Test
    public void whenInitializeBeanWithExceptionCatchingAndUnactiveTransactionThenRollbackInvocation() {

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);
            when(query.getResultList()).thenThrow(RuntimeException.class);
            when(entityTransaction.isActive()).thenReturn(false);

            try {

                new Bean();

            } catch (RuntimeException ignore) {}

            verify(entityTransaction, never()).rollback();
        }

    }

    /*
     *
     * Добавление сущности через addEntry без RuntimeException приводит к выполнению следующей последовательности вызовов методов
     *
     *  */
    @Test
    public void whenAddingEntryAndGetEntriesWithNoExceptionThenMethodInvocationSequence() {

        Coordinates coordinates = new Coordinates();
        coordinates.setValueX(0d);
        coordinates.setValueY(0d);
        coordinates.setValueR(2d);

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);

            Bean bean = new Bean();

            bean.addEntry(coordinates);

            verify(entityTransaction, times(2)).begin();
            verify(entityManager, times(1)).persist(coordinates);
            verify(entityTransaction, times(2)).commit();

        }

    }

    /*
     *
     * Добавление сущности через addEntry приводит к добавлению в массив entries объекта bean той же самой сущности
     *
     *  */
    @Test
    public void whenAddingEntryAndGetEntriesThenGetSameEntry() {

        Coordinates coordinates = new Coordinates();
        coordinates.setValueX(0d);
        coordinates.setValueY(0d);
        coordinates.setValueR(2d);

        List<Coordinates> returnedWhenAddEntry;

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);

            Bean bean = new Bean();

            bean.addEntry(coordinates);

            returnedWhenAddEntry = bean.getEntries();

        }

        assertEquals(coordinates, returnedWhenAddEntry.get(returnedWhenAddEntry.size() - 1));

    }

    /*
     *
     * Добавление сущности через addEntry при RuntimeException и активной транзакции приводит к откату изменений (rollback)
     *
     *  */
    @Test
    public void whenAddingEntryAndGetEntriesWithExceptionAndActiveTransactionThenGetRollback() {

        Coordinates coordinates = new Coordinates();
        coordinates.setValueX(0d);
        coordinates.setValueY(0d);
        coordinates.setValueR(2d);

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);
            doThrow(RuntimeException.class).when(entityManager).persist(coordinates);
            when(entityTransaction.isActive()).thenReturn(true);

            Bean bean = new Bean();

            try {
                bean.addEntry(coordinates);
            } catch (RuntimeException ignored) {}

            verify(entityTransaction, times(1)).rollback();

        }

    }

    /*
     *
     * Добавление сущности через addEntry при RuntimeException и неактивной транзакции никогда не приводит к откату изменений (rollback)
     *
     *  */
    @Test
    public void whenAddingEntryAndGetEntriesWithExceptionAndUnactiveTransactionThenGetRollback() {

        Coordinates coordinates = new Coordinates();
        coordinates.setValueX(0d);
        coordinates.setValueY(0d);
        coordinates.setValueR(2d);

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactoryExampleExampleExampleExampleExample);
            doThrow(RuntimeException.class).when(entityManager).persist(coordinates);
            when(entityTransaction.isActive()).thenReturn(false);

            Bean bean = new Bean();

            try {
                bean.addEntry(coordinates);
            } catch (RuntimeException ignored) {}

            verify(entityTransaction, never()).rollback();

        }

    }

}