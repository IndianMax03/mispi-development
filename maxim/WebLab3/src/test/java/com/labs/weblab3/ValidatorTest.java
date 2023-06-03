package com.labs.weblab3;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import com.labs.weblab3.util.Randomizer;

import static org.junit.Assert.*;

public class ValidatorTest {

    private static Validator validator;
    private static final double X_MIN = -2;
    private static final double X_MAX = 2;
    private static final double X_STEP = 0.5;
    private static final double Y_MIN = -3;
    private static final double Y_MAX = 5;
    private static final double Y_STEP = 0.01;
    private static final double R_MIN = 2;
    private static final double R_MAX = 5;
    private static final double R_STEP = 0.1;


    /*
     *
     * Инициализация объекта класса для проверки
     *
     *  */
    @BeforeClass
    public static void init() {
        validator = new Validator();
    }

    @After
    public void afterTest() {
        System.out.println("x = " + validator.getValueX());
        System.out.println("y = " + validator.getValueY());
        System.out.println("r/2 = " + validator.getValueR()/2);
        System.out.println("r = " + validator.getValueR());
    }

    /*
     *
     * Установка MIN_X <= x < MAX_X должна пройти успешно
     *
     *  */
    @Test
    public void whenSetXFromMinimumToUninclusiveMaximumThenGetValidX() {
        double x = Randomizer.generateRandom(X_MIN, X_MAX);
        validator.setValueX(x);
        assertEquals(x, validator.getValueX(), 0);
    }

    /*
     *
     * Установка x = MAX_X должна пройти успешно
     *
     *  */
    @Test
    public void whenSetXEqualsMaximumThenGetValidX() {
        double x = X_MAX;
        validator.setValueX(x);
        assertEquals(x, validator.getValueX(), 0);
    }

    /*
     *
     * Установка MIN_Y <= y < MAX_Y должна пройти успешно
     *
     *  */
    @Test
    public void whenSetYFromMinimumToUninclusiveMaximumThenGetValidY() {
        double randomValue = Randomizer.generateRandom(Y_MIN, Y_MAX);
        validator.setValueY(randomValue);
        assertEquals(randomValue, validator.getValueY(), 0);
    }

    /*
     *
     * Установка y = MAX_Y должна пройти успешно
     *
     *  */
    @Test
    public void whenSetYEqualsMaximumThenGetValidY() {
        double y = Y_MAX;
        validator.setValueY(y);
        assertEquals(y, validator.getValueY(), 0);
    }

    /*
     *
     * Установка MIN_R <= r < MAX_R должна пройти успешно
     *
     *  */
    @Test
    public void whenSetRFromMinimumToUninclusiveMaximumThenGetValidR() {
        double randomValue = Randomizer.generateRandom(R_MIN, R_MAX);
        validator.setValueR(randomValue);
        assertEquals(randomValue, validator.getValueR(), 0);
    }

    /*
     *
     * Установка r = MAX_R должна пройти успешно
     *
     *  */
    @Test
    public void whenSetREqualsMaximumThenGetValidR() {
        double r = R_MAX;
        validator.setValueR(r);
        assertEquals(r, validator.getValueR(), 0);
    }

    /* setHitResult() должен быть приватным ахахаха, иначе в нем нет смысла */

    /*
     *
     * Установка x > X_MAX должна окончиться IllegalArgumentException
     *
     *  */
    @Test(expected = IllegalArgumentException.class)
    public void whenSetXHigherThanMaximumThenIllegalArgumentException() {
        double randomDelta = Randomizer.generateRandom(X_STEP, 2 * X_STEP);
        validator.setValueX(X_MAX + randomDelta);
    }

    /*
     *
     * Установка y > Y_MAX должна окончиться IllegalArgumentException
     *
     *  */
    @Test(expected = IllegalArgumentException.class)
    public void whenSetYHigherThanMaximumThenIllegalArgumentException() {
        double randomDelta = Randomizer.generateRandom(Y_STEP, 2 * Y_STEP);
        validator.setValueX(Y_MAX + randomDelta);
    }

    /*
     *
     * Установка r > R_MAX должна окончиться IllegalArgumentException
     *
     *  */
    @Test(expected = IllegalArgumentException.class)
    public void whenSetRHigherThanMaximumThenIllegalArgumentException() {
        double randomDelta = Randomizer.generateRandom(R_STEP, 2 * R_STEP);
        validator.setValueX(R_MAX + randomDelta);
    }

    /*
     *
     * Установка x < X_MIN должна окончиться IllegalArgumentException
     *
     *  */
    @Test(expected = IllegalArgumentException.class)
    public void whenSetXLowerThanMinimumThenIllegalArgumentException() {
        double randomDelta = Randomizer.generateRandom(X_STEP, 2 * X_STEP);
        validator.setValueX(X_MIN - randomDelta);
    }

    /*
     *
     * Установка y < Y_MIN должна окончиться IllegalArgumentException
     *
     *  */
    @Test(expected = IllegalArgumentException.class)
    public void whenSetYLowerThanMinimumThenIllegalArgumentException() {
        double randomDelta = Randomizer.generateRandom(Y_STEP, 2 * Y_STEP);
        validator.setValueY(Y_MIN - randomDelta);
    }

