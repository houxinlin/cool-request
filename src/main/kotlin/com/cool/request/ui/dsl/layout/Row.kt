// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.cool.request.ui.dsl.layout

import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.components.Label
import com.intellij.ui.components.noteComponent
import com.intellij.ui.layout.ComponentPredicate
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.JLabel

@JvmDefaultWithCompatibility
interface BaseBuilder

@JvmDefaultWithCompatibility
interface RowBuilder : BaseBuilder {
    fun createChildRow(
        label: JLabel? = null,
        isSeparated: Boolean = false,
        noGrid: Boolean = false,
        @Nls title: String? = null
    ): Row


    fun createNoteOrCommentRow(component: JComponent): Row


    fun row(label: JLabel? = null, separated: Boolean = false, init: Row.() -> Unit): Row {
        return createChildRow(label = label, isSeparated = separated).apply(init)
    }


    fun row(label: @Nls String?, separated: Boolean = false, init: Row.() -> Unit): Row {
        return row(label?.let { Label(it) }, separated = separated, init)
    }


    fun titledRow(@NlsContexts.BorderTitle title: String, init: Row.() -> Unit): Row

    fun noteRow(@Nls text: String, linkHandler: ((url: String) -> Unit)? = null) {
        createNoteOrCommentRow(noteComponent(text, linkHandler))
    }


    fun onGlobalApply(callback: () -> Unit): Row


    fun onGlobalReset(callback: () -> Unit): Row


    fun onGlobalIsModified(callback: () -> Boolean): Row
}


abstract class Row : Cell(), RowBuilder {
    @get:Deprecated("Use Kotlin UI DSL Version 2")
    @get:ApiStatus.ScheduledForRemoval
    @set:Deprecated("Use Kotlin UI DSL Version 2")
    @set:ApiStatus.ScheduledForRemoval
    abstract var enabled: Boolean

    @get:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @get:ApiStatus.ScheduledForRemoval
    @set:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @set:ApiStatus.ScheduledForRemoval
    abstract var visible: Boolean

    @get:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @get:ApiStatus.ScheduledForRemoval
    @set:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @set:ApiStatus.ScheduledForRemoval
    abstract var subRowsEnabled: Boolean

    @get:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @get:ApiStatus.ScheduledForRemoval
    @set:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @set:ApiStatus.ScheduledForRemoval
    abstract var subRowsVisible: Boolean

    /**
     * Indent for child rows of this row, expressed in steps (multiples of [SpacingConfiguration.indentLevel]). Replaces indent
     * calculated from row nesting.
     */
    @get:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @get:ApiStatus.ScheduledForRemoval
    @set:Deprecated("Use Kotlin UI DSL Version 2", level = DeprecationLevel.HIDDEN)
    @set:ApiStatus.ScheduledForRemoval

    abstract var subRowIndent: Int

    @PublishedApi


    internal abstract fun alignRight()


    abstract fun largeGapAfter()

    /**
     * Shares cell between components.
     *
     * @param isFullWidth If `true`, the cell occupies the full width of the enclosing component.
     */


    inline fun cell(isVerticalFlow: Boolean = false, isFullWidth: Boolean = false, init: InnerCell.() -> Unit) {
        setCellMode(true, isVerticalFlow, isFullWidth)
        InnerCell(this).init()
        setCellMode(false, isVerticalFlow, isFullWidth)
    }

    @PublishedApi


    internal abstract fun setCellMode(value: Boolean, isVerticalFlow: Boolean, fullWidth: Boolean)
}


enum class GrowPolicy {


    SHORT_TEXT,


    MEDIUM_TEXT
}


fun Row.enableIf(predicate: ComponentPredicate) {
    enabled = predicate()
    predicate.addListener { enabled = it }
}
