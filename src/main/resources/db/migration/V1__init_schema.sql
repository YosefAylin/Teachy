-- Create users table with inheritance (single table strategy)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    connected BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    user_type VARCHAR(31) NOT NULL -- Discriminator column for inheritance
);

-- Create courses table
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create teacher_courses junction table
CREATE TABLE teacher_courses (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE(teacher_id, course_id)
);

-- Create lesson table (serves as enrollment mechanism)
CREATE TABLE lesson (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    student_id BIGINT,
    teacher_id BIGINT,
    description TEXT,
    timestamp TIMESTAMP,
    status VARCHAR(50), -- PENDING, REJECTED, ACCEPTED
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create schedules table
CREATE TABLE schedules (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT,
    student_id BIGINT,
    lesson_id BIGINT,
    scheduled_time TIMESTAMP,
    status VARCHAR(50), -- SCHEDULED, COMPLETED, CANCELLED
    notes TEXT,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE
);

-- Create study_materials table
CREATE TABLE study_materials (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT,
    uploader_id BIGINT,
    file_name VARCHAR(255),
    file_size BIGINT,
    description TEXT,
    file_data BYTEA,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
    FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_lesson_course ON lesson(course_id);
CREATE INDEX idx_lesson_student ON lesson(student_id);
CREATE INDEX idx_lesson_teacher ON lesson(teacher_id);
CREATE INDEX idx_schedules_teacher ON schedules(teacher_id);
CREATE INDEX idx_schedules_student ON schedules(student_id);
CREATE INDEX idx_schedules_time ON schedules(scheduled_time);
CREATE INDEX idx_study_materials_lesson ON study_materials(lesson_id);
CREATE INDEX idx_teacher_courses_teacher ON teacher_courses(teacher_id);
CREATE INDEX idx_teacher_courses_course ON teacher_courses(course_id);
