plugins {
    alias(libs.plugins.pixelplayer.library)
    alias(libs.plugins.pixelplayer.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":api_credentials"))
            implementation(project(":domain_login"))
            implementation(project(":network"))
        }
    }
}
