package com.aeroferia.api.config;

import com.aeroferia.api.entity.Category;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.User;
import com.aeroferia.api.entity.enums.UserRole;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${aeroferia.admin.initial-email}")
    private String adminEmail;

    @Value("${aeroferia.admin.initial-password}")
    private String adminPassword;

    @Value("${aeroferia.admin.initial-name}")
    private String adminName;

    @Value("${aeroferia.admin.initial-phone}")
    private String adminPhone;

    @Override
    @Transactional
    public void run(String... args) {
        initAdminUser();
        initCategories();
        initStores();
        log.info("AeroFeria DataInitializer completado exitosamente.");
    }

    private void initAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .fullName(adminName)
                    .phoneNumber(adminPhone)
                    .locationProvince("Buenos Aires")
                    .locationCity("CABA")
                    .role(UserRole.ROLE_ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("Usuario Administrador inicial creado con email: {}", adminEmail);
        }
    }

    private void initCategories() {
        if (categoryRepository.count() > 0) return;

        createCategoryWithSubs("Aviones y Planeadores", "aviones-y-planeadores", "plane", 1, List.of(
                "Entrenadores", "Acrobáticos 3D", "Escala y Warbirds", "Jets EDF y Turbina", "Planeadores y Veleros"
        ));

        createCategoryWithSubs("Helicópteros y Drones", "helicopteros-y-drones", "crosshair", 2, List.of(
                "Helicópteros 3D", "Drones FPV Freestyle y Racing", "Drones de Filmación"
        ));

        createCategoryWithSubs("Radiocontrol y Electrónica", "radios-y-electronica", "radio", 3, List.of(
                "Transmisores y Radios", "Receptores", "Servos y Brazos", "Controladoras y Giróscopos", "Variadores ESC", "Telemetría y Sensores"
        ));

        createCategoryWithSubs("Motores y Combustión", "motores-y-combustion", "gauge", 4, List.of(
                "Motores Brushless", "Motores Glow 2T y 4T", "Motores Nafteros Gasolina", "Microturbinas", "Hélices y Escapes"
        ));

        createCategoryWithSubs("Baterías y Cargadores", "baterias-y-cargadores", "battery-charging", 5, List.of(
                "Baterías LiPo 2S a 6S", "Baterías LiFe y Li-Ion", "Cargadores Balanceadores", "Fuentes de Alimentación"
        ));

        createCategoryWithSubs("Accesorios, Materiales y Taller", "accesorios-y-taller", "wrench", 6, List.of(
                "Trenes y Ruedas", "Entelados Monokote / Oracover", "Adhesivos Epoxi / Ciano", "Madera Balsa y Fibra de Carbono", "Herramientas de Campo"
        ));
    }

    private void createCategoryWithSubs(String parentName, String parentSlug, String icon, int order, List<String> subs) {
        Category parent = Category.builder()
                .name(parentName)
                .slug(parentSlug)
                .iconName(icon)
                .displayOrder(order)
                .isActive(true)
                .build();
        parent = categoryRepository.save(parent);

        int subOrder = 1;
        for (String subName : subs) {
            String subSlug = parentSlug + "-" + subName.toLowerCase()
                    .replace(" ", "-")
                    .replace("/", "-");
            Category sub = Category.builder()
                    .parent(parent)
                    .name(subName)
                    .slug(subSlug)
                    .displayOrder(subOrder++)
                    .isActive(true)
                    .build();
            categoryRepository.save(sub);
        }
    }

    private void initStores() {
        createStoreIfAbsent("HobbyMotors", "hobbymotors", "contacto@hobbymotors.com.ar",
                "Referente histórico del aeromodelismo argentino con más de 30 años en radiocontrol, motores de competición y repuestos oficiales.",
                "Futaba, O.S. Engines, Spektrum, Align, E-flite, Dubro",
                "Av. del Libertador 4520", "Vicente López", "Buenos Aires", "5491145678901",
                "https://hobbymotors.com.ar", "@hobbymotorsrc");

        createStoreIfAbsent("AeroJols", "aerojols", "ventas@aerojols.com.ar",
                "Comercio especializado en aeromodelos acrobáticos gigantes, transmisiones FrSky y motores nafteros de gran cilindrada.",
                "FrSky, RadioMaster, DLE Engines, Saito, T-Motor, Kavan",
                "Calle 12 N° 840", "La Plata", "Buenos Aires", "5492214567890",
                "https://aerojols.com.ar", "@aerojols_rc");
    }

    private void createStoreIfAbsent(String name, String slug, String email, String description,
                                     String brands, String address, String city, String province,
                                     String whatsapp, String web, String instagram) {
        if (storeRepository.existsBySlug(slug)) return;

        User owner = userRepository.findByEmail(email).orElseGet(() -> {
            User u = User.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode("StorePass2026!"))
                    .fullName("Encargado " + name)
                    .phoneNumber(whatsapp)
                    .locationProvince(province)
                    .locationCity(city)
                    .role(UserRole.ROLE_USER)
                    .isActive(true)
                    .build();
            return userRepository.save(u);
        });

        Store store = Store.builder()
                .user(owner)
                .name(name)
                .slug(slug)
                .logoUrl("/uploads/stores/" + slug + "-logo.webp")
                .bannerUrl("/uploads/stores/" + slug + "-banner.webp")
                .description(description)
                .brandsRepresented(brands)
                .shipsNationwide(true)
                .addressLine(address)
                .locationCity(city)
                .locationProvince(province)
                .whatsappNumber(whatsapp)
                .websiteUrl(web)
                .instagramHandle(instagram)
                .isVerified(true)
                .isActive(true)
                .build();
        storeRepository.save(store);
        log.info("Tienda Oficial sembrada: {} ({})", name, slug);
    }
}
