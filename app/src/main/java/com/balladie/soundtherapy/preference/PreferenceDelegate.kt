package com.balladie.soundtherapy.preference

import android.content.SharedPreferences
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("unchecked_cast", "implicit_cast_to_any")
class PreferenceDelegate<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: PrefKey,
    private val type: KClass<out Any>
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return (when(type) {
            String::class -> sharedPreferences.getString(key.name, "")
            Boolean::class -> sharedPreferences.getBoolean(key.name, false)
            Float::class -> sharedPreferences.getFloat(key.name, 0f)
            Int::class -> sharedPreferences.getInt(key.name, 0)
            Long::class -> sharedPreferences.getLong(key.name, 0L)
            else -> throw IllegalStateException("Preference must be either String, Boolean, Float, Int or Long.")
        } as T).takeIf { sharedPreferences.contains(key.name) }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        with(sharedPreferences.edit()) {
            value?.let {
                when(type) {
                    String::class -> putString(key.name, it as String)
                    Boolean::class -> putBoolean(key.name, it as Boolean)
                    Float::class -> putFloat(key.name, it as Float)
                    Int::class -> putInt(key.name, it as Int)
                    Long::class -> putLong(key.name, it as Long)
                    else -> throw IllegalStateException("Preference must be either String, Boolean, Float, Int or Long.")
                }
            } ?: run {
                remove(key.name)
            }
            commit()
        }
    }
}

fun prefString(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<String>(sharedPreferences, key, String::class)

fun prefBoolean(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Boolean>(sharedPreferences, key, Boolean::class)

fun prefFloat(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Float>(sharedPreferences, key, Float::class)

fun prefInt(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Int>(sharedPreferences, key, Int::class)

fun prefLong(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Long>(sharedPreferences, key, Long::class)
