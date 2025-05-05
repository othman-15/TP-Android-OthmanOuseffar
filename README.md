# 🛒 Application Catalogue Produits

## 👤 Auteur
**Othman Ouseffar**

## 📱 Description
Cette application mobile simple (développée avec **Jetpack Compose** et **Kotlin**) permet d’afficher une liste de produits avec leurs détails (image, prix, ancien prix). Elle utilise une navigation basique entre un écran d’accueil et une page de détails.

## ⚙️ Dépendances

Le projet utilise les bibliothèques suivantes :

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Coil](https://coil-kt.github.io/coil/compose/) : pour le chargement des images

### Dans `build.gradle` (module)
Assurez-vous d’avoir les dépendances suivantes :
![bl](https://github.com/user-attachments/assets/46cb1bb0-fd7a-464a-874e-0d636c2fd89d)
![aio](https://github.com/user-attachments/assets/b0e81e4f-55d7-4625-b66c-df03e07d6244)

```groovy
implementation "androidx.navigation:navigation-compose:2.7.3"
implementation "io.coil-kt:coil-compose:2.4.0"
implementation "androidx.compose.material3:material3:1.1.2"
