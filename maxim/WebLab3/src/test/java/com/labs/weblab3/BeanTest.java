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
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;

    @Mock
    private Query query;

    @Before
    public void init() {
        openMocks(this);
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);

            new Bean();

            persistence.verify(() -> Persistence.createEntityManagerFactory(persistenceUnit));
            verify(entityManagerFactory, times(1)).createEntityManager();
            verify(entityManager, times(1)).getTransaction();
            verify(transaction, times(1)).begin();
            verify(entityManager, times(1)).createQuery(queryString);
            verify(query, times(1)).getResultList();
            verify(transaction, times(1)).commit();
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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);
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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);
            when(query.getResultList()).thenThrow(RuntimeException.class);
            when(transaction.isActive()).thenReturn(true);

            try {

                new Bean();

            } catch (RuntimeException ignore) {}

            verify(transaction, times(1)).rollback();
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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);
            when(query.getResultList()).thenThrow(RuntimeException.class);
            when(transaction.isActive()).thenReturn(false);

            try {

                new Bean();

            } catch (RuntimeException ignore) {}

            verify(transaction, never()).rollback();
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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);

            Bean bean = new Bean();

            bean.addEntry(coordinates);

            verify(transaction, times(2)).begin();
            verify(entityManager, times(1)).persist(coordinates);
            verify(transaction, times(2)).commit();

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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);

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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);
            doThrow(RuntimeException.class).when(entityManager).persist(coordinates);
            when(transaction.isActive()).thenReturn(true);

            Bean bean = new Bean();

            try {
                bean.addEntry(coordinates);
            } catch (RuntimeException ignored) {}

            verify(transaction, times(1)).rollback();

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

            persistence.when(() -> Persistence.createEntityManagerFactory(persistenceUnit)).thenReturn(entityManagerFactory);
            doThrow(RuntimeException.class).when(entityManager).persist(coordinates);
            when(transaction.isActive()).thenReturn(false);

            Bean bean = new Bean();

            try {
                bean.addEntry(coordinates);
            } catch (RuntimeException ignored) {}

            verify(transaction, never()).rollback();

        }

    }

}