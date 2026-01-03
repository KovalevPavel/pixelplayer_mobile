plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(project(":api_credentials"))
        implementation(project(":domain_login"))
        implementation(project(":feature_login"))
        implementation(project(":network"))
    }
}