    /*
     *
     * Установка r < MIN_R должна окончиться IllegalArgumentException
     *
     *  */
    @Test(expected = IllegalArgumentException.class)
    public void whenSetRLowerThanMinimumThenIllegalArgumentException() {
        double randomDelta = Randomizer.generateRandom(R_STEP, 2 * R_STEP);
        validator.setValueR(R_MIN - randomDelta);
    }

    /*
     *
     * Установка r <= 0 должна окончиться IllegalArgumentException
     *
     *  */
    @Test(expected = IllegalArgumentException.class)
    public void whenSetRLessOrEqualsZeroThenIllegalArgumentException() {
        double randomValue = Randomizer.generateRandomDouble();
        if (randomValue > 0) {
            randomValue *= -1;
        }
        validator.setValueR(randomValue);
    }

    /*
     *
     * Для I четверти (x >= 0; y >= 0):
     * при x < r/2 и y < r проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenXIsSmallerThanHalfRAndYIsSmallerThanRInFirstQuarterThenCheckHitIsTrue() {
        double r = Randomizer.generateRandom(R_MIN, R_MAX);
        double x = Randomizer.generateRandom(0, Math.min(r/2, X_MAX));
        double y = Randomizer.generateRandom(0, r);
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для I четверти (x >= 0; y >= 0):
     * при x = r/2 и y = r проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenXIsEqualsHalfRAndYIsEqualsRInFirstQuarterThenCheckHitIsTrue() {
        double r = Randomizer.generateRandom(R_MIN, R_MAX);
        double x = Math.min(r/2, X_MAX);
        double y = r;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для I четверти (x >= 0; y >= 0):
     * при x > r/2 и y <= r проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXIsHigherThanHalfRAndYIsLessOrEqualsRInFirstQuarterThenCheckHitIsFalse() {
        double r = Randomizer.generateRandom(R_MIN, 2 * X_MAX - X_STEP);
        double dx = Randomizer.generateRandom(X_STEP/10, X_STEP/2);
        double x = r/2 + dx;
        double y = Randomizer.generateRandom(0, r);
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для I четверти (x >= 0; y >= 0):
     * при x <= r/2 и y > r проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXIsLessOrEqualsThanHalfRAndYIsHigherThanRInFirstQuarterThenCheckHitIsFalse() {
        double r = Randomizer.generateRandom(R_MIN, (Y_MAX - 2 * Y_STEP));
        double dy = Randomizer.generateRandom(Y_STEP, 2 * Y_STEP);
        double x = Randomizer.generateRandom(0, Math.min(r/2, X_MAX));
        double y = r + dy;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для I четверти (x >= 0; y >= 0):
     * при x > r/2 и y > r проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXIsHigherThanHalfRAndYIsHigherThanRInFirstQuarterThenCheckHitIsFalse() {
        double r = Randomizer.generateRandom(R_MIN, 2 * X_MAX - X_STEP);
        double dr = Randomizer.generateRandom(X_STEP/10, X_STEP/2);
        double x = r/2 + dr;
        double y = r + dr;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для II четверти (x < 0; y > 0):
     * при любых x и y проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXIsLowerThanZeroAndYIsHigherThanZeroInSecondQuarterThenCheckHitIsFalse() {
        double x = Randomizer.generateRandom(X_MIN, 0);
        double y = Randomizer.generateRandom(Y_STEP, Y_MAX);
        double r = Randomizer.generateRandom(R_MIN, R_MAX);
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для III четверти (x < 0; y < 0):
     * при x^2 + y^2 == r^2 проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenSquaredXPlusSquaredYEqualsSquaredRInThirdQuarterThenCheckHitIsTrue() {
        double r = Randomizer.generateRandom(R_MIN, R_MAX);
        double x = Math.max(-r/2, X_MIN);
        double y = Math.sqrt(Math.pow(r/2, 2) - Math.pow(x, 2));
        y *= y > 0 ? (-1) : 1;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для III четверти (x < 0; y < 0):
     * при x^2 + y^2 <= r^2 проверка методом checkHit() должна окончиться успехом
     * примечание: в редких случаях этот тест может не проходить из-за погрешности вычислений
     *
     *  */
    @Test
    public void whenSquaredXPlusSquaredYIsLowerOrEqualsSquaredRInThirdQuarterThenCheckHitIsTrue() {
        double r = Randomizer.generateRandom(R_MIN, R_MAX);
        double x = Randomizer.generateRandom(Math.max(-r/2, X_MIN), 0);
        double y = Randomizer.generateRandom(Math.sqrt(Math.pow(r/2, 2) - Math.pow(x, 2)), 0);
        y *= y > 0 ? (-1) : 1;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для III четверти (x < 0; y < 0):
     * при y = -r (< -r/2) и -r/2 <= x < 0 проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenYLessThanNegativeHalfRInThirdQuarterThenCheckHitIsFalse() {
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN));
        double x = Randomizer.generateRandom(Math.max(-r/2, X_MIN), 0);
        double y = -r;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для III четверти (x < 0; y < 0):
     * при -r/2 <= y < 0 и x < -r/2 проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXLessThanNegativeHalfRInThirdQuarterThenCheckHitIsFalse() {
        double r = Randomizer.generateRandom(R_MIN, 2 * X_MAX - X_STEP);
        double dx = Randomizer.generateRandom(X_STEP/10, X_STEP/2);
        double x = -r/2 - dx;
        double y = Randomizer.generateRandom(-r/2, 0);
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для IV четверти (x > 0; y < 0):
     * при 0 < x < r/2 и 0 < y < 2 * x - r проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenXBetweenZeroAndHalfRAndYBetweenZeroAndDoubleXMinusRInclusivelyInFourthQuarterThenCheckHitIsTrue() {
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN));
        double x = Randomizer.generateRandom(X_STEP/100, r/2);
        double y = Randomizer.generateRandom(0, 2 * x - r);
        y *= y > 0 ? (-1) : 1;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для IV четверти (x > 0; y < 0):
     * при 0 < x < r/2 и y = 2 * x - r проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenXBetweenZeroAndHalfRAndYIsDoubleXMinusRInFourthQuarterThenCheckHitIsTrue() {
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN));
        double x = Randomizer.generateRandom(X_STEP/100, r/2);
        double y = 2 * x - r;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для IV четверти (x > 0; y < 0):
     * при 0 < x < r/2 и y > 2 * x - 2 проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXBetweenZeroAndHalfRAndYHigherThanDoubleXMinusRInFourthQuarterThenCheckHitIsFalse() {
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN) - 2 * Y_STEP);
        double x = Randomizer.generateRandom(X_STEP/100, r/2);
        double y = 2 * x - r;
        double dy = Randomizer.generateRandom(Y_STEP, 2 * Y_STEP);
        y *= y > 0 ? (-1) : 1;
        y -= dy;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для IV четверти (x > 0; y < 0):
     * при 0 < y < -r и x > y/2 + r/2 проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenYBetweenZeroAndNegativeRAndXHigherThanHalfYPlusHalfRInFourthQuarterThenCheckHitIsFalse() {
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN) - X_STEP);
        double y = -Randomizer.generateRandom(0, r);
        double dx = Randomizer.generateRandom(X_STEP/10, X_STEP/2);
        double x = y / 2 + r / 2 + dx;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Ox:
     * при y = 0 и -r/2 <= x < r/2 проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenYIsZeroAndXBetweenNegativeHalfRAndHalfRThenCheckHitIsTrue() {
        double y = 0;
        double r = Randomizer.generateRandom(R_MIN, 2 * X_MAX);
        double x = Randomizer.generateRandom(-r/2, r/2);
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Ox:
     * при y = 0 и x = r/2 проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenYIsZeroAndXIsHalfRAndHalfRThenCheckHitIsTrue() {
        double y = 0;
        double r = Randomizer.generateRandom(R_MIN, 2 * X_MAX);
        double x = r/2;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Ox:
     * при y = 0 и x > r/2 проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenYIsZeroAndXHigherThanHalfRThenCheckHitIsFalse() {
        double y = 0;
        double r = Randomizer.generateRandom(R_MIN, 2 * X_MAX - X_STEP);
        double dx = Randomizer.generateRandom(X_STEP/10, X_STEP/2);
        double x = r/2 + dx;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Ox:
     * при y = 0 и x < -r/2 проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenYIsZeroAndXLowerThanHalfRThenCheckHitIsFalse() {
        double y = 0;
        double r = Randomizer.generateRandom(R_MIN, 2 * X_MAX - X_STEP);
        double dx = Randomizer.generateRandom(X_STEP/10, X_STEP/2);
        double x = -r/2 - dx;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Oy:
     * при x = 0 и -r <= y < r проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenXIsZeroAndYBetweenNegativeRAndRThenCheckHitIsTrue() {
        double x = 0;
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN));
        double y = Randomizer.generateRandom(-r, r);
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Oy:
     * при x = 0 и y = r проверка методом checkHit() должна окончиться успехом
     *
     *  */
    @Test
    public void whenXIsZeroAndYIsRThenCheckHitIsTrue() {
        double x = 0;
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN));
        double y = r;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertTrue(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Oy:
     * при x = 0 и y > r проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXIsZeroAndYHigherRThenCheckHitIsFalse() {
        double x = 0;
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN) - 2 * Y_STEP);
        double dy = Randomizer.generateRandom(Y_STEP/10, Y_STEP/2);
        double y = r + dy;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

    /*
     *
     * Для координатной оси Oy:
     * при x = 0 и y < -r проверка методом checkHit() должна окончиться провалом
     *
     *  */
    @Test
    public void whenXIsZeroAndYLowerNegativeRThenCheckHitIsFalse() {
        double x = 0;
        double r = Randomizer.generateRandom(R_MIN, Math.abs(Y_MIN) - 2 * Y_STEP);
        double dy = Randomizer.generateRandom(Y_STEP/10, Y_STEP/2);
        double y = -r - dy;
        validator.setValueX(x);
        validator.setValueY(y);
        validator.setValueR(r);
        assertFalse(validator.checkHit());
    }

}