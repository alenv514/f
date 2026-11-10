<?php
// Ajusta usuario y clave segun tu XAMPP/WAMP
$conn = new mysqli("localhost", "root", "", "banco_db");
if ($conn->connect_error) {
    die("Error de conexion: " . $conn->connect_error);
}
$conn->set_charset("utf8mb4");
