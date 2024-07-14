package com.cool.request.ui.dsl.layout

import com.cool.request.rmi.agent.AgentRMIManager
import com.intellij.openapi.project.Project
import com.intellij.ui.table.JBTable
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.table.DefaultTableModel

class TracePanel(
    val project: Project,
    private val traceMap: MutableMap<String, MutableSet<String>>
) {

    fun table(): JBTable {
        return JBTable(ReadTableModel(project, traceMap))
    }

    fun removeButton(table: JBTable, traceMap: MutableMap<String, MutableSet<String>>): JComponent {
        return JButton("remove").apply {
            addActionListener {
                val selectedRow = table.selectedRow
                if (selectedRow != -1) {
                    (table.model as ReadTableModel).removeRow(0)
                }
            }
        }
    }

    class ReadTableModel(
        val project: Project,
        private val traceMap: MutableMap<String, MutableSet<String>>
    ) : DefaultTableModel() {
        init {
            val header = arrayOf("Class", "Method")
            val flatMap = traceMap.flatMap { clz ->
                clz.value.map { method ->
                    arrayOf(clz.key, method)
                }
            }.toTypedArray()
            setDataVector(flatMap, header)
        }

        override fun isCellEditable(row: Int, column: Int): Boolean {
            return false
        }

        override fun removeRow(row: Int) {
            val clz = super.getValueAt(row, 0) as String
            val method = super.getValueAt(row, 1) as String
            super.removeRow(row)
            traceMap[clz]?.remove(method)
            if (traceMap[clz]?.isEmpty() == true) {
                traceMap.remove(clz)
            }
            AgentRMIManager.getAgentRMIManager(project).cancelCustomMethod(clz, method)
        }
    }

}