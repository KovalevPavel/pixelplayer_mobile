plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain_main_flow"))
            implementation(project(":core_main_flow"))
            implementation(project(":feature_main_flow"))
            implementation(project(":network"))
            implementation(project(":api_artists"))
        }
    }
}
