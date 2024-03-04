// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.cool.request.ui.dsl.layout

import com.cool.request.ui.dsl.layout.migLayout.MigLayoutBuilder
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.util.ui.JBUI
import java.awt.Container
import javax.swing.JComponent

@PublishedApi
internal fun createLayoutBuilder(): LayoutBuilder {
    return LayoutBuilder(MigLayoutBuilder(createIntelliJSpacingConfiguration()))
}

interface LayoutBuilderImpl {

    val rootRow: Row


    fun build(container: Container, layoutConstraints: Array<out LCFlags>)


    val preferredFocusedComponent: JComponent?

    // Validators applied when Apply is pressed


    val validateCallbacks: List<() -> ValidationInfo?>

    // Validators applied immediately on input


    val componentValidateCallbacks: Map<JComponent, () -> ValidationInfo?>

    // Validation applicants for custom validation events


    val customValidationRequestors: Map<JComponent, List<(() -> Unit) -> Unit>>


    val applyCallbacks: Map<JComponent?, List<() -> Unit>>


    val resetCallbacks: Map<JComponent?, List<() -> Unit>>


    val isModifiedCallbacks: Map<JComponent?, List<() -> Boolean>>
}

// https://jetbrains.github.io/ui/controls/input_field/#spacing


private fun createIntelliJSpacingConfiguration(): SpacingConfiguration {
    return object : SpacingConfiguration {
        override val horizontalGap = JBUI.scale(6)
        override val componentVerticalGap = JBUI.scale(6)
        override val labelColumnHorizontalGap = JBUI.scale(6)
        override val largeHorizontalGap = JBUI.scale(16)
        override val largeVerticalGap = JBUI.scale(20)

        override val shortTextWidth = JBUI.scale(250)
        override val maxShortTextWidth = JBUI.scale(350)

        override val unitSize = JBUI.scale(4)

        override val dialogTopBottom = JBUI.scale(10)
        override val dialogLeftRight = JBUI.scale(12)

        override val commentVerticalTopGap = JBUI.scale(6)

        override val indentLevel: Int
            get() = JBUI.scale(20)
    }
}
