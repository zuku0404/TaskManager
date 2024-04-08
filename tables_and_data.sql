CREATE TABLE accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
)

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
    ON DELETE CASCADE
)

CREATE TABLE boards (
    board_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
)

CREATE TABLE tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    user_id INT,
    board_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE SET NULL,
    FOREIGN KEY (board_id) REFERENCES boards(board_id)
    ON DELETE CASCADE
)

INSERT INTO accounts (login, password) VALUES
('john_doe', 'john123_password'),
('jane_smith', 'smith456_password'),
('michael_johnson', 'michael789_password'),
('emily_davis', 'emily456_password'),
('william_brown', 'brown123_password');

INSERT INTO users (first_name, last_name, account_id) VALUES
('John', 'Doe', 1),
('Jane', 'Smith', 2),
('Michael', 'Johnson', 3),
('Emily', 'Davis', 4),
('William', 'Brown', 5);

INSERT INTO boards (name) VALUES
('Garden'),
('Housework'),
('Car'),
('Grocery shopping'),
('Home project');

INSERT INTO tasks (title, description, user_id, board_id)
VALUES
    ('Trim the lawn', 'Trim the lawn in front of the house', 1, 1),
    ('Plant flowers', 'Plant new flowers in the garden', 2, 1),
    ('Repair the fence', 'Repair the damaged fence', 3, 1),
    ('Make compost', 'Prepare compost for fertilization', 4, 1),
    ('Clean the kitchen', 'Clean the entire kitchen', 5, 2),
    ('Wash the windows', 'Wash the windows in the living room and kitchen', 1, 2),
    ('Read a book', 'Read a new book', 2, 2),
    ('Cook dinner', 'Prepare dinner for the whole family', 3, 2),
    ('Wash the car', 'Thoroughly wash the car', 4, 3),
    ('Refuel', 'Refuel the car at the station', 5, 3),
    ('Fix the tire', 'Fix the punctured tire', 1, 3),
    ('Change oil', 'Change the oil in the engine', 2, 3),
    ('Do the grocery shopping', 'Do the grocery shopping', 3, 4),
    ('Buy vegetables', 'Buy fresh vegetables for dinner', 4, 4),
    ('Purchase fruits', 'Purchase fresh fruits for breakfast', 5, 4),
    ('Get bread', 'Get some bread from the bakery', 1, 4),
    ('Plan the home project', 'Plan the next home improvement project', 2, 5),
    ('Buy materials', 'Buy necessary materials for the project', 3, 5),
    ('Contact contractor', 'Contact a contractor for further details', 4, 5),
    ('Start renovation', 'Start the renovation process', 5, 5);