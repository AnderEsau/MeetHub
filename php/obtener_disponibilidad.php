<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "config/conexion.php";

$sala_id = isset($_GET['sala_id']) ? intval($_GET['sala_id']) : 0;
$fecha_txt = isset($_GET['fecha']) ? trim($_GET['fecha']) : "";


$fecha = false;
foreach (['Y-m-d', 'd/m/Y'] as $formato) {
    $d = DateTime::createFromFormat($formato, $fecha_txt);
    if ($d && $d->format($formato) === $fecha_txt) {
        $fecha = $d->format('Y-m-d');
        break;
    }
}

if ($sala_id <= 0 || !$fecha) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode(["error" => "Debe enviar una sala y una fecha válidas"]);
    exit;
}


if ((int) date('N', strtotime($fecha)) > 5) {
    header("HTTP/1.1 400 Bad Request");
    echo json_encode(["error" => "Solo se puede consultar de lunes a viernes"]);
    exit;
}

try {
    $stmt = $pdo->prepare("SELECT id_sala, nombre_sala, hora_apertura, hora_cierre FROM tblsalas WHERE id_sala = ? AND estado = 'Activa'");
    $stmt->execute([$sala_id]);
    $sala = $stmt->fetch();

    if (!$sala) {
        header("HTTP/1.1 404 Not Found");
        echo json_encode(["error" => "La sala no existe o no está disponible"]);
        exit;
    }


    $stmt = $pdo->prepare("SELECT hora_inicio, hora_fin FROM tblhorarios
                           WHERE estado = 'Activo' AND hora_inicio >= ? AND hora_fin <= ?
                           ORDER BY hora_inicio ASC");
    $stmt->execute([$sala['hora_apertura'], $sala['hora_cierre']]);
    $franjas = $stmt->fetchAll();

    $horarios = [];
    foreach ($franjas as $franja) {
        $hora_ini = $franja['hora_inicio'];
        $hora_fin = $franja['hora_fin'];


        $stmt = $pdo->prepare("SELECT DISTINCT estado_reserva FROM tblreserva
                               WHERE id_sala = ? AND fecha_reserva = ? AND hora_inicio < ? AND hora_fin > ?
                                 AND estado_reserva IN ('Aprobada', 'Realizada', 'Pendiente')");
        $stmt->execute([$sala_id, $fecha, $hora_fin, $hora_ini]);
        $estados = $stmt->fetchAll(PDO::FETCH_COLUMN);

        if (in_array('Aprobada', $estados) || in_array('Realizada', $estados)) {
            $estado = "Ocupado";
        } elseif (in_array('Pendiente', $estados)) {
            $estado = "Pendiente";
        } else {
            $estado = "Libre";
        }

        $horarios[] = [
            "hora_inicio" => substr($hora_ini, 0, 5),
            "hora_fin" => substr($hora_fin, 0, 5),
            "estado" => $estado
        ];
    }

    echo json_encode([
        "id_sala" => (int) $sala['id_sala'],
        "nombre_sala" => $sala['nombre_sala'],
        "fecha" => $fecha,
        "horarios" => $horarios
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    header("HTTP/1.1 500 Internal Server Error");
    echo json_encode(["error" => "Error al obtener la disponibilidad: " . $e->getMessage()]);
}
?>
