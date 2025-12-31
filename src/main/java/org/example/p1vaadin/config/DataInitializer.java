package org.example.p1vaadin.config;

import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.*;
import org.example.p1vaadin.repository.EventRepository;
import org.example.p1vaadin.repository.ReservationRepository;
import org.example.p1vaadin.repository.UserRepository;
import org.example.p1vaadin.util.CodeGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            EventRepository eventRepository,
            ReservationRepository reservationRepository,
            PasswordEncoder passwordEncoder,
            CodeGenerator codeGenerator
    ) {
        return args -> {
            System.out.println("=== Initialisation des données ===");

            // ============ USERS ============
            User admin = userRepository.save(User.builder()
                    .nom("Admin")
                    .prenom("Super")
                    .email("admin@event.ma")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    . actif(true)
                    .telephone("0600000001")
                    .build());
            System.out.println("✅ Admin créé:  " + admin.getEmail());

            User org1 = userRepository.save(User.builder()
                    . nom("Alami")
                    .prenom("Mohamed")
                    .email("organizer1@event.ma")
                    .password(passwordEncoder.encode("org123"))
                    .role(Role.ORGANIZER)
                    .actif(true)
                    .telephone("0600000002")
                    .build());
            System.out.println("✅ Organizer 1 créé: " + org1.getEmail());

            User org2 = userRepository.save(User.builder()
                    .nom("Bennani")
                    .prenom("Fatima")
                    .email("organizer2@event.ma")
                    .password(passwordEncoder.encode("org123"))
                    .role(Role.ORGANIZER)
                    .actif(true)
                    .telephone("0600000003")
                    .build());
            System.out.println("✅ Organizer 2 créé: " + org2.getEmail());

            User client1 = userRepository.save(User.builder()
                    .nom("Tazi")
                    .prenom("Ahmed")
                    .email("client1@event.ma")
                    .password(passwordEncoder.encode("client123"))
                    . role(Role.CLIENT)
                    .actif(true)
                    .telephone("0600000004")
                    .build());
            System.out.println("✅ Client 1 créé: " + client1.getEmail());

            User client2 = userRepository.save(User. builder()
                    .nom("Idrissi")
                    .prenom("Sara")
                    .email("client2@event.ma")
                    .password(passwordEncoder.encode("client123"))
                    . role(Role.CLIENT)
                    .actif(true)
                    .telephone("0600000005")
                    .build());
            System.out.println("✅ Client 2 créé: " + client2.getEmail());

            // ============ EVENTS ============
            LocalDateTime now = LocalDateTime.now();

            // CONCERTS
            Event concert1 = eventRepository.save(Event.builder()
                    .titre("Concert Saad Lamjarred")
                    .description("Grand concert live à Casablanca")
                    .categorie(EventCategory.CONCERT)
                    .dateDebut(now. plusDays(10))
                    .dateFin(now.plusDays(10).plusHours(4))
                    .lieu("Théâtre Mohammed V")
                    .ville("Casablanca")
                    .capaciteMax(500)
                    .prixUnitaire(300.0)
                    .organisateur(org1)
                    .statut(EventStatus. PUBLIE)
                    .build());

            Event concert2 = eventRepository.save(Event.builder()
                    .titre("Festival Gnaoua")
                    .description("Festival de musique Gnaoua")
                    . categorie(EventCategory.CONCERT)
                    .dateDebut(now.plusDays(20))
                    .dateFin(now.plusDays(22))
                    .lieu("Place Moulay Hassan")
                    .ville("Essaouira")
                    . capaciteMax(2000)
                    .prixUnitaire(150.0)
                    .organisateur(org2)
                    .statut(EventStatus.PUBLIE)
                    .build());

            Event concert3 = eventRepository.save(Event.builder()
                    .titre("Jazz Night")
                    .description("Soirée Jazz")
                    .categorie(EventCategory.CONCERT)
                    .dateDebut(now. plusDays(5))
                    .dateFin(now.plusDays(5).plusHours(3))
                    .lieu("Le Dhow")
                    .ville("Rabat")
                    .capaciteMax(100)
                    .prixUnitaire(250.0)
                    .organisateur(org1)
                    .statut(EventStatus. PUBLIE)
                    .build());

            // THEATRE
            Event theatre1 = eventRepository.save(Event.builder()
                    .titre("Pièce Molière")
                    .description("Le Malade Imaginaire")
                    .categorie(EventCategory.THEATRE)
                    .dateDebut(now.plusDays(7))
                    .dateFin(now.plusDays(7).plusHours(2))
                    .lieu("Théâtre National")
                    .ville("Rabat")
                    .capaciteMax(300)
                    .prixUnitaire(200.0)
                    .organisateur(org1)
                    .statut(EventStatus. PUBLIE)
                    .build());

            Event theatre2 = eventRepository.save(Event.builder()
                    .titre("Comédie Marocaine")
                    .description("Spectacle comique")
                    .categorie(EventCategory.THEATRE)
                    .dateDebut(now.plusDays(14))
                    .dateFin(now.plusDays(14).plusHours(2))
                    .lieu("Megarama")
                    .ville("Marrakech")
                    .capaciteMax(400)
                    .prixUnitaire(180.0)
                    .organisateur(org2)
                    .statut(EventStatus. PUBLIE)
                    .build());

            Event theatre3 = eventRepository.save(Event.builder()
                    .titre("Drame Classique")
                    . description("Antigone")
                    .categorie(EventCategory.THEATRE)
                    .dateDebut(now.plusDays(30))
                    .dateFin(now.plusDays(30).plusHours(3))
                    .lieu("Institut Français")
                    .ville("Fès")
                    .capaciteMax(150)
                    .prixUnitaire(120.0)
                    .organisateur(org1)
                    .statut(EventStatus.BROUILLON)
                    .build());

            // CONFERENCE
            Event conf1 = eventRepository.save(Event.builder()
                    . titre("DevDays Maroc")
                    .description("Conférence développeurs")
                    .categorie(EventCategory.CONFERENCE)
                    .dateDebut(now. plusDays(15))
                    .dateFin(now. plusDays(15).plusHours(8))
                    .lieu("Technopark")
                    .ville("Casablanca")
                    .capaciteMax(200)
                    .prixUnitaire(500.0)
                    .organisateur(org2)
                    .statut(EventStatus.PUBLIE)
                    .build());

            Event conf2 = eventRepository.save(Event.builder()
                    .titre("AI Summit")
                    .description("Intelligence Artificielle")
                    . categorie(EventCategory.CONFERENCE)
                    .dateDebut(now.plusDays(25))
                    .dateFin(now.plusDays(25).plusHours(6))
                    .lieu("UM6P")
                    .ville("Benguerir")
                    .capaciteMax(300)
                    .prixUnitaire(400.0)
                    .organisateur(org1)
                    .statut(EventStatus.PUBLIE)
                    .build());

            Event conf3 = eventRepository.save(Event.builder()
                    .titre("Startup Weekend")
                    .description("48h pour créer une startup")
                    .categorie(EventCategory.CONFERENCE)
                    .dateDebut(now.plusDays(35))
                    .dateFin(now.plusDays(37))
                    .lieu("CasaNearshore")
                    .ville("Casablanca")
                    .capaciteMax(100)
                    .prixUnitaire(350.0)
                    .organisateur(org2)
                    .statut(EventStatus. PUBLIE)
                    .build());

            // SPORT
            Event sport1 = eventRepository. save(Event.builder()
                    .titre("Marathon Marrakech")
                    .description("Marathon international")
                    .categorie(EventCategory.SPORT)
                    .dateDebut(now. plusDays(40))
                    .dateFin(now.plusDays(40).plusHours(6))
                    .lieu("Centre-ville")
                    .ville("Marrakech")
                    . capaciteMax(5000)
                    .prixUnitaire(100.0)
                    .organisateur(org1)
                    .statut(EventStatus. PUBLIE)
                    .build());

            Event sport2 = eventRepository.save(Event.builder()
                    .titre("Match Raja vs Wydad")
                    .description("Derby casablancais")
                    .categorie(EventCategory.SPORT)
                    .dateDebut(now.plusDays(12))
                    .dateFin(now.plusDays(12).plusHours(2))
                    .lieu("Stade Mohammed V")
                    .ville("Casablanca")
                    .capaciteMax(45000)
                    .prixUnitaire(80.0)
                    .organisateur(org2)
                    .statut(EventStatus.PUBLIE)
                    .build());

            Event sport3 = eventRepository. save(Event.builder()
                    .titre("Tournoi Tennis")
                    .description("Open de Tanger")
                    .categorie(EventCategory.SPORT)
                    .dateDebut(now. plusDays(50))
                    .dateFin(now.plusDays(55))
                    .lieu("Royal Tennis Club")
                    .ville("Tanger")
                    . capaciteMax(1000)
                    .prixUnitaire(200.0)
                    .organisateur(org1)
                    .statut(EventStatus. BROUILLON)
                    .build());

            // AUTRE
            Event autre1 = eventRepository.save(Event.builder()
                    .titre("Salon du Livre")
                    .description("Salon international")
                    .categorie(EventCategory.AUTRE)
                    .dateDebut(now.plusDays(60))
                    .dateFin(now.plusDays(65))
                    .lieu("OFEC")
                    .ville("Casablanca")
                    . capaciteMax(10000)
                    .prixUnitaire(50.0)
                    .organisateur(org2)
                    .statut(EventStatus. PUBLIE)
                    .build());

            Event autre2 = eventRepository.save(Event.builder()
                    .titre("Exposition Art")
                    .description("Art contemporain marocain")
                    .categorie(EventCategory.AUTRE)
                    .dateDebut(now.plusDays(8))
                    .dateFin(now.plusDays(15))
                    .lieu("Musée Mohammed VI")
                    .ville("Rabat")
                    .capaciteMax(500)
                    .prixUnitaire(70.0)
                    .organisateur(org1)
                    .statut(EventStatus.PUBLIE)
                    .build());

            Event autre3 = eventRepository.save(Event.builder()
                    .titre("Festival Culinaire")
                    .description("Gastronomie marocaine")
                    .categorie(EventCategory.AUTRE)
                    . dateDebut(now.minusDays(5))
                    .dateFin(now.minusDays(3))
                    .lieu("Place Jemaa el-Fna")
                    .ville("Marrakech")
                    .capaciteMax(2000)
                    .prixUnitaire(0.0)
                    .organisateur(org2)
                    .statut(EventStatus. TERMINE)
                    .build());

            System.out.println("✅ 15 événements créés");

            // ============ RESERVATIONS ============
            List<Event> publishedEvents = List.of(concert1, concert2, concert3, theatre1, theatre2, conf1, conf2, conf3, sport1, sport2, autre1, autre2);
            List<User> clients = List.of(client1, client2);

            int resCount = 0;
            for (int i = 0; i < 20; i++) {
                Event event = publishedEvents.get(i % publishedEvents.size());
                User client = clients.get(i % clients.size());
                int places = (i % 5) + 1;

                Reservation res = reservationRepository.save(Reservation. builder()
                        .utilisateur(client)
                        . evenement(event)
                        . nombrePlaces(places)
                        . montantTotal(places * event.getPrixUnitaire())
                        .codeReservation(codeGenerator.generateReservationCode())
                        . statut(i % 3 == 0 ? ReservationStatus.EN_ATTENTE : ReservationStatus.CONFIRMEE)
                        .commentaire(i % 2 == 0 ? "Réservation test " + i : null)
                        .build());
                resCount++;
            }

            System.out.println("✅ " + resCount + " réservations créées");
            System.out.println("=== Initialisation terminée ===");
            System.out.println("");
            System.out.println("📧 Comptes disponibles:");
            System.out. println("   - admin@event.ma / admin123 (ADMIN)");
            System.out.println("   - organizer1@event.ma / org123 (ORGANIZER)");
            System.out.println("   - organizer2@event.ma / org123 (ORGANIZER)");
            System.out.println("   - client1@event. ma / client123 (CLIENT)");
            System.out.println("   - client2@event. ma / client123 (CLIENT)");
            System.out.println("");
        };
    }
}