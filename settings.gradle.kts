pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven { 
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.noties")
                includeGroup("com.github.noties.Markwon")
            }
        }
    }
}

rootProject.name = "email"
include(":app")
 