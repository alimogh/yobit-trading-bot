/*
 * The GNU Affero General Public License v3.0 (AGPL-3.0)
 *
 * Copyright (c) 2020 Ilya Lysenko
 *
 * Permissions of this strongest copyleft license are conditioned on making available complete source code of licensed
 * works and modifications, which include larger works using a licensed work, under the same license.
 * Copyright and license notices must be preserved. Contributors provide an express grant of patent rights.
 * When a modified version is used to provide a service over a network,  the complete source code of the modified
 * version must be made available.
 */
package ru._x100.yobitbot.indicator;

import ru._x100.yobitbot.model.Candle;

import java.util.List;

import static java.lang.Math.abs;

/**
 * Implementation of the CCI (Commodity Channel Index) indicator
 */
public class CciIndicator implements Indicator {
    private int periodsCount = 14;

    @Override
    public double calculate(List<Candle> candles) {
        candles = limitCandles(candles, periodsCount);

        if (candles.size() < periodsCount) {
            return Double.NaN;
        }
        Candle latestCandle = getLatestCandle(candles);
        double typicalPrice = calcTypicalPrice(latestCandle);
        double sma = calcSma(candles);
        double mean = calcMean(candles, sma);

        return (typicalPrice - sma) / (.015 * mean);
    }

    private double calcSma(List<Candle> candles) {
        double sma = 0;
        for (int i = 0; i < periodsCount; i++) {
            sma += calcTypicalPrice(candles.get(i));
        }
        sma /= periodsCount;
        return sma;
    }

    private double calcMean(List<Candle> candles, double sma) {
        double mean = 0;
        for (int i = 0; i < periodsCount; i++) {
            double typicalPrice = calcTypicalPrice(candles.get(i));
            mean += abs(sma - typicalPrice);
        }
        mean /= periodsCount;
        return mean;
    }

    private double calcTypicalPrice(Candle candle) {
        return (candle.getH() + candle.getL() + candle.getC()) / 3;
    }

    private Candle getLatestCandle(List<Candle> candles) {
        return candles.get(0);
    }
}
