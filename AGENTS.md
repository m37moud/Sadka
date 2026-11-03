## Build and Verification

Use the Gradle wrapper and configure the Android SDK in untracked `local.properties`. Current scaffold settings are compile/target SDK 37, minimum API 24, Android JVM target 11, and Gradle daemon JDK 21. The plan targets minimum API 26, iOS 16, and JVM target/JDK 21; reconcile these in Task 1 rather than assuming that migration already happened. Gradle/catalog/wrapper files describe the actual build; planned pins are candidates until compiled together. Verify compatible stable versions against official sources before dependency changes. Select JDK/Xcode per command; do not change global Java or `xcode-select` settings.

- `./gradlew :app:assembleDebug` (or `:androidApp:assembleDebug`) — build the Android debug APK.
- `./gradlew :app:installDebug` (or `:androidApp:installDebug`) — install on a connected device/emulator.
- `./gradlew :app:lintDebug :app:lintRelease` (or `:androidApp:lint*`) — Android lint.
- `./gradlew :app:assembleRelease` (or `:androidApp:assembleRelease`) — build release output; the scaffold has no release signing configuration and minification is disabled.
- `./gradlew :shared:tasks --all` — confirm generated KMP task names at the compatibility gate.
- `./gradlew :shared:testAndroidHostTest :shared:iosSimulatorArm64Test :app:testDebugUnitTest` — shared/native/unit checks, subject to required toolchains.
- `./gradlew :app:connectedDebugAndroidTest` — connected Android journeys when implemented.
- `./gradlew :desktopApp:run` and `./gradlew :shared:jvmTest` — existing desktop development checks.

For iOS, inspect the actual Xcode project/scheme and available simulator before building/testing; use the plan's Daymark commands after its host migration. Simulator results do not prove physical location, background execution, signing, or App Group behavior.

Automated test creation and coverage targets are optional for now unless explicitly requested for the task. Run relevant existing checks and report verification gaps honestly; do not claim an unperformed acceptance check passed. Start with toolchain/native feasibility gates before dependent feature work. Record missing hardware/OAuth/signing prerequisites as incomplete and continue independent work. Device evidence must cover Android delay/Doze/reboot and manufacturer differences, iOS reminders/widgets/suspension, failure recovery, and EN/AR × light/dark on both platforms. Save redacted UI evidence in `artifacts/`. Documentation-only changes need link/configuration review and `git diff --check`, not application tests.

## Android and Compose Skills for Agents

Read and apply the applicable installed `SKILL.md` files before writing, editing, refactoring, or fixing code; listing a skill without following its workflow is insufficient. Use `clean-code-guard` for production code, `compose-expert:compose-expert` for shared or Android Compose code, and both Compose skills for Android-specific Compose work. Paths below are machine-specific; locate equivalent installations elsewhere. Adapt recommendations to this repository's Compose Multiplatform architecture; preserve the existing UI architecture, including any XML-based screens, unless migration is requested. Repository contracts, verified dependency versions, and existing architecture take precedence over generic skill examples; do not introduce Hilt, Navigation 2, or Android-only APIs into shared code from those examples.

- [android-cli](/Users/m37moud/.agents/skills/android-cli/SKILL.md): SDK/emulators, deployment, and official documentation. Read `references/interact.md` before layout/screenshots and `references/journeys.md` before journeys.
- [android-dev](/Users/m37moud/.codex/skills/android-dev/SKILL.md): architecture, lifecycle, debugging, performance, and releases. Adapt recommendations to existing Kotlin/CMP conventions; automated test creation and coverage targets are not required for now.
- [android_ui_verification](/Users/m37moud/.codex/skills/android_ui_verification/SKILL.md): emulator UI checks/screenshots. Inspect bounds before tapping; use native logcat rather than ReactNativeJS filters. Save evidence in `artifacts/`.
- [android-ui-journey-testing](/Users/m37moud/.codex/skills/android-ui-journey-testing/SKILL.md): XML journeys; execute sequentially, fail fast, mark remaining steps skipped, and emit JSON results. Also produce Markdown when following CLI journey guidance.
- [clean-code-guard](.agents/skills/clean-code-guard/SKILL.md): apply its imperatives while writing production code, then perform the mandatory guard pass described below.
- [android-jetpack-compose-expert](/Users/m37moud/.codex/skills/android-jetpack-compose-expert/SKILL.md): required alongside `compose-expert:compose-expert` when implementing, migrating, or changing Android Compose UI, state management, side effects, navigation, or performance. Adapt Android examples to the repository's constructor injection and multiplatform contracts.
- [compose-expert:compose-expert](/Users/m37moud/.codex/skills-src/compose-skill/skills/compose-expert/SKILL.md): required when writing or changing shared Compose/CMP or Android Compose code. Read the topic references selected by its routing table before implementation, including `references/multiplatform.md` for shared code; consult state, side-effect, navigation, performance, theming, and accessibility references as relevant. Use its review workflow for Compose review requests.

