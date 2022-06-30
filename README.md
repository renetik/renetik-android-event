<!---Header--->
[![Android CI](https://github.com/renetik/renetik-android-event/workflows/Android%20CI/badge.svg)
](https://github.com/renetik/renetik-android-event/actions/workflows/android.yml)
# Renetik Android - Event & Property
#### [https://github.com/renetik/renetik-android-event](https://github.com/renetik/renetik-android-event/)
#### [Documentation](https://renetik.github.io/renetik-android-event/)
Framework to enjoy, improve and speed up your application development while writing readable code.
Used as library in music production and performance app Renetik Instruments www.renetik.com as well
as in other projects.

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

Step 2. Add the dependency

```gradle
dependencies {
    implementation 'com.renetik.library:renetik-android-event:$renetik-android-version'
}
```
## Examples from included unit tests:
#### Event Property usage example 
```
	private val property: CSEventProperty<String> = property("initial")

	@Test
	fun onSecondEventCancel() {
		property.onChange { registration, _ ->
			eventCounter++
			if (eventCounter == 2) registration.cancel()
		}
		property.value = "testOne"
		property.value = "testTwo"
		property.value = "testThree"
		assertEquals(2, eventCounter)
		assertEquals("testThree", property.value)
	}
```
#### Event Property with parent registration 
```
	private class TestOwner(parent: TestOwner? = null) : CSEventOwnerHasDestroyBase(parent) {
		val property = property(0)

		init {
			register(parent?.property?.onChange { property.value = it })
		}
	}

	private val parent = TestOwner()
	private val parentChild = TestOwner(parent)
	private val parentChildChild = TestOwner(parentChild)

	@Test
	fun parentChildChildDestroy() {
		parent.property.value = 3
		assertEquals(3, parentChildChild.property.value)

		parentChildChild.destroy()
		parent.property.value = 5
		assertEquals(5, parent.property.value)
		assertEquals(5, parentChild.property.value)
		assertEquals(3, parentChildChild.property.value)
	}
```
#### Event example 
```
	private var eventCounter = 0
	private var eventValue: String? = ""
	private val event = event<String>()

	@Test
	fun onSecondEventCancel() {
		event.add { registration, value ->
			eventCounter++
			eventValue = value
			if (eventCounter == 2) registration.cancel()
		}
		event.fire("testOne")
		event.fire("testTwo")
		event.fire("testThree")
		assertEquals(2, eventCounter)
		assertEquals("testTwo", eventValue)
	}
```
#### Event with parent registration
```
	class TestOwner(parent: TestOwner? = null) : CSEventOwnerHasDestroyBase(parent) {
		val event = event<Int>()
		var eventValue: Int? = null

		init {
			register(event.listen { eventValue = it })
			register(parent?.event?.listen(event::fire))
		}
	}

	private val parent = TestOwner()
	private val parentChild = TestOwner(parent)
	private val parentChildChild = TestOwner(parentChild)

	@Test
	fun parentChildChildDestroy() {
		parent.event.fire(3)
		assertEquals(3, parentChildChild.eventValue)

		parentChildChild.destroy()
		parent.event.fire(5)
		assertEquals(5, parent.eventValue)
		assertEquals(5, parentChild.eventValue)
		assertEquals(3, parentChildChild.eventValue)
	}
```    

