# Plateforme de Gestion d'Événements

## Description du Projet

 Ce projet est une application web complète de gestion d'événements développée avec **Spring Boot** et **Vaadin**. Elle permet aux utilisateurs de créer, gérer et réserver des événements de différentes catégories (concerts, théâtre, conférences, sports, etc.).

### Fonctionnalités Principales

-  **Authentification et autorisation** multi-rôles (Admin, Organisateur, Client)
-  **Gestion des événements** : création, modification, publication, annulation
-  **Système de réservation** : réservation de places avec génération de code unique
-  **Gestion des utilisateurs** : inscription, profils, activation/désactivation
-  **Tableaux de bord** personnalisés selon les rôles
-  **Interface utilisateur moderne** avec Vaadin Flow
-  **Sécurité** avec Spring Security et BCrypt

---

##  Technologies Utilisées

### Backend
- **Java 21**
- **Spring Boot 3.5.8**
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Web
- **Hibernate** (ORM)
- **Lombok** (réduction du code boilerplate)

### Frontend
- **Vaadin 24.9.6** (Framework UI Java)
- **Vaadin Flow Components**
- **Vaadin Lumo Theme**
- **CSS personnalisé**

### Base de Données
- **H2 Database** (en mémoire pour le développement)
- Compatible avec MySQL, PostgreSQL (configuration à adapter)

### Outils de Build
- **Maven 3.x**
- **Maven Compiler Plugin**
- **Vaadin Maven Plugin**

---

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :

-  **Java JDK 21** ou supérieur
-  **Maven 3.8+**
-  **IDE recommandé** : IntelliJ IDEA, Eclipse ou VS Code
-  **Navigateur web moderne** (Chrome, Firefox, Edge)

---

## Installation

### 1️ Cloner le projet

```bash
git clone <url-du-repo>
cd P1Vaadin
```

### 2️ Installer les dépendances

```bash
mvn clean install
```

### 3 Préparer le frontend Vaadin

```bash
mvn vaadin:prepare-frontend
```

---

## Configuration de la Base de Données

### Configuration H2 (par défaut)

Le projet est configuré pour utiliser **H2** en mode mémoire. Aucune configuration supplémentaire n'est requise.

**Fichier** : `src/main/resources/application.properties`

```properties
# Application
spring.application.name=event-booking

# H2 Database
spring.datasource.url=jdbc:h2:mem:eventdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Vaadin
vaadin.launch-browser=false
vaadin.whitelisted-packages=org.example.p1vaadin

# Logging
logging.level.org.springframework.security=DEBUG
```

###  Accès à la Console H2

Une fois l'application démarrée, accédez à la console H2 :

- **URL** : `http://localhost:8080/h2-console`
- **JDBC URL** : `jdbc:h2:mem:eventdb`
- **Username** : `sa`
- **Password** : *(laisser vide)*


## Schéma SQL de la Base de Données

Le schéma suivant est généré automatiquement par Hibernate lors du démarrage de l'application.

### Script SQL

```sql
-- Table des utilisateurs
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    date_inscription TIMESTAMP,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    telephone VARCHAR(50)
);

-- Table des événements
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    categorie VARCHAR(50) NOT NULL,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    lieu VARCHAR(255) NOT NULL,
    ville VARCHAR(255) NOT NULL,
    capacite_max INTEGER NOT NULL,
    prix_unitaire DOUBLE NOT NULL,
    image_url VARCHAR(500),
    organisateur_id BIGINT NOT NULL,
    statut VARCHAR(50) NOT NULL,
    date_creation TIMESTAMP,
    date_modification TIMESTAMP,
    FOREIGN KEY (organisateur_id) REFERENCES users(id)
);

-- Table des réservations
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    evenement_id BIGINT NOT NULL,
    nombre_places INTEGER NOT NULL,
    montant_total DOUBLE NOT NULL,
    date_reservation TIMESTAMP,
    statut VARCHAR(50) NOT NULL,
    code_reservation VARCHAR(50) NOT NULL UNIQUE,
    commentaire VARCHAR(500),
    FOREIGN KEY (utilisateur_id) REFERENCES users(id),
    FOREIGN KEY (evenement_id) REFERENCES events(id)
);

-- Index pour améliorer les performances
CREATE INDEX idx_events_organisateur ON events(organisateur_id);
CREATE INDEX idx_events_statut ON events(statut);
CREATE INDEX idx_events_categorie ON events(categorie);
CREATE INDEX idx_reservations_utilisateur ON reservations(utilisateur_id);
CREATE INDEX idx_reservations_evenement ON reservations(evenement_id);
CREATE INDEX idx_reservations_code ON reservations(code_reservation);
```

