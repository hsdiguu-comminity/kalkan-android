// Tüm modüller için ortak eklentiler. Sürümler gradle/libs.versions.toml içindedir.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
