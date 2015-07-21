CREATE TABLE work_sessions(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tracking_id INT UNSIGNED NOT NULL,
    last_logged_period_id INT UNSIGNED NOT NULL,
    status varchar(64) NOT NULL,
    mouse_actions_count INT UNSIGNED NOT NULL,
    keyboard_mouse_actions_count INT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY(tracking_id) REFERENCES trackings(id),
    FOREIGN KEY(last_logged_period_id) REFERENCES work_periods(id)
);

ALTER TABLE work_periods ADD COLUMN work_session_id INT UNSIGNED NOT NULL REFERENCES work_sessions(id);
