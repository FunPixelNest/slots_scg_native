pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SlotsNative"
include(":app", ":engine")

// Theme art lives in themes/theme_<id>/src/main/assets and is packaged into the app
// (see app/build.gradle.kts), so every theme is unlocked offline.
