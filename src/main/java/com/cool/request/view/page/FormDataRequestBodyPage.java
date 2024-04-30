/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FormDataRequestBodyPage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.cool.request.components.http.FormDataInfo;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.table.FormDataModeFactory;
import com.cool.request.view.table.RowDataState;
import com.cool.request.view.table.TableDataRow;
import com.cool.request.view.table.TablePanel;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;


public class FormDataRequestBodyPage extends TablePanel {


    public FormDataRequestBodyPage(Project project, IRequestParamManager iRequestParamManager) {
        super(new FormDataModeFactory(project,iRequestParamManager));
    }

    public List<FormDataInfo> getFormData(RowDataState state) {
        List<FormDataInfo> result = new ArrayList<>();
        List<TableDataRow> tableDataRows;
        switch (state) {
            case available:
                tableDataRows = listTableData(tableDataRow -> tableDataRow.getData()[0].equals(Boolean.TRUE));
                break;
            case not_available:
                tableDataRows = listTableData(tableDataRow -> tableDataRow.getData()[0].equals(Boolean.FALSE));
                break;
            default:
                tableDataRows = listTableData(tableDataRow -> Boolean.TRUE);
        }
        for (TableDataRow tableDataRow : tableDataRows) {
            result.add(new FormDataInfo(tableDataRow.getData()[1].toString(),
                    tableDataRow.getData()[2].toString(),
                    tableDataRow.getData()[3].toString()));
        }
        return result;
    }

    public void setFormData(List<FormDataInfo> formDataInfos) {
        removeAllData();
        for (FormDataInfo formData : formDataInfos) {
            Object[] newRowWithData = ((FormDataModeFactory) getTableModeFactory()).createNewRowWithData(formData);
            addNewRow(newRowWithData);
        }
    }
}