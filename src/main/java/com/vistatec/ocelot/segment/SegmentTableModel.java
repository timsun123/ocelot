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
package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.rules.NullITSMetadata;
import com.vistatec.ocelot.rules.RuleConfiguration;

import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

/**
 * Table model that repackages SegmentController and rule data for use
 * in a SegmentView.
 */
public class SegmentTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private RuleConfiguration ruleConfig;
    protected HashMap<String, Integer> colNameToIndex;
    protected HashMap<Integer, String> colIndexToName;
    public static final int NUMFLAGS = 5;
    public static final int NONFLAGCOLS = 4;
    public static final String COLSEGNUM = "#";
    public static final String COLSEGSRC = "Source";
    public static final String COLSEGTGT = "Target";
    public static final String COLSEGTGTORI = "Target Original";
    private SegmentController segmentController;

    public SegmentTableModel(SegmentController segmentController,
                             RuleConfiguration ruleConfig) {
        this.segmentController = segmentController;
        this.ruleConfig = ruleConfig;
        colNameToIndex = new HashMap<String, Integer>();
        colNameToIndex.put(COLSEGNUM, 0);
        colNameToIndex.put(COLSEGSRC, 1);
        colNameToIndex.put(COLSEGTGT, 2);
        colNameToIndex.put(COLSEGTGTORI, 3);
        colIndexToName = new HashMap<Integer, String>();
        for (String key : colNameToIndex.keySet()) {
            colIndexToName.put(colNameToIndex.get(key), key);
        }
    }

    @Override
    public String getColumnName(int col) {
        return col < NONFLAGCOLS ? colIndexToName.get(col) : "";
    }

    public int getColumnIndex(String col) {
        return colNameToIndex.get(col);
    }

    int getSegmentNumColumnIndex() {
        return getColumnIndex(SegmentTableModel.COLSEGNUM);
    }

    int getSegmentSourceColumnIndex() {
        return getColumnIndex(SegmentTableModel.COLSEGSRC);
    }

    int getSegmentTargetColumnIndex() {
        return getColumnIndex(SegmentTableModel.COLSEGTGT);
    }

    int getSegmentTargetOriginalColumnIndex() {
        return getColumnIndex(SegmentTableModel.COLSEGTGTORI);
    }

    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == getColumnIndex(COLSEGNUM)) {
            return Integer.class;
        }
        if (columnIndex == getColumnIndex(COLSEGSRC)
                || columnIndex == getColumnIndex(COLSEGTGT)
                || columnIndex == getColumnIndex(COLSEGTGTORI)) {
            return SegmentVariant.class;
        }
        return ITSMetadata.class;
    }

    @Override
    public int getRowCount() {
        return segmentController.getNumSegments();
    }

    @Override
    public int getColumnCount() {
        return NONFLAGCOLS + NUMFLAGS;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == getColumnIndex(COLSEGNUM)) {
            return getSegment(row).getSegmentNumber();
        }
        if (col == getColumnIndex(COLSEGSRC)) {
            return getSegment(row).getSource();
        }
        if (col == getColumnIndex(COLSEGTGT)) {
            return getSegment(row).getTarget();
        }
        if (col == getColumnIndex(COLSEGTGTORI)) {
            return getSegment(row).getOriginalTarget();
        }
        Object ret = ruleConfig.getTopDataCategory(
                getSegment(row), col-NONFLAGCOLS);
        return ret != null ? ret : NullITSMetadata.getInstance();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == colNameToIndex.get(COLSEGTGT);
    }

    Segment getSegment(int row) {
        return segmentController.getSegment(row);
    }
}
