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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru._x100.yobitbot.utils.CalculationHelper.smma;

/**
 * Implementation of the RSI (Relative Strength Index) indicator
 */
public class RsiIndicator implements Indicator {
    private int periodsCount = 14;

    @Override
    public double calculate(List<Candle> candles) {
        Collections.reverse(candles);
        if (candles.size() < periodsCount + 1) {
            return Double.NaN;
        }
        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        for (int i = 0; i < candles.size() - 1; i++) {
            double todayPrice = candles.get(i + 1).getC();
            double yesterdayPrice = candles.get(i).getC();

            if (todayPrice - yesterdayPrice > 0) {
                gains.add(todayPrice - yesterdayPrice);
                losses.add(0d);
            } else if (todayPrice - yesterdayPrice < 0) {
                losses.add(yesterdayPrice - todayPrice);
                gains.add(0d);
            } else {
                gains.add(0d);
                losses.add(0d);
            }
        }
        double avgU = smma(gains, periodsCount);
        double avgD = smma(losses, periodsCount);

        if (avgD == 0) {
            return 100;
        }
        double rs = avgU / avgD;
        return 100 - 100 / (1 + rs);
    }
}
