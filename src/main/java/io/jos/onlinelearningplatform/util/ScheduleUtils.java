package io.jos.onlinelearningplatform.util;

import io.jos.onlinelearningplatform.model.Lesson;
import io.jos.onlinelearningplatform.model.Schedule;

import java.util.List;
import java.util.stream.Collectors;

public class ScheduleUtils {

    public static List<Schedule> convertLessonsToSchedules(List<Lesson> lessons) {
        return lessons.stream()
                .map(lesson -> {
                    Schedule schedule = new Schedule();
                    schedule.setTeacher(lesson.getTeacher());
                    schedule.setStudent(lesson.getStudent());
                    schedule.setLesson(lesson);
                    schedule.setScheduledTime(lesson.getTimestamp());
                    schedule.setStatus(lesson.getStatus());
                    return schedule;
                })
                .collect(Collectors.toList());
    }
}
