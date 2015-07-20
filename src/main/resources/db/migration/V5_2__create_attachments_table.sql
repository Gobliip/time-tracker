CREATE TABLE attachments(
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    moment_id INT UNSIGNED NOT NULL,
    content MEDIUMBLOB,
    url varchar(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    status varchar(255) NOT NULL,
    location varchar(64) NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    FOREIGN KEY(moment_id) REFERENCES moments(id)
);

INSERT INTO attachments (moment_id, url, content, created_at, status, location)
    SELECT id, CONCAT('/attachments/', id, "/raw"), attachment, NOW(), 'AVAILABLE', 'DATABASE'
    FROM moments;

ALTER TABLE moments DROP COLUMN attachment;