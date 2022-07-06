<!---Header--->
[![Android Build](https://github.com/renetik/renetik-android-event/workflows/Android%20Build/badge.svg)
](https://github.com/renetik/renetik-android-event/actions/workflows/android.yml)

# Renetik Android - Event & Property
#### [https://github.com/renetik/renetik-android-event](https://github.com/renetik/renetik-android-event/) ➜ [Documentation](https://renetik.github.io/renetik-android-event/)

Framework to enjoy, improve and speed up your application development while writing readable code.
Used as library in many projects and improving it while developing new projects.
I am open for [Hire](https://renetik.github.io) or investment in my mobile app music production & perfromance project Renetik Instruments www.renetik.com.

## Installation
```gradle
allprojects {
    repositories {
        // For master-SNAPSHOT
        maven { url 'https://github.com/renetik/maven-snapshot/raw/master/repository' }
        // For release builds
        maven { url 'https://github.com/renetik/maven/raw/master/repository' }
    }
}
```
```gradle
dependencies {
    implementation 'com.renetik.library:renetik-android-event:$renetik-android-version'
}
```
## Examples
```kotlin
/**
 * Simple event use cases
 */
class EventTest {
    @Test
    fun testListen() {
        val event = event()
        var count = 0
        event.listen { count += 1 }
        event.fire()
        event.fire()
        assertEquals(count, 2)
    }

    @Test
    fun testArgListen() {
        val event = event<Int>()
        var count = 0
        event.listen { count += it }
        event.fire(2)
        event.fire(3)
        assertEquals(count, 5)
    }

    @Test
    fun testListenOnce() {
        val event = event()
        var count = 0
        event.listenOnce { count += 1 }
        event.fire()
        event.fire()
        assertEquals(count, 1)
    }

    @Test
    fun testArgListenOnce() {
        val event = event()
        var count = 0
        event.listenOnce { count += 1 }
        event.fire()
        event.fire()
        assertEquals(count, 1)
    }

    @Test
    fun testEventCancel() {
        val event = event()
        var count = 0
        event.listen { registration, _ ->
            count += 1
            if (count == 2) registration.cancel()
        }
        event.fire()
        event.fire()
        event.fire()
        assertEquals(count, 2)
    }

    @Test
    fun testStringEventCancel() {
        val event = event<String>()
        var value: String? = null
        event.listen { registration, newValue ->
            if (newValue == "second") registration.cancel()
            value = newValue
        }
        event.fire("first")
        assertEquals("first", value)
        event.fire("second")
        assertEquals("second", value)
        event.fire("third")
        assertEquals("second", value)
    }

    @Test
    fun testEventPause() {
        val event = event()
        var count = 0
        val registration = event.listen { count += 1 }
        registration.pause { event.fire() }
        assertEquals(count, 0)
        event.fire()
        assertEquals(count, 1)
    }
}

```

```kotlin
/**
 * Simple event property use cases
 */
class EventPropertyTest {

    @Test
    fun testOnChange() {
        var count = 0
        var value: String by property("initial") { count += 1 }
        value = "second"
        value = "third"
        assertEquals(count, 2)
        assertEquals("third", value)
    }

    @Test
    fun testOnApply() {
        var count = 0
        var value: String by property("initial") { count += 1 }.apply()
        value = "second"
        value = "third"
        assertEquals(count, 3)
        assertEquals("third", value)
    }

    @Test
    fun testArgListen() {
        var count = 0
        var value: Int by property(0) { count += 1 }
        value += 2
        value += 3
        assertEquals(5, value)
        assertEquals(2, count)
    }

    @Test
    fun testEquals() {
        var count = 0
        var value: String by property("") { count += 1 }
        value = "second"
        value = "second"
        assertEquals(count, 1)
        assertEquals("second", value)
    }

    @Test
    fun testNotFireAndOnChangeOnce() {
        var count = 0
        val property: CSEventProperty<String> = property("")
        property.onChangeOnce { count += 1 }
        property.value("one", fire = false)
        property.value = "two"
        property.value = "three"
        assertEquals(count, 1)
        assertEquals("three", property.value)
    }

    @Test
    fun testEventCancel() {
        var count = 0
        val property: CSEventProperty<Int> = property(0)
        property.onChange { registration, value ->
            count += value
            if (count > 2) registration.cancel()
        }
        property.value = 1
        property.value = 2
        property.value = 3
        assertEquals(count, 3)
    }

    @Test
    fun testEventPause() {
        var count = 0
        val property: CSEventProperty<Int> = property(0)
        val registration = property.onChange { count += it }
        registration.pause { property.value = 1 }
        assertEquals(count, 0)
        property.value = 2
        assertEquals(count, 2)
    }
}
``` 

```kotlin
/**
 * Event unregister after owner nulled
 */
class EventOwnerEventTest {
    @Test
    fun testUnregisteredAfterNilled() {
        val owner = CSEventOwnerHasDestroyBase()
        val event = event()
        var count = 0
        owner.register(event.listen { count += 1 })
        event.fire()
        event.fire()
        assertEquals(count, 2)
        owner.destroy()
        event.fire()
        assertEquals(count, 2)
    }
}
``` 

```kotlin
/**
 * Event property unregister after owner nulled
 */
class EventOwnerPropertyTest {
    class SomeClass(parent: SomeClass? = null) : CSEventOwnerHasDestroyBase(parent) {
        val string = property("initial value")

        init {
            register(parent?.string?.onChange { string.value = it })
        }
    }

    @Test
    fun testUnregisteredAfterNilled() {
        val instance1 = SomeClass()
        val instance2 = SomeClass(instance1)
        val instance3 = SomeClass(instance2)
        assertEquals(instance3.string.value, "initial value")
        instance1.string.value = "first value"
        assertEquals(instance3.string.value, "first value")
        instance2.destroy()
        instance1.string.value = "second value"
        assertEquals(instance3.string.value, "first value")
    }
}
``` 
## Renetik Android - Libraries
#### [https://github.com/renetik/renetik-android-core](https://github.com/renetik/renetik-android-core/) ➜ [Documentation](https://renetik.github.io/renetik-android-core/)
#### [https://github.com/renetik/renetik-android-json](https://github.com/renetik/renetik-android-json/) ➜ [Documentation](https://renetik.github.io/renetik-android-json/)
#### [https://github.com/renetik/renetik-android-event](https://github.com/renetik/renetik-android-event/) ➜ [Documentation](https://renetik.github.io/renetik-android-event/)
#### [https://github.com/renetik/renetik-android-store](https://github.com/renetik/renetik-android-store/) ➜ [Documentation](https://renetik.github.io/renetik-android-store/)
#### [https://github.com/renetik/renetik-android-preset](https://github.com/renetik/renetik-android-preset/) ➜ [Documentation](https://renetik.github.io/renetik-android-preset/)
#### [https://github.com/renetik/renetik-android-framework](https://github.com/renetik/renetik-android-framework/) ➜ [Documentation](https://renetik.github.io/renetik-android-framework/)
