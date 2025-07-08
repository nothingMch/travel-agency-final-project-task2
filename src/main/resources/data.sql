-- Добавление пользователей
INSERT INTO "User" (id, username, email, password, role, phone_number, balance, account_status) VALUES
(RANDOM_UUID(), 'admin', 'admin@gmail.com', '$2a$10$yhuknk/e2YmDp2RYJRhlj.AV.VUqSF144a3wOnHiAj.tPllOIWDBy', 'ADMIN', '1234567890', 1000, TRUE),
(RANDOM_UUID(), 'manager', 'manager@gmail.com', '$2a$10$7BHR8HgSQXqree5AMHrhSesu8TGicco46xdI1R1S.yZwkGHetaiqS', 'MANAGER', '0987654321', 500, TRUE),
('3b11f979-b05f-4874-913b-198268b80308', 'user', 'user@gmail.com', '$2a$10$MgoZsdTMxa7s8AJ4cRoxwO41VjcXUginA3ddxIsD.wKhD3QpQoTPq', 'USER', '5555555555', 200, TRUE),
('8926f435-c079-497a-8f19-7e5a76ec250a', 'blockedUser', 'user1@gmail.com', '$2a$10$MgoZsdTMxa7s8AJ4cRoxwO41VjcXUginA3ddxIsD.wKhD3QpQoTPq', 'USER', '5555555555', 200, FALSE);

---- Добавление туров
INSERT INTO Tour (id, title, description, price, tour_type, transfer_type, hotel_type, arrival_date, eviction_date, is_hot) VALUES
  ('645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'Safari Adventure', 'Explore the wild', 500, 'SAFARI', 'JEEPS', 'FOUR_STARS', '2025-07-01', '2025-07-10', TRUE),
  (RANDOM_UUID(), 'Wine Tasting Tour', 'Experience the best wines', 300, 'WINE', 'BUS', 'FIVE_STARS', '2025-06-15', '2025-06-20', FALSE),
  (RANDOM_UUID(), 'Cultural Journey', 'Immerse yourself in the history and traditions', 700, 'CULTURAL', 'TRAIN', 'THREE_STARS', '2025-08-01', '2025-08-05', TRUE),
  (RANDOM_UUID(), 'Adventure Trek', 'Conquer rugged terrains with an experienced guide', 1000, 'ADVENTURE', 'PLANE', 'FOUR_STARS', '2025-09-10', '2025-09-15', FALSE),
  (RANDOM_UUID(), 'Eco Tour', 'Explore natural wonders and learn about conservation', 800, 'ECO', 'BUS', 'THREE_STARS', '2025-10-01', '2025-10-07', TRUE),
  (RANDOM_UUID(), 'Relaxing Beach Holiday', 'Enjoy sun and sea on golden beaches', 600, 'LEISURE', 'SHIP', 'FIVE_STARS', '2025-11-05', '2025-11-12', FALSE),
  (RANDOM_UUID(), 'Mountain Retreat', 'Unwind in the tranquility of mountain scenery', 900, 'HEALTH', 'BUS', 'FOUR_STARS', '2025-12-01', '2025-12-08', TRUE),
  (RANDOM_UUID(), 'Sports Bootcamp', 'Achieve peak fitness with top coaches', 750, 'SPORTS', 'PRIVATE_CAR', 'THREE_STARS', '2026-01-15', '2026-01-22', FALSE),
  (RANDOM_UUID(), 'Historical Exploration', 'Discover ancient cities and learn their stories', 450, 'CULTURAL', 'MINIBUS', 'TWO_STARS', '2026-02-10', '2026-02-15', FALSE),
  (RANDOM_UUID(), 'Luxury Safari', 'Stay in a luxury lodge amidst wildlife', 2000, 'SAFARI', 'PLANE', 'FIVE_STARS', '2026-03-05', '2026-03-15', TRUE),
  (RANDOM_UUID(), 'Extreme Adventure', 'Push your limits in extreme sports activities', 1200, 'ADVENTURE', 'JEEPS', 'FOUR_STARS', '2026-03-20', '2026-03-27', FALSE),
  (RANDOM_UUID(), 'Rainforest Discovery', 'Experience the lush greenery of tropical forests', 1000, 'ECO', 'ELECTRICAL_CARS', 'FIVE_STARS', '2026-04-10', '2026-04-20', TRUE),
  (RANDOM_UUID(), 'Wine Lovers Retreat', 'Indulge in wine tasting and gourmet meals', 1100, 'WINE', 'PLANE', 'FOUR_STARS', '2026-05-01', '2026-05-07', FALSE),
  (RANDOM_UUID(), 'Family Adventure', 'Perfect for family bonding with nature', 950, 'HEALTH', 'BUS', 'THREE_STARS', '2026-06-15', '2026-06-22', TRUE),
  (RANDOM_UUID(), 'City Culture Tour', 'Explore urban art and museums', 400, 'CULTURAL', 'TRAIN', 'THREE_STARS', '2026-07-05', '2026-07-10', FALSE),
  (RANDOM_UUID(), 'Relaxation Getaway', 'Relax and rejuvenate with spa treatments', 1300, 'HEALTH', 'PRIVATE_CAR', 'FIVE_STARS', '2026-08-01', '2026-08-08', TRUE),
  (RANDOM_UUID(), 'Safari with Experts', 'Learn from wildlife experts on this safari', 1800, 'SAFARI', 'JEEPS', 'FIVE_STARS', '2026-08-20', '2026-08-30', TRUE),
  (RANDOM_UUID(), 'Gastronomy and Wine', 'Savor exquisite meals with perfect wine pairings', 1500, 'WINE', 'SHIP', 'FOUR_STARS', '2026-09-10', '2026-09-15', FALSE),
  (RANDOM_UUID(), 'Eco-Friendly Adventure', 'Learn eco-tourism while exploring nature', 1200, 'ECO', 'ELECTRICAL_CARS', 'FIVE_STARS', '2026-10-05', '2026-10-15', TRUE),
  (RANDOM_UUID(), 'Leisurely Boat Ride', 'Enjoy serene waters on a luxury yacht', 700, 'LEISURE', 'SHIP', 'FOUR_STARS', '2026-11-01', '2026-11-10', FALSE),
  (RANDOM_UUID(), 'Cultural Immersion', 'Deep dive into the traditions of locals', 500, 'CULTURAL', 'BUS', 'THREE_STARS', '2026-12-10', '2026-12-15', TRUE),
  (RANDOM_UUID(), 'Winter Sports', 'Thrill yourself with skiing and snowboarding', 800, 'SPORTS', 'PLANE', 'FOUR_STARS', '2027-01-01', '2027-01-07', FALSE),
  (RANDOM_UUID(), 'Tropical Paradise', 'Relax on pristine beaches', 1400, 'LEISURE', 'PLANE', 'FIVE_STARS', '2027-02-15', '2027-02-25', TRUE);

---- Добавление ваучеров
INSERT INTO Voucher (id, user_id, tour_id, status) VALUES
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID'),
(RANDOM_UUID(), '3b11f979-b05f-4874-913b-198268b80308', '645e3643-5a63-40b6-81cd-0e43b2fac9e7', 'PAID');