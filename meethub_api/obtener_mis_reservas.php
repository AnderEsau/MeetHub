<?php
// obtener_mis_reservas.php
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$tipo_usuario = isset($_GET['tipo_usuario']) ? $_GET['tipo_usuario'] : "";
$id_usuario = isset($_GET['id_usuario']) ? intval($_GET['id_usuario']) : 0;
$sin_sugerencia = isset($_GET['sin_sugerencia']) ? intval($_GET['sin_sugerencia']) : 0;

if ($id_usuario <= 0 || ($tipo_usuario !== "Alumno" && $tipo_usuario !== "Docente" && $tipo_usuario !== "Externo")) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode(["error" => "Debe enviar un tipo de usuario y un id de usuario válidos"]);
    exit;
}

try {
    if ($sin_sugerencia === 1) {
        if ($tipo_usuario === "Alumno") {
            $stmt = $pdo->prepare("SELECT r.id_reserva, r.id_sala, s.nombre_sala, r.fecha_reserva, r.hora_inicio, r.hora_fin, r.total_asistentes, r.descripcion_evento, r.estado_reserva, r.motivo_rechazo_cancelacion FROM tblreserva r INNER JOIN tblsalas s ON s.id_sala = r.id_sala LEFT JOIN tblsugerencias g ON g.id_reserva = r.id_reserva WHERE r.id_alumno = ? AND g.id_reserva IS NULL AND (r.estado_reserva = 'Realizada' OR r.estado_reserva = 'Aprobada') ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC");
        } elseif ($tipo_usuario === "Docente") {
            $stmt = $pdo->prepare("SELECT r.id_reserva, r.id_sala, s.nombre_sala, r.fecha_reserva, r.hora_inicio, r.hora_fin, r.total_asistentes, r.descripcion_evento, r.estado_reserva, r.motivo_rechazo_cancelacion FROM tblreserva r INNER JOIN tblsalas s ON s.id_sala = r.id_sala LEFT JOIN tblsugerencias g ON g.id_reserva = r.id_reserva WHERE r.matricula = ? AND g.id_reserva IS NULL AND (r.estado_reserva = 'Realizada' OR r.estado_reserva = 'Aprobada') ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC");
        } else {
            $stmt = $pdo->prepare("SELECT r.id_reserva, r.id_sala, s.nombre_sala, r.fecha_reserva, r.hora_inicio, r.hora_fin, r.total_asistentes, r.descripcion_evento, r.estado_reserva, r.motivo_rechazo_cancelacion FROM tblreserva r INNER JOIN tblsalas s ON s.id_sala = r.id_sala LEFT JOIN tblsugerencias g ON g.id_reserva = r.id_reserva WHERE r.id_externo = ? AND g.id_reserva IS NULL AND (r.estado_reserva = 'Realizada' OR r.estado_reserva = 'Aprobada') ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC");
        }
    } else {
        if ($tipo_usuario === "Alumno") {
            $stmt = $pdo->prepare("SELECT r.id_reserva, r.id_sala, s.nombre_sala, r.fecha_reserva, r.hora_inicio, r.hora_fin, r.total_asistentes, r.descripcion_evento, r.estado_reserva, r.motivo_rechazo_cancelacion FROM tblreserva r INNER JOIN tblsalas s ON s.id_sala = r.id_sala WHERE r.id_alumno = ? ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC");
        } elseif ($tipo_usuario === "Docente") {
            $stmt = $pdo->prepare("SELECT r.id_reserva, r.id_sala, s.nombre_sala, r.fecha_reserva, r.hora_inicio, r.hora_fin, r.total_asistentes, r.descripcion_evento, r.estado_reserva, r.motivo_rechazo_cancelacion FROM tblreserva r INNER JOIN tblsalas s ON s.id_sala = r.id_sala WHERE r.matricula = ? ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC");
        } else {
            $stmt = $pdo->prepare("SELECT r.id_reserva, r.id_sala, s.nombre_sala, r.fecha_reserva, r.hora_inicio, r.hora_fin, r.total_asistentes, r.descripcion_evento, r.estado_reserva, r.motivo_rechazo_cancelacion FROM tblreserva r INNER JOIN tblsalas s ON s.id_sala = r.id_sala WHERE r.id_externo = ? ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC");
        }
    }

    $stmt->execute([$id_usuario]);
    $reservas = $stmt->fetchAll();

    foreach ($reservas as &$r) {
        $r['id_reserva'] = (int)$r['id_reserva'];
        $r['id_sala'] = (int)$r['id_sala'];
        $r['total_asistentes'] = (int)$r['total_asistentes'];
        if ($r['id_reserva'] < 10) {
            $r['folio'] = "MH-00" . $r['id_reserva'];
        } elseif ($r['id_reserva'] < 100) {
            $r['folio'] = "MH-0" . $r['id_reserva'];
        } else {
            $r['folio'] = "MH-" . $r['id_reserva'];
        }
    }

    echo json_encode($reservas);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode(["error" => "Error al obtener las reservas: " . $e->getMessage()]);
}
?>