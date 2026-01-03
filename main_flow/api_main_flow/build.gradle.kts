plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain_main_flow"))
            implementation(project(":feature_main_flow"))
        }
    }
}
