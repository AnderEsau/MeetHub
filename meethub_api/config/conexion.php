<?php

// conexion.php
// Abre la conexión con la base de datos. Todas las APIs cargan este archivo con require_once.
// Antes de subirlo al servidor hay que escribir el usuario y la contraseña de la base de datos.

// Zona horaria de la UTHH. Sirve para que "hoy" sea la fecha correcta al calcular los días de anticipación.
date_default_timezone_set("America/Mexico_City");

$host = "localhost";
$db = "u845581198_MeetHub";
$user = "u845581198_Equipo_7";
$pass = "EQUIPo72026";

try {
    // Nos conectamos con utf8mb4, el mismo juego de caracteres de las tablas, para que las tildes y la ñ se vean bien.
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
    // Si una consulta falla, PDO lanza una excepción que podemos atrapar con try/catch.
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Cada fila llega como un arreglo con el nombre de cada columna, por ejemplo $fila['nombre'].
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

} catch (PDOException $e) {
    // Si la conexión falla, respondemos con el código HTTP 500 y un JSON que explica el problema.
    header("Content-Type: application/json", true, 500);
    echo json_encode([
        "error" => "Error crítico de conexión al servidor",
        "detalle" => $e->getMessage()
    ]);
    exit;
}
?>
