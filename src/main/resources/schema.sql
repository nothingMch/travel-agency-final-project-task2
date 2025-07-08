CREATE TABLE "User" (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone_number VARCHAR(15),
    balance DOUBLE PRECISION DEFAULT 0,
    account_status BOOLEAN DEFAULT TRUE
);


CREATE TABLE Tour (
    id UUID PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    tour_type VARCHAR(50) NOT NULL,
    transfer_type VARCHAR(50) NOT NULL,
    hotel_type VARCHAR(50),
    arrival_date DATE,
    eviction_date DATE,
    is_hot BOOLEAN DEFAULT FALSE
);


CREATE TABLE Voucher (
    id UUID PRIMARY KEY,
    user_id UUID,
    tour_id UUID,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "User"(id) ON DELETE CASCADE,
    FOREIGN KEY (tour_id) REFERENCES Tour(id) ON DELETE CASCADE
);

CREATE TABLE password_reset_token (
    token VARCHAR(100) PRIMARY KEY,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "User"(id) ON DELETE CASCADE
);

