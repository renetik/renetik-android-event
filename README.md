<!---Header-->
[![Android Build](https://github.com/renetik/renetik-android-event/workflows/Android%20Build/badge.svg)](https://github.com/renetik/renetik-android-event/actions/workflows/android.yml)
[![Maven Packages](https://img.shields.io/badge/Maven-GitHub%20Packages-blue)](https://github.com/renetik/maven)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple)](https://kotlinlang.org)

## Renetik Android — Event & Property
### [Repository](https://github.com/renetik/renetik-android-event/) • [API Docs](https://renetik.github.io/renetik-android-event/)

Lightweight event and reactive property primitives for Android/Kotlin.

- **Events**: type-safe signals with `listen`, `listenOnce`, `pause`/`resume`, `cancel`, and optional main-thread delivery.
- **Properties**: observable values with `onChange`, `computed`, two‑way `connect`, pausable updates, and Kotlin delegate helpers.
- **Registrations**: disposable subscriptions with lifecycle helpers, `pause`/`resume`, and `eventCancel` for clean teardown.

Used across Renetik projects to keep app logic clear and decoupled.

### Installation
Add the Renetik GitHub Maven repositories and the dependency:

```gradle
allprojects {
    repositories {
        // SNAPSHOTs
        maven { url 'https://github.com/renetik/maven-snapshot/raw/master/repository' }
        // Releases
        maven { url 'https://github.com/renetik/maven/raw/master/repository' }
        // Or mavenLocal() if you publish locally
    }
}
```

```gradle
dependencies {
    implementation 'com.renetik.library:renetik-android-event:$renetik-android-version'
}
```

### Compatibility
- **minSdk**: 26
- **target/compileSdk**: 35
- **Kotlin**: 2.2.10
- **AGP**: 8.1.x

### Quick start
Events:
```kotlin
import renetik.android.event.CSEvent.Companion.event

val onTick = event<Unit>()
val reg = onTick.listen { println("tick") }
onTick.fire()
reg.cancel()
```

Properties:
```kotlin
import renetik.android.event.property.CSProperty.Companion.property

var name by property("Guest") { println("changed to $it") }
name = "Alice"
```

Listen once and pause/resume:
```kotlin
val clicks = event<Unit>()
clicks.listenOnce { println("first and only") }
val r = clicks.listen { println("every time") }
r.pause(); clicks.fire(); r.resume(); clicks.fire()
```

Two‑way connect and computed properties:
```kotlin
val a = property(0)
val b = property(0)
a.connect(b) // keep in sync both ways
val percent = a.computed(from = { it / 100f }, to = { (it * 100).toInt() })
```

### Core concepts
- **`CSEvent<T>`**: `listen`, `listenOnce`, `fire(T)`, `pause()/resume()`, `isListened`, `clear()`; `onMain()` to deliver on main thread.
- **`CSProperty<T>`**: `value`, `onChange`, `fireChange`, `pause()/resume(fire)`; helpers: `computed`, `connect`, `paused {}`, delegates.
- **`CSRegistration`**: result of `listen`/`onChange`; supports `pause()/resume()/cancel()`, `isActive`, and `eventCancel`.

### Delegates (derived values and projections)
Delegates let you derive values and signals from existing events/properties with minimal boilerplate. They return lightweight `CSHasChangeValue<T>` or `CSProperty<T>` that stay in sync.

- From `CSEvent<T>`:
  - `delegate { from() }` • `delegateLate { from() }`

- From `CSProperty<T>` and `CSHasChangeValue<T>`:
  - `computed(from, to)` and `computed { get, set }`
  - `delegateValue { transform(it) }`, `delegate { combine(...) }`
  - Convenience delegates: boolean logic (`and`, `or`, `not`), equality checks, null checks, type casts, list delegates, etc.

Examples:
```kotlin
// Project an event into a property-like value that updates on each fire
val meterEvent = event<Int>()
val latest = meterEvent.delegate { it }
println(latest.value)

// Late (nullable) projection
val maybeLatest = meterEvent.delegateLate { it }

// Compute a percent view over an Int property with two-way mapping
val volume = property(50)
val volumePercent = volume.computed(from = { it / 100f }, to = { (it * 100).toInt() })

// Boolean delegates
val isMuted = property(false)
val isAudible = !isMuted // via computed/not delegate

// Combine multiple change sources
val width = property(100)
val height = property(50)
val area = CSProperty.property(parent = null, item1 = width, item2 = height, item3 = isAudible) { w, h, audible ->
    if (audible) w * h else 0
}
```

### Threading
Deliver event callbacks on the main thread by chaining `onMain(owner)` on the created event:

```kotlin
val owner = /* something implementing CSHasDestruct */
val e = CSEvent.event<Unit>().onMain(owner)
e.listen { /* runs on main */ }
```

### Coroutines
Suspend-friendly primitives and helpers:

- Await on values (for any `CSHasChangeValue<T>`):
  - `waitFor { condition(it) }`
  - For booleans: `waitForTrue()`, `waitForFalse()`, plus `suspendIfTrue()/suspendIfFalse()`
- Await next change (for any `CSHasChange<T>`):
  - `waitForChange()` returns the new value
- Suspend types:
  - `CSSuspendEvent<T>`: `listen { suspend }`, `fire(arg)` is `suspend`
  - `CSSuspendProperty<T>`: `onChange { suspend }`
- Launch helpers (default dispatcher = Main):
  - `onChangeLaunch { suspend }`, `actionLaunch { suspend }`
  - Boolean: `onTrueLaunch {}`, `onFalseLaunch {}`
  - From `CSHasRegistrations`: `launch { job -> }`, `launchWhileActive {}`, `launch(key) {}`

Examples:
```kotlin
// Wait for a property to reach a state
val isReady = property(false)
scope.launch { isReady.waitForTrue() }

// Await the next value change
val value = property(0)
scope.launch {
    val next = value.waitForChange()
}

// Suspend event
val onLoaded = CSSuspendEvent.suspendEvent<Unit>()
scope.launch { onLoaded.listen { /* suspending work */ } }
scope.launch { onLoaded() } // fire Unit

// Launch on change
value.onChangeLaunch { newValue ->
    // suspending work using newValue
}
```

### API Reference
Browse the generated docs: [API Docs](https://renetik.github.io/renetik-android-event/)

### Related Renetik libraries
- [renetik-android-core](https://github.com/renetik/renetik-android-core/) • [Docs](https://renetik.github.io/renetik-android-core/)
- [renetik-android-json](https://github.com/renetik/renetik-android-json/) • [Docs](https://renetik.github.io/renetik-android-json/)
- [renetik-android-event](https://github.com/renetik/renetik-android-event/) • [Docs](https://renetik.github.io/renetik-android-event/)
- [renetik-android-store](https://github.com/renetik/renetik-android-store/) • [Docs](https://renetik.github.io/renetik-android-store/)
- [renetik-android-preset](https://github.com/renetik/renetik-android-preset/) • [Docs](https://renetik.github.io/renetik-android-preset/)
- [renetik-android-framework](https://github.com/renetik/renetik-android-framework/) • [Docs](https://renetik.github.io/renetik-android-framework/)

### Contributing
Issues and PRs are welcome. Please include a clear description and small, focused changes.

### License
This library is released under the terms of the license in `LICENSE.txt`.

—
If you find this useful, consider supporting the broader Renetik Instruments effort at `https://www.renetik.com` or get in touch for [Hire](https://renetik.github.io).
