<?php
// registrar_sugerencia.php
// Guarda la sugerencia que el usuario escribe en el Buzón.
// Método: POST (JSON)
// Recibe: tipo_usuario, id_usuario, id_reserva y comentario.
// Responde: exito y mensaje.
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$jsoninput = file_get_contents("php://input");
$data = json_decode($jsoninput, true);

$tipo_usuario = isset($data['tipo_usuario']) ? trim($data['tipo_usuario']) : "";
$id_usuario = isset($data['id_usuario']) ? intval($data['id_usuario']) : 0;
$id_reserva = isset($data['id_reserva']) ? intval($data['id_reserva']) : 0;
$comentario = isset($data['comentario']) ? trim($data['comentario']) : "";

// Columna de tblreserva que le corresponde a cada tipo de usuario.
$campos = ["Alumno" => "id_alumno", "Docente" => "matricula", "Externo" => "id_externo"];

if (!isset($campos[$tipo_usuario]) || $id_usuario <= 0 || $id_reserva <= 0 || empty($comentario)) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Escribe tu sugerencia o comentario"
    ]);
    exit;
}

try {
    // La reserva debe ser del usuario y ya haberse usado (Realizada, o Aprobada con la hora de fin ya pasada).
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM tblreserva r
                           WHERE r.id_reserva = ? AND r." . $campos[$tipo_usuario] . " = ?
                             AND (r.estado_reserva = 'Realizada'
                                  OR (r.estado_reserva = 'Aprobada' AND TIMESTAMP(r.fecha_reserva, r.hora_fin) <= ?))");
    $stmt->execute([$id_reserva, $id_usuario, date('Y-m-d H:i:s')]);

    if ($stmt->fetchColumn() == 0) {
        echo json_encode(["exito" => false, "mensaje" => "Solo puedes opinar sobre una sala que ya utilizaste"]);
        exit;
    }

    // Solo se permite una sugerencia por reserva.
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM tblsugerencias WHERE id_reserva = ?");
    $stmt->execute([$id_reserva]);

    if ($stmt->fetchColumn() > 0) {
        echo json_encode(["exito" => false, "mensaje" => "Ya enviaste una sugerencia para esta reserva"]);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO tblsugerencias (id_reserva, comentario) VALUES (?, ?)");
    $stmt->execute([$id_reserva, $comentario]);

    echo json_encode([
        "exito" => true,
        "mensaje" => "¡Gracias! Tu sugerencia fue enviada"
    ]);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode([
        "exito" => false,
        "mensaje" => "Error en el servidor: " . $e->getMessage()
    ]);
}
?>
