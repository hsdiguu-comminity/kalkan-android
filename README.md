# Kalkan – Android

Kalkan'ın Android uygulaması; sitenin çekirdeğini içerir.

**Sorumlu:** Ali Alp Kocaoğlu · **Diller:** Kotlin, XML, Gradle Kotlin DSL · **Stack:** Jetpack Compose, Material 3, MVVM

## Klasör yapısı

```
gradle/libs.versions.toml           Bağımlılık sürümleri (version catalog)
app/src/main/
├── AndroidManifest.xml             İzinler (kamera, bildirim), paylaş menüsü intent filtreleri
├── java/com/kalkan/app/
│   ├── ui/screens/auth/            Giriş & 2FA
│   ├── ui/screens/chat/            Asistan
│   ├── ui/screens/support/         Canlı destek
│   ├── ui/screens/tools/           Link & QR tarayıcı (CameraX + ML Kit)
│   ├── ui/screens/reports/         İhbar formu
│   ├── ui/screens/webview/         Dashboard & akademi (WebView)
│   ├── ui/theme/  navigation/  components/
│   ├── data/remote/  local/  repository/   Retrofit, Room, DataStore
│   ├── di/                         Hilt modülleri
│   ├── notifications/              Firebase Cloud Messaging
│   └── share/                      "Paylaş → Kalkan ile tara"
└── res/values/                     strings.xml, colors.xml, themes.xml
app/src/test/  app/src/androidTest/ JUnit, Compose UI Test
```

> **READ_SMS izni kullanılmaz.** Kullanıcıya görünen tüm metinler `strings.xml` içinde tutulur.

## Kurulum

Henüz yapılmadı – Gradle dosyaları boştur, ekip tarafından doldurulacak (Gradle wrapper dahil).

## Çalışma kuralları

- `main` korumalıdır; her değişiklik ayrı bir branch ve pull request ile gelir, en az 1 onay gerekir.
- Branch adları: `feature/<alan>-<kısa-açıklama>`, `fix/<alan>-<kısa-açıklama>`
- Commit mesajları: Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`)
- `.env` dosyaları repoya eklenmez; örnek değerler `.env.example` içinde tutulur.
- Projenin tamamı için bkz. [`CLAUDE.md`](CLAUDE.md).
