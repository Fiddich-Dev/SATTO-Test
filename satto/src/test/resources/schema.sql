drop table if exists users CASCADE;
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       studentId VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       nickname VARCHAR(255),
                       department VARCHAR(255),
                       grade INT,
                       isPublic BOOLEAN DEFAULT TRUE,
                       profileImage VARCHAR(255),
                       role VARCHAR(255) NOT NULL,
                       UNIQUE(id)
);