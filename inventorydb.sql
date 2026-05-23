-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 23, 2026 at 11:28 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inventorydb`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`, `description`) VALUES
(1, 'Kitchen Appliances', 'Indian kitchen equipment and appliances'),
(2, 'Electronics', 'Mobile phones, laptops, and electronic gadgets'),
(3, 'Grocery & Staples', 'Indian food grains, pulses, and cooking essentials'),
(4, 'Fashion & Clothing', 'Traditional and western clothing items'),
(5, 'Home & Furniture', 'Indian home decor and furniture items'),
(6, 'fvgbg', 'gbhny');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `placed_by` varchar(50) DEFAULT NULL,
  `order_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('pending','paid','cancelled') DEFAULT 'pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `product_id`, `quantity`, `placed_by`, `order_date`, `status`) VALUES
(1, 4, 2, 'vikram_singh', '2025-08-24 19:41:32', 'paid'),
(2, 2, 1, 'vikram_singh', '2025-08-24 20:44:05', 'paid'),
(3, 1, 3, 'h123', '2025-08-24 21:00:35', 'paid'),
(4, 3, 1, 'vikram_singh', '2025-08-25 04:29:41', 'cancelled'),
(5, 2, 1, 'vikram_singh', '2025-08-25 04:31:03', 'paid');

--
-- Triggers `orders`
--
DELIMITER $$
CREATE TRIGGER `trg_prevent_negative_stock` BEFORE INSERT ON `orders` FOR EACH ROW BEGIN
  DECLARE current_stock INT;

  SELECT QuantityInStock INTO current_stock
  FROM products
  WHERE ProductID = NEW.product_id;

  IF current_stock < NEW.quantity THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Not enough stock available!';
  END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_reduce_stock` AFTER INSERT ON `orders` FOR EACH ROW BEGIN
  UPDATE products
  SET QuantityInStock = QuantityInStock - NEW.quantity
  WHERE ProductID = NEW.product_id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `ProductID` int(11) NOT NULL,
  `ProductName` varchar(100) NOT NULL,
  `Description` text DEFAULT NULL,
  `CategoryID` int(11) DEFAULT NULL,
  `SupplierID` int(11) DEFAULT NULL,
  `QuantityInStock` int(11) DEFAULT 0,
  `UnitPrice` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`ProductID`, `ProductName`, `Description`, `CategoryID`, `SupplierID`, `QuantityInStock`, `UnitPrice`) VALUES
(1, 'Prestige Pressure Cooker 3L', 'Stainless steel pressure cooker for Indian cooking', 1, 2, 41, 1875.00),
(2, 'Samsung Galaxy M34 5G', '6GB RAM, 128GB Storage, 6000mAh Battery', 2, 1, 12, 18999.00),
(3, 'Fortune Chakki Fresh Atta 5kg', 'Whole wheat flour for chapati and roti', 3, 4, 50, 250.00),
(4, 'Women\'s Silk Saree', 'Traditional Banarasi silk saree with zari work', 4, 3, 8, 4500.00),
(5, 'Wooden Dining Table Set', '6-seater solid wood dining table with chairs', 5, 5, 8, 22500.00),
(6, 'fghj', 'fbh', 1, 2, 1, 37.00),
(7, 'Realme Narzo 60 5G', '8GB RAM, 128GB Storage, Super AMOLED Display', 2, 1, 12, 15999.00),
(8, 'Tata Sampann Toor Dal 1kg', 'Premium quality pigeon peas for Indian cooking', 3, 4, 40, 180.00),
(9, 'Men\'s Kurta Pyjama Set', 'Cotton traditional ethnic wear for men', 4, 3, 18, 1999.00),
(10, 'Queen Size Bed with Storage', 'Engineered wood bed with storage drawers', 5, 5, 6, 18500.00),
(14, 'millet', 'pure', 3, 4, 140, 15.00);

-- --------------------------------------------------------

--
-- Table structure for table `product_requests`
--