## UI Guidelines: Theming, Small Screens, and Reusable Components

Follow these mandatory rules across all UI features, screens, and reusable components:

### 1. Theme and Design System Rules
- **Material 3 Foundation**: Build all UI strictly with Material 3 (`androidx.compose.material3.*`). Material 2 (`androidx.compose.material.*`) is legacy and only present for temporary bridge compatibility; never introduce new Material 2 imports or components, and migrate existing ones when modifying a screen.
- **Brand Identity & Color Tokens**:
  - Use `MaterialTheme.colorScheme` tokens (`primary`, `surface`, `surfaceContainer`, `onSurface`, `outlineVariant`, etc.) for all standard surfaces, typography, and controls.
  - Use `IslamicTheme.colors` (`LocalIslamicColors`) for brand-specific Islamic tokens (e.g., `goldOrnament`, Quranic accents, ayah dividers, prayer markers).
  - **Zero Hardcoded Colors**: Never hardcode colors (e.g. `Color(0xFF...)` or `Color.Black` / `Color.White`) in screen or component composables. Every color must resolve through the Material 3 theme or `IslamicTheme` tokens.
- **Theme Parity (Light & Dark Modes)**:
  - Every screen, dialog, and component must look first-class in both Light and Dark themes.
  - Guarantee WCAG AA contrast ratios (minimum 4.5:1 for normal text, 3:1 for large/bold text) across both palettes. Always pair container tokens with their matching `on*` content token (e.g., `primaryContainer` with `onPrimaryContainer`, `surface` with `onSurface`).
  - Dynamic Color (Material You) defaults to `false` (`dynamicColor = false`) in `SadkaQuranTheme` to maintain the botanical green and gold Quranic identity.
- **Typography & Font Scaling**:
  - Use `MaterialTheme.typography` for app UI and navigation.
  - Use `QuranTypography` for Quranic script (e.g., Uthmani / Amiri Arabic fonts).
  - All font sizes must use `sp` units (never `dp`). Layouts must gracefully accommodate system accessibility text scaling (e.g., font scale up to 1.5x) and user Quran font size adjustments without text truncation or broken cards.
- **System Bars**:
  - Synchronize status bar and navigation bar colors with `MaterialTheme.colorScheme.surface` or container colors via `WindowCompat` with appropriate light/dark icon contrast.

### 2. Small Screen & Responsive Layout Standards
- **Compact Viewport Targets**:
  - Support and verify compact screen widths down to 320dp (small phones, foldable cover displays, split-screen / multi-window mode) and short vertical viewports (< 600dp).
- **Flexible Dimensions (No Hardcoded Overflows)**:
  - Do not use rigid fixed widths or heights on containers or cards (e.g., hardcoded `Modifier.width(340.dp)` or `Modifier.height(500.dp)`).
  - Use adaptive layout modifiers: `Modifier.fillMaxWidth()`, `Modifier.weight()`, `Modifier.wrapContentHeight()`, and bounded constraints (`Modifier.widthIn(max = 420.dp)`, `Modifier.heightIn(min = ...)`) to let content adapt fluidly.
  - Use responsive padding and gutters (e.g., 12.dp–16.dp horizontal gutters on compact displays instead of large fixed margins).
- **Defensive Scrolling Safeguards**:
  - Always wrap content in scrollable containers (`Modifier.verticalScroll(rememberScrollState())` or `LazyColumn`) for dialogs, bottom sheets, settings screens, and detail cards. Content must never be obscured or inaccessible when the on-screen keyboard appears or when rendered on a short screen.
- **Text Wrapping & Resilience**:
  - Set `softWrap = true`, configure explicit `maxLines`, and apply `overflow = TextOverflow.Ellipsis` where appropriate for titles, surah metadata, and translation descriptions. Never assume text fits in a single line.
