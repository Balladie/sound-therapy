package com.balladie.soundtherapy.view.ui.base

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import kotlin.reflect.KProperty

open class BaseViewModel : ViewModel(), Observable {

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChange(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    fun <T> bindableField(fieldId: Int, initialValue: T) = BindableFieldDelegate(this, fieldId, initialValue)
}

class BindableFieldDelegate<T> (
    val viewModel: BaseViewModel,
    val fieldId: Int,
    initialValue: T
) {
    var field: T = initialValue

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = field

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        field = value
        viewModel.notifyPropertyChange(fieldId)
    }
}
