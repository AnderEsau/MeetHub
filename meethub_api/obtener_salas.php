<?php
// obtener_salas.php
// Entrega la lista de salas activas para el catálogo.
// Método: GET (no recibe parámetros)
// Responde: un arreglo JSON con una sala por elemento.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

try {
    // El campo 'equipamiento' junta en un solo texto el equipo activo de cada sala,
    // por ejemplo "Proyector Epson HDMI, Pantalla de Proyección".
    $stmt = $pdo->query("SELECT s.id_sala, s.nombre_sala, s.id_tipo_sala, t.nombre_tipo, s.ubicacion, s.descripcion,
                                s.capacidad, s.capacidad_minima, s.hora_apertura, s.hora_cierre, s.imagen_url,
                                (SELECT GROUP_CONCAT(q.nombre_equipamiento ORDER BY q.id_equipamiento SEPARATOR ', ')
                                   FROM tblequipamiento q
                                  WHERE q.id_sala = s.id_sala AND q.estado = 'Activo') AS equipamiento
                         FROM tblsalas s
                         INNER JOIN tbltipo_sala t ON t.id_tipo_sala = s.id_tipo_sala
                         WHERE s.estado = 'Activa'
                         ORDER BY s.id_sala ASC");
    $salas = $stmt->fetchAll();

    // Convertimos los números a enteros para que coincidan con la data class de Kotlin.
    foreach ($salas as &$s) {
        $s['id_sala'] = (int) $s['id_sala'];
        $s['id_tipo_sala'] = (int) $s['id_tipo_sala'];
        $s['capacidad'] = (int) $s['capacidad'];
        $s['capacidad_minima'] = (int) $s['capacidad_minima'];
        $s['hora_apertura'] = substr($s['hora_apertura'], 0, 5);
        $s['hora_cierre'] = substr($s['hora_cierre'], 0, 5);
        $s['equipamiento'] = $s['equipamiento'] !== null ? $s['equipamiento'] : "";
    }

    echo json_encode($salas, JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode(["error" => "Error al obtener el catálogo de salas: " . $e->getMessage()]);
}
?>
