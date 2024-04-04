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


INSERT INTO boards (name) VALUES
("Gardening"),
("Home Maintenance"),
("Shopping List");

INSERT INTO tasks (title, description, user_id, board_id) VALUES
     ('Plant new flowers', 'Plant a variety of colorful flowers in the garden', 1, 1),
    ('Trim bushes', 'Use pruning shears to trim overgrown bushes in the backyard', 2, 1),
    ('Buy potting soil', 'Purchase potting soil for repotting plants in the garden', 3, 1),
    ('Pick vegetables', 'Harvest ripe vegetables from the vegetable garden', 4, 1),
    ('Fix broken fence', 'Repair sections of the backyard fence that are damaged', 1, 2),
    ('Replace lightbulbs', 'Purchase and replace burnt-out lightbulbs around the house', 2, 2),
    ('Schedule pest control', 'Call pest control to schedule treatment for ants in the kitchen', 3, 2),
    ('Clean HVAC filters', 'Purchase new filters and clean HVAC system filters', 4, 2),
    ('Buy groceries', 'Purchase groceries for the week including fruits, vegetables, and dairy products', 1, 3),
    ('Get gardening tools', 'Purchase new gardening tools such as shovels and gloves', 2, 3),
    ('Stock up on cleaning supplies', 'Purchase cleaning supplies such as detergent and sponges', 3, 3),
    ('Get pet food', 'Buy pet food and treats for the upcoming week', 4, 3);