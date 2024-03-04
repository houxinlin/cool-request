// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.cool.request.ui.dsl.layout

import com.intellij.BundleBase
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.util.bind
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.panel.ComponentPanelBuilder
import com.intellij.openapi.util.NlsContexts.*
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.*
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.ui.layout.selected
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.ApiStatus
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.text.JTextComponent
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KMutableProperty0

@DslMarker


annotation class CellMarker


data class PropertyBinding<V>(val get: () -> V, val set: (V) -> Unit)

@PublishedApi


internal fun <T> createPropertyBinding(prop: KMutableProperty0<T>, propType: Class<T>): PropertyBinding<T> {
    if (prop is CallableReference) {
        val name = prop.name
        val receiver = (prop as CallableReference).boundReceiver
        if (receiver != null) {
            val baseName = name.removePrefix("is")
            val nameCapitalized = StringUtil.capitalize(baseName)
            val getterName = if (name.startsWith("is")) name else "get$nameCapitalized"
            val setterName = "set$nameCapitalized"
            val receiverClass = receiver::class.java

            try {
                val getter = receiverClass.getMethod(getterName)
                val setter = receiverClass.getMethod(setterName, propType)
                return PropertyBinding({ getter.invoke(receiver) as T }, { setter.invoke(receiver, it) })
            } catch (e: Exception) {
                // ignore
            }

            try {
                val field = receiverClass.getDeclaredField(name)
                field.isAccessible = true
                return PropertyBinding({ field.get(receiver) as T }, { field.set(receiver, it) })
            } catch (e: Exception) {
                // ignore
            }
        }
    }
    return PropertyBinding(prop.getter, prop.setter)
}


@Deprecated("Use MutableProperty and Kotlin UI DSL 2")
fun <T> PropertyBinding<T>.toNullable(): PropertyBinding<T?> {
    return PropertyBinding({ get() }, { set(it!!) })
}


inline fun <reified T : Any> KMutableProperty0<T>.toBinding(): PropertyBinding<T> {
    return createPropertyBinding(this, T::class.javaPrimitiveType ?: T::class.java)
}


inline fun <reified T : Any> KMutableProperty0<T?>.toNullableBinding(defaultValue: T): PropertyBinding<T> {
    return PropertyBinding({ get() ?: defaultValue }, { set(it) })
}

class ValidationInfoBuilder(val component: JComponent) {
    fun error(@DialogMessage message: String): ValidationInfo = ValidationInfo(message, component)
    fun warning(@DialogMessage message: String): ValidationInfo =
        ValidationInfo(message, component).asWarning().withOKEnabled()
}

@JvmDefaultWithCompatibility
interface CellBuilder<out T : JComponent> {


    val component: T


    fun comment(
        @DetailedDescription text: String, maxLineLength: Int = ComponentPanelBuilder.MAX_COMMENT_WIDTH,
        forComponent: Boolean = false
    ): CellBuilder<T>


    fun focused(): CellBuilder<T>


    fun withValidationOnApply(callback: ValidationInfoBuilder.(T) -> ValidationInfo?): CellBuilder<T>


    fun withValidationOnInput(callback: ValidationInfoBuilder.(T) -> ValidationInfo?): CellBuilder<T>


    fun onApply(callback: () -> Unit): CellBuilder<T>


    fun onReset(callback: () -> Unit): CellBuilder<T>


    fun onIsModified(callback: () -> Boolean): CellBuilder<T>

    /**
     * All components of the same group share will get the same BoundSize (min/preferred/max),
     * which is that of the biggest component in the group
     */

    @Deprecated("Use Kotlin UI DSL Version 2, see Cell.widthGroup()", level = DeprecationLevel.HIDDEN)
    fun sizeGroup(name: String): CellBuilder<T>


    fun growPolicy(growPolicy: GrowPolicy): CellBuilder<T>


    fun constraints(vararg constraints: CCFlags): CellBuilder<T>

    /**
     * If this method is called, the value of the component will be stored to the backing property only if the component is enabled.
     */


    fun applyIfEnabled(): CellBuilder<T>


    fun <V> withBinding(
        componentGet: (T) -> V,
        componentSet: (T, V) -> Unit,
        modelBinding: PropertyBinding<V>
    ): CellBuilder<T> {
        onApply { if (shouldSaveOnApply()) modelBinding.set(componentGet(component)) }
        onReset { componentSet(component, modelBinding.get()) }
        onIsModified { shouldSaveOnApply() && componentGet(component) != modelBinding.get() }
        return this
    }


    @ApiStatus.Internal

    fun <V> withBindingInt(
        componentGet: (T) -> V,
        componentSet: (T, V) -> Unit,
        modelBinding: PropertyBinding<V>
    ): CellBuilder<T> {
        onApply { if (shouldSaveOnApply()) modelBinding.set(componentGet(component)) }
        onReset { componentSet(component, modelBinding.get()) }
        onIsModified { shouldSaveOnApply() && componentGet(component) != modelBinding.get() }
        return this
    }


