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
package com.vistatec.ocelot.rules;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.vistatec.ocelot.rules.RuleConfiguration.FilterMode;
import com.vistatec.ocelot.rules.RuleConfiguration.StateQualifierMode;

/**
 * Window for selecting which filter rules to apply to the segment table.
 */
public class FilterView extends JPanel implements Runnable, ActionListener {
    private static final long serialVersionUID = 1L;

    private JFrame frame;
    private static Image icon;
    RuleConfiguration filterRules;
    private String allString = "All Segments",
            metadataString = "All w/metadata",
            customString = "Selected Rules:";
    private JRadioButton all, allWithMetadata, custom,
            allStates, customStates;
    private RulesTable rulesTable;
    private StateQualifierTable statesTable;

    public FilterView(RuleConfiguration filterRules, Image icon) {
        super(new GridBagLayout());
        this.filterRules = filterRules;
        this.icon = icon;
        setBorder(new EmptyBorder(10,10,10,10));

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.gridwidth = 3;
        int gridy = 0;

        JLabel title = new JLabel("Show segments matching rules:");
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        gridBag.fill = GridBagConstraints.HORIZONTAL;
        add(title, gridBag);

        all = new JRadioButton(allString);
        all.setSelected(filterRules.getFilterMode() == FilterMode.ALL);
        all.addActionListener(this);
        gridBag.gridwidth = 1;
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        gridBag.fill = 0;
        add(all, gridBag);

        allWithMetadata = new JRadioButton(metadataString);
        allWithMetadata.setSelected(filterRules.getFilterMode() == FilterMode.ALL_WITH_METADATA);
        allWithMetadata.addActionListener(this);
        gridBag.gridx = 1;
        add(allWithMetadata, gridBag);

        custom = new JRadioButton(customString);
        custom.setSelected(filterRules.getFilterMode() == FilterMode.SELECTED_SEGMENTS);
        custom.addActionListener(this);
        gridBag.gridx = 2;
        add(custom, gridBag);

        ButtonGroup filterGroup = new ButtonGroup();
        filterGroup.add(all);
        filterGroup.add(allWithMetadata);
        filterGroup.add(custom);

        rulesTable = new RulesTable(filterRules);
        gridBag = new GridBagConstraints();
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        gridBag.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.weightx = 1.0;
        gridBag.weighty = 1.0;
        gridBag.fill = GridBagConstraints.HORIZONTAL;
        gridBag.insets = new Insets(10, 10, 10, 10);

        add(rulesTable.getTable(), gridBag);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gridBag = new GridBagConstraints();
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        gridBag.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.weightx = 0;
        gridBag.weighty = 0;
        gridBag.insets = new Insets(10, 0, 10, 0);
        gridBag.fill = GridBagConstraints.HORIZONTAL;
        add(sep, gridBag);

        JLabel sqTitle = new JLabel("Filter segments by state-qualifier:");
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        gridBag.fill = GridBagConstraints.HORIZONTAL;
        add(sqTitle, gridBag);
        
        allStates = new JRadioButton("Show All");
        allStates.setSelected(filterRules.getStateQualifierMode() == StateQualifierMode.ALL);
        allStates.addActionListener(this);
        gridBag = new GridBagConstraints();
        gridBag.gridy = gridy++;
        add(allStates, gridBag);
        customStates = new JRadioButton("Show Only These:");
        customStates.setSelected(filterRules.getStateQualifierMode() == StateQualifierMode.SELECTED_STATES);
        customStates.addActionListener(this);
        gridBag.gridx = 1;
        add(customStates, gridBag);
        ButtonGroup statesGroup = new ButtonGroup();
        statesGroup.add(allStates);
        statesGroup.add(customStates);

        this.statesTable = new StateQualifierTable(filterRules);
        gridBag = new GridBagConstraints();
        gridBag.gridx = 0;
        gridBag.gridy = gridy++;
        gridBag.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.fill = GridBagConstraints.HORIZONTAL;
        gridBag.insets = new Insets(10, 10, 10, 10);

        add(statesTable.getTable(), gridBag);
    }

    @Override
    public void run() {
        frame = new JFrame("Filters");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(icon);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == all) {
            rulesTable.setAllowSelection(false);
            filterRules.setFilterMode(FilterMode.ALL);
        } else if (ae.getSource() == allWithMetadata) {
            rulesTable.setAllowSelection(false);
            filterRules.setFilterMode(FilterMode.ALL_WITH_METADATA);
        } else if (ae.getSource() == custom) {
            rulesTable.setAllowSelection(true);
            filterRules.setFilterMode(FilterMode.SELECTED_SEGMENTS);
        } else if (ae.getSource() == allStates) {
            statesTable.setAllowSelection(false);
            filterRules.setStateQualifierMode(StateQualifierMode.ALL);
        } else if (ae.getSource() == customStates) {
            statesTable.setAllowSelection(true);
            filterRules.setStateQualifierMode(StateQualifierMode.SELECTED_STATES);
        }
    }
}