### Modèle de Données

**Relations principales** :
- `User (1) → (N) Event` : Un organisateur peut créer plusieurs événements
- `User (1) → (N) Reservation` : Un client peut avoir plusieurs réservations
- `Event (1) → (N) Reservation` : Un événement peut avoir plusieurs réservations

**Énumérations** :
- **Role** : `ADMIN`, `ORGANIZER`, `CLIENT`
- **EventStatus** : `BROUILLON`, `PUBLIE`, `ANNULE`, `TERMINE`
- **EventCategory** : `CONCERT`, `THEATRE`, `CONFERENCE`, `SPORT`, `AUTRE`
- **ReservationStatus** : `EN_ATTENTE`, `CONFIRMEE`, `ANNULEE`

---

## Instructions de Lancement

### Mode Développement

```bash
mvn spring-boot:run
```

Ou avec le wrapper Maven :

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

L'application sera accessible sur **http://localhost:8080**

### Mode Production

1. **Build du projet** :

```bash
mvn clean package -Pproduction
```

2. **Lancer le JAR** :

```bash
java -jar target/P1Vaadin-0.0.1-SNAPSHOT.jar
```

---

##  Comptes de Test

L'application initialise automatiquement des comptes de test au démarrage :

| Rôle | Email | Mot de passe | Description |
|------|-------|--------------|-------------|
|  **Admin** | admin@event.ma | admin123 | Accès complet au système |
|  **Organisateur 1** | organizer1@event.ma | org123 | Création et gestion d'événements |
|  **Organisateur 2** | organizer2@event.ma | org123 | Création et gestion d'événements |
|  **Client 1** | client1@event.ma | client123 | Réservation d'événements |
|  **Client 2** | client2@event.ma | client123 | Réservation d'événements |

---

##  Structure du Projet

```
P1Vaadin/
├── src/
│   ├── main/
│   │   ├── java/org/example/p1vaadin/
│   │   │   ├── config/              # Configuration Spring & Security
│   │   │   │   ├── DataInitializer.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── domain/              # Entités JPA
│   │   │   │   ├── enums/
│   │   │   │   │   ├── EventCategory.java
│   │   │   │   │   ├── EventStatus.java
│   │   │   │   │   ├── ReservationStatus.java
│   │   │   │   │   └── Role.java
│   │   │   │   ├── Event.java
│   │   │   │   ├── Reservation.java
│   │   │   │   └── User.java
│   │   │   ├── exception/           # Exceptions personnalisées
│   │   │   ├── repository/          # Repositories Spring Data JPA
│   │   │   │   ├── EventRepository.java
│   │   │   │   ├── ReservationRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/            # Sécurité Spring Security
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── UserPrincipal.java
│   │   │   ├── service/             # Services métier
│   │   │   │   ├── EventService.java
│   │   │   │   ├── ReservationService.java
│   │   │   │   ├── ThemeService.java
│   │   │   │   └── UserService.java
│   │   │   ├── util/                # Utilitaires
│   │   │   │   └── CodeGenerator.java
│   │   │   ├── views/               # Vues Vaadin
│   │   │   │   ├── admin/           # Vues Administrateur
│   │   │   │   ├── auth/            # Login & Register
│   │   │   │   ├── client/          # Vues Client
│   │   │   │   ├── organizer/       # Vues Organisateur
│   │   │   │   ├── publics/         # Vues Publiques
│   │   │   │   └── MainLayout.java
│   │   │   └── P1VaadinApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── static/
│   │   │   └── templates/
│   │   └── frontend/                # Ressources frontend
│   │       ├── styles/
│   │       └── themes/
│   └── test/                        # Tests unitaires et d'intégration
├── pom.xml                          # Configuration Maven
└── README.md                        # Ce fichier
```

---

##  Fonctionnalités par Rôle

###  Administrateur
- Vue d'ensemble complète du système
- Gestion des utilisateurs (activation/désactivation)
- Gestion de tous les événements
- Gestion de toutes les réservations
- Statistiques globales

### Organisateur
- Tableau de bord personnel
- Création et modification d'événements
- Publication/annulation d'événements
- Gestion des réservations pour ses événements
- Statistiques de ses événements

### Client
- Parcours des événements disponibles
- Réservation de places
- Gestion de ses réservations
- Profil utilisateur
- Historique des réservations

---


## Build

```bash
mvn clean install
```