    fun withGraphProperty(property: GraphProperty<*>): CellBuilder<T>


    fun enabled(isEnabled: Boolean)


    fun enableIf(predicate: ComponentPredicate): CellBuilder<T>


    fun visible(isVisible: Boolean)


    fun visibleIf(predicate: ComponentPredicate): CellBuilder<T>


    @ApiStatus.Internal
    fun shouldSaveOnApply(): Boolean


    fun withLargeLeftGap(): CellBuilder<T>


    fun withLeftGap(): CellBuilder<T>
}


fun <T : JComponent> CellBuilder<T>.applyToComponent(task: T.() -> Unit): CellBuilder<T> {
    return also { task(component) }
}


fun <T : JTextComponent> CellBuilder<T>.withTextBinding(modelBinding: PropertyBinding<String>): CellBuilder<T> {
    return withBindingInt(JTextComponent::getText, JTextComponent::setText, modelBinding)
}


fun <T : AbstractButton> CellBuilder<T>.withSelectedBinding(modelBinding: PropertyBinding<Boolean>): CellBuilder<T> {
    return withBindingInt(AbstractButton::isSelected, AbstractButton::setSelected, modelBinding)
}


val CellBuilder<AbstractButton>.selected: ComponentPredicate
    get() = component.selected

// separate class to avoid row related methods in the `cell { } `
@CellMarker


abstract class Cell : BaseBuilder {
    /**
     * Sets how keen the component should be to grow in relation to other component **in the same cell**. Use `push` in addition if need.
     * If this constraint is not set the grow weight is set to 0 and the component will not grow (unless some automatic rule is not applied.
     * Grow weight will only be compared against the weights for the same cell.
     */


    val growX: CCFlags = CCFlags.growX


    val growY: CCFlags = CCFlags.growY


    val grow: CCFlags = CCFlags.grow

    /**
     * Makes the column that the component is residing in grow with `weight`.
     */


    val pushX: CCFlags = CCFlags.pushX

    /**
     * Makes the row that the component is residing in grow with `weight`.
     */


    val pushY: CCFlags = CCFlags.pushY


    @ApiStatus.Internal
    fun label(
        @Label text: String,
        style: UIUtil.ComponentStyle? = null,
        fontColor: UIUtil.FontColor? = null,
        bold: Boolean = false
    ): CellBuilder<JLabel> {
        val label = Label(text, style, fontColor, bold)
        return component(label)
    }


    @ApiStatus.Internal
    fun label(
        @Label text: String,
        font: JBFont,
        fontColor: UIUtil.FontColor? = null
    ): CellBuilder<JLabel> {
        val label = Label(text, fontColor = fontColor, font = font)
        return component(label)
    }


    fun link(
        @LinkLabel text: String,
        style: UIUtil.ComponentStyle? = null,
        action: () -> Unit
    ): CellBuilder<JComponent> {
        val result = Link(text, style, action)
        return component(result)
    }


    fun browserLink(@LinkLabel text: String, url: String): CellBuilder<JComponent> {
        val result = BrowserLink(text, url)
        return component(result)
    }


    fun button(@Button text: String, actionListener: (event: ActionEvent) -> Unit): CellBuilder<JButton> {
        val button = JButton(BundleBase.replaceMnemonicAmpersand(text))
        button.addActionListener(actionListener)
        return component(button)
    }


    inline fun checkBox(
        @Checkbox text: String,
        isSelected: Boolean = false,
        @DetailedDescription comment: String? = null,
        crossinline actionListener: (event: ActionEvent, component: JCheckBox) -> Unit
    ): CellBuilder<JBCheckBox> {
        return checkBox(text, isSelected, comment)
            .applyToComponent {
                addActionListener(ActionListener { actionListener(it, this) })
            }
    }

    @JvmOverloads


    fun checkBox(
        @Checkbox text: String,
        isSelected: Boolean = false,
        @DetailedDescription comment: String? = null
    ): CellBuilder<JBCheckBox> {
        val result = JBCheckBox(text, isSelected)
        return result(comment = comment)
    }


    fun checkBox(
        @Checkbox text: String,
        prop: KMutableProperty0<Boolean>,
        @DetailedDescription comment: String? = null
    ): CellBuilder<JBCheckBox> {
        return checkBox(text, prop.toBinding(), comment)
    }


    fun checkBox(
        @Checkbox text: String,
        getter: () -> Boolean,
        setter: (Boolean) -> Unit,
        @DetailedDescription comment: String? = null
    ): CellBuilder<JBCheckBox> {
        return checkBox(text, PropertyBinding(getter, setter), comment)
    }


    private fun checkBox(
        @Checkbox text: String,
        modelBinding: PropertyBinding<Boolean>,
        @DetailedDescription comment: String?
    ): CellBuilder<JBCheckBox> {
        val component = JBCheckBox(text, modelBinding.get())
        return component(comment = comment).withSelectedBinding(modelBinding)
    }


