CREATE TABLE work_periods(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    start_moment_id INT UNSIGNED NOT NULL,
    end_moment_id INT UNSIGNED NOT NULL,
    mouse_actions_count INT UNSIGNED NOT NULL,
    keyboard_mouse_actions_count INT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY(start_moment_id) REFERENCES moments(id),
    FOREIGN KEY(end_moment_id) REFERENCES moments(id)
);