CREATE TABLE `product_requests` (
  `request_id` int(11) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `requested_by` varchar(50) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `request_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `purchaseorders`
--

CREATE TABLE `purchaseorders` (
  `purchase_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `purchase_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `purchaseorders`
--

INSERT INTO `purchaseorders` (`purchase_id`, `product_id`, `quantity`, `supplier_id`, `purchase_date`) VALUES
(1, 1, 5, 2, '2025-08-24 19:35:52'),
(2, 1, 22, 1, '2025-08-25 04:48:42');

-- --------------------------------------------------------

--
-- Table structure for table `salesorders`
--

CREATE TABLE `salesorders` (
  `sales_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `sales_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `salesorders`
--

INSERT INTO `salesorders` (`sales_id`, `product_id`, `quantity`, `customer_name`, `sales_date`) VALUES
(1, 2, 1, 'vrund', '2025-08-24 19:36:45'),
(2, 6, 54, 'dfdg', '2025-08-25 04:50:35');

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL,
  `supplier_name` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`supplier_id`, `supplier_name`, `phone`, `email`, `address`) VALUES
(1, 'Mumbai Electronics Mart', '+91-9876543210', 'orders@mumbaielectronics.com', 'Shop No. 45, Lamington Road, Mumbai, Maharashtra 400004'),
(2, 'Delhi Kitchen Supplies', '+91-8765432109', 'supplies@delhikitchen.in', 'G-12, Chandni Chowk, New Delhi, Delhi 110006'),
(3, 'Bangalore Fashion House', '+91-7654321098', 'info@blrfashion.com', 'No. 78, Commercial Street, Bangalore, Karnataka 560001'),
(4, 'Chennai Grocery Wholesalers', '+91-6543210987', 'orders@chennaigrocery.co.in', '123, T Nagar, Chennai, Tamil Nadu 600017'),
(5, 'Kolkata Furniture Emporium', '+91-9432109876', 'kolkata@furnitureemporium.in', '45, Park Street, Kolkata, West Bengal 700016'),
(6, 'asd', '8200893871', 'aayushimav@gmail.com', 'sdd');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','user') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `password`, `role`) VALUES
('94842', 'harvishh101', 'user'),
('99999', 'harshitt121', 'admin'),
('amit_patel', 'amit7890', 'user'),
('h123', 'Harshitt121', 'user'),
('h1234', 'harshitd123', 'user'),
('priya_sharma', 'priya@456', 'admin'),
('rajesh_kumar', 'secure123', 'admin'),
('riya', 'riyashah120527', 'user'),
('sneha_gupta', 'sneha2024', 'user'),
('vikram_singh', 'vikram123', 'user');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `placed_by` (`placed_by`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`ProductID`),
  ADD KEY `CategoryID` (`CategoryID`),
  ADD KEY `SupplierID` (`SupplierID`);

--
-- Indexes for table `product_requests`
--
ALTER TABLE `product_requests`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `requested_by` (`requested_by`);

--
-- Indexes for table `purchaseorders`
--
ALTER TABLE `purchaseorders`
  ADD PRIMARY KEY (`purchase_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `salesorders`
--
ALTER TABLE `salesorders`
  ADD PRIMARY KEY (`sales_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`supplier_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `ProductID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `product_requests`
--
ALTER TABLE `product_requests`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `purchaseorders`
--
ALTER TABLE `purchaseorders`
  MODIFY `purchase_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `salesorders`
--
ALTER TABLE `salesorders`
  MODIFY `sales_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `supplier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`ProductID`),
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`placed_by`) REFERENCES `users` (`username`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`CategoryID`) REFERENCES `categories` (`category_id`),
  ADD CONSTRAINT `products_ibfk_2` FOREIGN KEY (`SupplierID`) REFERENCES `suppliers` (`supplier_id`);

--
-- Constraints for table `product_requests`
--
ALTER TABLE `product_requests`
  ADD CONSTRAINT `product_requests_ibfk_1` FOREIGN KEY (`requested_by`) REFERENCES `users` (`username`);

--
-- Constraints for table `purchaseorders`
--
ALTER TABLE `purchaseorders`
  ADD CONSTRAINT `purchaseorders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`ProductID`),
  ADD CONSTRAINT `purchaseorders_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`);

--
-- Constraints for table `salesorders`
--
ALTER TABLE `salesorders`
  ADD CONSTRAINT `salesorders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`ProductID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
