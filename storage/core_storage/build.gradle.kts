plugins {
    alias(libs.plugins.pixelplayer.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":api_storage"))

            implementation(libs.datastore)
            implementation(libs.datastore.preferences)
        }
    }
}