    fun checkBox(
        @Checkbox text: String,
        property: GraphProperty<Boolean>,
        @DetailedDescription comment: String? = null
    ): CellBuilder<JBCheckBox> {
        val component = JBCheckBox(text, property.get())
        return component(comment = comment).withGraphProperty(property).applyToComponent { component.bind(property) }
    }


    fun <T> comboBox(
        model: ComboBoxModel<T>,
        getter: () -> T?,
        setter: (T?) -> Unit,
        renderer: ListCellRenderer<T?>? = null
    ): CellBuilder<ComboBox<T>> {
        return comboBox(model, PropertyBinding(getter, setter), renderer)
    }


    fun <T> comboBox(
        model: ComboBoxModel<T>,
        modelBinding: PropertyBinding<T?>,
        renderer: ListCellRenderer<T?>? = null
    ): CellBuilder<ComboBox<T>> {
        return component(ComboBox(model))
            .applyToComponent {
                this.renderer = renderer ?: SimpleListCellRenderer.create("") { it.toString() }
                selectedItem = modelBinding.get()
            }
            .withBindingInt(
                { component -> component.selectedItem as T? },
                { component, value -> component.setSelectedItem(value) },
                modelBinding
            )
    }


    inline fun <reified T : Any> comboBox(
        model: ComboBoxModel<T>,
        prop: KMutableProperty0<T>,
        renderer: ListCellRenderer<T?>? = null
    ): CellBuilder<ComboBox<T>> {
        return comboBox(model, prop.toBinding().toNullable(), renderer)
    }


    fun <T> comboBox(
        model: ComboBoxModel<T>,
        property: GraphProperty<T>,
        renderer: ListCellRenderer<T?>? = null
    ): CellBuilder<ComboBox<T>> {
        return comboBox(model, PropertyBinding(property::get, property::set).toNullable(), renderer)
            .withGraphProperty(property)
            .applyToComponent { bind(property) }
    }


    fun textField(prop: KMutableProperty0<String>, columns: Int? = null): CellBuilder<JBTextField> =
        textField(prop.toBinding(), columns)


    fun textField(getter: () -> String, setter: (String) -> Unit, columns: Int? = null): CellBuilder<JBTextField> =
        textField(PropertyBinding(getter, setter), columns)


    fun textField(binding: PropertyBinding<String>, columns: Int? = null): CellBuilder<JBTextField> {
        return component(JBTextField(binding.get(), columns ?: 0))
            .withTextBinding(binding)
    }


    fun spinner(
        getter: () -> Int,
        setter: (Int) -> Unit,
        minValue: Int,
        maxValue: Int,
        step: Int = 1
    ): CellBuilder<JBIntSpinner> {
        val spinner = JBIntSpinner(getter(), minValue, maxValue, step)
        return component(spinner).withBindingInt(
            JBIntSpinner::getNumber,
            JBIntSpinner::setNumber,
            PropertyBinding(getter, setter)
        )
    }


    fun scrollPane(component: Component): CellBuilder<JScrollPane> {
        return component(JBScrollPane(component))
    }


    abstract fun <T : JComponent> component(component: T): CellBuilder<T>


    abstract fun <T : JComponent> component(component: T, viewComponent: JComponent): CellBuilder<T>


    operator fun <T : JComponent> T.invoke(
        vararg constraints: CCFlags,
        growPolicy: GrowPolicy? = null,
        @DetailedDescription comment: String? = null
    ): CellBuilder<T> = component(this).apply {
        constraints(*constraints)
        if (comment != null) comment(comment)
        if (growPolicy != null) growPolicy(growPolicy)
    }


    internal fun internalPlaceholder(): CellBuilder<JComponent> {
        return component(JPanel().apply {
            minimumSize = Dimension(0, 0)
            preferredSize = Dimension(0, 0)
            maximumSize = Dimension(0, 0)
        })
    }
}


class InnerCell(val cell: Cell) : Cell() {


    override fun <T : JComponent> component(component: T): CellBuilder<T> {
        return cell.component(component)
    }


    override fun <T : JComponent> component(component: T, viewComponent: JComponent): CellBuilder<T> {
        return cell.component(component, viewComponent)
    }
}


@Deprecated(
    "Use com.intellij.ui.dsl.listCellRenderer.BuilderKt.textListCellRenderer/listCellRenderer instead",
    level = DeprecationLevel.HIDDEN
)
fun <T> listCellRenderer(renderer: SimpleListCellRenderer<T?>.(value: T, index: Int, isSelected: Boolean) -> Unit): SimpleListCellRenderer<T?> {
    return object : SimpleListCellRenderer<T?>() {
        override fun customize(list: JList<out T?>, value: T?, index: Int, selected: Boolean, hasFocus: Boolean) {
            if (value != null) {
                renderer(this, value, index, selected)
            }
        }
    }
}
