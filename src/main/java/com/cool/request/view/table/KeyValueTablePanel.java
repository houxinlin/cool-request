/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * KeyValueTablePanel.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.table;


import com.cool.request.components.http.KeyValue;

import java.util.List;
import java.util.stream.Collectors;

public class KeyValueTablePanel extends TablePanel {
    public KeyValueTablePanel(SuggestFactory suggestFactory) {
        super(new KeyValueTableModeFactory(suggestFactory));
    }

    public KeyValueTablePanel(TableModeFactory<?> tableModeFactory) {
        super(tableModeFactory);
    }

    public List<KeyValue> getTableMap(RowDataState rowDataState) {
        List<TableDataRow> tableDataRows = null;
        switch (rowDataState) {
            case available:
                tableDataRows = listTableData(tableDataRow -> tableDataRow.getData()[0].equals(Boolean.TRUE));
                break;
            case not_available:
                tableDataRows = listTableData(tableDataRow -> tableDataRow.getData()[0].equals(Boolean.FALSE));
                break;
            default:
                tableDataRows = listTableData(tableDataRow -> Boolean.TRUE);
        }

        return tableDataRows.stream()
                .map(tableDataRow ->
                        new KeyValue(tableDataRow.getData()[1].toString(),
                                tableDataRow.getData()[2].toString())).collect(Collectors.toList());
    }

    public void setTableData(List<KeyValue> keyValues) {
        removeAllData();
        for (KeyValue keyValue : keyValues) {
            addNewRow(((KeyValueTableModeFactory) getTableModeFactory()).createNewRowWithData(keyValue));
        }
    }
}
