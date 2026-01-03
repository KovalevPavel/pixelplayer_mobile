plugins {
    alias(libs.plugins.pixelplayer.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":api_credentials"))
            implementation(project(":api_storage"))
        }
    }
}
