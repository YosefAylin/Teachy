package io.jos.onlinelearningplatform.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TEACHER")
@NoArgsConstructor
@Data
public class Teacher extends User {


    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Lesson> meetings;


    public Teacher(String username, String email, String hash) {
        super(username, email, hash, "TEACHER");
        this.meetings = new ArrayList<>();

    }
}
