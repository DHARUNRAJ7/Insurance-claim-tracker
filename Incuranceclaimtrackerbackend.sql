DROP DATABASE IF EXISTS insurance_db;
CREATE DATABASE insurance_db;
USE insurance_db;

CREATE TABLE claims (
    id INT AUTO_INCREMENT PRIMARY KEY,
    policy_number VARCHAR(20) not null,
    claimant_name VARCHAR(50) not null,
    amount DOUBLE not null,
    status VARCHAR(20)
);
SELECT * FROM claims;
