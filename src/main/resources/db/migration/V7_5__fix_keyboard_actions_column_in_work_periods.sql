ALTER TABLE work_periods ADD COLUMN keyboard_actions_count INT UNSIGNED;

UPDATE work_periods SET keyboard_actions_count=keyboard_mouse_actions_count;

ALTER TABLE work_periods DROP COLUMN keyboard_mouse_actions_count;