package ru.vladimir.sazonov.dispatchLog.service.impl;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vladimir.sazonov.dispatchLog.model.EmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.model.ToWhomWasReported;
import ru.vladimir.sazonov.dispatchLog.service.PrintDescriptionService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Data
public class PrintDescriptionServiceImpl implements PrintDescriptionService {

    @Value("${path_to_print}")
    private String PATH_TO_PRINT;

    @Override
    public void printTripsList(List<EmergencyTrips> emergencyTrips, LocalDateTime start, LocalDateTime end, String region) {
        LocalDate date = LocalDate.now();
        int count = 0;
        StringBuilder builder = new StringBuilder();
        for (EmergencyTrips trip : emergencyTrips) {
            builder.append("\n\n")
                    .append(++count)
                    .append(") ")
                    .append(trip.getDateTime().toLocalDate())
                    .append(" ")
                    .append(trip.getCheckOutTime() == null ? trip.getMessageTime() : trip.getCheckOutTime())
                    .append("\n")
                    .append(trip.getSettlement().getName())
                    .append(", ")
                    .append(trip.getAddress())
                    .append("\n")
                    .append(trip.getDescript());
        }
        DateTimeFormatter localizedDateTime = DateTimeFormatter.ofPattern("HH ч. mm мин. dd-MM-yyyy");
        Path path = Path.of(PATH_TO_PRINT, date.format(DateTimeFormatter.ofPattern("yyyy")),
                date.format(DateTimeFormatter.ofPattern("MM")),
                date.format(DateTimeFormatter.ofPattern("dd")),
                "Данные по пожарам " +
                        (region.equals("city") ? "в городе" : "в области")
                        + " с " + start.format(localizedDateTime) +
                        " по " + end.format(localizedDateTime) + ".doc");
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
            writer.write("Информация по пожарам, произошедшим в " +
                    (region.equals("city") ? "МО \"г. Магадан\"" :  "Магаданской области") + "\n" +
                    "за период с " +
                    start.format(localizedDateTime) + " по " + end.format(localizedDateTime) + "\n\n" +
                    "Всего пожаров: " + count);
            writer.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void printEmergencyTripDesc(EmergencyTrips trip) {
        LocalDate date = LocalDate.now();
        String category = trip.getTripCategory().getName();
        String address = trip.getAddress();
        Path path = Path.of(PATH_TO_PRINT, date.format(DateTimeFormatter.ofPattern("yyyy")),
                date.format(DateTimeFormatter.ofPattern("MM")),
                date.format(DateTimeFormatter.ofPattern("dd")),
                category + ", " + trip.getSettlement().getName() + ", " +
                        trip.getAddress().replaceAll("[/\"]", "") + ", " + trip.getDateTime().toLocalDate() + ".doc");
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Дата и время      ")
                    .append("Выезд  ")
                    .append("Прибытие  ")
                    .append("Cтвол  ")
                    .append("ЛОК    ")
                    .append("ЛОГ    ")
                    .append("Полн.ликв.  ")
                    .append("Возвр.  ")
                    .append("Эвак.  ")
                    .append("Спасено  ")
                    .append("Травм.  ")
                    .append("Погибло\n\n")
                    .append(trip.getDateTime().toLocalDate())
                    .append(" ")
                    .append(trip.getMessageTime())
                    .append("  ")
                    .append(trip.getCheckOutTime() == null ? "     " : trip.getCheckOutTime())
                    .append("  ")
                    .append(trip.getArrivalTime() == null ? "     " : trip.getArrivalTime())
                    .append("     ")
                    .append(trip.getFirstBarrelTime() == null ? "     " : trip.getFirstBarrelTime())
                    .append("  ")
                    .append(trip.getLocTime() == null ? "     " : trip.getLocTime())
                    .append("  ")
                    .append(trip.getLogTime() == null ? "     " : trip.getLogTime())
                    .append("  ")
                    .append(trip.getCompleteLiqTime() == null ? "     " : trip.getCompleteLiqTime())
                    .append("       ")
                    .append(trip.getReturnTime() == null ? "     " : trip.getReturnTime())
                    .append("   ")
                    .append(trip.getEvacuated())
                    .append(trip.getEvacuated() < 100 ? (trip.getEvacuated() < 10 ? "  " : " ") : "")
                    .append("    ")
                    .append(trip.getRescued())
                    .append(trip.getRescued() < 100 ? (trip.getRescued() < 10 ? "  " : " ") : "")
                    .append("      ")
                    .append(trip.getInjured())
                    .append(trip.getInjured() < 100 ? (trip.getInjured() < 10 ? "  " : " ") : "")
                    .append("     ")
                    .append(trip.getDied())
                    .append("\n\n")
                    .append("Категория:  ")
                    .append(category)
                    .append("\n\n\n")
                    .append(trip.getSettlement().getName())
                    .append(", ")
                    .append(address)
                    .append("\n\n")
                    .append(trip.getDescript())
                    .append("\n\n\n")

                    .append("Выезжали подразделения:\n\n")
                    .append("Подразделение           ")
                    .append("Отд. ")
                    .append("Л/с ")
                    .append("Техника ")
                    .append("Выезд ")
                    .append("Приб.  ")
                    .append("Возвр. ")
                    .append("\"А\" ")
                    .append("\"Б\" ")
                    .append("\"ЛС\" ")
                    .append("\"ГПС\" ")
                    .append("\"Пурга\" ")
                    .append("\"СВП\" ")
                    .append("ГДЗС ")
                    .append("Вр.ГДЗС ")
                    .append("Гос.номeр\n\n");

            trip.getAlarmDivisionWorkList().forEach(e ->
                    builder.append(e.getDivision().getName())
                            .append(" ".repeat(Math.max(0, 22 - e.getDivision().getName().length())))
                            .append("  ")
                            .append(e.getDivisionNumber().equals("Резерв") ? "Р" : e.getDivisionNumber())
                            .append("    ")
                            .append(e.getStuff())
                            .append("   ")
                            .append(e.getCarType().getName())
                            .append(" ".repeat(Math.max(0, 4 - e.getCarType().getName().length())))
                            .append("    ")
                            .append(e.getCheckOutTime())
                            .append(" ")
                            .append(e.getArrivalTime() == null ? "     " : e.getArrivalTime())
                            .append("  ")
                            .append(e.getReturnTime() == null ? "      " : e.getReturnTime())
                            .append("   ")
                            .append(e.getBarrelA())
                            .append("   ")
                            .append(e.getBarrelB())
                            .append("   ")
                            .append(e.getBarrelLs())
                            .append("    ")
                            .append(e.getBarrelGps())
                            .append("     ")
                            .append(e.getBarrelPurga())
                            .append("       ")
                            .append(e.getBarrelSvp())
                            .append("    ")
                            .append(e.getGdzsCount())
                            .append("    ")
                            .append(e.getGdzsWork())
                            .append(e.getGdzsWork() < 100 ? (e.getGdzsWork() < 10 ? "       " : "      ") : "     ")
                            .append(e.getCarNumber().isBlank() ? "" : e.getCarNumber())
                            .append("\n\n"));
            builder.append("\nСообщено:\n\n");
            int count = 1;
            for (ToWhomWasReported reported : trip.getToWhomWasReportedList()) {
                String nameReported = reported.getNameWasReported();
                String posReported = reported.getPositionWasReported().getName();
                builder.append(posReported)
                        .append(":")
                        .append(" ".repeat(Math.max(0, 12 - posReported.length())))
                        .append("  ")
                        .append(nameReported)
                        .append(" ".repeat(Math.max(0, 15 - nameReported.length())))
                        .append("  ")
                        .append(reported.getMessageTime())
                        .append(count++ % 3 == 0 ? "\n\n" : " | ");
            }
            writer.write(builder.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
