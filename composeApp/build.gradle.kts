plugins {
    alias(libs.plugins.pixelplayer.library)
    alias(libs.plugins.pixelplayer.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":core_design"))
            implementation(project(":core_storage"))
            implementation(project(":api_credentials"))
            implementation(project(":core_credentials"))
            implementation(project(":core_ui"))
            implementation(project(":api_login"))
            implementation(project(":api_main_flow"))
            implementation(project(":core_player"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
