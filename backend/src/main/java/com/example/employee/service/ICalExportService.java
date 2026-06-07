package com.example.employee.service;

import com.example.employee.entity.CalendarEvent;
import com.example.employee.entity.EventType;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ICalExportService {

    private static final DateTimeFormatter ICAL_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final ZoneId UTC = ZoneId.of("UTC");

    public String exportToICal(List<CalendarEvent> events, String calendarName) {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//Team Calendar//CN\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n");
        sb.append("METHOD:PUBLISH\r\n");
        sb.append("X-WR-CALNAME:").append(calendarName != null ? calendarName : "Team Calendar").append("\r\n");
        sb.append("X-WR-TIMEZONE:Asia/Shanghai\r\n");

        for (CalendarEvent event : events) {
            sb.append(convertToVEvent(event));
        }

        sb.append("END:VCALENDAR\r\n");
        return sb.toString();
    }

    private String convertToVEvent(CalendarEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VEVENT\r\n");

        String uid = event.getId() != null
                ? event.getId() + "@team-calendar"
                : UUID.randomUUID().toString() + "@team-calendar";
        sb.append("UID:").append(uid).append("\r\n");

        String dtStamp = java.time.LocalDateTime.now().atZone(UTC).format(ICAL_DATE_FORMAT);
        sb.append("DTSTAMP:").append(dtStamp).append("\r\n");

        String displayName = getEventTypeDisplayName(event.getEventType());
        String summary = "[" + displayName + "] " + event.getTitle();
        sb.append("SUMMARY:").append(escapeICalText(summary)).append("\r\n");

        if (event.getIsAllDay() != null && event.getIsAllDay()) {
            sb.append("DTSTART;VALUE=DATE:")
                    .append(event.getStartTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .append("\r\n");
            sb.append("DTEND;VALUE=DATE:")
                    .append(event.getEndTime().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .append("\r\n");
        } else {
            sb.append("DTSTART:")
                    .append(event.getStartTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).format(ICAL_DATE_FORMAT))
                    .append("\r\n");
            sb.append("DTEND:")
                    .append(event.getEndTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).format(ICAL_DATE_FORMAT))
                    .append("\r\n");
        }

        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            sb.append("DESCRIPTION:").append(escapeICalText(event.getDescription())).append("\r\n");
        }

        if (event.getLocation() != null && !event.getLocation().isEmpty()) {
            sb.append("LOCATION:").append(escapeICalText(event.getLocation())).append("\r\n");
        }

        if (event.getColor() != null && !event.getColor().isEmpty()) {
            sb.append("COLOR:").append(event.getColor()).append("\r\n");
        }

        if (event.getEmployeeName() != null && !event.getEmployeeName().isEmpty()) {
            sb.append("ORGANIZER;CN=").append(event.getEmployeeName()).append(":mailto:no-reply@team-calendar\r\n");
        }

        sb.append("CATEGORIES:").append(displayName).append("\r\n");

        if (event.getCreatedAt() != null) {
            sb.append("CREATED:")
                    .append(event.getCreatedAt().atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).format(ICAL_DATE_FORMAT))
                    .append("\r\n");
        }

        if (event.getUpdatedAt() != null) {
            sb.append("LAST-MODIFIED:")
                    .append(event.getUpdatedAt().atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).format(ICAL_DATE_FORMAT))
                    .append("\r\n");
        }

        sb.append("END:VEVENT\r\n");
        return sb.toString();
    }

    private String getEventTypeDisplayName(String eventType) {
        try {
            return EventType.valueOf(eventType).getDisplayName();
        } catch (Exception e) {
            return "自定义";
        }
    }

    private String escapeICalText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n");
    }
}
