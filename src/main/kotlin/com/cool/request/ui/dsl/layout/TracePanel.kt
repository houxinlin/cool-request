package com.cool.request.ui.dsl.layout

import com.cool.request.rmi.agent.AgentRMIManager
import com.cool.request.view.page.BaseTablePanelWithToolbarPanelImpl
import com.intellij.openapi.project.Project
import com.intellij.ui.table.JBTable
import javax.swing.table.DefaultTableModel

class TracePanel(
    private val pro: Project,
    private var traceMap: MutableMap<String, MutableSet<String>>
) : BaseTablePanelWithToolbarPanelImpl(
    pro,
    ToolbarBuilder().enabledAdd().enabledRemove().enabledSaveButton(), ReadTableModel()
) {

    fun init() {
        traceMap.forEach { clz ->
            clz.value.map { method ->
                defaultTableModel.addRow(arrayOf(clz.key, method))
            }
        }
    }

    override fun getTableHeader(): Array<out Any> = arrayOf("Class", "Method")

    override fun getNewNullRowData(): Array<out Any> = arrayOf("", "")

    override fun saveRows() {
        super.saveRows()
        AgentRMIManager.getAgentRMIManager(project).clear()
        defaultTableModel.dataVector.forEach {
            AgentRMIManager.getAgentRMIManager(project).addCustomMethod(it[0] as String, it[1] as String)
        }
    }

    override fun removeRow() {
        stopEditor()
        for (i in jTable.selectedRows) defaultTableModel.removeRow(i)
        jTable.invalidate()
    }

    override fun initDefaultTableModel(jTable: JBTable, defaultTableModel: DefaultTableModel) {
        defaultTableModel.setDataVector(null, getTableHeader())
        jTable.columnModel.getColumn(0).minWidth = 400
        jTable.columnModel.getColumn(0).maxWidth = 400
        jTable.columnModel.getColumn(1).minWidth = 300
        jTable.columnModel.getColumn(1).maxWidth = 300
    }

    class ReadTableModel : DefaultTableModel() {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }

}