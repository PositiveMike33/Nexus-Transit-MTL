# Séquence d'Onboarding Interactive & Résilience Réseau STM GTFS-RT

Mise en place d'une séquence d'onboarding immersive sous forme de diaporama interactif guidant la prise en main des modules MCP (STM, SkyFi, Pieces) et la capture des préférences de mobilité urbaine, couplée à une sécurisation du pipeline réseau STM GTFS-RT avec repli transparent vers le cache local Room et données de synthèse.

## Décisions Validées & Points d'Arbitrage

> [!IMPORTANT]
> Les trois décisions fondamentales issues de la phase de cadrage ont été intégrées dans l'architecture :
> - **Résilience Réseau STM** : En cas de timeout ou de latence excessive sur l'API externe STM, bascule transparente et silencieuse vers le cache local Room (`StmDatabase` / `VehiclePositionDao`) et les données de synthèse pour garantir la continuité de l'affichage cartographique et du calcul ToT.
> - **Expérience d'Onboarding** : Diaporama interactif multi-étapes déclenché au premier lancement (avec indicateurs de pas, découverte animée des moteurs MCP et formulaire interactif de personnalisation), également réouvrable à la demande depuis la barre d'outils.
> - **Persistance des Préférences** : Stockage local rapide et découplé via `SharedPreferences` (ou DataStore) pour enregistrer le statut d'achèvement de l'onboarding, les lignes de métro/bus favorites et le mode de transport privilégié.

## 1. Vue d'Ensemble & Concept

- **Objectif** : Offrir une première impression magistrale et fluide aux usagers du transport montréalais, démystifiant l'interaction entre les 3 serveurs MCP (STM en temps réel, satellite SkyFi pour les densités de trafic, mémoire Pieces pour la rétention de contexte) tout en collectant leurs habitudes de trajet.
- **Utilisateurs Cibles** : Citoyens, navetteurs et opérateurs montréalais recherchant des itinéraires prédictifs enrichis par l'intelligence artificielle (Tree of Thoughts) sans subir d'interruptions liées aux instabilités réseau de l'API STM.
- **Valeur Clé** : Zéro écran blanc ou blocage en cas d'indisponibilité de l'API STM distante, et personnalisation immédiate des suggestions dès les premières secondes d'utilisation.

## 2. Expérience Utilisateur & Design Visuel

### Parcours Utilisateur
1. **Étape 1 : Présentation de NexusTransit & Philosophie MCP**
   - Révélation de l'écosystème : orchestration en temps réel entre flux GTFS-RT, vision satellitaire et mémoire sémantique.
2. **Étape 2 : Le Hub MCP en Action**
   - Focus interactif sur les 3 moteurs :
     - *STM MCP* : Véhicules, retards, positions en temps réel.
     - *SkyFi MCP* : Densité de congestion captée depuis l'orbite terrestre.
     - *Pieces MCP* : Mémoire continue de vos trajets et contextes de déplacement.
3. **Étape 3 : Personnalisation Immédiate**
   - Sélecteurs tactiles rapides pour le mode favori (Métro, Bus, Mixte), saisie ou sélection rapide des lignes préférées (Ligne Verte, Orange, Ligne 24, 55, etc.) et niveau de tolérance aux retards.
4. **Étape 4 : Décollage vers l'Application**
   - Sauvegarde instantanée des préférences et transition fluide vers le tableau de bord principal.

### Direction Artistique & Composants
- **Palette** : Noir spatial profond (`#0B0F19`), accents Cyan Cyber (`#00F2FE`), Vert STM officiel (`#00A859`), et ambre d'alerte (`#FFB300`).
- **Composants M3** : Surface avec coins adoucis (24.dp), puces de sélection interactives (`FilterChip`), indicateurs de progression par points animés (`HorizontalPager` / row indicators), boutons d'action d'au moins 48.dp de hauteur tactile.
- **Accès persistant** : Icône discrète d'aide / découverte dans le TopAppBar pour réviser l'onboarding ou rééditer ses préférences à tout moment.

## 3. Décisions Produit & Compromis Techniques

