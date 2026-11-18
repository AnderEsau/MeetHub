<?php
// detalle_sala.php
// Entrega los datos de una sala y el equipamiento que tiene (pantalla Detalle de sala).
// Método: GET
// Recibe: sala_id en la dirección, por ejemplo detalle_sala.php?sala_id=4
// Responde: un objeto JSON con la sala, equipamiento_texto y el arreglo equipamiento.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

// Recibimos el sala_id por GET.
$sala_id = isset($_GET['sala_id']) ? intval($_GET['sala_id']) : 0;

if ($sala_id <= 0) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode(["error" => "El ID de sala proporcionado no es válido"]);
    exit;
}

try {
    // 1. Buscamos los datos de la sala.
    $stmt = $pdo->prepare("SELECT s.id_sala, s.nombre_sala, s.id_tipo_sala, t.nombre_tipo, s.ubicacion, s.descripcion,
                                  s.capacidad, s.capacidad_minima, s.hora_apertura, s.hora_cierre, s.imagen_url
                           FROM tblsalas s
                           INNER JOIN tbltipo_sala t ON t.id_tipo_sala = s.id_tipo_sala
                           WHERE s.id_sala = ? AND s.estado = 'Activa'");
    $stmt->execute([$sala_id]);
    $sala = $stmt->fetch();

    if (!$sala) {
        header("HTTP/1.1 404 Not Found");
        echo json_encode(["error" => "La sala no existe o no está disponible"]);
        exit;
    }

    // 2. Buscamos el equipamiento de la sala.
    $stmt = $pdo->prepare("SELECT id_equipamiento, nombre_equipamiento, descripcion, tipo_equipamiento, cantidad
                           FROM tblequipamiento
                           WHERE id_sala = ? AND estado = 'Activo'
                           ORDER BY id_equipamiento");
    $stmt->execute([$sala_id]);
    $equipamiento = $stmt->fetchAll();

    $texto = [];
    foreach ($equipamiento as &$e) {
        $e['id_equipamiento'] = (int) $e['id_equipamiento'];
        $e['cantidad'] = (int) $e['cantidad'];
        $texto[] = $e['cantidad'] . " " . $e['nombre_equipamiento']; // por ejemplo "40 Sillas Ergonómicas"
    }

    // 3. Convertimos los números a enteros y armamos la respuesta.
    $sala['id_sala'] = (int) $sala['id_sala'];
    $sala['id_tipo_sala'] = (int) $sala['id_tipo_sala'];
    $sala['capacidad'] = (int) $sala['capacidad'];
    $sala['capacidad_minima'] = (int) $sala['capacidad_minima'];
    $sala['hora_apertura'] = substr($sala['hora_apertura'], 0, 5);
    $sala['hora_cierre'] = substr($sala['hora_cierre'], 0, 5);
    $sala['equipamiento_texto'] = implode(", ", $texto); // texto listo para mostrarlo en "Mobiliario y equipamiento"
    $sala['equipamiento'] = $equipamiento;

    echo json_encode($sala, JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode(["error" => "Error al obtener el detalle de la sala: " . $e->getMessage()]);
}
?>
