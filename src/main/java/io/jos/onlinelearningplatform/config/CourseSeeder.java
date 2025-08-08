package io.jos.onlinelearningplatform.config;

import io.jos.onlinelearningplatform.model.Course;
import io.jos.onlinelearningplatform.repository.CourseRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseSeeder implements ApplicationRunner {
    private final CourseRepository courseRepo;

    public CourseSeeder(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (courseRepo.count() > 0) return;

        List<Course> defaults = List.of(
                make("Hebrew Language (Reading & Writing)", "MATHEMATICS",
                        "Reading fluency, writing basics, comprehension."),
                make("Mathematics", "Mathematics",
                        "Numbers, operations, shapes, measurement."),
                make("English", "English",
                        "Basic vocabulary, reading and simple conversation."),
                make("Science & Technology","SCIENCE_TECHNOLOGY",
                        "Plants/animals, weather, materials, simple experiments."),
                make("Homeland, Society & Civics", "HOMELAND_SOCIETY_CIVICS",
                        "Community, rules, responsibility, cooperation."),
                make("History & Geography", "HISTORY_GEOGRAPHY",
                        "Maps, continents, early history stories."),
                make("Jewish Culture / Tanakh", "JEWISH_CULTURE_TANAKH",
                        "Selected stories and values (as applicable)."),
                make("Arts & Music", "ARTS_MUSIC",
                        "Drawing, crafting, rhythm, simple instruments."),
                make("Physical Education", "PHYSICAL_EDUCATION",
                        "Movement, coordination, games, teamwork."),
                make("Computers / ICT", "COMPUTERS_ICT",
                        "Basic typing, safe use, simple apps.")        );
        courseRepo.saveAll(defaults);
    }

    private Course make(String title, String subject, String desc) {
        Course c = new Course();
        c.setTitle(title);
        c.setSubject(subject);
        c.setDescription(desc);
        return c;
    }
}
