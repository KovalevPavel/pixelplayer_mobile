plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain_artists"))
            implementation(project(":feature_artists"))
        }
    }
}
