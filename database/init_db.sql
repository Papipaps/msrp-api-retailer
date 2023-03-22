CREATE SEQUENCE profile_sequence START 1;
CREATE SEQUENCE token_sequence START 1;
CREATE SEQUENCE product_sequence START 1;
CREATE SEQUENCE orders_sequence START 1;
CREATE SEQUENCE order_product_sequence START 1;

CREATE TABLE IF NOT EXISTS profile (
    id BIGINT DEFAULT nextval('profile_sequence'),
    username VARCHAR(30) UNIQUE NOT NULL,
    first_name VARCHAR(40) NOT NULL,
    last_name VARCHAR(40) NOT NULL,
    email VARCHAR(60) UNIQUE NOT NULL,
    postal_code VARCHAR(15) NOT NULL,
    city VARCHAR(40) NOT NULL,
    roles VARCHAR(255) NOT NULL,
    company_name VARCHAR(40) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS confirmation_token (
    id BIGINT DEFAULT nextval('token_sequence'),
    token VARCHAR(64) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    updated_at TIMESTAMP,
    profile_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product (
    id BIGINT DEFAULT nextval('product_sequence'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    description TEXT,
    color VARCHAR(255),
    stock INT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT DEFAULT nextval('orders_sequence'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    profile_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (profile_id) REFERENCES profile(id),
    FOREIGN KEY (product_id) REFERENCES product(id),
    PRIMARY KEY (id)

);





