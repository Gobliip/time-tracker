CREATE TABLE moments(
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	tracking_id INT UNSIGNED NOT NULL,
	moment_instant DATETIME NOT NULL,
	memo VARCHAR(1024),
	created_at DATETIME NOT NULL, 
	updated_at DATETIME,
	FOREIGN KEY(tracking_id) REFERENCES trackings(id)
);