- **Accessible Touch Targets**:
  - All interactive elements (buttons, icon buttons, ayah click targets, dropdown triggers) must maintain a minimum touch target size of 48dp × 48dp per Material 3 and WCAG standards (`Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)` or `Modifier.minimumInteractiveComponentSize()`).
  - Ensure adjacent touch targets do not overlap on dense compact screens.
- **Dialogs & Bottom Sheets on Compact Screens**:
  - Prefer `ModalBottomSheet` for complex actions and selections on compact screens.
  - For `Dialog` components, use proportional or bounded widths (e.g., `Modifier.fillMaxWidth(0.92f)` or `Modifier.widthIn(max = 400.dp)`) with internal vertical scroll, ensuring confirm/cancel action buttons remain visible above the bottom edge.

### 3. Reusable Composable Component Standards
- **Location & Architecture**:
  - Reusable components belong in `app/src/main/java/com/example/sadkaquran/ui/component/` (or shared multiplatform UI modules).
  - Organize components following Atomic Design:
    - **Atoms**: Single-responsibility elements (custom badges, status chips, audio buttons, icon containers).
    - **Molecules**: Combinations of atoms (surah list items, audio playback controls, font size sliders, ayah action rows).
    - **Organisms**: Complex self-contained UI sections (MiniPlayerBar, ReciterPickerDialog, AyahActionBottomSheet).
- **Modifier Discipline**:
  - **Mandatory First Optional Parameter**: Every reusable composable MUST expose `modifier: Modifier = Modifier` as its first optional parameter.
  - Apply this `modifier` to the root layout element.
  - Do not bake external margins or fixed position offsets into the component's internal root modifier; the caller must control external placement.
- **Slot-Based APIs**:
  - Favor slot parameters (`content: @Composable () -> Unit`, `leadingIcon: (@Composable () -> Unit)? = null`, `trailingContent: (@Composable () -> Unit)? = null`, `actions: @Composable RowScope.() -> Unit = {}`) over rigid parameter lists or boolean styling flags.
- **State Hoisting & ViewModel Decoupling**:
  - Reusable components must be stateless or hoist their state.
  - **Never pass a `ViewModel` into a reusable component in `ui/component/`**. Pass immutable data/primitives down and event lambdas (`onValueChange`, `onClick`, `onDismiss`) up.
  - Keep components easily testable, previewable, and reusable in multiple screens or dialogs.
- **Stability & Performance**:
  - Annotate custom data models used in public composable signatures with `@Immutable` or `@Stable`.
  - Avoid passing unstable collections or recreating unstable lambdas inline that prevent Compose compiler strong skipping optimization.
- **Preview Support**:
  - Every reusable component must provide `@Preview` functions covering:
    1. Standard Light Theme preview.
    2. Dark Theme preview.
    3. (Recommended) Compact screen / large font scale preview (e.g., `fontScale = 1.5f`, `widthDp = 320`) to verify accessibility and responsive resilience.

## Production Code Review

Use [clean-code-guard](.agents/skills/clean-code-guard/SKILL.md) throughout production code-writing tasks. Read `references/ai-failure-modes.md` first and apply its imperatives while implementing. After non-trivial production code changes, run its guard pass and self-check before presenting, committing, or merging. Fix violations and repeat the self-check after every subsequent production code edit in the same session. Report actual fixes or remaining findings using the skill's format, ending with `clean-code-guard: clean` or `clean-code-guard: <N> fixed, <M> flagged for author`. If the skill cannot be located, apply the installed [clean-code](/Users/m37moud/.codex/skills/clean-code/SKILL.md) checklist as the fallback and explicitly report that the guard itself was unavailable.

Apply the plan's RF1–RF5 review focus: sensor validity, competing edits/claims, calendar boundaries, literal/localized text, and obsolete network callbacks. Repeat review after subsequent code edits. For review-only requests, report findings without editing unless asked. Preserve behavior during refactors. This production review excludes documentation, tooling configuration, and test-code review; automated test creation remains optional.

## Commits and Pull Requests

Use focused, imperative subjects. Stage only task-owned files and inspect the staged diff, including `git diff --cached --check`; preserve unrelated staged/unstaged work. PRs describe behavior, relevant issues, actual verification results, remaining native/release gates, and screenshots for UI changes. Never claim a concept, fake adapter, zero-test run, or unavailable device passed a native acceptance gate.
