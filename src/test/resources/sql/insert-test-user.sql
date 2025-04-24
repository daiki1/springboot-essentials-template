-- Insert test user with encoded password (bcrypt hash of "testpassword")
INSERT INTO users (`id`,`username`,`password`,`email`,`enabled`) VALUES(1, 'testuser_121_unitTest_unique_username39', '$2a$10$at5hhq9SSN8kb06srp6BXeQMhkj9Q4C1.59alTN.74VnCTjtWSkvC', 'testuser@mail.com', TRUE);

-- Link user to role
INSERT INTO user_roles (`user_id`, `role_id`) VALUES (1, 1);