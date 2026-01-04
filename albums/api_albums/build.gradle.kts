plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain_albums"))
            implementation(project(":feature_albums"))
        }
    }
}
