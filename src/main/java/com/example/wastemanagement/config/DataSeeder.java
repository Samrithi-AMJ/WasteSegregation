package com.example.wastemanagement.config;

import com.example.wastemanagement.entity.*;
import com.example.wastemanagement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Seeds the database with realistic sample data on first run so the
 * dashboard and every screen has something meaningful to show.
 * Runs only when the relevant tables are empty, so it is safe to restart
 * the application without duplicating data.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CitizenRepository citizenRepository;
    private final WorkerRepository workerRepository;
    private final VehicleRepository vehicleRepository;
    private final WasteCollectionRepository collectionRepository;
    private final SegregationRepository segregationRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random(42);

    public DataSeeder(CitizenRepository citizenRepository,
                       WorkerRepository workerRepository,
                       VehicleRepository vehicleRepository,
                       WasteCollectionRepository collectionRepository,
                       SegregationRepository segregationRepository,
                       ComplaintRepository complaintRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.citizenRepository = citizenRepository;
        this.workerRepository = workerRepository;
        this.vehicleRepository = vehicleRepository;
        this.collectionRepository = collectionRepository;
        this.segregationRepository = segregationRepository;
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (citizenRepository.count() > 0) {
            return; // already seeded
        }

        String[] areas = {"Ward 1", "Ward 2", "Ward 3", "Pollachi North", "Pollachi South"};

        // ---------------- Citizens ----------------
        String[][] citizenData = {
                {"Arun Kumar", "9876543210", "arun.kumar@example.com", "12 Gandhi Street"},
                {"Priya Raman", "9876543211", "priya.raman@example.com", "45 Nehru Nagar"},
                {"Suresh Babu", "9876543212", "suresh.babu@example.com", "7 Anna Salai"},
                {"Kavitha Devi", "9876543213", "kavitha.devi@example.com", "23 Market Road"},
                {"Manoj Kannan", "9876543214", "manoj.kannan@example.com", "9 Temple Street"},
                {"Divya Shree", "9876543215", "divya.shree@example.com", "56 Bazaar Street"},
                {"Ravi Chandran", "9876543216", "ravi.chandran@example.com", "18 South Street"},
                {"Meena Kumari", "9876543217", "meena.kumari@example.com", "33 North Street"},
                {"Vijay Anand", "9876543218", "vijay.anand@example.com", "61 College Road"},
                {"Lakshmi Priya", "9876543219", "lakshmi.priya@example.com", "14 Station Road"}
        };

        List<Citizen> citizens = new java.util.ArrayList<>();
        int ward = 1;
        for (String[] data : citizenData) {
            Citizen citizen = new Citizen();
            citizen.setName(data[0]);
            citizen.setPhone(data[1]);
            citizen.setEmail(data[2]);
            citizen.setAddress(data[3]);
            citizen.setArea(areas[random.nextInt(areas.length)]);
            citizen.setWardNumber(((ward - 1) % 5) + 1);
            ward++;
            citizens.add(citizenRepository.save(citizen));
        }

        // ---------------- Workers ----------------
        String[][] workerData = {
                {"Muthu Selvam", "9123456780", "Ward 1"},
                {"Karthik Raja", "9123456781", "Ward 2"},
                {"Senthil Kumar", "9123456782", "Ward 3"},
                {"Bala Murugan", "9123456783", "Pollachi North"},
                {"Ganesan P", "9123456784", "Pollachi South"}
        };
        WorkerStatus[] workerStatuses = {WorkerStatus.ACTIVE, WorkerStatus.ACTIVE, WorkerStatus.ACTIVE,
                WorkerStatus.ON_LEAVE, WorkerStatus.ACTIVE};

        List<Worker> workers = new java.util.ArrayList<>();
        for (int i = 0; i < workerData.length; i++) {
            Worker worker = new Worker();
            worker.setName(workerData[i][0]);
            worker.setPhone(workerData[i][1]);
            worker.setAssignedArea(workerData[i][2]);
            worker.setStatus(workerStatuses[i]);
            workers.add(workerRepository.save(worker));
        }

        // ---------------- Vehicles ----------------
        Object[][] vehicleData = {
                {"TN38AB1234", VehicleType.GARBAGE_TRUCK, "Selvam K", 2000.0, VehicleStatus.AVAILABLE},
                {"TN38AB5678", VehicleType.MINI_TRUCK, "Rajesh M", 800.0, VehicleStatus.ON_ROUTE},
                {"TN38AB9012", VehicleType.COMPACTOR, "Dinesh S", 3000.0, VehicleStatus.AVAILABLE},
                {"TN38AB3456", VehicleType.GARBAGE_TRUCK, "Anbu R", 2000.0, VehicleStatus.MAINTENANCE},
                {"TN38AB7890", VehicleType.MINI_TRUCK, "Kumar V", 800.0, VehicleStatus.AVAILABLE}
        };

        List<Vehicle> vehicles = new java.util.ArrayList<>();
        for (Object[] data : vehicleData) {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleNumber((String) data[0]);
            vehicle.setVehicleType((VehicleType) data[1]);
            vehicle.setDriverName((String) data[2]);
            vehicle.setCapacity((Double) data[3]);
            vehicle.setStatus((VehicleStatus) data[4]);
            vehicles.add(vehicleRepository.save(vehicle));
        }

        // ---------------- Waste Collections + Segregation ----------------
        WasteType[] wasteTypes = WasteType.values();
        CollectionStatus[] statuses = {CollectionStatus.COLLECTED, CollectionStatus.COLLECTED,
                CollectionStatus.COLLECTED, CollectionStatus.PENDING, CollectionStatus.MISSED};

        for (int i = 0; i < 25; i++) {
            Citizen citizen = citizens.get(random.nextInt(citizens.size()));
            Worker worker = workers.get(random.nextInt(workers.size()));
            Vehicle vehicle = vehicles.get(random.nextInt(vehicles.size()));

            WasteCollection collection = new WasteCollection();
            collection.setCitizen(citizen);
            collection.setWorker(worker);
            collection.setVehicle(vehicle);
            collection.setArea(citizen.getArea());
            collection.setWasteType(wasteTypes[random.nextInt(wasteTypes.length)]);
            collection.setQuantity(Math.round((5 + random.nextDouble() * 45) * 100.0) / 100.0);

            int daysAgo = random.nextInt(20);
            collection.setCollectionDate(LocalDate.now().minusDays(daysAgo));

            CollectionStatus status = statuses[random.nextInt(statuses.length)];
            // Old pending records read as overdue automatically by the dashboard logic
            collection.setStatus(status);

            WasteCollection saved = collectionRepository.save(collection);

            if (status == CollectionStatus.COLLECTED) {
                Segregation segregation = new Segregation();
                segregation.setWasteCollection(saved);
                double total = saved.getQuantity();
                double wet = Math.round(total * (0.3 + random.nextDouble() * 0.2) * 100.0) / 100.0;
                double dry = Math.round(total * (0.2 + random.nextDouble() * 0.2) * 100.0) / 100.0;
                double plastic = Math.round(Math.max(0, total - wet - dry) * 0.5 * 100.0) / 100.0;
                double paper = Math.round(Math.max(0, total - wet - dry - plastic) * 0.6 * 100.0) / 100.0;
                double other = Math.round(Math.max(0, total - wet - dry - plastic - paper) * 100.0) / 100.0;

                segregation.setWetQuantity(wet);
                segregation.setDryQuantity(dry);
                segregation.setPlasticQuantity(plastic);
                segregation.setPaperQuantity(paper);
                segregation.setOtherQuantity(other);

                long categoriesUsed = List.of(wet, dry, plastic, paper, other).stream()
                        .filter(v -> v > 0).count();
                if (categoriesUsed >= 3) {
                    segregation.setSegregationStatus(SegregationStatus.PROPER);
                } else if (categoriesUsed == 2) {
                    segregation.setSegregationStatus(SegregationStatus.PARTIAL);
                } else {
                    segregation.setSegregationStatus(SegregationStatus.IMPROPER);
                }
                segregationRepository.save(segregation);
            }
        }

        // ---------------- Complaints ----------------
        String[] complaintLocations = {
                "Near Bus Stand", "Behind Municipal Office", "Main Market Street",
                "Government School Road", "Community Hall Area", "Temple Street Junction",
                "Railway Colony", "Housing Board Colony", "Old Bus Stand", "Water Tank Road"
        };
        String[] complaintDescriptions = {
                "Garbage has not been collected for two days.",
                "Overflowing waste bin attracting stray animals.",
                "Mixed waste dumped near the drainage causing bad odor.",
                "Collection vehicle skipped our street this week.",
                "Plastic waste burning reported near residential area.",
                "Bin not replaced after previous collection.",
                "Waste collection worker did not show up today.",
                "Segregated waste got mixed during pickup.",
                "Public bin damaged and waste scattered on road.",
                "Delay in collection causing hygiene issues."
        };
        ComplaintStatus[] complaintStatuses = {ComplaintStatus.PENDING, ComplaintStatus.ASSIGNED,
                ComplaintStatus.IN_PROGRESS, ComplaintStatus.RESOLVED, ComplaintStatus.REJECTED};

        for (int i = 0; i < 10; i++) {
            Complaint complaint = new Complaint();
            complaint.setCitizen(citizens.get(random.nextInt(citizens.size())));
            complaint.setLocation(complaintLocations[i]);
            complaint.setDescription(complaintDescriptions[i]);
            complaint.setComplaintDate(LocalDate.now().minusDays(random.nextInt(15)));
            complaint.setStatus(complaintStatuses[random.nextInt(complaintStatuses.length)]);
            complaintRepository.save(complaint);
        }

        // ---------------- Users ----------------
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Create a citizen login for the first three seeded citizens
        for (int i = 0; i < 3; i++) {
            Citizen citizen = citizens.get(i);
            User citizenUser = new User();
            citizenUser.setUsername("citizen" + (i + 1));
            citizenUser.setPassword(passwordEncoder.encode("citizen123"));
            citizenUser.setRole(Role.CITIZEN);
            citizenUser.setCitizenId(citizen.getCitizenId());
            userRepository.save(citizenUser);
        }

        System.out.println("=================================================");
        System.out.println("Sample data seeded successfully.");
        System.out.println("Admin login    -> username: admin      password: admin123");
        System.out.println("Citizen login  -> username: citizen1   password: citizen123");
        System.out.println("=================================================");
    }
}
