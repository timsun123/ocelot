/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.its.stats;

/**
 * Aggregate data representation for LQI displayed in SegmentAttributeTableView.
 */
public class LanguageQualityIssueStats implements ITSStats {
    private String type, value;
    private Double minRange, maxRange;
    private Integer count = 0;

    @Override
    public String getDataCategory() {
        return "LQI";
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        if (minRange != null && maxRange != null) {
            return minRange + "-" + maxRange;
        } else if (value != null) {
            return value;
        }
        return null;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    public void setRange(double range) {
        if (minRange == null || minRange > range) {
            minRange = range;
        }
        if (maxRange == null || maxRange < range) {
            maxRange = range;
        }
        count++;
    }

    public void setValue(String val) {
        this.value = val;
        count++;
    }
}
