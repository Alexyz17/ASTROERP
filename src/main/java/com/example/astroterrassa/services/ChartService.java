package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.*;
import com.example.astroterrassa.model.*;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Autowired
    private SugerenciaRepository sugerenciaRepository;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Autowired
    private EventoPersonaRepository eventoUsuarioRepository;

    @Autowired
    private MaterialRepository materialRepository;

    public Map<String, Long> getChartData(String dataType, String year, String month) {
        List<?> dataObjects = null;
        Function<Object, LocalDate> dateExtractor = null;
        Function<Object, Long> valueExtractor = null;

        switch (dataType) {
            case "Usuarios registrados":
                dataObjects = userRepository.findAll();
                dateExtractor = obj -> LocalDate.from(((User) obj).getRegisterDate());
                break;
            case "Simpatizantes":
                dataObjects = userRepository.findAll();
                dateExtractor = obj -> {
                    Date membresia = ((User) obj).getMembresia();
                    return membresia != null ? membresia.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
                };
                break;
            case "Eventos":
            case "SugerenciasDate":
                dataObjects = eventoRepository.findAll();
                dateExtractor = obj -> ((Evento) obj).getFecha_taller_evento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                break;
            case "Genero":
                return getUsersByGenderData();
            case "Edad":
                return getUsersByAgeData();
            case "SimpatizantesVsNo":
                return getSimpatizantesVsNo();
            case "Tipos de sugerencias":
                return getTiposSugerencias();
            case "EventosActivos":
                return getEventosActivos();
            case "Cuotas":
                return getCuotas();
            case "Roles":
                return getUsersRolesData();
            case "EventosUser":
               return getEventosUsuarios();
            case "TipoEvento":
                return getTiposEventos();
            case "notify":
                return getNotify();
            case "AuthMethod":
                return getAuthMethod();
            case "MaterialCount":
                return getMaterialCount();
            case "Material":
                return getMaterialName();
            case "MaterialUbi":
                return getMaterialUbi();
            case "MembresiaCaducada":
                return getMembresiaCaducada();
            case "UsuariosDNI":
                return getUsuariosDNI();
            case "Balance":
                dataObjects = pagoRepository.findAll();
                dateExtractor = obj -> ((Pago) obj).getFechaPago().atZone(ZoneId.systemDefault()).toLocalDate();
                valueExtractor = obj -> (long) ((Pago) obj).getPrecio();
                break;
            case "Usuarios conectados":
                dataObjects = userRepository.findAll();
                dateExtractor = obj -> LocalDate.from(((User) obj).getLastDt());
                break;
            default:
                throw new IllegalArgumentException("Tipo de datos no soportado: " + dataType);
        }

        return getDataByDate(dataObjects, dateExtractor, dataType, year, month, valueExtractor);
    }

    private Map<String, Long> getUsuariosDNI() {
        List<User> users = userRepository.findAll();
        Map<String, Long> usersRole = new HashMap<>();

        for (User user : users) {
            if (user.getDni() != null) {
                usersRole.put("Con DNI: ", usersRole.getOrDefault("Con DNI: ", 0L) + 1);
            } else {
                usersRole.put("Sin DNI: ", usersRole.getOrDefault("Sin DNI: ", 0L) + 1);
            }
        }

        return usersRole;
    }

    private Map<String, Long> getAuthMethod() {
        List<User> users = userRepository.findAll();
        Map<String, Long> usersRole = new HashMap<>();

        for (User user : users) {
            if (user.getAuthType() != null) {
                usersRole.put("Google: ", usersRole.getOrDefault("Google: ", 0L) + 1);
            }
             else {
                usersRole.put("Cuenta: ", usersRole.getOrDefault("Cuenta: ", 0L) + 1);
            }
        }

        return usersRole;
    }

    private Map<String, Long> getMembresiaCaducada() {
        List<User> users = userRepository.findAll();
        Map<String, Long> usersRole = new HashMap<>();

        for (User user : users) {
            if (user.getMembresia() != null) {
                LocalDate fechaMembresia = user.getMembresia().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate fechaActual = LocalDate.now();
                if (fechaMembresia.plusYears(1).isBefore(fechaActual)) {
                    usersRole.put("Caducada: ", usersRole.getOrDefault("Caducada: ", 0L) + 1);
                } else {
                    usersRole.put("No caducada: ", usersRole.getOrDefault("No caducada: ", 0L) + 1);
                }
            }
        }

        return usersRole;
    }

    private Map<String, Long> getMaterialUbi() {
        List<Material> materiales = materialRepository.findAll();
        Map<String, Long> materialUbi = new HashMap<>();

        for (Material material : materiales) {
            if (Objects.isNull(material.getUbicacion())) {
                continue;
            }
            materialUbi.put(material.getUbicacion(), materialUbi.getOrDefault(material.getUbicacion(), 0L) + 1);
        }

        return materialUbi;
    }

    private Map<String, Long> getMaterialName() {
        List<Material> materiales = materialRepository.findAll();
        Map<String, Long> materialName = new HashMap<>();

        for (Material material : materiales) {
            if (Objects.isNull(material.getMaterial_nombre())) {
                continue;
            }
            materialName.put(material.getMaterial_nombre(), materialName.getOrDefault(material.getMaterial_nombre(), 0L) + 1);
        }

        return materialName;
    }

    private Map<String, Long> getMaterialCount() {
        List<Material> materiales = materialRepository.findAll();
        Map<String, Long> materialCount = new HashMap<>();

        for (Material material : materiales) {
            if (Objects.isNull(material.getCantidad())) {
                continue;
            }
            materialCount.put(material.getMaterial_nombre(), materialCount.getOrDefault(material.getMaterial_nombre(), 0L) + material.getCantidad());
        }

        return materialCount;
    }

    private Map<String, Long> getNotify() {
        List<User> users = userRepository.findAll();
        Map<String, Long> usersNotify = new HashMap<>();

        for (User user : users) {
            if (Objects.isNull(user.getNotify())) {
                continue;
            }
            if (user.getNotify() == 1) {
                usersNotify.put("Si: ", usersNotify.getOrDefault("Si: ", 0L) + 1);
            } else if (user.getNotify() == 0) {
                usersNotify.put("No: ", usersNotify.getOrDefault("No: ", 0L) + 1);
            }
        }

        return usersNotify;
    }

    private Map<String, Long> getTiposEventos() {
        List<TipoEvento> eventos = tipoEventoRepository.findAll();
        Map<String, Long> eventosCountsByType = new HashMap<>();

        for (TipoEvento evento : eventos) {
            if (Objects.isNull(evento.getTitulo())) {
                continue;
            }
            eventosCountsByType.put(evento.getTitulo(), eventosCountsByType.getOrDefault(evento.getTitulo(), 0L) + 1);
        }

        return eventosCountsByType;
    }

    public Map<String, Long> getEventosUsuarios() {
        List<Evento> eventos = eventoRepository.findAll();
        Map<String, Long> eventosCountsByType = new HashMap<>();

        for (Evento evento : eventos) {
            if (Objects.isNull(evento.getTitulo())) {
                continue;
            }
            eventosCountsByType.put(evento.getTitulo(), eventosCountsByType.getOrDefault(evento.getTitulo(), 0L) + 1);
        }

        return eventosCountsByType;
    }

    private Map<String, Long> getDataByDate(List<?> dataObjects, Function<Object, LocalDate> dateExtractor, String dataType, String year, String month, Function<Object, Long> valueExtractor) {
        String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        Map<String, Long> countsByDate = new LinkedHashMap<>();

        if (!year.equals("all") && month.equals("all")) {
            for (String monthName : monthNames) {
                countsByDate.put(monthName, 0L);
            }
        }

        if (!year.isEmpty() && !month.isEmpty() && !year.equals("all") && !month.equals("all")) {
            YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
            int daysInMonth = yearMonth.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                countsByDate.put(String.valueOf(day), 0L);
            }
        }

        for (Object dataObject : dataObjects) {
            if (dataObject == null) {
                continue;
            }

            LocalDate date = dateExtractor.apply(dataObject);
            if (date == null) {
                continue;
            }

            String key;
            if (year.equals("all") && (month.equals("all") || month.isEmpty())) {
                key = String.valueOf(date.getYear());
            } else if (!year.isEmpty() && month.equals("all")) {
                if (String.valueOf(date.getYear()).equals(year)) {
                    key = monthNames[date.getMonthValue() - 1];
                } else {
                    continue;
                }
            } else if (!year.isEmpty() && !month.isEmpty()) {
                if (String.valueOf(date.getYear()).equals(year) && String.valueOf(date.getMonthValue()).equals(month)) {
                    key = String.valueOf(date.getDayOfMonth());
                } else {
                    continue;
                }
            } else {
                continue;
            }

            if (valueExtractor != null) {
                countsByDate.put(key, countsByDate.getOrDefault(key, 0L) + valueExtractor.apply(dataObject));
            } else {
                countsByDate.put(key, countsByDate.getOrDefault(key, 0L) + 1);
            }
        }

        return countsByDate;
    }

    private Map<String, Long> getEventosActivos() {
        List<Evento> eventos = eventoRepository.findAll();
        Map<String, Long> eventoActivo = new HashMap<>();

        for (Evento evento : eventos) {
            if (evento.getStatus() == 1) {
                eventoActivo.put("Activos: ", eventoActivo.getOrDefault("Activos: ", 0L) + 1);
            } else if (evento.getStatus() == 0) {
                eventoActivo.put("Inactivos: ", eventoActivo.getOrDefault("Inactivos: ", 0L) + 1);
            }
        }

        return eventoActivo;
    }

    private Map<String, Long> getTiposSugerencias() {
        List<Sugerencia> sugerencias = sugerenciaRepository.findAll();
        Map<String, Long> sugerenciasCountsByType = new HashMap<>();

        for (Sugerencia sugerencia : sugerencias) {
            if (Objects.isNull(sugerencia.getTipoSugerencia())) {
                continue;
            }
            sugerenciasCountsByType.put(sugerencia.getTipoSugerencia(), sugerenciasCountsByType.getOrDefault(sugerencia.getTipoSugerencia(), 0L) + 1);
        }
        return sugerenciasCountsByType;

    }

    private Map<String, Long> getSimpatizantesVsNo() {
        List<User> users = userRepository.findAll();
        Map<String, Long> usersRole = new HashMap<>();

        for (User user : users) {
            if (Objects.isNull(user.getPermisos())) {
                continue;
            }
            if (user.getPermisos() == 0) {
                usersRole.put("Miembros: ", usersRole.getOrDefault("Miembros: ", 0L) + 1);
            } else if (user.getPermisos() == 3) {
                usersRole.put("Simpatizantes: ", usersRole.getOrDefault("Simpatizantes: ", 0L) + 1);
            }
        }

        return usersRole;
    }

    public Map<String, Long> getUsersByGenderData() {
        List<User> users = userRepository.findAll();

        // Agrupar los usuarios por género y contar cuántos usuarios hay de cada género
        return users.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(user -> {
            switch (user.getGenero()) {
                case 0:
                    return "Otros";
                case 1:
                    return "Hombre";
                case 2:
                    return "Mujer";
                default:
                    return "Desconocido";
            }
        }, Collectors.counting()));
    }

    public Map<String, Long> getUsersByAgeData() {
        List<User> users = userRepository.findAll();

        return users.stream().filter(Objects::nonNull).filter(user -> user.getFecha_nt() != null).collect(Collectors.groupingBy(user -> {
            LocalDate birthDate = user.getFecha_nt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            if (age < 18) {
                return "Menos de 18";
            } else if (age < 25) {
                return "18-24";
            } else if (age < 35) {
                return "25-34";
            } else if (age < 45) {
                return "35-44";
            } else if (age < 55) {
                return "45-54";
            } else if (age < 65) {
                return "55-64";
            } else {
                return "65 o más";
            }
        }, Collectors.counting()));
    }

    private Map<String, Long> getCuotas() {
        List<Pago> pagos = pagoRepository.findAll();

        return pagos.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(Pago::getProducto, Collectors.counting()));
    }

    private Map<String, Long> getUsersRolesData() {
        List<UsersRoles> users = usersRolesRepository.findAll();

        return users.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(UsersRoles::getRolNombre, Collectors.counting()));
    }

    public byte[] generarCsv(String data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);

        Map<String, Object> chartData = new Gson().fromJson(data, Map.class);
        for (Map.Entry<String, Object> entry : chartData.entrySet()) {
            pw.println(entry.getKey() + "," + entry.getValue());
        }

        pw.flush();
        pw.close();

        return baos.toByteArray();
    }

}
