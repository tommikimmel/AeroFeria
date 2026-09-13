package com.aeroferia.api.config;

import com.aeroferia.api.entity.Category;
import com.aeroferia.api.entity.Publication;
import com.aeroferia.api.entity.PublicationImage;
import com.aeroferia.api.entity.Store;
import com.aeroferia.api.entity.User;
import com.aeroferia.api.entity.enums.Currency;
import com.aeroferia.api.entity.enums.ItemCondition;
import com.aeroferia.api.entity.enums.PublicationStatus;
import com.aeroferia.api.entity.enums.UserRole;
import com.aeroferia.api.repository.CategoryRepository;
import com.aeroferia.api.repository.PublicationRepository;
import com.aeroferia.api.repository.StoreRepository;
import com.aeroferia.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final PublicationRepository publicationRepository;
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
        initPublications();
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
            String cleanSub = Normalizer.normalize(subName, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .toLowerCase()
                    .replace(" ", "-")
                    .replace("/", "-");
            String subSlug = parentSlug + "-" + cleanSub;
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

    private void initPublications() {
        if (publicationRepository.count() > 0) return;

        Category catRadios = findCategory("radios-y-electronica-transmisores-y-radios", "radios-y-electronica");
        Category catMotores = findCategory("motores-y-combustion-motores-nafteros-gasolina", "motores-y-combustion");
        Category catAviones = findCategory("aviones-y-planeadores-acrobaticos-3d", "aviones-y-planeadores");
        Category catBaterias = findCategory("baterias-y-cargadores-baterias-lipo-2s-a-6s", "baterias-y-cargadores");
        Category catDrones = findCategory("helicopteros-y-drones-drones-fpv-freestyle-y-racing", "helicopteros-y-drones");

        Store hobbyMotors = storeRepository.findBySlug("hobbymotors").orElse(null);
        Store aeroJols = storeRepository.findBySlug("aerojols").orElse(null);
        User adminUser = userRepository.findByEmail(adminEmail).orElse(null);

        if (catRadios != null && hobbyMotors != null) {
            createSamplePub("Futaba 16IZ Super FASSTest 2.4GHz + Receptor R7108SB",
                    "futaba-16iz-super-fasstest-2-4ghz",
                    "Radiocontrol profesional Futaba 16IZ Super con protocolo FASSTest bidireccional y telemetría en tiempo real. Pantalla táctil color a toda luz, 16 canales proporcionales + 2 switches adicionales, batería LiPo de 2000mAh y cargador USB-C. Incluye receptor R7108SB con bus S.Bus2. Garantía oficial HobbyMotors Argentina.",
                    ItemCondition.NEW, new BigDecimal("850.00"), Currency.USD,
                    hobbyMotors.getUser(), hobbyMotors, catRadios,
                    "Vicente López", "Buenos Aires",
                    "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800&auto=format&fit=crop&q=80");
        }

        if (catMotores != null && aeroJols != null) {
            createSamplePub("Motor DLE 55cc Gasolina 2 Tiempos con CDI y Escape",
                    "motor-dle-55cc-gasolina-2t-cdi",
                    "Motor DLE 55 monocilíndrico 5.5 HP a 8500 RPM. Con tan solo 3 vuelos de ablande en banco con lubricante Motul 800 al 30:1. Compresión impecable, incluye encendido electrónico CDI original, bujía NGK CM6, escape pitts de aluminio y separadores de dural para bancada. Listo para montar en avión acrobático 50cc.",
                    ItemCondition.LIKE_NEW, new BigDecimal("480.00"), Currency.USD,
                    aeroJols.getUser(), aeroJols, catMotores,
                    "La Plata", "Buenos Aires",
                    "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=800&auto=format&fit=crop&q=80");
        }

        if (catAviones != null && adminUser != null) {
            createSamplePub("Avión Acrobático Extra 330SC 35% Balsa y Fibra de Carbono",
                    "avion-acrobatico-extra-330sc-35-balsa-carbono",
                    "Extra 330SC envergadura 2.60m construido con balsa seleccionada AAA y cuadernas en laminado de carbono. Entelado completo en Oracover alemán original de alta visibilidad. Incluye fundas térmicas acolchadas para alas y bayoneta de carbono de 30mm. Impecable estado sin golpes ni detalles estructurales.",
                    ItemCondition.USED_GOOD, new BigDecimal("1250.00"), Currency.USD,
                    adminUser, null, catAviones,
                    "CABA", "Buenos Aires",
                    "https://images.unsplash.com/photo-1519074069444-1ba4ea16e911?w=800&auto=format&fit=crop&q=80");
        }

        if (catBaterias != null && hobbyMotors != null) {
            createSamplePub("Batería LiPo Gens Ace Tattu 6S 5000mAh 60C XT90",
                    "bateria-lipo-gens-ace-tattu-6s-5000mah-60c",
                    "Pack LiPo de alto rendimiento Gens Ace Tattu 22.2V 6S 5000mAh 60C continuo (120C pico). Ideal para jets EDF de 80-90mm, helicópteros clase 550-600 y aviones 3D eléctricos. Celdas niveladas con resistencia interna inferior a 2 mΩ. Cable siliconado 10AWG con conector XT90 anti-spark.",
                    ItemCondition.NEW, new BigDecimal("165000.00"), Currency.ARS,
                    hobbyMotors.getUser(), hobbyMotors, catBaterias,
                    "Vicente López", "Buenos Aires",
                    "https://images.unsplash.com/photo-1563770660941-20978e870e26?w=800&auto=format&fit=crop&q=80");
        }

        if (catDrones != null && aeroJols != null) {
            createSamplePub("Sistema Digital DJI O3 Air Unit HD FPV con Cámara y Antena",
                    "sistema-digital-dji-o3-air-unit-hd-fpv",
                    "Módulo de transmisión de video digital HD DJI O3 Air Unit con resolución 4K/60fps y latencia ultra baja de 28ms. Compatible con DJI Goggles 2 y DJI Goggles Integra. Incluye cámara, módulo de transmisión, antena dual y cableado para controladoras Betaflight.",
                    ItemCondition.NEW, new BigDecimal("295.00"), Currency.USD,
                    aeroJols.getUser(), aeroJols, catDrones,
                    "La Plata", "Buenos Aires",
                    "https://images.unsplash.com/photo-1527977966376-1c8408f9f108?w=800&auto=format&fit=crop&q=80");
        }

        if (catMotores != null && adminUser != null) {
            createSamplePub("Motor Saito FA-100 Cuatro Tiempos Glow Special Edition",
                    "motor-saito-fa-100-cuatro-tiempos-glow",
                    "Motor 4T Saito FA-100 japonés con terminación Golden Knight (cárter negro y tapas doradas). Sonido maquetero inigualable, excelente respuesta de aceleración y compresión de fábrica. Incluye escape original de aluminio y bujía Saito 4T. Ideal para maquetas escala 60-90.",
                    ItemCondition.LIKE_NEW, new BigDecimal("340.00"), Currency.USD,
                    adminUser, null, catMotores,
                    "CABA", "Buenos Aires",
                    "https://images.unsplash.com/photo-1517524008697-84bbe3c3fd98?w=800&auto=format&fit=crop&q=80");
        }

        log.info("Publicaciones iniciales de aeromodelismo sembradas con éxito.");
    }

    private Category findCategory(String preferredSlug, String fallbackSlug) {
        return categoryRepository.findBySlug(preferredSlug)
                .or(() -> categoryRepository.findBySlug(preferredSlug.replace("-acrobaticos-", "-acrobáticos-")
                        .replace("-baterias-", "-baterías-")))
                .or(() -> categoryRepository.findBySlug(fallbackSlug))
                .orElse(null);
    }

    private void createSamplePub(String title, String slug, String description,
                                 ItemCondition condition, BigDecimal price, Currency currency,
                                 User user, Store store, Category category,
                                 String city, String province, String imageUrl) {
        Publication pub = Publication.builder()
                .title(title)
                .slug(slug)
                .description(description)
                .condition(condition)
                .price(price)
                .currency(currency)
                .user(user)
                .store(store)
                .category(category)
                .locationCity(city)
                .locationProvince(province)
                .status(PublicationStatus.ACTIVE)
                .viewsCount(12)
                .whatsappClicksCount(3)
                .build();

        PublicationImage img = PublicationImage.builder()
                .publication(pub)
                .imageUrl(imageUrl)
                .isCover(true)
                .displayOrder(1)
                .build();
        pub.getImages().add(img);

        publicationRepository.save(pub);
    }
}
