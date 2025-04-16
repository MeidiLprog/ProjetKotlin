# Amioupas – Projet Android 2025

Une application Android Kotlin ludique autour de l’univers des Amiibos de Nintendo.  
L'utilisateur sélectionne des séries de jeux, télécharge les personnages associés et joue à un quiz visuel !

---

## Objectif du projet

Créer une app Android en Kotlin qui :

- Récupère des données depuis l’API publique [amiiboapi.com](https://www.amiiboapi.com)
- Permet à l'utilisateur de sélectionner des séries de jeux
- Télécharge les personnages (Amiibos) associés et les stocke localement
- Lance un quiz en image avec 3 propositions aléatoires
- Gère un score en temps réel
- Intègre de la musique, des animations et une interface agréable

---

## Fonctionnalités principales

## Écran principal (MainActivity)
- Lancement du jeu
- Accès aux options (volume, musique)
- Bouton Quitter (avec System.exit)

### Sélection de séries de jeux (`GameSeriesSelectionActivity`)
- Affiche dynamiquement toutes les GameSeries (via l'API)
- Sélection multiple avec ListView
- Pré-sélection aléatoire de 4 séries
- Téléchargement des amiibos associés via Retrofit
- Enregistrement local avec Realm

## Écran de jeu (GameActivity)
- Affiche une image d’un personnage aléatoire
- Affiche 3 réponses possibles (noms ou séries)
- Détection de swipe gauche/droite pour changer le type de question
- Score dynamique avec couleur
- Toasts de feedback utilisateur
- Changement automatique de question

## Musique MusicManager.kt
- Lecture d'une musique de fond en boucle
- Volume contrôlable depuis les options
- Pause automatique quand l’app est en arrière-plan (via `Musique_Glob`)
- Reprise automatique quand l’app revient

---

## Architecture technique

| Élément | Lib / outil utilisé | Rôle |
|--------|----------------------|------|
| API Amiibo | Retrofit + Gson | Récupération des personnages |
| Stockage local | Realm Kotlin | Enregistrement et lecture des Amiibos |
| Affichage des images | Glide | Chargement d'images depuis les URL |
| Audio | `MediaPlayer` | Gestion de la musique de fond |
| Quiz | Custom logic | Génération dynamique des questions |
| UI | Android XML + Kotlin | Layouts + logique interactive |
| Cycle de vie global | ProcessLifecycleOwner | Pause/reprise musique automatique |

---

## Lancer le projet

1. Cloner le repo :
   ```bash
   git clone https://github.com/ton-compte/amioupas.git
