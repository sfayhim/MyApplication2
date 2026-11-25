# Oncology Watch — Pierre Fabre

Application Android de veille scientifique en oncologie développée avec **Jetpack Compose** et connectée à l'API [Cure Cancer With AI](https://www.curecancerwithai.com/developers).

## Fonctionnalités principales

- **Authentification locale** : écran de connexion collaborateur avec accès rapide et vérification via base SQLite locale.
- **Tableau de bord** : vue d'ensemble de la veille oncologique, barre de recherche rapide et raccourcis vers les modules (Recherche PubMed, Essais cliniques, Approbations FDA, Prédictions IA).
- **Recherche scientifique multi-sources** : recherche par mots-clés (avec correspondance français → anglais) et filtres sur les publications PubMed, essais cliniques, approbations FDA, actualités et articles de blog.
- **Consultation détaillée** : affichage complet d'un article, essai clinique, médicament ou actualité, avec ouverture de la source externe.
- **Favoris hors-ligne (Room)** : sauvegarde et suppression locale des éléments favoris depuis l'écran de détail, accessibles via l'écran **Mes Favoris**.
- **Prédictions IA (IBM MAMMAL & Pharma)** :
  - **ClinTox** : prédiction de toxicité clinique d'une molécule (SMILES).
  - **DTI** : estimation d'affinité médicament–cible (`pKd`).
  - **PPI** : prédiction d'interaction protéine–protéine.
  - **Pharma Search** : recherche de composés pharmaceutiques similaires.

## Stack technique

- **UI** : Kotlin, Jetpack Compose, Material 3
- **Architecture** : MVVM (`ViewModel` / `AndroidViewModel`, `UiState`, `StateFlow`)
- **Navigation** : Navigation Compose (`NavHost`, `ModalNavigationDrawer`, arguments typés)
- **Réseau** : Ktor Client (`Android` engine, `ContentNegotiation`, `kotlinx.serialization`)
- **Persistance locale** : Room (`@Entity`, `@Dao`, `@Database`, `Flow`) & KSP
- **Asynchronisme** : Kotlin Coroutines (`viewModelScope`, `Dispatchers.IO`)