- **Gestion des Timeouts Réseau** :
  - *Approche retenue* : OkHttpClient avec timeout court (3s) pour la détection rapide d'échec dans l'intercepteur `StmApiLoggingInterceptor`, doublé d'un `try/catch` robuste dans `StmRepository` qui renvoie immédiatement la dernière position connue stockée dans `VehiclePositionDao` ou le jeu synthétique de secours sans lever d'exception non interceptée.
  - *Justification* : Évite de bloquer l'UI de l'utilisateur pendant 15 secondes d'attente réseau stérile.
- **Persistance Découplée de l'Onboarding** :
  - *Approche retenue* : `PreferencesManager` basé sur `SharedPreferences` Android avec StateFlow dans `MainViewModel`.
  - *Justification* : Ultra-rapide, synchrone au démarrage, ne dépendant pas des migrations de schéma Room pour l'état d'affichage de l'onboarding.

## 4. Architecture Technique & Stratégie des Données

### Diagramme d'Architecture Système

```
┌─────────────────────────────────────────────────────────────────┐
│                          MainActivity                           │
│  ┌───────────────────────┐           ┌───────────────────────┐  │
│  │   TopAppBar Action    │           │   Navigation Host     │  │
│  │  (Relancer Onboarding)│           │ (Routes, MCP, AI, STA)│  │
│  └───────────┬───────────┘           └───────────┬───────────┘  │
└──────────────┼───────────────────────────────────┼──────────────┘
               │                                   │
               ▼                                   ▼
┌─────────────────────────────────────────────────────────────────┐
│               OnboardingModal / OnboardingPager                 │
│  - Slide 1: Introduction NexusTransit & ToT                     │
│  - Slide 2: Explication interactive des MCP (STM, SkyFi, Pieces)│
│  - Slide 3: Saisie des préférences (Lignes, Modes, Tolérance)   │
└──────────────────────────────┬──────────────────────────────────┘
                               │ Sauvegarde des préférences
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│               MainViewModel & PreferencesManager                │
│  - isFirstLaunch: StateFlow<Boolean>                            │
│  - userTransitPreferences: StateFlow<UserPreferences>           │
└──────────────────────────────┬──────────────────────────────────┘
                               │
               ┌───────────────┴───────────────┐
               ▼                               ▼
┌──────────────────────────────┐ ┌──────────────────────────────┐
│        SharedPreferences     │ │        StmRepository         │
│  - onboarding_completed      │ │  - OkHttpClient (Timeout 3s) │
│  - preferred_lines           │ │  - Fallback vers Room Dao    │
│  - default_transport_mode    │ │  - Données GTFS de secours   │
└──────────────────────────────┘ └──────────────────────────────┘
```

### Modèle de Données des Préférences
- `hasCompletedOnboarding`: Booléen déterminant l'affichage automatique au lancement.
- `favoriteLines`: Ensemble de chaînes (ex: "Ligne Orange", "Ligne 24 Sherbrooke").
- `preferredMode`: Chaîne ("METRO", "BUS", "MULTIMODAL").
- `delayToleranceMinutes`: Entier (ex: 5 min).

### Déroulement de l'Exécution
1. **Étape 1 : Optimisation Réseau et Résilience StmRepository** :
   - Ajuster les timeouts OkHttpClient dans `NetworkModule` (connect/read 3s au lieu de 15s pour un basculement rapide).
   - Sécuriser `StmRepository.kt` pour que le timeout déclenche immédiatement et silencieusement le chargement du cache `VehiclePositionDao` ou de la synthèse GTFS sans log d'erreur bloquant.
2. **Étape 2 : Création du Gestionnaire de Préférences** :
   - Implémenter `UserPreferencesManager.kt` stockant les choix d'accueil et le statut d'onboarding.
3. **Étape 3 : Création du Composant d'Onboarding** :
   - Développer `OnboardingDialog.kt` (ou `OnboardingSheet.kt`) avec animation, sélection par puces et bouton de validation.
4. **Étape 4 : Câblage dans MainActivity & MainViewModel** :
   - Vérifier le statut au lancement et afficher le diaporama si non complété.
   - Ajouter le bouton dans le TopAppBar pour pouvoir réafficher l'onboarding à tout moment.
5. **Étape 5 : Compilation & Vérification** :
   - Valider la compilation via `compile_applet`.
